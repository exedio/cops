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

import java.io.IOException;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

public class RequestLimiterTest extends TestCase
{
	static class Request extends DummyRequest
	{
		final HttpSession session;

		Request(final boolean session)
		{
			this.session = session ? new DummySession() : null;
		}
		@Override
		public String getPathInfo()
		{
			return "dingdong";
		}

		@Override
		public HttpSession getSession(final boolean create)
		{
			assertFalse(create);
			return session;
		}
	}

	public void testSession() throws IOException
	{
		final RequestLimiter rl = new RequestLimiter(3, 1000, "Deny Message");

		final Request requestSession = new Request(true);
		final DummyResponse response = new DummyResponse();
		assertFalse(rl.doRequest(requestSession, response));
		assertFalse(rl.doRequest(requestSession, response));
		assertFalse(rl.doRequest(requestSession, response));
		assertFalse(rl.doRequest(requestSession, response));
		assertFalse(rl.doRequest(requestSession, response));
	}

	public void testNoSession() throws IOException
	{
		final RequestLimiter rl = new RequestLimiter(3, 1000, "Deny Message");

		final Request request = new Request(false);
		final DummyResponse responseOk = new DummyResponse();
		assertFalse(rl.doRequest(request, responseOk));
		assertFalse(rl.doRequest(request, responseOk));
		assertFalse(rl.doRequest(request, responseOk));

		final DummyResponse responseDeny = new DummyResponse(){
			@Override
			public void sendError(final int status, final String message)
			{
				assertEquals(503, status);
				assertEquals("Deny Message", message);
			}
		};
		assertTrue(rl.doRequest(request, responseDeny));
		assertTrue(rl.doRequest(request, responseDeny));
	}

	@SuppressWarnings("unused")
	public void testError()
	{
		try
		{
			new RequestLimiter(0, 0, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("threshold must be greater than zero, but was 0", e.getMessage());
		}
		try
		{
			new RequestLimiter(1, 0, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("interval must be greater than zero, but was 0", e.getMessage());
		}
		new RequestLimiter(1, 1, null);
	}
}
