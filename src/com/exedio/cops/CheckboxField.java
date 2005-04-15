
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
	public CheckboxField(final Form form, final Object key, final String name, final boolean readOnly, final boolean value, final boolean hidden)
	{
		super(form, key, name, readOnly, value ? VALUE_ON : null, hidden);
		this.content = value;
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public CheckboxField(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		super(form, key, name, readOnly, hidden);

		final String value = this.value;
		if(value==null)
			content = false;
		else if(VALUE_ON.equals(value))
			content = true;
		else
			throw new RuntimeException(name+'-'+value);
	}
	
	public void write(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
	public Object getContent()
	{
		return Boolean.valueOf(content);
	}
	
}
