
package com.exedio.cops;


public class LongField extends TextField
{
	final Long content;
	
	public LongField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);

		if(value.length()>0)
		{
			long parsed = 0;
			try
			{
				parsed = Long.parseLong(value);
			}
			catch(NumberFormatException e)
			{
				error = "bad number: "+e.getMessage();
			}
			content = error==null ? new Long(parsed) : null;
		}
		else
			content = null;
	}
	
	public Object getContent()
	{
		return content;
	}
	
}
