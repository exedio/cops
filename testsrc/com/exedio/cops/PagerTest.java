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

import junit.framework.TestCase;

public class PagerTest extends TestCase
{
	public void testPager()
	{
		final Pager first = i(p(10), 45);
		assertIt(first, 0, 10, 45, true, false);
		
		assertIt(i(first.previous(), 45), 0, 10, 45, true, false);
		assertIt(i(first.first()   , 45),    0, 10, 45, true, false);

		final Pager page2 = i(first.next(), 45);
		assertIt(page2, 10, 10, 45, false, false);
		
		final Pager first2 = i(page2.previous(), 45);
		assertIt(first2, 0, 10, 45, true, false);
		
		final Pager last = i(first.last(), 45);
		assertIt(last, 40, 10, 45, false, true);
		
		assertIt(i(last.next(), 45), 50, 10, 45, false, true); // TODO should be 40 instead of 50
		assertIt(i(last.last(), 45), 40, 10, 45, false, true);
		
		final Pager first3 = i(last.first(), 45);
		assertIt(first3, 0, 10, 45, true, false);
		
		final Pager page2l20 = i(page2.switchLimit(20), 45);
		assertIt(page2l20, 10, 20, 45, false, false);

		assertIt(i(page2l20.previous(), 45),  0, 20, 45, true, false);
		assertIt(i(page2l20.first(),    45),  0, 20, 45, true, false);
		assertIt(i(page2l20.next(),     45), 30, 20, 45, false, true);
		assertIt(i(page2l20.last(),     45), 40, 20, 45, false, true);
	}
	
	private static final Pager p(final int limit)
	{
		return new Pager(limit);
	}
	
	private static final Pager i(final Pager pager, final int total)
	{
		pager.init(total);
		return pager;
	}
	
	private static final void assertIt(
			final Pager pager,
			final int offset, final int limit,
			final int total,
			final boolean first, final boolean last)
	{
		assertEquals(limit,  pager.getLimit());
		assertEquals(offset, pager.getOffset());
		assertEquals(total,  pager.getTotal());
		assertEquals(first,  pager.isFirst());
		assertEquals(last,   pager.isLast());
	}
}
