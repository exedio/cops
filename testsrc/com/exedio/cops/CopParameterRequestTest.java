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

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
			assertEqualsParameterMap(request);
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
			assertEqualsParameterMap("ding", asList("zack"), request);
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
			assertEqualsParameterMap("ding", asList("zack"), "dong", asList("zock"), request);
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
			assertEqualsParameterMap("ding", asList("zack", "zock"), request);
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
			assertEqualsParameterMap("ding", asList("zack", "zock"), "dong", asList("zick"), request);
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
			assertEqualsParameterMap("ding", asList("sla/sh"), request);
		}
	}

	static final void assertParameters(final CopParameterRequest request)
	{
		assertEquals(null, request.getParameter("xxx"));
		assertEquals(Collections.EMPTY_LIST, asList(request.getParameterValues("xxx")));

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
	}

	private static void assertEqualsParameterMap(
			final CopParameterRequest request)
	{
		assertEquals(Collections.emptyMap(), convertParameterMap(request));
	}

	private static void assertEqualsParameterMap(
			final String key1,
			final List<String> value1,
			final CopParameterRequest request)
	{
		assertEquals(Collections.singletonMap(key1, value1), convertParameterMap(request));
	}

	private static void assertEqualsParameterMap(
			final String key1,
			final List<String> value1,
			final String key2,
			final List<String> value2,
			final CopParameterRequest request)
	{
		final LinkedHashMap<String, List<String>> expected = new LinkedHashMap<>();
		expected.put(key1, value1);
		expected.put(key2, value2);
		assertEquals(expected, convertParameterMap(request));
	}

	private static Map<String, List<String>> convertParameterMap(final CopParameterRequest request)
	{
		final Map<?,?> parameters = request.getParameterMap();
		final LinkedHashMap<String, List<String>> result = new LinkedHashMap<>();
		for(final Map.Entry<?,?> e : parameters.entrySet())
			result.put((String)e.getKey(), asList((String[])e.getValue()));
		return Collections.unmodifiableMap(result);
	}
}
