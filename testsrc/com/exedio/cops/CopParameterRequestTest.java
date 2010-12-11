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

import static java.util.Arrays.asList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

public class CopParameterRequestTest extends TestCase
{
	public void testIt()
	{
		final HttpServletRequest nested = new DummyRequest();
		{
			final CopParameterRequest request = new CopParameterRequest(nested, "test.html");
			assertEquals("/test.html", request.getPathInfo());
			assertEquals(null, request.getQueryString());

			assertParameters(request);
			final Enumeration<?> names = request.getParameterNames();
			assertFalse(names.hasMoreElements());
		}
		{
			final CopParameterRequest request = new CopParameterRequest(nested, "test.html?ding=zack");
			assertEquals("/test.html", request.getPathInfo());
			assertEquals("ding=zack", request.getQueryString());

			assertParameters(request);
			assertEquals("zack", request.getParameter("ding"));
			assertEquals(asList("zack"), asList(request.getParameterValues("ding")));
			final Enumeration<?> names = request.getParameterNames();
			assertEquals("ding", names.nextElement());
			assertFalse(names.hasMoreElements());
		}
		{
			final CopParameterRequest request = new CopParameterRequest(nested, "test.html?ding=zack&dong=zock");
			assertEquals("/test.html", request.getPathInfo());
			assertEquals("ding=zack&dong=zock", request.getQueryString());

			assertParameters(request);
			assertEquals("zack", request.getParameter("ding"));
			assertEquals("zock", request.getParameter("dong"));
			assertEquals(asList("zack"), asList(request.getParameterValues("ding")));
			assertEquals(asList("zock"), asList(request.getParameterValues("dong")));
			final Enumeration<?> names = request.getParameterNames();
			assertEquals("ding", names.nextElement());
			assertEquals("dong", names.nextElement());
			assertFalse(names.hasMoreElements());
		}
		{
			final CopParameterRequest request = new CopParameterRequest(nested, "test.html?ding=zack&ding=zock");
			assertEquals("/test.html", request.getPathInfo());
			assertEquals("ding=zack&ding=zock", request.getQueryString());

			assertParameters(request);
			assertEquals("zack", request.getParameter("ding"));
			assertEquals(asList("zack", "zock"), asList(request.getParameterValues("ding")));
			final Enumeration<?> names = request.getParameterNames();
			assertEquals("ding", names.nextElement());
			assertFalse(names.hasMoreElements());
		}
		{
			final CopParameterRequest request = new CopParameterRequest(nested, "test.html?ding=zack&dong=zick&ding=zock");
			assertEquals("/test.html", request.getPathInfo());
			assertEquals("ding=zack&dong=zick&ding=zock", request.getQueryString());

			assertParameters(request);
			assertEquals("zack", request.getParameter("ding"));
			assertEquals("zick", request.getParameter("dong"));
			assertEquals(asList("zack", "zock"), asList(request.getParameterValues("ding")));
			assertEquals(asList("zick"), asList(request.getParameterValues("dong")));
			final Enumeration<?> names = request.getParameterNames();
			assertEquals("ding", names.nextElement());
			assertEquals("dong", names.nextElement());
			assertFalse(names.hasMoreElements());
		}
		{
			final CopParameterRequest request = new CopParameterRequest(nested, "test.html?ding=sla%2Fsh");
			assertEquals("/test.html", request.getPathInfo());
			assertEquals("ding=sla%2Fsh", request.getQueryString());

			assertParameters(request);
			assertEquals("sla/sh", request.getParameter("ding"));
			assertEquals(asList("sla/sh"), asList(request.getParameterValues("ding")));
			final Enumeration<?> names = request.getParameterNames();
			assertEquals("ding", names.nextElement());
			assertFalse(names.hasMoreElements());
		}
	}

	static final void assertParameters(final CopParameterRequest request)
	{
		assertEquals(null, request.getParameter("xxx"));
		assertEquals(Collections.EMPTY_LIST, Arrays.asList(request.getParameterValues("xxx")));

		try
		{
			request.getParameter(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
		try
		{
			request.getParameterValues(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
		try
		{
			request.getParameterMap();
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("not yet implemented", e.getMessage());
		}
	}
}
