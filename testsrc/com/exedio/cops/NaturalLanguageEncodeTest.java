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

import junit.framework.TestCase;

public class NaturalLanguageEncodeTest extends TestCase
{
	public void testIt()
	{
		assertEquals(null, e(null));
		assertEquals("", e(""));
		assertEquals("a", e("a"));
		assertEquals("", e("."));
		assertEquals("", e(".."));
		assertEquals("-a-b", e(".a.b.")); // TODO
		assertEquals("-a-b", e("..a..b..")); // TODO
		assertEquals("-a-b", e("...a...b...")); // TODO
		assertEquals("a-b", e("a.b."));
		assertEquals("a-b", e("a..b.."));
		assertEquals("a-b", e("a...b..."));
		assertEquals("09AZaz", e("09AZaz"));
		assertEquals("", e("^!\"$%&/()=?`'{}[]\\*+~#,;.:-_\n\r\u00e4"));
		assertEquals("abc-def", e("abc...def"));
		assertEquals("-abcdef", e("...abcdef..."));
		assertEquals("-abcdef", e("...abcdef"));
		assertEquals("abcdef", e("abcdef..."));
		assertEquals("abc-def", e("abc.def"));
		assertEquals("-abcdef", e(".abcdef."));
		assertEquals("-abcdef", e(".abcdef"));
		assertEquals("abcdef", e("abcdef."));
	}

	private static String e(final String s)
	{
		return Cop.encodeNaturalLanguageSegment(s);
	}
}
