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

import java.util.Iterator;

import junit.framework.TestCase;

public class PagerTest extends TestCase
{
	public void testPager()
	{
		final Pager first = i(p(10), 10, 45);
		assertIt(first, 0, 10, 1, 10, 45, true, false, true, false);
		
		assertIt(i(first.previous(), 10, 45), 0, 10, 1, 10, 45, true, true, true, false);
		assertIt(i(first.first()   , 10, 45), 0, 10, 1, 10, 45, true, true, true, false);

		final Pager page2 = i(first.next(), 10, 45);
		assertIt(page2, 10, 10, 11, 20, 45, true, false, false, false);
		
		final Pager first2 = i(page2.previous(), 10, 45);
		assertIt(first2, 0, 10, 1, 10, 45, true, false, true, false);
		
		final Pager last = i(first.last(), 5, 45);
		assertIt(last, 40, 10, 41, 45, 45, true, false, false, true);
		
		assertIt(i(last.next(), 5, 45), 40, 10, 41, 45, 45, true, true, false, true);
		assertIt(i(last.last(), 5, 45), 40, 10, 41, 45, 45, true, true, false, true);
		
		final Pager first3 = i(last.first(), 10, 45);
		assertIt(first3, 0, 10, 1, 10, 45, true, false, true, false);
		
		final Pager page2l20 = i(page2.switchLimit(20), 20, 45);
		assertIt(page2l20, 10, 20, 11, 30, 45, true, false, false, false);
		assertIt(i(page2.switchLimit(10), 10, 45), 10, 10, 11, 20, 45, true, true, false, false);

		assertIt(i(page2l20.previous(), 20, 45),  0, 20,  1, 20, 45, true, false, true, false);
		assertIt(i(page2l20.first(),    20, 45),  0, 20,  1, 20, 45, true, false, true, false);
		assertIt(i(page2l20.next(),     15, 45), 30, 20, 31, 45, 45, true, false, false, true);
		assertIt(i(page2l20.last(),      5, 45), 40, 20, 41, 45, 45, true, false, false, true);
	}
	
	public void testNotNeeded()
	{
		final Pager p = i(p(10), 10, 10);
		assertIt(p, 0, 10, 1, 10, 10, false, false, true, true);

		final Pager pn = i(p(10), 10, 11);
		assertIt(pn, 0, 10, 1, 10, 11, true, false, true, false);
	}
	
	public void testNewLimit()
	{
		final Pager p = i(p(10), 10, 500);
		final Iterator<Pager> i = p.newLimits().iterator();
		assertIt(i(i.next(),  10, 500), 0,  10, 1,  10, 500, true, true,  true, false);
		assertIt(i(i.next(),  20, 500), 0,  20, 1,  20, 500, true, false, true, false);
		assertIt(i(i.next(),  50, 500), 0,  50, 1,  50, 500, true, false, true, false);
		assertIt(i(i.next(), 100, 500), 0, 100, 1, 100, 500, true, false, true, false);
		assertIt(i(i.next(), 200, 500), 0, 200, 1, 200, 500, true, false, true, false);
		assertIt(i(i.next(), 500, 500), 0, 500, 1, 500, 500, true, false, true, true);
		assertFalse(i.hasNext());
	}
	
	private static final Pager p(final int limit)
	{
		return new Pager(limit);
	}
	
	private static final Pager i(final Pager pager, final int page, final int total)
	{
		pager.init(page, total);
		return pager;
	}
	
	private static final void assertIt(
			final Pager pager,
			final int offset, final int limit,
			final int from, final int to,
			final int total,
			final boolean needed, final boolean neutral,
			final boolean first, final boolean last)
	{
		assertEquals(offset,  pager.getOffset());
		assertEquals(limit,   pager.getLimit());
		assertEquals(from,    pager.getFrom());
		assertEquals(to,      pager.getTo());
		assertEquals(total,   pager.getTotal());
		assertEquals(needed,  pager.isNeeded());
		assertEquals(neutral, pager.isNeutral());
		assertEquals(first,   pager.isFirst());
		assertEquals(last,    pager.isLast());
	}
}
