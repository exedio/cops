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

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class DummyResponse implements HttpServletResponse
{
	@Override
	public String encodeURL(final String url)
	{
		throw new RuntimeException();
	}

	@Override
	public void addCookie(final Cookie arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void addDateHeader(final String arg0, final long arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void addHeader(final String arg0, final String arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void addIntHeader(final String arg0, final int arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public boolean containsHeader(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public String encodeRedirectURL(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	@Deprecated
	public String encodeRedirectUrl(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	@Deprecated
	public String encodeUrl(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void sendError(final int arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void sendError(final int arg0, final String arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void sendRedirect(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void setDateHeader(final String arg0, final long arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void setHeader(final String arg0, final String arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void setIntHeader(final String arg0, final int arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void setStatus(final int arg0)
	{
		throw new RuntimeException();
	}

	@Override
	@Deprecated
	public void setStatus(final int arg0, final String arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void flushBuffer()
	{
		throw new RuntimeException();
	}

	@Override
	public int getBufferSize()
	{
		throw new RuntimeException();
	}

	@Override
	public String getCharacterEncoding()
	{
		throw new RuntimeException();
	}

	@Override
	public String getContentType()
	{
		throw new RuntimeException();
	}

	@Override
	public Locale getLocale()
	{
		throw new RuntimeException();
	}

	@Override
	public ServletOutputStream getOutputStream()
	{
		throw new RuntimeException();
	}

	@Override
	public PrintWriter getWriter()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isCommitted()
	{
		throw new RuntimeException();
	}

	@Override
	public void reset()
	{
		throw new RuntimeException();
	}

	@Override
	public void resetBuffer()
	{
		throw new RuntimeException();
	}

	@Override
	public void setBufferSize(final int arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void setCharacterEncoding(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void setContentLength(final int arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void setContentLengthLong(final long len)
	{
		throw new RuntimeException();
	}

	@Override
	public void setContentType(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void setLocale(final Locale arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public int getStatus()
	{
		throw new RuntimeException();
	}

	@Override
	public String getHeader(final String string)
	{
		throw new RuntimeException();
	}

	@Override
	public Collection<String> getHeaders(final String string)
	{
		throw new RuntimeException();
	}

	@Override
	public Collection<String> getHeaderNames()
	{
		throw new RuntimeException();
	}

	@Override
	public void setTrailerFields(final Supplier<Map<String, String>> supplier)
	{
		throw new RuntimeException();
	}

	@Override
	public Supplier<Map<String, String>> getTrailerFields()
	{
		throw new RuntimeException();
	}
}
