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

final class EnvironmentRequest
{
	private EnvironmentRequest()
	{
		// prevent instantiation
	}

	static String getURL(final String environment, final boolean needsSecure, final String url)
	{
		String e = environment;
		if(needsSecure)
		{
			final int pos = e.indexOf(":8080/");
			if(pos>0)
				e = e.substring(0, pos) + ":8443" + e.substring(pos+5);
			else if (e.endsWith(":8080"))
				e = e.substring(0, (e.length()-5)) + ":8443";
		}
		else
		{
			final int pos = e.indexOf(":8443/");
			if(pos>0)
				e = e.substring(0, pos) + ":8080" + e.substring(pos+5);
			else if (e.endsWith(":8443"))
				e = e.substring(0, (e.length()-5)) + ":8080";
		}

		return (needsSecure ? "https://" : "http://") + e + '/' + url;

	}
}
