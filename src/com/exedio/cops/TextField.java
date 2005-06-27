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


public abstract class TextField extends Field
{
	
	/**
	 * Constructs a form field with an initial value.
	 */
	public TextField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public TextField(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		super(form, key, name, readOnly, hidden);
	}
	
	/**
	 * Let the content of the <code>type</code> attribute of the <code>input</code> tag
	 * contain <code>password</code> instead of <code>text</code>,
	 * if you use {@link #write(PrintStream)}.
	 */
	public boolean password = false;

	/**
	 * The content of the <code>size</code> attribute of the <code>input</code> tag,
	 * if you use {@link #write(PrintStream)}.
	 */
	public int size = 30;
	
	public void write(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
}
