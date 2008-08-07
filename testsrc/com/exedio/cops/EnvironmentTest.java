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

import com.exedio.cops.CopTest.HttpsCop;
import com.exedio.cops.CopTest.TestCop;

public class EnvironmentTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Cop.setEnvironment(CopTest.ENVIRONMENT);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		Cop.removeEnvironment();
		super.tearDown();
	}
	
	public void testIt()
	{
		{
			final TestCop cop = new TestCop();
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", cop.toString());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", cop.toStringNonEncoded());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", cop.toAbsolute());
		}
		{
			final TestCop cop = new TestCop("ding");
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", cop.toString());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", cop.toStringNonEncoded());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", cop.toAbsolute());
		}
		{
			final TestCop cop = new TestCop("ding", "dong");
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&amp;param2=dong", cop.toString());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", cop.toStringNonEncoded());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", cop.toAbsolute());
		}
		{
			final TestCop cop = new TestCop(0.0, "shop/hallo.html");
			assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", cop.toString());
			assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", cop.toStringNonEncoded());
			assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", cop.toAbsolute());
		}
		{
			final HttpsCop cop = new HttpsCop(null);
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toString());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute());
		}
		{
			final HttpsCop cop = new HttpsCop(false);
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toString());
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute());
		}
		{
			final HttpsCop cop = new HttpsCop(true);
			assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toString());
			assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.toAbsolute());
		}
	}
}