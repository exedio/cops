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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

final class EnvironmentRequest implements HttpServletRequest
{
	final String environment;
	
	EnvironmentRequest(final String environment)
	{
		this.environment = environment;
	}
	
	String getURL(final Boolean needsSecure, final String url)
	{
		final boolean secure = needsSecure!=null && needsSecure.booleanValue();
		
		String e = environment;
		if(secure)
		{
			final int pos = e.indexOf(":8080/");
			if(pos>0)
				e = e.substring(0, pos) + ":8443" + e.substring(pos+5);
		}
		else
		{
			final int pos = e.indexOf(":8443/");
			if(pos>0)
				e = e.substring(0, pos) + ":8080" + e.substring(pos+5);
		}
		
		return (secure ? "https://" : "http://") + e + '/' + url;
		
	}

	public String getAuthType()
	{
		throw new RuntimeException();
	}

	public String getContextPath()
	{
		throw new RuntimeException();
	}

	public Cookie[] getCookies()
	{
		throw new RuntimeException();
	}

	public long getDateHeader(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	public String getHeader(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	@Deprecated
	public Enumeration<?> getHeaderNames()
	{
		throw new RuntimeException();
	}

	@Deprecated
	public Enumeration<?> getHeaders(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	public int getIntHeader(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	public String getMethod()
	{
		throw new RuntimeException();
	}

	public String getPathInfo()
	{
		throw new RuntimeException();
	}

	public String getPathTranslated()
	{
		throw new RuntimeException();
	}

	public String getQueryString()
	{
		throw new RuntimeException();
	}

	public String getRemoteUser()
	{
		throw new RuntimeException();
	}

	public String getRequestURI()
	{
		throw new RuntimeException();
	}

	public StringBuffer getRequestURL()
	{
		throw new RuntimeException();
	}

	public String getRequestedSessionId()
	{
		throw new RuntimeException();
	}

	public String getServletPath()
	{
		throw new RuntimeException();
	}

	public HttpSession getSession()
	{
		throw new RuntimeException();
	}

	public HttpSession getSession(boolean arg0)
	{
		throw new RuntimeException();
	}

	public Principal getUserPrincipal()
	{
		throw new RuntimeException();
	}

	public boolean isRequestedSessionIdFromCookie()
	{
		throw new RuntimeException();
	}

	public boolean isRequestedSessionIdFromURL()
	{
		throw new RuntimeException();
	}

	@Deprecated
	public boolean isRequestedSessionIdFromUrl()
	{
		throw new RuntimeException();
	}

	public boolean isRequestedSessionIdValid()
	{
		throw new RuntimeException();
	}

	public boolean isUserInRole(String arg0)
	{
		throw new RuntimeException();
	}

	public Object getAttribute(String arg0)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public Enumeration<?> getAttributeNames()
	{
		throw new RuntimeException();
	}

	public String getCharacterEncoding()
	{
		throw new RuntimeException();
	}

	public int getContentLength()
	{
		throw new RuntimeException();
	}

	public String getContentType()
	{
		throw new RuntimeException();
	}

	public ServletInputStream getInputStream() throws IOException
	{
		throw new IOException();
	}

	public String getLocalAddr()
	{
		throw new RuntimeException();
	}

	public String getLocalName()
	{
		throw new RuntimeException();
	}

	public int getLocalPort()
	{
		throw new RuntimeException();
	}

	public Locale getLocale()
	{
		throw new RuntimeException();
	}

	@Deprecated
	public Enumeration<?> getLocales()
	{
		throw new RuntimeException();
	}

	public String getParameter(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	@Deprecated
	public Map<?,?> getParameterMap()
	{
		throw new RuntimeException();
	}

	@Deprecated
	public Enumeration<?> getParameterNames()
	{
		throw new RuntimeException();
	}

	public String[] getParameterValues(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	public String getProtocol()
	{
		throw new RuntimeException();
	}

	public BufferedReader getReader() throws IOException
	{
		throw new IOException();
	}

	@Deprecated
	public String getRealPath(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	public String getRemoteAddr()
	{
		throw new RuntimeException();
	}

	public String getRemoteHost()
	{
		throw new RuntimeException();
	}

	public int getRemotePort()
	{
		throw new RuntimeException();
	}

	public RequestDispatcher getRequestDispatcher(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	public String getScheme()
	{
		throw new RuntimeException();
	}

	public String getServerName()
	{
		throw new RuntimeException();
	}

	public int getServerPort()
	{
		throw new RuntimeException();
	}

	public boolean isSecure()
	{
		throw new RuntimeException();
	}

	public void removeAttribute(String arg0)
	{
		throw new RuntimeException(arg0);
	}

	public void setAttribute(String arg0, Object arg1)
	{
		throw new RuntimeException(arg0);
	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException
	{
		throw new UnsupportedEncodingException(arg0);
	}
}
