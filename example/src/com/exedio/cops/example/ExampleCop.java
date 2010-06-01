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

package com.exedio.cops.example;

import static com.exedio.cops.example.Example_Jspm.writePagerButton;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cops.Cop;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;

public class ExampleCop extends Cop implements Pageable
{
	private static final String PATH_INFO = "number.html";
	
	private static final String NUMBER = "n";
	private static final String BOOL = "b";
	private static final String STRING = "s";
	private static final String COP = "cop";
	private static final String NEEDS_SECURE = "sec";
	private static final String REDIRECT_LOOP = "rl";
	private static final String DIR_LEVEL = "dirLevel";
	
	private static final int NUMBER_DEFAULT = 0;
	private static final int REDIRECT_LOOP_DEFAULT = 0;
	private static final Pager.Config PAGER_CONFIG = new Pager.Config(10, 20, 23, 100, 500);
	
	private final int number;
	private final boolean bool;
	final String string;
	final ExampleCop cop;
	private final boolean needsSecure;
	private final int redirectLoop;
	final Pager pager;
	private final int dirLevel;
	
	private static final String dirLevel(final String pathInfo, final int dirLevel, final String string)
	{
		if(dirLevel<=0||string==null)
			return pathInfo;
		final StringBuilder bf = new StringBuilder();
		for(int i = 0; i<dirLevel; i++)
			bf.append(Cop.encodeNaturalLanguageSegment(string) + String.valueOf(i)).append('/');
		bf.append(pathInfo);
		return bf.toString();
	}
	
	ExampleCop(
			final int number,
			final boolean bool,
			final String string,
			final ExampleCop cop,
			final boolean needsSecure,
			final int redirectLoop,
			final Pager pager,
			final int dirLevel)
	{
		super(dirLevel(PATH_INFO, dirLevel, string));
		this.number = number;
		this.bool = bool;
		this.string = string;
		this.cop = cop;
		this.needsSecure = needsSecure;
		this.redirectLoop = redirectLoop;
		this.pager = pager;
		this.dirLevel = dirLevel;
		
		addParameter(NUMBER, number, NUMBER_DEFAULT);
		addParameter(BOOL, bool);
		addParameter(STRING, string);
		addParameter(COP, cop);
		addParameter(NEEDS_SECURE, needsSecure);
		addParameter(REDIRECT_LOOP, redirectLoop==REDIRECT_LOOP_DEFAULT?REDIRECT_LOOP_DEFAULT:redirectLoop+1, REDIRECT_LOOP_DEFAULT); // make redirect loop in redirectToCanonical
		pager.addParameters(this);
		addParameter(DIR_LEVEL, dirLevel, 0);
	}
	
	static ExampleCop getCop(final HttpServletRequest request)
	{
		return new ExampleCop(
				getIntParameter(request, NUMBER, NUMBER_DEFAULT),
				getBooleanParameter(request, BOOL),
				request.getParameter(STRING),
				getCopParameterIfFound(request, COP),
				getBooleanParameter(request, NEEDS_SECURE),
				getIntParameter(request, REDIRECT_LOOP, REDIRECT_LOOP_DEFAULT),
				PAGER_CONFIG.newPager(request), getIntParameter(request, DIR_LEVEL, 0));
	}
	
	static final ExampleCop getCopParameterIfFound(
			final HttpServletRequest request,
			final String name)
	{
		final HttpServletRequest backRequest = getCopParameter(request, name);
		if(backRequest==null)
			return null;
		
		return getCop(backRequest);
	}
	
	public ExampleCop set(final int number)
	{
		return new ExampleCop(number, bool, string, cop, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop add(final int addend)
	{
		return new ExampleCop(number + addend, bool, string, cop, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop toggle()
	{
		return new ExampleCop(number, !bool, string, cop, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop setString(final String string)
	{
		return new ExampleCop(number, bool, string, cop, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop setCopSelf()
	{
		return new ExampleCop(number, bool, string, this, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop setCopNull()
	{
		return new ExampleCop(number, bool, string, null, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop toSecure(final boolean needsSecure)
	{
		return new ExampleCop(number, bool, string, cop, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop toRedirectLoop()
	{
		return new ExampleCop(number, bool, string, cop, needsSecure, 1, pager, dirLevel);
	}
	
	public Pager getPager()
	{
		return pager;
	}
	
	public ExampleCop toPage(final Pager pager)
	{
		return new ExampleCop(number, bool, string, cop, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	public ExampleCop toDirLevel(final int dirLevel)
	{
		return new ExampleCop(number, bool, string, cop, needsSecure, redirectLoop, pager, dirLevel);
	}
	
	static void writePager(final Out out, final Pageable cop)
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
			out.append(" item ");
			out.append(pager.getFrom());
			out.append('-');
			out.append(pager.getTo());
			out.append(" of ");
			out.append(pager.getTotal());
			out.append("<br> page ");
			out.append(pager.getPage());
			out.append(" of ");
			out.append(pager.getTotalPages());
			out.append(" pages ");
			if(pager.hasBeforeNewPages())
				out.append("... ");
			for(final Pager newPage : pager.newPages())
				writePagerButton(out, cop, newPage, String.valueOf(newPage.getPage()));
			if(pager.hasAfterNewPages())
				out.append(" ...");
		}
	}
	
	@Override
	protected boolean needsSecure()
	{
		return needsSecure;
	}
}
