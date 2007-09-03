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

import javax.servlet.http.HttpServletRequest;

public final class Pager
{
	private static final String OFFSET = "st"; // TODO change value to "off"
	private static final String LIMIT  = "ct"; // TODO change value to "lim"

	private static final int OFFSET_MIN = 0;
	private final int limitDefault;

	private static final int limitCeiling  = 1000;
	final int offset;
	final int limit;
	
	public Pager(final int limitDefault)
	{
		this(limitDefault, OFFSET_MIN, limitDefault);
	}
	
	private Pager(final int limitDefault, final int offset, int limit)
	{
		if(limit>limitCeiling)
			limit = limitCeiling;

		this.limitDefault = limitDefault;
		this.offset = offset;
		this.limit = limit;
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
	
	private int totalIfInitialized = -1;
	
	private int total()
	{
		if(totalIfInitialized<0)
			throw new IllegalStateException(String.valueOf(totalIfInitialized));
		
		return totalIfInitialized;
	}
	
	public void init(final int total)
	{
		if(total<0)
			throw new IllegalArgumentException(String.valueOf(total));
		if(totalIfInitialized>=0)
			throw new IllegalStateException(String.valueOf(totalIfInitialized));
		
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
		return new Pager(limitDefault, OFFSET_MIN, limit);
	}
	
	public Pager last()
	{
		return new Pager(limitDefault, ((total()-1)/limit)*limit, limit);
	}
	
	public Pager previous()
	{
		int newOffset = offset - limit;
		if(newOffset<OFFSET_MIN)
			newOffset = OFFSET_MIN;
		return new Pager(limitDefault, newOffset, limit);
	}
	
	public Pager next()
	{
		int newOffset = offset + limit;
		return new Pager(limitDefault, newOffset, limit);
	}
	
	public Pager switchLimit(final int newLimit)
	{
		return new Pager(limitDefault, offset, newLimit);
	}
	
	public int getTotal()
	{
		return total();
	}

	public boolean isTotalEmpty()
	{
		return total()==0;
	}

	public static final Pager newPager(final HttpServletRequest request, final int limitDefault)
	{
		return new Pager(limitDefault,
				Cop.getIntParameter(request, OFFSET, OFFSET_MIN),
				Cop.getIntParameter(request, LIMIT,  limitDefault));
	}
}
