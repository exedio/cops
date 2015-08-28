/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cops;

import static java.util.Objects.requireNonNull;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class Resource
{
	final String name;
	private final String contentType;

	/**
	 * rounded to full seconds
	 */
	private volatile long lastModified;

	private final Object contentLock = new Object();
	private byte[] content;

	private volatile String hostOverride = null;

	private volatile boolean log = false;

	private final VolatileLong response200Count = new VolatileLong();
	private final VolatileLong response304Count = new VolatileLong();
	private final VolatileLong response301ByNameCount = new VolatileLong();
	private final VolatileLong response301ByFingerprintCount = new VolatileLong();
	private final VolatileLong response404ByQueryCount = new VolatileLong();

	public Resource(final String name)
	{
		this(name, getContentTypeFromName(name));
	}

	public Resource(final String name, final String contentType)
	{
		this.name = requireNonNull(name, "name");
		this.contentType = requireNonNull(contentType, "contentType");
		this.lastModified = roundLastModified(System.currentTimeMillis());

		if(name.indexOf('/')>=0)
			throw new IllegalArgumentException(
					"name must not contain slash ('/'), but was " + name);
	}

	public String getName()
	{
		return name;
	}

	public String getContentType()
	{
		return contentType;
	}

	public Date getLastModified()
	{
		return new Date(lastModified);
	}

	void setLastModified(final long lastModified)
	{
		if(lastModified<0)
			throw new IllegalArgumentException("lastModified must not be negative, but was " + lastModified);

		this.lastModified = roundLastModified(lastModified);
	}

	private static long roundLastModified(final long lastModified)
	{
		final long remainder = lastModified%1000;
		return (remainder==0) ? lastModified : (lastModified-remainder+1000);
	}

	public int getContentLength()
	{
		return content!=null ? content.length : -1;
	}

	public long getResponse200Count()
	{
		return response200Count.get();
	}

	public long getResponse304Count()
	{
		return response304Count.get();
	}

	public long getResponse301ByNameCount()
	{
		return response301ByNameCount.get();
	}

	public long getResponse301ByFingerprintCount()
	{
		return response301ByFingerprintCount.get();
	}

	public long getResponse404ByQueryCount()
	{
		return response404ByQueryCount.get();
	}

	@Override
	public String toString()
	{
		return name;
	}


	private String pathIfInitialized;

	public String getPath()
	{
		final String result = pathIfInitialized;
		if(result==null)
			throw new IllegalStateException("not yet initialized: " + name);
		return result;
	}

	public String getURL(final HttpServletRequest request)
	{
		requireNonNull(request, "request");

		final StringBuilder bf = new StringBuilder();

		final String hostOverride = this.hostOverride;
		if(hostOverride!=null)
			bf.append(request.getScheme()).append("://").
				append(hostOverride);

		bf.append(request.getContextPath()).
			append(request.getServletPath()).
			append('/').append(getPath());

		return bf.toString();
	}

	public String getAbsoluteURL(final HttpServletRequest request)
	{
		requireNonNull(request, "request");

		final String hostOverride = this.hostOverride;
		return
			request.getScheme() + "://" +
			(hostOverride==null ? request.getHeader("Host") : hostOverride) +
			request.getContextPath() +
			request.getServletPath() +
			'/' + getPath();
	}

	public final String getAbsoluteURL(final String token)
	{
		requireNonNull(token, "token");
		return EnvironmentRequest.getURL(token, false, getPath());
	}

	void init(final Class<?> resourceLoader, final String rootPath)
	{
		synchronized(contentLock)
		{
			if(content!=null)
				return;

			try(final InputStream in = resourceLoader.getResourceAsStream(name))
			{
				if(in==null)
					throw new RuntimeException("no resource for "+name);
				try(ByteArrayOutputStream out = new ByteArrayOutputStream())
				{
					final byte[] buf = new byte[20*1024];
					for(int len = in.read(buf); len>=0; len = in.read(buf))
						out.write(buf, 0, len);
					content = out.toByteArray();
					pathIfInitialized = rootPath + '/' + makeFingerprint(content) + '/' + name;
				}
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	private static String makeFingerprint(final byte[] content)
	{
		final MessageDigest digester = MessageDigestUtil.getInstance("MD5");
		digester.update(content);
		return Hex.encodeLower(digester.digest());
	}

	String getHostOverride()
	{
		return hostOverride;
	}

	void setHostOverride(final String hostOverride)
	{
		this.hostOverride = hostOverride;
	}

	boolean getLog()
	{
		return log;
	}

	void setLog(final boolean log)
	{
		this.log = log;
	}

	private static final String REQUEST_IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String RESPONSE_LAST_MODIFIED = "Last-Modified";

	void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		if(content==null)
			throw new RuntimeException("not initialized: "+name);

		// NOTE
		// This code prevents a Denial of Service attack against the caching mechanism.
		// Query strings can be used to effectively disable the cache by using many urls
		// for one resource. Therefore they are forbidden completely.
		if(request.getQueryString()!=null)
		{
			response.setStatus(SC_NOT_FOUND);
			response404ByQueryCount.inc();
			return;
		}

		response.setContentType(contentType);
		response.setDateHeader(RESPONSE_LAST_MODIFIED, lastModified);
		// RFC 2616:
		// To mark a response as "never expires," an origin server sends an
		// Expires date approximately one year from the time the response is
		// sent. HTTP/1.1 servers SHOULD NOT send Expires dates more than one
		// year in the future.
		response.setHeader("Cache-Control", "max-age=" + 60*60*24*363); // 363 days

		final long ifModifiedSince = request.getDateHeader(REQUEST_IF_MODIFIED_SINCE);

		if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
		{
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			response304Count.inc();
			if(log)
				log(request, "Not Modified");
		}
		else
		{
			BodySender.send(response, content);
			response200Count.inc();
			if(log)
				log(request, "Delivered");
		}
	}

	void doRedirect(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final boolean byName)
	{
		response.setStatus(SC_MOVED_PERMANENTLY);
		// There is no need for absolute url anymore: http://en.wikipedia.org/wiki/HTTP_location
		response.setHeader(
				"Location",
				request.getContextPath() +
				request.getServletPath() +
				'/' + getPath());

		(byName ? response301ByNameCount : response301ByFingerprintCount).inc();
		if(log)
			log(request, byName ? "Redirect by name" : "Redirect by fingerprint");
	}

	private static void log(final HttpServletRequest request, final String action)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("-----------------" + action + "\n");
		bf.append(CopsServlet.report(request));
		bf.append("-----------------\n");
		System.out.print(bf);
	}

	private static String getContentTypeFromName(final String name)
	{
		if(name.endsWith(".css"))
			return "text/css";
		else if(name.endsWith(".txt"))
			return "text/plain";
		else if(name.endsWith(".js"))
			return "application/x-javascript";
		else if(name.endsWith(".png"))
			return "image/png";
		else if(name.endsWith(".gif"))
			return "image/gif";
		else if(name.endsWith(".jpg"))
			return "image/jpeg";
		else if(name.endsWith(".ico"))
			return "image/x-icon";
		else
			throw new RuntimeException("no content type known for " + name + ", specify content type explicitly");
	}
}
