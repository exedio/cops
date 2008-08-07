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

import static com.exedio.cops.example.Example_Jspm.writePagerButton;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cops.Cop;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;

public class NumberCop extends Cop implements Pageable
{
	private static final String NAME = "number.html";
	private static final String NUMBER = "n";
	private static final String BOOL = "b";
	private static final String STRING = "s";
	
	private static final int NUMBER_DEFAULT = 0;
	private static final Pager.Config PAGER_CONFIG = new Pager.Config(10, 20, 23, 100, 500);
	
	private final int number;
	private final boolean bool;
	final String string;
	final Pager pager;
	
	NumberCop(final int number, final boolean bool, final String string, final Pager pager)
	{
		super(NAME);
		this.number = number;
		this.bool = bool;
		this.string = string;
		this.pager = pager;
		
		addParameter(NUMBER, number, NUMBER_DEFAULT);
		addParameter(BOOL, bool);
		addParameter(STRING, string);
		pager.addParameters(this);
	}
	
	static NumberCop getCop(final HttpServletRequest request)
	{
		return new NumberCop(
				getIntParameter(request, NUMBER, NUMBER_DEFAULT),
				getBooleanParameter(request, BOOL),
				request.getParameter(STRING), PAGER_CONFIG.newPager(request));
	}
	
	public NumberCop add(final int addend)
	{
		return new NumberCop(number + addend, bool, string, pager);
	}
	
	public NumberCop toggle()
	{
		return new NumberCop(number, !bool, string, pager);
	}
	
	public NumberCop setString(final String string)
	{
		return new NumberCop(number, bool, string, pager);
	}
	
	public Pager getPager()
	{
		return pager;
	}
	
	public NumberCop toPage(final Pager pager)
	{
		return new NumberCop(number, bool, string, pager);
	}
	
	static void writePager(final StringBuilder out, final Pageable cop)
	{
		final Pager pager = cop.getPager();
		if(pager.isNeeded())
		{
			writePagerButton(out, cop, pager.first(),    "&lt;&lt;");
			writePagerButton(out, cop, pager.previous(), "&lt;");
			writePagerButton(out, cop, pager.next(),     "&gt;");
			writePagerButton(out, cop, pager.last(),     "&gt;&gt;");
			for(final Pager newLimit : pager.newLimits())
				writePagerButton(out, cop, newLimit, String.valueOf(newLimit.getLimit()));
			out.append(' ');
			out.append(pager.getFrom());
			out.append('-');
			out.append(pager.getTo());
			out.append('/');
			out.append(pager.getTotal());
		}
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
