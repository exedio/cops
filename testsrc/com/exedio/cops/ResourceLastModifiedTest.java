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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class ResourceLastModifiedTest extends TestCase
{
	public void testURL()
	{
		final Date before = new Date();
		final Resource r1 = new Resource("ResourceTest.class", "major/minor");
		final Date after = new Date();

		final Date lastModified = r1.getLastModified();
		assertWithin(new Date((before.getTime()/1000)*1000), new Date(((after.getTime()/1000)+1)*1000), lastModified);

		r1.init(ResourceTest.class);
		assertEquals(lastModified, r1.getLastModified());

		r1.init(ResourceTest.class);
		assertEquals(lastModified, r1.getLastModified());

		r1.setLastModified(12000);
		assertEquals(12000, r1.getLastModified().getTime());

		r1.setLastModified(11999);
		assertEquals(12000, r1.getLastModified().getTime());

		r1.setLastModified(12001);
		assertEquals(13000, r1.getLastModified().getTime());

		r1.setLastModified(0);
		assertEquals(0, r1.getLastModified().getTime());

		try
		{
			r1.setLastModified(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("lastModified must not be negative, but was -1", e.getMessage());
		}
		assertEquals(0, r1.getLastModified().getTime());
	}

	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	public static final void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBefore.after(actual));
		assertTrue(message, !expectedAfter.before(actual));
	}
}
