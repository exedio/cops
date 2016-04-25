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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class DummyRequest implements HttpServletRequest
{
	@Override
	public String getAuthType()
	{
		throw new RuntimeException();
	}

	@Override
	public String getContextPath()
	{
		throw new RuntimeException();
	}

	@Override
	public Cookie[] getCookies()
	{
		throw new RuntimeException();
	}

	@Override
	public long getDateHeader(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public String getHeader(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public Enumeration<String> getHeaderNames()
	{
		throw new RuntimeException();
	}

	@Override
	public Enumeration<String> getHeaders(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public int getIntHeader(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public String getMethod()
	{
		throw new RuntimeException();
	}

	@Override
	public String getPathInfo()
	{
		throw new RuntimeException();
	}

	@Override
	public String getPathTranslated()
	{
		throw new RuntimeException();
	}

	@Override
	public String getQueryString()
	{
		throw new RuntimeException();
	}

	@Override
	public String getRemoteUser()
	{
		throw new RuntimeException();
	}

	@Override
	public String getRequestURI()
	{
		throw new RuntimeException();
	}

	@Override
	public StringBuffer getRequestURL()
	{
		throw new RuntimeException();
	}

	@Override
	public String getRequestedSessionId()
	{
		throw new RuntimeException();
	}

	@Override
	public String getServletPath()
	{
		throw new RuntimeException();
	}

	@Override
	public HttpSession getSession()
	{
		throw new RuntimeException();
	}

	@Override
	public HttpSession getSession(final boolean arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public Principal getUserPrincipal()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isRequestedSessionIdFromURL()
	{
		throw new RuntimeException();
	}

	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isRequestedSessionIdValid()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isUserInRole(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public Object getAttribute(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public Enumeration<String> getAttributeNames()
	{
		throw new RuntimeException();
	}

	@Override
	public String getCharacterEncoding()
	{
		throw new RuntimeException();
	}

	@Override
	public int getContentLength()
	{
		throw new RuntimeException();
	}

	@Override
	public String getContentType()
	{
		throw new RuntimeException();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		throw new IOException();
	}

	@Override
	public String getLocalAddr()
	{
		throw new RuntimeException();
	}

	@Override
	public String getLocalName()
	{
		throw new RuntimeException();
	}

	@Override
	public int getLocalPort()
	{
		throw new RuntimeException();
	}

	@Override
	public Locale getLocale()
	{
		throw new RuntimeException();
	}

	@Override
	public Enumeration<Locale> getLocales()
	{
		throw new RuntimeException();
	}

	@Override
	public String getParameter(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public Map<String,String[]> getParameterMap()
	{
		throw new RuntimeException();
	}

	@Override
	public Enumeration<String> getParameterNames()
	{
		throw new RuntimeException();
	}

	@Override
	public String[] getParameterValues(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public String getProtocol()
	{
		throw new RuntimeException();
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		throw new IOException();
	}

	@Override
	@Deprecated
	public String getRealPath(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public String getRemoteAddr()
	{
		throw new RuntimeException();
	}

	@Override
	public String getRemoteHost()
	{
		throw new RuntimeException();
	}

	@Override
	public int getRemotePort()
	{
		throw new RuntimeException();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public String getScheme()
	{
		throw new RuntimeException();
	}

	@Override
	public String getServerName()
	{
		throw new RuntimeException();
	}

	@Override
	public int getServerPort()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isSecure()
	{
		throw new RuntimeException();
	}

	@Override
	public void removeAttribute(final String arg0)
	{
		throw new RuntimeException();
	}

	@Override
	public void setAttribute(final String arg0, final Object arg1)
	{
		throw new RuntimeException();
	}

	@Override
	public void setCharacterEncoding(final String arg0)
			throws UnsupportedEncodingException
	{
		throw new UnsupportedEncodingException();
	}

	@Override
	public boolean authenticate(HttpServletResponse hsr) throws IOException, ServletException
	{
		throw new RuntimeException();
	}

	@Override
	public void login(String string, String string1) throws ServletException
	{
		throw new RuntimeException();
	}

	@Override
	public void logout() throws ServletException
	{
		throw new RuntimeException();
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException
	{
		throw new RuntimeException();
	}

	@Override
	public Part getPart(String string) throws IOException, ServletException
	{
		throw new RuntimeException();
	}

	@Override
	public ServletContext getServletContext()
	{
		throw new RuntimeException();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException
	{
		throw new RuntimeException();
	}

	@Override
	public AsyncContext startAsync(ServletRequest sr, ServletResponse sr1) throws IllegalStateException
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isAsyncStarted()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isAsyncSupported()
	{
		throw new RuntimeException();
	}

	@Override
	public AsyncContext getAsyncContext()
	{
		throw new RuntimeException();
	}

	@Override
	public DispatcherType getDispatcherType()
	{
		throw new RuntimeException();
	}
}
