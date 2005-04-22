
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
	
	public void write(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
}
