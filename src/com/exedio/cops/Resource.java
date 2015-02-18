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
	private String contentFingerprint;

	private volatile String hostOverride = null;

	private volatile boolean log = false;

	private final VolatileLong response200Count = new VolatileLong();
	private final VolatileLong response304Count = new VolatileLong();

	public Resource(final String name)
	{
		this(name, getContentTypeFromName(name));
	}

	public Resource(final String name, final String contentType)
	{
		if(name==null)
			throw new NullPointerException("name");
		if(contentType==null)
			throw new NullPointerException("contentType");

		this.name = name;
		this.contentType = contentType;
		this.lastModified = roundLastModified(System.currentTimeMillis());
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

	@Override
	public String toString()
	{
		return name;
	}

	public String getURL(final HttpServletRequest request)
	{
		if(request==null)
			throw new NullPointerException("request");
		if(contentFingerprint==null)
			throw new RuntimeException("not initialized: "+name);

		final StringBuilder bf = new StringBuilder();

		final String hostOverride = this.hostOverride;
		if(hostOverride!=null)
			bf.append(request.getScheme()).append("://").
				append(hostOverride);

		bf.append(request.getContextPath()).
			append(request.getServletPath()).
			append('/').append(contentFingerprint).
			append('/').append(name);

		return bf.toString();
	}

	public String getAbsoluteURL(final HttpServletRequest request)
	{
		if(request==null)
			throw new NullPointerException("request");
		if(contentFingerprint==null)
			throw new RuntimeException("not initialized: "+name);

		final String hostOverride = this.hostOverride;
		return
			request.getScheme() + "://" +
			(hostOverride==null ? request.getHeader("Host") : hostOverride) +
			request.getContextPath() +
			request.getServletPath() +
			'/' + contentFingerprint +
			'/' + name;
	}

	public final String getAbsoluteURL(final String token)
	{
		if(token==null)
			throw new NullPointerException("token");
		return EnvironmentRequest.getURL(token, false, name); // TODO fingerprint
	}

	void init(final Class<?> resourceLoader)
	{
		synchronized(contentLock)
		{
			if(content!=null)
				return;

			final InputStream in = resourceLoader.getResourceAsStream(name);
			if(in==null)
				throw new RuntimeException("no resource for "+name);
			try
			{
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final byte[] buf = new byte[20*1024];
				for(int len = in.read(buf); len>=0; len = in.read(buf))
					out.write(buf, 0, len);
				content = out.toByteArray();
				contentFingerprint = makeFingerprint(content);
				out.close();
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				try
				{
					in.close();
				}
				catch(final IOException e)
				{
					throw new RuntimeException(e);
				}
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
	private static final String RESPONSE_EXPIRES = "Expires";
	private static final String RESPONSE_LAST_MODIFIED = "Last-Modified";

	void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		if(content==null)
			throw new RuntimeException("not initialized: "+name);

		response.setContentType(contentType);
		response.setDateHeader(RESPONSE_LAST_MODIFIED, lastModified);
		final long now = System.currentTimeMillis();
		// RFC 2616:
		// To mark a response as "never expires," an origin server sends an
		// Expires date approximately one year from the time the response is
		// sent. HTTP/1.1 servers SHOULD NOT send Expires dates more than one
		// year in the future.
		response.setDateHeader(RESPONSE_EXPIRES, now + 1000l*60*60*24*363); // 363 days

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
