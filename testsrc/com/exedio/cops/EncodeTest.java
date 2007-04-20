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

public class EncodeTest extends TestCase
{
	public void testEncode()
	{
		assertEquals(null, Cop.encodeXml(null));
		assertEquals("", Cop.encodeXml(""));
		assertEquals("x", Cop.encodeXml("x"));
		assertEquals("&lt;", Cop.encodeXml("<"));
		assertEquals("&gt;", Cop.encodeXml(">"));
		assertEquals("&quot;", Cop.encodeXml("\""));
		assertEquals("&apos;", Cop.encodeXml("'"));
		assertEquals("&amp;", Cop.encodeXml("&"));
		assertEquals("&apos;tralla&quot;", Cop.encodeXml("'tralla\""));
		assertEquals("&gt;kno&amp;llo&lt;", Cop.encodeXml(">kno&llo<"));
	}
}
