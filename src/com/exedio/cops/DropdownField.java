/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.util.ArrayList;
import java.util.HashMap;


public class DropdownField extends Field
{
	public final ArrayList names = new ArrayList();
	final HashMap values = new HashMap();
	
	/**
	 * Constructs a form field with an initial value.
	 * @throws NullPointerException if value is null.
	 */
	public DropdownField(final Form form, final Object key, final String name, final String value)
	{
		super(form, key, name, value);

		if(value==null)
			throw new NullPointerException("value for field "+name+" must not be null");
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 * @throws NullPointerException if request does not contain a parameter for name.
	 */
	public DropdownField(final Form form, final Object key, final String name)
	{
		super(form, key, name);

		if(value==null)
			throw new NullPointerException("value for field "+name+" must be in request");
	}
	
	public String getValue(final String name)
	{
		return (String)values.get(name);
	}
	
	public boolean isChecked(final String checkValue)
	{
		return value.equals(checkValue);
	}
	
	/**
	 * @throws NullPointerException if name or value is null
	 */
	public void addOption(final String name, final String value)
	{
		if(name==null)
			throw new NullPointerException("name must not be null");
		if(value==null)
			throw new NullPointerException("value must not be null");
		
		names.add(name);
		values.put(name, value);
	}
	
	public void writeIt(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
}
