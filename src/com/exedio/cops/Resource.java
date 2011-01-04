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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	/**
	 * Sets the offset, the Expires http header is set into the future.
	 * Together with a http reverse proxy this ensures,
	 * that for that time no request for that data will reach the servlet.
	 * This may reduce the load on the server.
	 */
	private volatile long expiresOffset = 1000 * 60 * 5; // 5 minutes

	private volatile long response200Count = 0;
	private volatile long response304Count = 0;

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
		return response200Count;
	}

	public long getResponse304Count()
	{
		return response304Count;
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

		final String hostOverride = this.hostOverride;
		if(hostOverride!=null)
			return
				request.getScheme() + "://" +
				hostOverride +
				request.getContextPath() +
				request.getServletPath() +
				'/' + name;
		else
			return
				request.getContextPath() +
				request.getServletPath() +
				'/' + name;
	}

	public String getAbsoluteURL(final HttpServletRequest request)
	{
		if(request==null)
			throw new NullPointerException("request");

		final String hostOverride = this.hostOverride;
		return
			request.getScheme() + "://" +
			(hostOverride==null ? request.getHeader("Host") : hostOverride) +
			request.getContextPath() +
			request.getServletPath() +
			'/' + name;
	}

	public final String getAbsoluteURL(final String token)
	{
		if(token==null)
			throw new NullPointerException("token");
		return EnvironmentRequest.getURL(token, false, name);
	}

	void init(final Class<?> resourceLoader)
	{
		synchronized(contentLock)
		{
			if(content!=null)
				return;

			InputStream in = null;
			try
			{
				in = resourceLoader.getResourceAsStream(name);
				if(in==null)
					throw new RuntimeException("no resource for "+name);

				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final byte[] buf = new byte[20*1024];
				for(int len = in.read(buf); len>=0; len = in.read(buf))
					out.write(buf, 0, len);
				content = out.toByteArray();
				out.close();
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				if(in!=null)
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
	}

	String getHostOverride()
	{
		return hostOverride;
	}

	void setHostOverride(final String hostOverride)
	{
		this.hostOverride = hostOverride;
	}

	long getExpiresSeconds()
	{
		return expiresOffset/1000;
	}

	void setExpiresSeconds(final long expiresSeconds)
	{
		if(expiresSeconds<0)
			throw new IllegalArgumentException("expiresSeconds must not be negative, but was " + expiresSeconds);

		this.expiresOffset = expiresSeconds*1000;
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
		response.setDateHeader(RESPONSE_EXPIRES, now+expiresOffset);

		final long ifModifiedSince = request.getDateHeader(REQUEST_IF_MODIFIED_SINCE);

		if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
		{
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			response304Count++; // may loose a few counts due to concurrency, but this is ok
		}
		else
		{
			BodySender.send(response, content);
			response200Count++; // may loose a few counts due to concurrency, but this is ok
		}
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
