/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


public class IntegerField extends TextField
{
	final Integer content;
	
	/**
	 * Constructs a form field with an initial value.
	 */
	public IntegerField(final Form form, final Object key, final String name, final Integer value)
	{
		super(form, key, name, (value==null) ? "" : String.valueOf(value));
		
		this.content = value;
	}

	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public IntegerField(final Form form, final Object key, final String name)
	{
		super(form, key, name);

		final String value = this.value;
		if(value.length()>0)
		{
			int parsed = 0;
			try
			{
				parsed = Integer.parseInt(value);
			}
			catch(NumberFormatException e)
			{
				error = "bad number: "+e.getMessage();
			}
			content = error==null ? new Integer(parsed) : null;
		}
		else
			content = null;
	}

	public Object getContent()
	{
		return content;
	}
	
}
