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

import com.exedio.cops.CopTest.HttpsCop;
import com.exedio.cops.CopTest.TestCop;

public class EnvironmentTest extends TestCase
{
	static final String ENVIRONMENT_8080 = "host.exedio.com:8080/contextPath/servletPath";
	static final String ENVIRONMENT_8443 = "host.exedio.com:8443/contextPath/servletPath";
	static final Resource resource = new Resource("ResourceTest.class", "major/minor");
	
	public void testIt()
	{
		HttpServletRequest request = new EnvironmentRequest(CopTest.ENVIRONMENT);
		{
			final TestCop cop = new TestCop();
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", cop.toURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", cop.toAbsolute(request));
			assertEquals("test.html", cop.toString());
		}
		{
			final TestCop cop = new TestCop("ding");
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", cop.toURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", cop.toAbsolute(request));
			assertEquals("test.html?param1=ding", cop.toString());
		}
		{
			final TestCop cop = new TestCop("ding", "dong");
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", cop.toURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", cop.toAbsolute(request));
			assertEquals("test.html?param1=ding&param2=dong", cop.toString());
		}
		{
			final TestCop cop = new TestCop(0.0, "shop/hallo.html");
			assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", cop.toURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", cop.toAbsolute(request));
			assertEquals("shop/hallo.html", cop.toString());
		}
		{
			final HttpsCop cop = new HttpsCop(null);
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final HttpsCop cop = new HttpsCop(false);
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final HttpsCop cop = new HttpsCop(true);
			assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		assertEquals("http://host.exedio.com/contextPath/servletPath/ResourceTest.class", resource.toURL(request));
		assertEquals("http://host.exedio.com/contextPath/servletPath/ResourceTest.class", resource.toAbsolute(request));
		assertEquals("ResourceTest.class", resource.toString());
		
		assertEquals(CopTest.ENVIRONMENT, Cop.getEnvironment(request));
		
		
		// port adjustments
		
		request = new EnvironmentRequest(ENVIRONMENT_8080);
		assertEquals(ENVIRONMENT_8080, Cop.getEnvironment(request));
		{
			final HttpsCop cop = new HttpsCop(null);
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final HttpsCop cop = new HttpsCop(false);
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final HttpsCop cop = new HttpsCop(true);
			assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", resource.toURL(request));
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", resource.toAbsolute(request));
		assertEquals("ResourceTest.class", resource.toString());
		
		
		request = new EnvironmentRequest(ENVIRONMENT_8443);
		assertEquals(ENVIRONMENT_8443, Cop.getEnvironment(request));
		{
			final HttpsCop cop = new HttpsCop(null);
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final HttpsCop cop = new HttpsCop(false);
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final HttpsCop cop = new HttpsCop(true);
			assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", cop.toURL(request));
			assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute(request));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", resource.toURL(request));
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", resource.toAbsolute(request));
		assertEquals("ResourceTest.class", resource.toString());
	}
}
