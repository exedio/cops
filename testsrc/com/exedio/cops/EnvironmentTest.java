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

import junit.framework.TestCase;

import com.exedio.cops.CopTest.SecureCop;

public class EnvironmentTest extends TestCase
{
	static final String TOKEN_8080 = "host.exedio.com:8080/contextPath/servletPath";
	static final String TOKEN_8443 = "host.exedio.com:8443/contextPath/servletPath";
	static final Resource resource = new Resource("ResourceTest.class", "major/minor");
	
	public void testIt()
	{
		{
			final SecureCop cop = new SecureCop(null);
			
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(CopTest.TOKEN));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final SecureCop cop = new SecureCop(false);
			
			assertEquals("http://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(CopTest.TOKEN));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final SecureCop cop = new SecureCop(true);
			
			assertEquals("https://host.exedio.com/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(CopTest.TOKEN));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		
		assertEquals("http://host.exedio.com/contextPath/servletPath/ResourceTest.class", resource.getAbsoluteURL(CopTest.TOKEN));
		assertEquals("ResourceTest.class", resource.toString());
		
		
		// port adjustments
		
		{
			final SecureCop cop = new SecureCop(null);
			
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(TOKEN_8080));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final SecureCop cop = new SecureCop(false);
			
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(TOKEN_8080));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final SecureCop cop = new SecureCop(true);
			
			assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(TOKEN_8080));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", resource.getAbsoluteURL(TOKEN_8080));
		assertEquals("ResourceTest.class", resource.toString());
		
		
		{
			final SecureCop cop = new SecureCop(null);
			
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(TOKEN_8443));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final SecureCop cop = new SecureCop(false);
			
			assertEquals("http://host.exedio.com:8080/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(TOKEN_8443));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		{
			final SecureCop cop = new SecureCop(true);
			
			assertEquals("https://host.exedio.com:8443/contextPath/servletPath/test.html?param1=value1", cop.getAbsoluteURL(TOKEN_8443));
			assertEquals("test.html?param1=value1", cop.toString());
		}
		
		assertEquals("http://host.exedio.com:8080/contextPath/servletPath/ResourceTest.class", resource.getAbsoluteURL(TOKEN_8443));
		assertEquals("ResourceTest.class", resource.toString());
	}
}
