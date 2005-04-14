
package com.exedio.cops;

import java.io.IOException;
import java.io.PrintStream;


public class TextField extends Field
{
	
	public TextField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);
	}
	
	public void write(final PrintStream out) throws IOException
	{
		Main_Jspm.write(out, this);
	}
	
}
