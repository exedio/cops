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
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class DummyResponse implements HttpServletResponse
{
	public String encodeURL(String url)
	{
		throw new RuntimeException();
	}

	public void addCookie(Cookie arg0)
	{
		throw new RuntimeException();
	}

	public void addDateHeader(String arg0, long arg1)
	{
		throw new RuntimeException();
	}

	public void addHeader(String arg0, String arg1)
	{
		throw new RuntimeException();
	}

	public void addIntHeader(String arg0, int arg1)
	{
		throw new RuntimeException();
	}

	public boolean containsHeader(String arg0)
	{
		throw new RuntimeException();
	}

	public String encodeRedirectURL(String arg0)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public String encodeRedirectUrl(String arg0)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public String encodeUrl(String arg0)
	{
		throw new RuntimeException();
	}

	public void sendError(int arg0)
	{
		throw new RuntimeException();
	}

	public void sendError(int arg0, String arg1)
	{
		throw new RuntimeException();
	}

	public void sendRedirect(String arg0)
	{
		throw new RuntimeException();
	}

	public void setDateHeader(String arg0, long arg1)
	{
		throw new RuntimeException();
	}

	public void setHeader(String arg0, String arg1)
	{
		throw new RuntimeException();
	}

	public void setIntHeader(String arg0, int arg1)
	{
		throw new RuntimeException();
	}

	public void setStatus(int arg0)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public void setStatus(int arg0, String arg1)
	{
		throw new RuntimeException();
	}

	public void flushBuffer()
	{
		throw new RuntimeException();
	}

	public int getBufferSize()
	{
		throw new RuntimeException();
	}

	public String getCharacterEncoding()
	{
		throw new RuntimeException();
	}

	public String getContentType()
	{
		throw new RuntimeException();
	}

	public Locale getLocale()
	{
		throw new RuntimeException();
	}

	public ServletOutputStream getOutputStream()
	{
		throw new RuntimeException();
	}

	public PrintWriter getWriter()
	{
		throw new RuntimeException();
	}

	public boolean isCommitted()
	{
		throw new RuntimeException();
	}

	public void reset()
	{
		throw new RuntimeException();
	}

	public void resetBuffer()
	{
		throw new RuntimeException();
	}

	public void setBufferSize(int arg0)
	{
		throw new RuntimeException();
	}

	public void setCharacterEncoding(String arg0)
	{
		throw new RuntimeException();
	}

	public void setContentLength(int arg0)
	{
		throw new RuntimeException();
	}

	public void setContentType(String arg0)
	{
		throw new RuntimeException();
	}

	public void setLocale(Locale arg0)
	{
		throw new RuntimeException();
	}
}
