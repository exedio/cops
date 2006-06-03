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

import java.io.IOException;
import java.io.PrintStream;


public class CheckboxField extends Field
{
	static final String VALUE_ON = "on";
	
	final boolean content;

	/**
	 * Constructs a form field with an initial value.
	 */
	public CheckboxField(final Form form, final Object key, final String name, final boolean value)
	{
		super(form, key, name, value ? VALUE_ON : null);
		this.content = value;
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public CheckboxField(final Form form, final Object key, final String name)
	{
		super(form, key, name);

		final String value = this.value;
		if(value==null)
			content = false;
		else if(VALUE_ON.equals(value))
			content = true;
		else
			throw new RuntimeException(name+'-'+value);
	}
	
	@Override
	public void writeIt(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
	@Override
	public Object getContent()
	{
		return Boolean.valueOf(content);
	}
	
}
