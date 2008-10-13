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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class ResourceTest extends TestCase
{
	public void testToString()
	{
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
		assertEquals(null, r1.toString());
		try
		{
			r1.toAbsolute();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no request available", e.getMessage());
		}
		
		
		r1.init(ResourceTest.class);
		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(lastModified, r1.getLastModified());
		final int contentLength = r1.getContentLength();
		assertTrue(String.valueOf(contentLength), contentLength>5);
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals(null, r1.toString());
		try
		{
			r1.toAbsolute();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no request available", e.getMessage());
		}
		
		
		// test idempotence of init
		r1.init(ResourceTest.class);
		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(lastModified, r1.getLastModified());
		assertEquals(contentLength, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals(null, r1.toString());
		try
		{
			r1.toAbsolute();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no request available", e.getMessage());
		}
		
		
		r1.setPath("/contextPath/servletPath/");
		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(lastModified, r1.getLastModified());
		assertEquals(contentLength, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("/contextPath/servletPath/ResourceTest.class", r1.toString());
		try
		{
			r1.toAbsolute();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no request available", e.getMessage());
		}
		
		
		CopsServlet.requests.set(new DummyRequest(){
			@Override public String getScheme(){return "scheme";}
			@Override public String getHeader(String name)
			{
				if("Host".equals(name))
					return "host";
				else
					throw new RuntimeException(name);
			}
		});
		assertEquals("ResourceTest.class", r1.getName());
		assertEquals("major/minor", r1.getContentType());
		assertEquals(lastModified, r1.getLastModified());
		assertEquals(contentLength, r1.getContentLength());
		assertEquals(0, r1.getResponse200Count());
		assertEquals(0, r1.getResponse304Count());
		assertEquals("/contextPath/servletPath/ResourceTest.class", r1.toString());
		assertEquals("scheme://host/contextPath/servletPath/ResourceTest.class", r1.toAbsolute());
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		CopsServlet.requests.remove();
		CopsServlet.responses.remove();
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
}