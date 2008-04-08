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

import java.io.BufferedReader;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

public class CopTest extends TestCase
{
	static final class TestCop extends Cop
	{
		TestCop()
		{
			super("test.html");
		}
		
		TestCop(final String param1)
		{
			super("test.html");
			addParameter("param1", param1);
		}
		
		TestCop(final String param1, final String param2)
		{
			super("test.html");
			addParameter("param1", param1);
			addParameter("param2", param2);
		}
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		CopsServlet.requests.remove();
	}
	
	public void testToString()
	{
		assertEquals("test.html", new TestCop().toString());
		assertEquals("test.html?param1=ding", new TestCop("ding").toString());
		assertEquals("test.html?param1=ding&param2=dong", new TestCop("ding", "dong").toString());
	}
	
	public void testToAbsolute()
	{
		CopsServlet.requests.set(new AbsReq());
		assertEquals("scheme://host.exedio.com/contextPath/servletPath/test.html", new TestCop().toAbsolute());
		assertEquals("scheme://host.exedio.com/contextPath/servletPath/test.html?param1=ding", new TestCop("ding").toAbsolute());
		assertEquals("scheme://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", new TestCop("ding", "dong").toAbsolute());
	}
	
	static final class AbsReq implements HttpServletRequest
	{
		public String getScheme()
		{
			return "scheme";
		}

		public String getHeader(final String name)
		{
			if("Host".equals(name))
				return "host.exedio.com";
			else
				throw new RuntimeException();
		}

		public String getContextPath()
		{
			return "/contextPath";
		}

		public String getServletPath()
		{
			return "/servletPath";
		}

		// --------- dummy -----------

		public String getAuthType()
		{
			throw new RuntimeException();
		}

		public Cookie[] getCookies()
		{
			throw new RuntimeException();
		}

		public long getDateHeader(String arg0)
		{
			throw new RuntimeException();
		}

		public Enumeration<?> getHeaderNames()
		{
			throw new RuntimeException();
		}

		public Enumeration<?> getHeaders(String arg0)
		{
			throw new RuntimeException();
		}

		public int getIntHeader(String arg0)
		{
			throw new RuntimeException();
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

		public ServletInputStream getInputStream()
		{
			throw new RuntimeException();
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

		public Enumeration<?> getLocales()
		{
			throw new RuntimeException();
		}

		public String getParameter(String arg0)
		{
			throw new RuntimeException();
		}

		public Map<?,?> getParameterMap()
		{
			throw new RuntimeException();
		}

		public Enumeration<?> getParameterNames()
		{
			throw new RuntimeException();
		}

		public String[] getParameterValues(String arg0)
		{
			throw new RuntimeException();
		}

		public String getProtocol()
		{
			throw new RuntimeException();
		}

		public BufferedReader getReader()
		{
			throw new RuntimeException();
		}

		@Deprecated
		public String getRealPath(String arg0)
		{
			throw new RuntimeException();
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
			throw new RuntimeException();
		}

		public void setAttribute(String arg0, Object arg1)
		{
			throw new RuntimeException();
		}

		public void setCharacterEncoding(String arg0)
		{
			throw new RuntimeException();
		}
	}
}
