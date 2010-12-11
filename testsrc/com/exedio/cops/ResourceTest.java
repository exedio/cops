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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

public class ResourceTest extends TestCase
{
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
			new Resource("ResourceTest.class", null);
		}
		catch(final NullPointerException e)
		{
			assertEquals("contentType", e.getMessage());
		}

		final Date before = new Date();
		final Resource r1 = new Resource("ResourceTest.class", "major/minor");
		final Date after = new Date();
		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		final Date lastModified = r1.getLastModified();
		assertWithin(new Date((before.getTime()/1000)*1000), new Date(((after.getTime()/1000)+1)*1000), lastModified);
		assertEquals(-1, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("ResourceTest.class", r1.toString());
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


		r1.init(ResourceTest.class);
		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(lastModified, r1.getLastModified());
		final int contentLength = r1.getContentLength();
		assertTrue(String.valueOf(contentLength), contentLength>5);
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("ResourceTest.class", r1.toString());
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
		r1.init(ResourceTest.class);
		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(lastModified, r1.getLastModified());
		assertEquals(contentLength, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("ResourceTest.class", r1.toString());
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


		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(lastModified, r1.getLastModified());
		assertEquals(contentLength, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("ResourceTest.class", r1.toString());

		assertEquals("/contextPath/servletPath/ResourceTest.class", r1.getURL(request()));
		assertEquals("scheme://host.exedio.com/contextPath/servletPath/ResourceTest.class", r1.getAbsoluteURL(request("scheme", "host.exedio.com")));
		assertEquals("http://host.exedio.com/contextPath/servletPath/ResourceTest.class", r1.getAbsoluteURL(CopTest.TOKEN));
		// port adjustments
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", r1.getAbsoluteURL(CopTest.TOKEN_8080));
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", r1.getAbsoluteURL(CopTest.TOKEN_8443));
		assertEquals(CopTest.TOKEN, Cop.getToken(request(null, "host.exedio.com")));
	}

	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	public static final void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBefore.after(actual));
		assertTrue(message, !expectedAfter.before(actual));
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
