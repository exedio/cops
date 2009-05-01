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

package com.exedio.cops.example;

import com.exedio.cops.Cop;
import com.exedio.cops.Resource;
import com.exedio.cops.XMLEncoder;

final class Out
{
	private final StringBuilder bf;
	
	Out(final StringBuilder bf)
	{
		this.bf = bf;
		
		assert bf!=null;
	}
	
	void append(final char c)
	{
		bf.append(c);
	}
	
	void append(final String s)
	{
		bf.append(s);
	}
	
	void append(final int i)
	{
		bf.append(i);
	}
	
	void append(final Resource resource)
	{
		bf.append(resource.toString());
	}
	
	void append(final Cop cop)
	{
		bf.append(XMLEncoder.encode(cop.toString()));
	}
}
