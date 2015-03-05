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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.enumeration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

final class CopParameterRequest implements HttpServletRequest
{
	private final HttpServletRequest nested;
	private final String pathInfo;
	private final String queryString;
	private final LinkedHashMap<String, ArrayList<String>> parameters;

	CopParameterRequest(final HttpServletRequest nested, final String value)
	{
		if(nested==null)
			throw new NullPointerException("nested");
		if(value==null)
			throw new NullPointerException("value");

		this.nested = nested;

		final int queryPos = value.indexOf('?');
		if(queryPos<0)
		{
			pathInfo = '/' + value;
			queryString = null;
			parameters  = null;
		}
		else
		{
			pathInfo = '/' + value.substring(0, queryPos);
			queryString = value.substring(queryPos + 1);
			parameters = new LinkedHashMap<>();

			int startPos = queryPos;
			while(true)
			{
				final int equalPos = value.indexOf('=' , startPos + 1);
				if(equalPos<0)
					throw new IllegalArgumentException(value);
				final String key = value.substring(startPos + 1, equalPos);
				final int endPos = value.indexOf('&', equalPos + 1);
				final String newValue = (endPos<0) ? value.substring(equalPos + 1) : value.substring(equalPos + 1, endPos);
				ArrayList<String> list = parameters.get(key);
				if(list==null)
				{
					list = new ArrayList<>();
					parameters.put(key, list);
				}
				list.add(decode(newValue));

				if(endPos<0)
					break;

				startPos = endPos;
			}
		}
	}

	private static String decode(final String s)
	{
		try
		{
			return URLDecoder.decode(s, UTF_8.name());
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPathInfo()
	{
		return pathInfo;
	}

	@Override
	public String getQueryString()
	{
		return queryString;
	}

	@Override
	public String getParameter(final String name)
	{
		if(name==null)
			throw new NullPointerException("name");
		if(parameters==null)
			return null;

		final ArrayList<String> list = parameters.get(name);
		if(list==null)
			return null;

		return list.get(0);
	}

	private static final String[] EMPTY_ARRAY = new String[]{};

	@Override
	public String[] getParameterValues(final String name)
	{
		if(name==null)
			throw new NullPointerException("name");
		if(parameters==null)
			return EMPTY_ARRAY;

		final ArrayList<String> list = parameters.get(name);
		if(list==null)
			return EMPTY_ARRAY;

		return list.toArray(new String[list.size()]);
	}

	private static final Enumeration<?> EMPTY_ENUMERATION = enumeration(Collections.<String>emptyList());

	@Override
	public Enumeration<?> getParameterNames()
	{
		if(parameters==null)
			return EMPTY_ENUMERATION;

		return enumeration(parameters.keySet());
	}

	@Override
	public Map<?,?> getParameterMap()
	{
		if(parameters==null)
			return Collections.EMPTY_MAP;

		final LinkedHashMap<String, String[]> result = new LinkedHashMap<>();
		for(final Map.Entry<String, ArrayList<String>> e : parameters.entrySet())
			result.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
		return Collections.unmodifiableMap(result);
	}

	// ---- delegated methods ------

	@Override
	public String getAuthType()
	{
		return nested.getAuthType();
	}

	@Override
	public String getContextPath()
	{
		return nested.getContextPath();
	}

	@Override
	public Cookie[] getCookies()
	{
		return nested.getCookies();
	}

	@Override
	public long getDateHeader(final String arg0)
	{
		return nested.getDateHeader(arg0);
	}

	@Override
	public String getHeader(final String arg0)
	{
		return nested.getHeader(arg0);
	}

	@Override
	public Enumeration<?> getHeaderNames()
	{
		return nested.getHeaderNames();
	}

	@Override
	public Enumeration<?> getHeaders(final String arg0)
	{
		return nested.getHeaders(arg0);
	}

	@Override
	public int getIntHeader(final String arg0)
	{
		return nested.getIntHeader(arg0);
	}

	@Override
	public String getMethod()
	{
		return nested.getMethod();
	}

	@Override
	public String getPathTranslated()
	{
		return nested.getPathTranslated();
	}

	@Override
	public String getRemoteUser()
	{
		return nested.getRemoteUser();
	}

	@Override
	public String getRequestURI()
	{
		return nested.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL()
	{
		return nested.getRequestURL();
	}

	@Override
	public String getRequestedSessionId()
	{
		return nested.getRequestedSessionId();
	}

	@Override
	public String getServletPath()
	{
		return nested.getServletPath();
	}

	@Override
	public HttpSession getSession()
	{
		return nested.getSession();
	}

	@Override
	public HttpSession getSession(final boolean arg0)
	{
		return nested.getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal()
	{
		return nested.getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie()
	{
		return nested.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL()
	{
		return nested.isRequestedSessionIdFromURL();
	}

	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl()
	{
		return nested.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid()
	{
		return nested.isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(final String arg0)
	{
		return nested.isUserInRole(arg0);
	}

	@Override
	public Object getAttribute(final String arg0)
	{
		return nested.getAttribute(arg0);
	}

	@Override
	public Enumeration<?> getAttributeNames()
	{
		return nested.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding()
	{
		return nested.getCharacterEncoding();
	}

	@Override
	public int getContentLength()
	{
		return nested.getContentLength();
	}

	@Override
	public String getContentType()
	{
		return nested.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		return nested.getInputStream();
	}

	@Override
	public String getLocalAddr()
	{
		return nested.getLocalAddr();
	}

	@Override
	public String getLocalName()
	{
		return nested.getLocalName();
	}

	@Override
	public int getLocalPort()
	{
		return nested.getLocalPort();
	}

	@Override
	public Locale getLocale()
	{
		return nested.getLocale();
	}

	@Override
	public Enumeration<?> getLocales()
	{
		return nested.getLocales();
	}

	@Override
	public String getProtocol()
	{
		return nested.getProtocol();
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		return nested.getReader();
	}

	@Override
	@Deprecated
	public String getRealPath(final String arg0)
	{
		return nested.getRealPath(arg0);
	}

	@Override
	public String getRemoteAddr()
	{
		return nested.getRemoteAddr();
	}

	@Override
	public String getRemoteHost()
	{
		return nested.getRemoteHost();
	}

	@Override
	public int getRemotePort()
	{
		return nested.getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String arg0)
	{
		return nested.getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme()
	{
		return nested.getScheme();
	}

	@Override
	public String getServerName()
	{
		return nested.getServerName();
	}

	@Override
	public int getServerPort()
	{
		return nested.getServerPort();
	}

	@Override
	public boolean isSecure()
	{
		return nested.isSecure();
	}

	@Override
	public void removeAttribute(final String arg0)
	{
		nested.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(final String arg0, final Object arg1)
	{
		nested.setAttribute(arg0, arg1);
	}

	@Override
	public void setCharacterEncoding(final String arg0)
			throws UnsupportedEncodingException
	{
		nested.setCharacterEncoding(arg0);
	}
}
