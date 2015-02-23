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

public class ResourceTest extends TestCase
{
	@SuppressWarnings("unused")
	public void testURL()
	{
		try
		{
			new Resource(null, null);
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
		try
		{
			new Resource("ResourceTest.bin", null);
		}
		catch(final NullPointerException e)
		{
			assertEquals("contentType", e.getMessage());
		}

		final Resource r1 = new Resource("ResourceTest.bin", "major/minor");
		final String fp = "dc20e98c25ae2db4ebf870d21d3dc5e2"; // fingerprint
		assertEquals("ResourceTest.bin", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(-1, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals(0, r1.getResponse301ByNameCount());
		assertEquals(0, r1.getResponse301ByFingerprintCount());
		assertEquals("ResourceTest.bin", r1.toString());
		try
		{
			r1.getPath();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not yet initialized: ResourceTest.bin", e.getMessage());
		}
		try
		{
			r1.getURL(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		try
		{
			r1.getAbsoluteURL((HttpServletRequest)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		try
		{
			r1.getURL(request());
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not yet initialized: ResourceTest.bin", e.getMessage());
		}
		try
		{
			r1.getAbsoluteURL(request("schemeX", "hostX"));
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not yet initialized: ResourceTest.bin", e.getMessage());
		}
		try
		{
			r1.getAbsoluteURL(CopTest.TOKEN);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not yet initialized: ResourceTest.bin", e.getMessage());
		}


		r1.init(ResourceTest.class, "resources");
		assertEquals("ResourceTest.bin", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(23, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("ResourceTest.bin", r1.toString());
		try
		{
			r1.getURL(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		try
		{
			r1.getAbsoluteURL((HttpServletRequest)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		try
		{
			r1.getAbsoluteURL((String)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("token", e.getMessage());
		}


		// test idempotence of init
		r1.init(ResourceTest.class, null);
		assertEquals("ResourceTest.bin", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(23, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("ResourceTest.bin", r1.toString());
		try
		{
			r1.getURL(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}
		try
		{
			r1.getAbsoluteURL((HttpServletRequest)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("request", e.getMessage());
		}


		assertEquals("ResourceTest.bin", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(23, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("ResourceTest.bin", r1.toString());

		assertEquals(null, r1.getHostOverride());
		assertEquals(                                            "resources/"+fp+"/ResourceTest.bin", r1.getPath());
		assertEquals(                   "/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getURL(request()));
		assertEquals("schemeX://hostX"+ "/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(request("schemeX", "hostX")));
		assertEquals("http://host.invalid/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(CopTest.TOKEN));
		// port adjustments
		assertEquals("http://host.invalid:8080/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(CopTest.TOKEN_8080));
		assertEquals("http://host.invalid:8080/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(CopTest.TOKEN_8443));
		assertEquals(CopTest.TOKEN, Cop.getToken(request(null, "host.invalid")));

		r1.setHostOverride("hostO.invalid");
		assertEquals("hostO.invalid", r1.getHostOverride());
		assertEquals(                                                "resources/"+fp+"/ResourceTest.bin", r1.getPath());
		assertEquals("schemeX://hostO.invalid/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getURL(request("schemeX", null)));
		assertEquals("schemeX://hostO.invalid/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(request("schemeX", null)));
		assertEquals("http://host.invalid"+ "/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(CopTest.TOKEN));
		// port adjustments
		assertEquals("http://host.invalid:8080/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(CopTest.TOKEN_8080));
		assertEquals("http://host.invalid:8080/contextPath/servletPath/resources/"+fp+"/ResourceTest.bin", r1.getAbsoluteURL(CopTest.TOKEN_8443));
		assertEquals(CopTest.TOKEN, Cop.getToken(request(null, "host.invalid")));
	}

	private static final HttpServletRequest request()
	{
		return request(null, null);
	}

	private static final HttpServletRequest request(final String scheme, final String host)
	{
		return new DummyRequest(){

			@Override
			public String getScheme()
			{
				if(scheme==null)
					throw new NullPointerException("scheme");
				return scheme;
			}

			@Override public String getContextPath(){return "/contextPath";}
			@Override public String getServletPath(){return "/servletPath";}
			@Override public String getHeader(final String name)
			{
				if("Host".equals(name))
				{
					if(host==null)
						throw new NullPointerException("host");
					return host;
				}
				else
					throw new RuntimeException(name);
			}
		};
	}
}
