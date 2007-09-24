/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public final class Pager
{
	private static final String OFFSET = "off";
	private static final String LIMIT  = "lim";

	private static final int OFFSET_MIN = 0;
	private final int limitDefault;

	private static final int limitCeiling = 1000;
	private final int offset;
	private final int limit;
	private final boolean neutral;
	
	public Pager(final int limitDefault)
	{
		this(limitDefault, OFFSET_MIN, limitDefault, false);
	}
	
	private Pager(final int limitDefault, final int offset, int limit, final boolean neutral)
	{
		if(limit>limitCeiling)
			limit = limitCeiling;

		this.limitDefault = limitDefault;
		this.offset = offset;
		this.limit = limit;
		this.neutral = neutral;
	}
	
	public void addParameters(final Cop cop)
	{
		if(offset!=OFFSET_MIN)
			cop.addParameter(OFFSET, String.valueOf(offset));
		if(limit!=limitDefault)
			cop.addParameter(LIMIT, String.valueOf(limit));
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public int getLimit()
	{
		return limit;
	}
	
	private int pageIfInitialized = -1;
	private int totalIfInitialized = -1;
	
	private int page()
	{
		if(pageIfInitialized<0)
			throw new IllegalStateException(String.valueOf(pageIfInitialized));
		
		return pageIfInitialized;
	}
	
	private int total()
	{
		if(totalIfInitialized<0)
			throw new IllegalStateException(String.valueOf(totalIfInitialized));
		
		return totalIfInitialized;
	}
	
	public void init(final int page, final int total)
	{
		if(page<0)
			throw new IllegalArgumentException(String.valueOf(page));
		if(total<0)
			throw new IllegalArgumentException(String.valueOf(total));
		if(pageIfInitialized>=0)
			throw new IllegalStateException(String.valueOf(pageIfInitialized));
		if(totalIfInitialized>=0)
			throw new IllegalStateException(String.valueOf(totalIfInitialized));
		
		this.pageIfInitialized  = page;
		this.totalIfInitialized = total;
	}
	
	public boolean isFirst()
	{
		return offset == OFFSET_MIN;
	}
	
	public boolean isLast()
	{
		return (offset+limit)>=total();
	}
	
	public Pager first()
	{
		return new Pager(limitDefault, OFFSET_MIN, limit, offset==OFFSET_MIN);
	}
	
	public Pager last()
	{
		final int newOffset = ((total()-1)/limit)*limit;
		return new Pager(limitDefault, newOffset, limit, offset==newOffset);
	}
	
	public Pager previous()
	{
		int newOffset = offset - limit;
		if(newOffset<OFFSET_MIN)
			newOffset = OFFSET_MIN;
		return new Pager(limitDefault, newOffset, limit, offset==newOffset);
	}
	
	public Pager next()
	{
		final int newOffset = offset + limit;
		return new Pager(limitDefault, newOffset, limit, offset==newOffset);
	}
	
	public Pager switchLimit(final int newLimit)
	{
		return new Pager(limitDefault, offset, newLimit, limit==newLimit);
	}
	
	public List<Pager> newLimits()
	{
		final ArrayList<Pager> result = new ArrayList<Pager>();
		final int max = Math.min(total(), limitCeiling);
		for(int factor = 1; true; factor*=10)
		{
			final int one = limitDefault * factor;
			if(one>max)
				break;
			result.add(switchLimit(one));
			
			final int two = 2*one;
			if(two>max)
				break;
			result.add(switchLimit(two));
			
			final int five = 5*one;
			if(two>five)
				break;
			result.add(switchLimit(five));
		}
		return result;
	}
	
	public int getFrom()
	{
		return offset + 1;
	}
	
	public int getTo()
	{
		return offset + page();
	}
	
	public int getTotal()
	{
		return total();
	}

	public boolean isTotalEmpty()
	{
		return total()==0;
	}
	
	public boolean isNeeded()
	{
		return total()>limitDefault;
	}

	public boolean isNeutral()
	{
		return neutral;
	}

	public static final Pager newPager(final HttpServletRequest request, final int limitDefault)
	{
		return new Pager(limitDefault,
				Cop.getIntParameter(request, OFFSET, OFFSET_MIN),
				Cop.getIntParameter(request, LIMIT,  limitDefault),
				false);
	}
}
