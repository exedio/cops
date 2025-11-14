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

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;

public class DummySession implements HttpSession
{
	@Override
	public Object getAttribute(final String s)
	{
		throw new RuntimeException();
	}

	@Override
	public Enumeration<String> getAttributeNames()
	{
		throw new RuntimeException();
	}

	@Override
	public long getCreationTime()
	{
		throw new RuntimeException();
	}

	@Override
	public String getId()
	{
		throw new RuntimeException();
	}

	@Override
	public long getLastAccessedTime()
	{
		throw new RuntimeException();
	}

	@Override
	public int getMaxInactiveInterval()
	{
		throw new RuntimeException();
	}

	@Override
	public ServletContext getServletContext()
	{
		throw new RuntimeException();
	}

	@Override
	public void invalidate()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isNew()
	{
		throw new RuntimeException();
	}

	@Override
	public void removeAttribute(final String s)
	{
		throw new RuntimeException();
	}

	@Override
	public void setAttribute(final String s, final Object obj)
	{
		throw new RuntimeException();
	}

	@Override
	public void setMaxInactiveInterval(final int i)
	{
		throw new RuntimeException();
	}
}
