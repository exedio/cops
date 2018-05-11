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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public final class Pager
{
	public static final class Config
	{
		final int[] limits;
		final int limitDefault;
		final int limitCeiling;

		public Config(final int... limits)
		{
			requireNonNull(limits, "limits");
			if(limits.length==0)
				throw new IllegalArgumentException("limits must not be empty");
			int n = 0;
			for(int i = 0; i<limits.length; i++)
			{
				final int limit = limits[i];
				if(limit<1)
					throw new IllegalArgumentException("limits must be greater zero, but was " + limit + " at index " + i);
				if(n>=limit)
					throw new IllegalArgumentException("limits must be monotonously increasing, but was " + n + ">=" + limit + " at index " + i);
				n = limit;
			}

			this.limits = limits.clone();
			this.limitDefault = limits[0];
			this.limitCeiling = limits[limits.length-1];
		}

		public Pager newPager()
		{
			return new Pager(this, OFFSET_MIN, limitDefault, false);
		}

		public Pager newPager(final HttpServletRequest request)
		{
			return new Pager(
					this,
					Cop.getIntParameter(request, OFFSET, OFFSET_MIN),
					Cop.getIntParameter(request, LIMIT,  limitDefault),
					false);
		}
	}

	private static final String OFFSET = "off";
	private static final String LIMIT  = "lim";

	private static final int OFFSET_MIN = 0;

	private final Config config;
	private final int offset;
	private final int limit;
	private final boolean neutral;

	Pager(final Config config, final int offset, int limit, final boolean neutral)
	{
		if(limit>config.limitCeiling)
			limit = config.limitCeiling;

		this.config = config;
		this.offset = offset;
		this.limit = limit;
		this.neutral = neutral;
	}

	public void addParameters(final Cop cop)
	{
		cop.addParameter(OFFSET, offset, OFFSET_MIN);
		cop.addParameter(LIMIT, limit, config.limitDefault);
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
			throw new IllegalStateException("must call init before");

		return pageIfInitialized;
	}

	private int total()
	{
		if(totalIfInitialized<0)
			throw new IllegalStateException("must call init before");

		return totalIfInitialized;
	}

	public void init(final int page, final int total)
	{
		if(page<0)
			throw new IllegalArgumentException("page must be positive, but was " + String.valueOf(page));
		if(total<0)
			throw new IllegalArgumentException("total must be positive, but was " + String.valueOf(total));
		if(pageIfInitialized>=0)
			throw new IllegalStateException("must not call init more than once");
		if(totalIfInitialized>=0)
			throw new IllegalStateException("must not call init more than once");

		this.pageIfInitialized  = page;
		this.totalIfInitialized = total;
	}

	/**
	 * @return a subList of <tt>all</tt> corresponding the the current page of the pager.
	 */
	public <E> List<E> init(final List<E> all)
	{
		final int allSize = all.size();
		final int toIndex = Math.min(offset + limit, allSize);
		final List<E> page =
				(toIndex>offset)
				? all.subList(offset, toIndex)
				: Collections.<E>emptyList();
		init(page.size(), allSize);
		return page;
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
		return new Pager(config, OFFSET_MIN, limit, offset==OFFSET_MIN);
	}

	public Pager last()
	{
		final int newOffset = ((total()-1)/limit)*limit;
		return new Pager(config, newOffset, limit, offset==newOffset);
	}

	public Pager previous()
	{
		int newOffset = offset - limit;
		if(newOffset<OFFSET_MIN)
			newOffset = OFFSET_MIN;
		return new Pager(config, newOffset, limit, offset==newOffset);
	}

	public Pager next()
	{
		final int newOffset = offset + limit;
		if(newOffset>=total())
			return last();
		return new Pager(config, newOffset, limit, offset==newOffset);
	}

	public Pager switchLimit(final int newLimit)
	{
		return new Pager(config, offset, newLimit, limit==newLimit);
	}

	public List<Pager> newLimits()
	{
		final ArrayList<Pager> result = new ArrayList<>();
		final int max = Math.min(total(), config.limitCeiling);
		for(final int limit : config.limits)
		{
			result.add(switchLimit(limit));
			if(limit>=max)
				break;
		}
		return result;
	}

	private static final int PAGE_CONTEXT = 3;
	private static final int PAGE_CONTEXT_SPAN = 2*PAGE_CONTEXT;

	public boolean hasBeforeNewPages()
	{
		final int page = offset / limit;
		return (page-PAGE_CONTEXT)>0;
	}

	public boolean hasAfterNewPages()
	{
		final int page = offset / limit;
		final int fromPage = Math.max(page-PAGE_CONTEXT, 0);
		return (fromPage+PAGE_CONTEXT_SPAN) < ((total()-1) / limit);
	}

	public List<Pager> newPages()
	{
		final int page = offset / limit;
		final int fromPage = Math.max(page-PAGE_CONTEXT, 0);
		final int toPage   = Math.min(fromPage+PAGE_CONTEXT_SPAN, ((total()-1) / limit));
		final ArrayList<Pager> result = new ArrayList<>();
		for(int newPage = fromPage; newPage<=toPage; newPage++)
		{
			final int pageOffset = newPage*limit; // TODO replace multiply by add
			result.add(new Pager(config, pageOffset, limit, pageOffset==offset));
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

	public int getPage()
	{
		return (offset / limit) + 1;
	}

	public int getTotalPages()
	{
		return ((total()-1) / limit) + 1;
	}

	public boolean isNeeded()
	{
		return total()>config.limitDefault;
	}

	public boolean isNeutral()
	{
		return neutral;
	}
}
