/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public final class Resource
{
	final String name;
	final String contentType;
	
	/**
	 * rounded to full seconds
	 */
	final long lastModified;

	final Object contentLock = new Object();
	byte[] content;

	public Resource(final ResourceSet set, final String name)
	{
		this(set, name, getContentTypeFromName(name));
	}

	public Resource(final ResourceSet set, final String name, final String contentType)
	{
		if(set==null)
			throw new NullPointerException();
		if(name==null)
			throw new NullPointerException();
		if(contentType==null)
			throw new NullPointerException();

		this.name = name;
		this.contentType = contentType;
		this.lastModified = ((System.currentTimeMillis()/1000)+1)*1000;
		
		set.add(this);
	}
	
	public String toString()
	{
		return name;
	}
	
	final void init(final Class resourceLoader)
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

	final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
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
			response.setStatus(response.SC_NOT_MODIFIED);
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
		}
	}
	
	public String url(final HttpServletRequest request)
	{
		return request.getContextPath() + request.getServletPath() + '/' + name;
	}
	
	private static final String getContentTypeFromName(final String name)
	{
		if(name.endsWith(".css"))
			return "text/css";
		else if(name.endsWith(".js"))
			return "application/x-javascript";
		else if(name.endsWith(".png"))
			return "image/png";
		else
			throw new RuntimeException("no content type known for "+name+", specify content type explicitly");
	}
}
