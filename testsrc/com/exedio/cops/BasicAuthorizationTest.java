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

import static com.exedio.cops.BasicAuthorization.getUserAndPassword;
import static java.util.Arrays.asList;

import javax.servlet.http.HttpServletRequest;
import junit.framework.TestCase;

public class BasicAuthorizationTest extends TestCase
{
	public void testOk()
	{
		//noinspection ConstantConditions
		assertEquals(
				asList("admin", "nimda"),
				asList(getUserAndPassword(new Request("Basic YWRtaW46bmltZGE="))));
	}

	public void testNoBasic()
	{
		assertNull(getUserAndPassword(new Request("asic YWRtaW46bmltZGE=")));
	}

	public void testNoHeader()
	{
		assertNull(getUserAndPassword(new Request(null)));
	}

	public void testRequestNull()
	{
		try
		{
			//noinspection ConstantConditions
			getUserAndPassword(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(
					"Cannot invoke \"" + HttpServletRequest.class.getName() + ".getHeader(String)\" " +
					"because \"request\" is null",
					e.getMessage());
		}
	}


	private static final class Request extends DummyRequest
	{
		private final String authorization;

		private Request(final String authorization)
		{
			this.authorization = authorization;
		}

		@Override
		public String getHeader(final String name)
		{
			//noinspection SwitchStatementWithTooFewBranches OK: prepares more branches
			return switch(name)
			{
				case "Authorization" -> authorization;
				default -> super.getHeader(name);
			};
		}
	}
}
