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

import javax.servlet.http.HttpServletRequest;

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
		
		TestCop(final boolean param1)
		{
			super("test.html");
			addParameter("param1", param1);
		}
		
		TestCop(final int param1)
		{
			super("test.html");
			addParameter("param1", param1, 0);
		}
		
		TestCop(double dummy, final String name)
		{
			super(name);
			if(dummy==0.0)
				dummy = 1.0;
		}
		
		private static final String pathInfo(final String[] dirs, final String name)
		{
			if(dirs==null)
				return name;
			final StringBuilder bf = new StringBuilder();
			for(final String dir : dirs)
				bf.append(Cop.encodeNaturalLanguageSegment(dir)).append('/');
			bf.append(name);
			return bf.toString();
		}
		
		TestCop(final String[] dirs)
		{
			super(pathInfo(dirs, "test.html"));
		}
		
		TestCop(final String[] dirs, final String param1)
		{
			super(pathInfo(dirs, "test.html"));
			addParameter("param1", param1);
		}
		
		TestCop(final String[] dirs, final String param1, final String param2)
		{
			super(pathInfo(dirs, "test.html"));
			addParameter("param1", param1);
			addParameter("param2", param2);
		}
	}
	
	public void testURL()
	{
		try
		{
			new TestCop(0.0, "shop?hallo.html");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cop pathInfo \"shop?hallo.html\" must not contain character ?", e.getMessage());
		}
		try
		{
			new TestCop(0.0, "shop&hallo.html");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cop pathInfo \"shop&hallo.html\" must not contain character &", e.getMessage());
		}
		try
		{
			new TestCop(0.0, "shop;hallo.html");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cop pathInfo \"shop;hallo.html\" must not contain character ;", e.getMessage());
		}
		assertEquals("test.html", new TestCop().toString());
		try
		{
			new TestCop().getURL(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		try
		{
			new TestCop().getAbsoluteURL((HttpServletRequest)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		try
		{
			new TestCop().getAbsoluteURL((String)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("token", e.getMessage());
		}
		try
		{
			Cop.getToken(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		
		final HttpServletRequest request = new AbsReq();
		{
			final TestCop cop = new TestCop();
			assertEquals("/contextPath/servletPath/test.html", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/test.html", cop.getAbsoluteURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", cop.getAbsoluteURL(TOKEN));
			assertEquals("http://host.exedio.com/test.html", cop.getAbsoluteURL(ROOT_TOKEN));
			assertEquals("test.html", cop.toString());
		}
		{
			final TestCop cop = new TestCop("ding");
			assertEquals("/contextPath/servletPath/test.html?param1=ding", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/test.html?param1=ding", cop.getAbsoluteURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", cop.getAbsoluteURL(TOKEN));
			assertEquals("http://host.exedio.com/test.html?param1=ding", cop.getAbsoluteURL(ROOT_TOKEN));
			assertEquals("test.html?param1=ding", cop.toString());
		}
		{
			final TestCop cop = new TestCop("ding", "dong");
			assertEquals("/contextPath/servletPath/test.html?param1=ding&param2=dong", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", cop.getAbsoluteURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", cop.getAbsoluteURL(TOKEN));
			assertEquals("http://host.exedio.com/test.html?param1=ding&param2=dong", cop.getAbsoluteURL(ROOT_TOKEN));
			assertEquals("test.html?param1=ding&param2=dong", cop.toString());
		}
		{
			final TestCop cop = new TestCop(0.0, "shop/hallo.html");
			assertEquals("/contextPath/servletPath/shop/hallo.html", cop.getURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", cop.getAbsoluteURL(CopTest.TOKEN));
			assertEquals("http://host.exedio.com/shop/hallo.html", cop.getAbsoluteURL(CopTest.ROOT_TOKEN));
			assertEquals("shop/hallo.html", cop.toString());
		}
		{
			final String ding = "slash/semi;question?amp&uuml\u00fcgarten#";
			final String dingenc = "slash%2Fsemi%3Bquestion%3Famp%26uuml%C3%BCgarten%23";
			final TestCop cop = new TestCop(ding, "dong");
			assertEquals("/contextPath/servletPath/test.html?param1=" + dingenc + "&param2=dong", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/test.html?param1=" + dingenc + "&param2=dong", cop.getAbsoluteURL(request));
			assertEquals("test.html?param1=" + dingenc + "&param2=dong", cop.toString());
		}
		{
			final TestCop cop = new TestCop(new String[]{"dir1"});
			assertEquals("/contextPath/servletPath/dir1/test.html", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/dir1/test.html", cop.getAbsoluteURL(request));
			assertEquals("dir1/test.html", cop.toString());
		}
		{
			final TestCop cop = new TestCop(new String[]{"dir1"}, "dong");
			assertEquals("/contextPath/servletPath/dir1/test.html?param1=dong", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/dir1/test.html?param1=dong", cop.getAbsoluteURL(request));
			assertEquals("dir1/test.html?param1=dong", cop.toString());
		}
		{
			final TestCop cop = new TestCop(new String[]{"dir1"}, "dong", "ding");
			assertEquals("/contextPath/servletPath/dir1/test.html?param1=dong&param2=ding", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/dir1/test.html?param1=dong&param2=ding", cop.getAbsoluteURL(request));
			assertEquals("dir1/test.html?param1=dong&param2=ding", cop.toString());
		}
		{
			final TestCop cop = new TestCop(new String[]{"dir1", "dir2 /;\u00e4"});
			assertEquals("/contextPath/servletPath/dir1/dir2/test.html", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/dir1/dir2/test.html", cop.getAbsoluteURL(request));
			assertEquals("dir1/dir2/test.html", cop.toString());
		}
		{
			final TestCop cop = new TestCop(new String[]{"dir1", "dir2 /;\u00e4"}, "dong");
			assertEquals("/contextPath/servletPath/dir1/dir2/test.html?param1=dong", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/dir1/dir2/test.html?param1=dong", cop.getAbsoluteURL(request));
			assertEquals("dir1/dir2/test.html?param1=dong", cop.toString());
		}
		{
			final TestCop cop = new TestCop(new String[]{"dir1", "dir2 /;\u00e4"}, "dong", "ding");
			assertEquals("/contextPath/servletPath/dir1/dir2/test.html?param1=dong&param2=ding", cop.getURL(request));
			assertEquals("scheme://host.exedio.com/contextPath/servletPath/dir1/dir2/test.html?param1=dong&param2=ding", cop.getAbsoluteURL(request));
			assertEquals("dir1/dir2/test.html?param1=dong&param2=ding", cop.toString());
		}
		{
			final TestCop cop = new TestCop("ding", null);
			assertEquals("/contextPath/servletPath/test.html?param1=ding", cop.getURL(request));
		}
		{
			final TestCop cop = new TestCop(false);
			assertEquals("/contextPath/servletPath/test.html", cop.getURL(request));
		}
		{
			final TestCop cop = new TestCop(true);
			assertEquals("/contextPath/servletPath/test.html?param1=t", cop.getURL(request));
		}
		{
			final TestCop cop = new TestCop(0);
			assertEquals("/contextPath/servletPath/test.html", cop.getURL(request));
		}
		{
			final TestCop cop = new TestCop(1);
			assertEquals("/contextPath/servletPath/test.html?param1=1", cop.getURL(request));
		}
		{
			final TestCop cop = new TestCop(-1);
			assertEquals("/contextPath/servletPath/test.html?param1=-1", cop.getURL(request));
		}
		
		assertEquals(TOKEN, Cop.getToken(request));
	}
	
	static final String TOKEN = "host.exedio.com/contextPath/servletPath";
	static final String ROOT_TOKEN = "host.exedio.com";
	static final String TOKEN_8080 = "host.exedio.com:8080/contextPath/servletPath";
	static final String TOKEN_8443 = "host.exedio.com:8443/contextPath/servletPath";
	static final String ROOT_TOKEN_8080 = "host.exedio.com:8080";
	static final String ROOT_TOKEN_8443 = "host.exedio.com:8443";
	
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
	
	public void testSecure()
	{
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1",  new SecureCop(null ).getAbsoluteURL(TOKEN));
		assertEquals("http://host.exedio.com/test.html?param1=value1",  new SecureCop(null ).getAbsoluteURL(ROOT_TOKEN));
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1",  new SecureCop(false).getAbsoluteURL(TOKEN));
		assertEquals("http://host.exedio.com/test.html?param1=value1",  new SecureCop(false).getAbsoluteURL(ROOT_TOKEN));
		assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", new SecureCop(true ).getAbsoluteURL(TOKEN));
		assertEquals("https://host.exedio.com/test.html?param1=value1", new SecureCop(true ).getAbsoluteURL(ROOT_TOKEN));
		
		// port adjustments
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1",  new SecureCop(null ).getAbsoluteURL(TOKEN_8080));
		assertEquals("http://host.exedio.com:8080/test.html?param1=value1",  new SecureCop(null ).getAbsoluteURL(ROOT_TOKEN_8080));
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1",  new SecureCop(false).getAbsoluteURL(TOKEN_8080));
		assertEquals("http://host.exedio.com:8080/test.html?param1=value1",  new SecureCop(false).getAbsoluteURL(ROOT_TOKEN_8080));
		assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", new SecureCop(true ).getAbsoluteURL(TOKEN_8080));
		assertEquals("https://host.exedio.com:8443/test.html?param1=value1", new SecureCop(true ).getAbsoluteURL(ROOT_TOKEN_8080));
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1",  new SecureCop(null ).getAbsoluteURL(TOKEN_8443));
		assertEquals("http://host.exedio.com:8080/test.html?param1=value1",  new SecureCop(null ).getAbsoluteURL(ROOT_TOKEN_8443));
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1",  new SecureCop(false).getAbsoluteURL(TOKEN_8443));
		assertEquals("http://host.exedio.com:8080/test.html?param1=value1",  new SecureCop(false).getAbsoluteURL(ROOT_TOKEN_8443));
		assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", new SecureCop(true ).getAbsoluteURL(TOKEN_8443));
		assertEquals("https://host.exedio.com:8443/test.html?param1=value1", new SecureCop(true ).getAbsoluteURL(ROOT_TOKEN_8443));

		
		HttpServletRequest request = new SecureRequest(false);
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(null).getURL(request));
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(false).getURL(request));
		assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", new SecureCop(true).getURL(request));
		request = new SecureRequest(true);
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(null).getURL(request));
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(true).getURL(request));
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", new SecureCop(false).getURL(request));
		
		// port adjustments
		request = new SecureRequest(false, 8080);
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(null).getURL(request));
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(false).getURL(request));
		assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", new SecureCop(true).getURL(request));
		request = new SecureRequest(true,  8443);
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(null).getURL(request));
		assertEquals("/contextPath/servletPath/test.html?param1=value1", new SecureCop(true).getURL(request));
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", new SecureCop(false).getURL(request));
	}
	
	static final class SecureCop extends Cop
	{
		final Boolean needsSecure;
		
		SecureCop(final Boolean needsSecure)
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
	
	static final class SecureRequest extends DummyRequest
	{
		final boolean secure;
		final int port;
		
		SecureRequest(final boolean secure)
		{
			this(secure, -1);
		}
		
		SecureRequest(final boolean secure, final int port)
		{
			this.secure = secure;
			this.port = port;
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
				return "host.exedio.com" + (port>=0 ? (":"+port) : "");
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
