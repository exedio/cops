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
		
		TestCop(double dummy, final String name)
		{
			super(name);
			if(dummy==0.0)
				dummy = 1.0;
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
			new TestCop(0.0, "shop?hallo.html");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cop name \"shop?hallo.html\" must not contain character ?", e.getMessage());
		}
		try
		{
			new TestCop(0.0, "shop&hallo.html");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cop name \"shop&hallo.html\" must not contain character &", e.getMessage());
		}
		try
		{
			new TestCop(0.0, "shop;hallo.html");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cop name \"shop;hallo.html\" must not contain character ;", e.getMessage());
		}
		try
		{
			new TestCop().toString();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no request available", e.getMessage());
		}
		try
		{
			new TestCop().toAbsolute();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no request available", e.getMessage());
		}
		CopsServlet.requests.set(new AbsReq());
		{
			final TestCop cop = new TestCop();
			assertEquals("/contextPath/servletPath/encoded(test.html)", cop.toString());
			assertEquals("/contextPath/servletPath/encoded(test.html)", cop.toStringNonEncoded());
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/encoded(test.html)", cop.toAbsolute());
		}
		{
			final TestCop cop = new TestCop("ding");
			assertEquals("/contextPath/servletPath/encoded(test.html?param1=ding)", cop.toString());
			assertEquals("/contextPath/servletPath/encoded(test.html?param1=ding)", cop.toStringNonEncoded());
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/encoded(test.html?param1=ding)", cop.toAbsolute());
		}
		{
			final TestCop cop = new TestCop("ding", "dong");
			assertEquals("/contextPath/servletPath/encoded(test.html?param1=ding&amp;param2=dong)", cop.toString());
			assertEquals("/contextPath/servletPath/encoded(test.html?param1=ding&param2=dong)", cop.toStringNonEncoded());
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/encoded(test.html?param1=ding&param2=dong)", cop.toAbsolute());
		}
		{
			final TestCop cop = new TestCop(0.0, "shop/hallo.html");
			assertEquals("/contextPath/servletPath/encoded(shop/hallo.html)", cop.toString());
			assertEquals("/contextPath/servletPath/encoded(shop/hallo.html)", cop.toStringNonEncoded());
		}
		{
			final String ding = "slash/semi;question?amp&uuml\u00fcgarten#";
			final String dingenc = "slash%2Fsemi%3Bquestion%3Famp%26uuml%C3%BCgarten%23";
			final TestCop cop = new TestCop(ding, "dong");
			assertEquals("/contextPath/servletPath/encoded(test.html?param1=" + dingenc + "&amp;param2=dong)", cop.toString());
			assertEquals("/contextPath/servletPath/encoded(test.html?param1=" + dingenc + "&param2=dong)", cop.toStringNonEncoded());
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/encoded(test.html?param1=" + dingenc + "&param2=dong)", cop.toAbsolute());
		}
		
		assertEquals(ENVIRONMENT, Cop.getEnvironment());
	}
	
	static final String ENVIRONMENT = "host.exedio.com/contextPath/servletPath";
	
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
