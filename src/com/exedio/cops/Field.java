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


public abstract class Field
{
	public final Object key;
	public final String name;
	final boolean readOnly;
	public final String value;
	public String error;
	private boolean written = false;

	/**
	 * Constructs a form field with an initial value.
	 */
	public Field(final Form form, final Object key, final String name, final boolean readOnly, final String value)
	{
		this.key = key;
		this.name = name;
		this.readOnly = readOnly;
		this.value = value;
		form.register(this);
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public Field(final Form form, final Object key, final String name, final boolean readOnly)
	{
		this.key = key;
		this.name = name;
		this.readOnly = readOnly;
		this.value = form.getParameter(name);
		form.register(this);
	}
	
	/**
	 * The content of the <code>style</code> attribute of the <code>input</code> tag,
	 * if you use {@link #write(PrintStream)}.
	 */
	public String style = null;
	
	public final boolean isReadOnly()
	{
		return readOnly;
	}
	
	public final String getName()
	{
		if(name==null)
			throw new RuntimeException();
		return name;
	}
	
	public final String getValue()
	{
		return value;
	}
	
	public final String getError()
	{
		return error;
	}
	
	public final boolean isWritten()
	{
		return written;
	}
	
	public final void write(final PrintStream out) throws IOException
	{
		if(written)
			throw new RuntimeException("field "+name+" has already been written");
		
		writeIt(out);
		written = true;
	}

	public abstract void writeIt(final PrintStream out) throws IOException;
	
	public Object getContent()
	{
		return value;
	}
	
}
