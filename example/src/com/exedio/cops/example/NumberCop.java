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
	private static final String STRING = "s";
	
	private static final int NUMBER_DEFAULT = 0;
	
	private final int number;
	private final boolean bool;
	final String string;
	
	NumberCop(final int number, final boolean bool, final String string)
	{
		super(NAME);
		this.number = number;
		this.bool = bool;
		this.string = string;
		
		
		addParameter(NUMBER, number, NUMBER_DEFAULT);
		
		addParameter(BOOL, bool);
		
		addParameter(STRING, string);
	}
	
	static NumberCop getCop(final HttpServletRequest request)
	{
		
		return new NumberCop(
				getIntParameter(request, NUMBER, NUMBER_DEFAULT),
				getBooleanParameter(request, BOOL),
				request.getParameter(STRING));
	}
	
	public NumberCop add(final int addend)
	{
		return new NumberCop(number + addend, bool, string);
	}
	
	public NumberCop toggle()
	{
		return new NumberCop(number, !bool, string);
	}
	
	public NumberCop setString(final String string)
	{
		return new NumberCop(number, bool, string);
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
