
package com.exedio.cops;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;


public class RadioField extends Field
{
	public final ArrayList names = new ArrayList();
	final HashMap values = new HashMap();
	
	public RadioField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);
	}
	
	public String getValue(final String name)
	{
		return (String)values.get(name);
	}
	
	public boolean isChecked(final String checkValue)
	{
		return value.equals(checkValue);
	}
	
	public void addOption(final String name, final String value)
	{
		names.add(name);
		values.put(name, value);
	}
	
	public void write(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
}
