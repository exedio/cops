
package com.exedio.cops;

import java.io.IOException;
import java.io.PrintStream;


public class CheckboxField extends Field
{
	public static final String VALUE_ON = "on";
	
	public CheckboxField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);
	}
	
	public boolean isChecked()
	{
		return VALUE_ON.equals(value);
	}
	
	public void write(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
	public Object getContent()
	{
		return Boolean.valueOf(isChecked());
	}
	
}
