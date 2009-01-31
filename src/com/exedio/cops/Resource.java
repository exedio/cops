/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class Resource
{
	final String name;
	private final String contentType;
	
	/**
	 * rounded to full seconds
	 */
	private final long lastModified;

	private final Object contentLock = new Object();
	private byte[] content;
	
	private volatile long response200Count = 0;
	private volatile long response304Count = 0;

	public Resource(final String name)
	{
		this(name, getContentTypeFromName(name));
	}

	public Resource(final String name, final String contentType)
	{
		if(name==null)
			throw new NullPointerException();
		if(contentType==null)
			throw new NullPointerException();

		this.name = name;
		this.contentType = contentType;
		this.lastModified = ((System.currentTimeMillis()/1000)+1)*1000;
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
		final HttpServletRequest request = CopsServlet.requests.get();
		if(request==null)
			throw new IllegalStateException("no request available");
		
		if(request instanceof EnvironmentRequest)
			return ((EnvironmentRequest)request).getURL(null, name);
		
		return request.getContextPath() + request.getServletPath() + '/' + name;
	}
	
	public String toAbsolute()
	{
		final HttpServletRequest request = CopsServlet.requests.get();
		if(request==null)
			throw new IllegalStateException("no request available");
		
		if(request instanceof EnvironmentRequest)
			return ((EnvironmentRequest)request).getURL(null, name);
		
		return
			request.getScheme() + "://" +
			request.getHeader("Host") +
			request.getContextPath() +
			request.getServletPath() +
			'/' + name;
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
			catch(IOException e)
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
					catch(IOException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
	
	/**
	 * Sets the offset, the Expires http header is set into the future.
	 * Together with a http reverse proxy this ensures,
	 * that for that time no request for that data will reach the servlet.
	 * This may reduce the load on the server.
	 *
	 * TODO: make this configurable, at best per resource.
	 */
	private static final long EXPIRES_OFFSET = 1000 * 60 * 5; // 5 minutes

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
		response.setDateHeader(RESPONSE_EXPIRES, now+EXPIRES_OFFSET);

		final long ifModifiedSince = request.getDateHeader(REQUEST_IF_MODIFIED_SINCE);
		
		if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
		{
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			response304Count++; // may loose a few counts due to concurrency, but this is ok
		}
		else
		{
			response.setContentLength(content.length);
	
			ServletOutputStream out = null;
			try
			{
				out = response.getOutputStream();
				out.write(content);
			}
			finally
			{
				if(out!=null)
					out.close();
			}
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
