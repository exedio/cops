
package com.exedio.cops;


public class DoubleField extends TextField
{
	final Double content;
	
	public DoubleField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);

		if(value.length()>0)
		{
			double parsed = 0;
			try
			{
				parsed = Double.parseDouble(value);
			}
			catch(NumberFormatException e)
			{
				error = "bad number: "+e.getMessage();
			}
			content = error==null ? new Double(parsed) : null;
		}
		else
			content = null;
	}
	
	public Object getContent()
	{
		return content;
	}
	
}
