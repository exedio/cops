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
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;

public class ResourcesRootPathTest extends TestCase
{
	@SuppressWarnings("unused")
	public void testDefault()
	{
		new Servlet();
	}

	public void testLength1()
	{
		create("x");
	}

	public void testEmpty()
	{
		try
		{
			create("");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("getResourcesRootPath must not return empty string", e.getMessage());
		}
	}

	public void testNull()
	{
		try
		{
			create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("getResourcesRootPath must not return null", e.getMessage());
		}
	}

	private Servlet create(final String path)
	{
		return new Servlet(){
			@Override protected String getResourcesRootPath()
			{
				return path;
			}
			private static final long serialVersionUID = 1l;
		};
	}

	static class Servlet extends CopsServlet
	{
		private static final long serialVersionUID = 1l;

		@SuppressWarnings("unused")
		private static final Resource resource = new Resource("ResourcesRootPathTest.class", "major/minor");

		@Override
		protected void doRequest(final HttpServletRequest request, final HttpServletResponse response)
		{
			throw new RuntimeException();
		}
	}
}
