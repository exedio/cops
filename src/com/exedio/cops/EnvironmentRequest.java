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
	
	static String getURL(final String environment, final Boolean needsSecure, final String url)
	{
		final boolean secure = needsSecure!=null && needsSecure.booleanValue();
		
		String e = environment;
		if(secure)
		{
			final int pos = e.indexOf(":8080/");
			if(pos>0)
				e = e.substring(0, pos) + ":8443" + e.substring(pos+5);
		}
		else
		{
			final int pos = e.indexOf(":8443/");
			if(pos>0)
				e = e.substring(0, pos) + ":8080" + e.substring(pos+5);
		}
		
		return (secure ? "https://" : "http://") + e + '/' + url;
		
	}
}
