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
	protected void setUp() throws Exception
	{
		CopsServlet.responses.set(new EncodeResponse());
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		CopsServlet.requests.remove();
		CopsServlet.responses.remove();
	}
	
	public void testToString()
	{
		try
		{
			new TestCop().toString();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		CopsServlet.requests.set(new AbsReq());
		assertEquals("/contextPath/servletPath/encoded(test.html)", new TestCop().toString());
		assertEquals("/contextPath/servletPath/encoded(test.html?param1=ding)", new TestCop("ding").toString());
		assertEquals("/contextPath/servletPath/encoded(test.html?param1=ding&param2=dong)", new TestCop("ding", "dong").toString());
	}
	
	public void testToAbsolute()
	{
		try
		{
			new TestCop().toAbsolute();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		CopsServlet.requests.set(new AbsReq());
		assertEquals("scheme://host.exedio.com/contextPath/servletPath/encoded(test.html)", new TestCop().toAbsolute());
		assertEquals("scheme://host.exedio.com/contextPath/servletPath/encoded(test.html?param1=ding)", new TestCop("ding").toAbsolute());
		assertEquals("scheme://host.exedio.com/contextPath/servletPath/encoded(test.html?param1=ding&param2=dong)", new TestCop("ding", "dong").toAbsolute());
	}
	
	static final class AbsReq extends DummyRequest
	{
		@Override
		public String getScheme()
		{
			return "scheme";
		}

		@Override
		public String getHeader(final String name)
		{
			if("Host".equals(name))
				return "host.exedio.com";
			else
				throw new RuntimeException();
		}

		@Override
		public String getContextPath()
		{
			return "/contextPath";
		}

		@Override
		public String getServletPath()
		{
			return "/servletPath";
		}
	}
	
	public void testHttps()
	{
		CopsServlet.requests.set(new HttpsReq(false));
		assertEquals("/contextPath/servletPath/encoded(test.html?param1=value1)", new HttpsCop(null).toString());
		assertEquals("/contextPath/servletPath/encoded(test.html?param1=value1)", new HttpsCop(false).toString());
		assertEquals("https://host.exedio.com/contextPath/servletPath/encoded(test.html?param1=value1)", new HttpsCop(true).toString());
		CopsServlet.requests.set(new HttpsReq(true));
		assertEquals("/contextPath/servletPath/encoded(test.html?param1=value1)", new HttpsCop(null).toString());
		assertEquals("/contextPath/servletPath/encoded(test.html?param1=value1)", new HttpsCop(true).toString());
		assertEquals("http://host.exedio.com/contextPath/servletPath/encoded(test.html?param1=value1)", new HttpsCop(false).toString());
	}
	
	static final class HttpsCop extends Cop
	{
		final Boolean needsSecure;
		
		HttpsCop(final Boolean needsSecure)
		{
			super("test.html");
			this.needsSecure = needsSecure;
			addParameter("param1", "value1");
		}
		
		@Override
		public Boolean needsSecure()
		{
			return needsSecure;
		}
	}
	
	static final class HttpsReq extends DummyRequest
	{
		final boolean secure;
		
		HttpsReq(final boolean secure)
		{
			this.secure = secure;
		}
		
		@Override
		public boolean isSecure()
		{
			return secure;
		}

		@Override
		public String getHeader(final String name)
		{
			if("Host".equals(name))
				return "host.exedio.com";
			else
				throw new RuntimeException();
		}

		@Override
		public String getContextPath()
		{
			return "/contextPath";
		}

		@Override
		public String getServletPath()
		{
			return "/servletPath";
		}
	}
}
