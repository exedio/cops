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

package com.exedio.cops.example;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cops.Cop;

public class NumberCop extends Cop
{
	private static final String NAME = "number.html";
	private static final String NUMBER = "n";
	private static final String BOOL = "b";
	
	private final int number;
	private final boolean bool;
	
	NumberCop(final int number, final boolean bool)
	{
		super(NAME);
		this.number = number;
		this.bool = bool;
		
		if(number!=0)
			addParameter(NUMBER, String.valueOf(number));
		if(bool)
			addParameter(BOOL, "t");
	}
	
	static NumberCop getCop(final HttpServletRequest request)
	{
		final String number = request.getParameter(NUMBER);
		return new NumberCop(number!=null ? Integer.valueOf(number) : 0, request.getParameter(BOOL)!=null);
	}
	
	public NumberCop add(final int addend)
	{
		return new NumberCop(number + addend, bool);
	}
	
	public NumberCop toggle()
	{
		return new NumberCop(number, !bool);
	}
	
	@Override
	protected Boolean needsSecure()
	{
		if(number<0)
			return Boolean.FALSE;
		else if(number>100)
			return Boolean.TRUE;
		else
			return null;
	}
}
