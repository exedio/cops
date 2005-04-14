
package com.exedio.cops;

import java.io.IOException;
import java.io.PrintStream;


public class Field
{
	public final Object key;
	public final String name;
	final boolean readOnly;
	public final String value;
	public String error;

	/**
	 * Constructs a form field with an inital value.
	 */
	public Field(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		this.key = key;
		this.name = name;
		this.readOnly = readOnly;
		this.value = value;
		form.register(this, hidden);
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public Field(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		this.key = key;
		this.name = name;
		this.readOnly = readOnly;
		this.value = form.getParameter(name);
		form.register(this, hidden);
	}
	
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
	
	// TODO make this method abstract
	public void write(final PrintStream out) throws IOException
	{
		throw new RuntimeException(name);
	}
	
	public Object getContent()
	{
		return value;
	}
	
}
