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
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", new TestCop().toString());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", new TestCop("ding").toString());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&amp;param2=dong", new TestCop("ding", "dong").toString());
		assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", new TestCop(0.0, "shop/hallo.html").toString());

		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", new TestCop().toStringNonEncoded());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", new TestCop("ding").toStringNonEncoded());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", new TestCop("ding", "dong").toStringNonEncoded());
		assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", new TestCop(0.0, "shop/hallo.html").toStringNonEncoded());

		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html", new TestCop().toAbsolute());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding", new TestCop("ding").toAbsolute());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=ding&param2=dong", new TestCop("ding", "dong").toAbsolute());
		assertEquals("http://host.exedio.com/contextPath/servletPath/shop/hallo.html", new TestCop(0.0, "shop/hallo.html").toAbsolute());

		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1",  new HttpsCop(null ).toString());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1",  new HttpsCop(false).toString());
		assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", new HttpsCop(true ).toString());

		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1",  new HttpsCop(null ).toAbsolute());
		assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1",  new HttpsCop(false).toAbsolute());
		assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", new HttpsCop(true ).toAbsolute());
	}
}
