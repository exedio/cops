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

	private static final int OFFSET_DEFAULT = 0;
	private final int LIMIT_DEFAULT;

	private static final int limitCeiling  = 1000;
	final int offset;
	final int limit;
	
	public Pager(final int LIMIT_DEFAULT)
	{
		this(LIMIT_DEFAULT, OFFSET_DEFAULT, LIMIT_DEFAULT);
	}
	
	private Pager(final int LIMIT_DEFAULT, final int offset, int limit)
	{
		if(limit>limitCeiling)
			limit = limitCeiling;

		this.LIMIT_DEFAULT = LIMIT_DEFAULT;
		this.offset = offset;
		this.limit = limit;
	}
	
	public void addParameters(final Cop cop)
	{
		if(offset!=OFFSET_DEFAULT)
			cop.addParameter(OFFSET, String.valueOf(offset));
		if(limit!=LIMIT_DEFAULT)
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
		return offset == 0;
	}
	
	public boolean isLast()
	{
		return (offset+limit)>=total();
	}
	
	public Pager first()
	{
		return new Pager(LIMIT_DEFAULT, 0, limit);
	}
	
	public Pager last()
	{
		return new Pager(LIMIT_DEFAULT, ((total()-1)/limit)*limit, limit);
	}
	
	public Pager previous()
	{
		int newOffset = offset - limit;
		if(newOffset<0)
			newOffset = 0;
		return new Pager(LIMIT_DEFAULT, newOffset, limit);
	}
	
	public Pager next()
	{
		int newOffset = offset + limit;
		return new Pager(LIMIT_DEFAULT, newOffset, limit);
	}
	
	public Pager switchLimit(final int newLimit)
	{
		return new Pager(LIMIT_DEFAULT, offset, newLimit);
	}
	
	public int getTotal()
	{
		return total();
	}

	public static final Pager newPager(final HttpServletRequest request, final int LIMIT_DEFAULT)
	{
		return new Pager(LIMIT_DEFAULT,
				Cop.getIntParameter(request, OFFSET, OFFSET_DEFAULT),
				Cop.getIntParameter(request, LIMIT,  LIMIT_DEFAULT));
	}
}
