
package com.exedio.cops;


public class IntegerField extends TextField
{
	final Integer content;
	
	public IntegerField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);

		if(value.length()>0)
		{
			int parsed = 0;
			try
			{
				parsed = Integer.parseInt(value);
			}
			catch(NumberFormatException e)
			{
				error = "bad number: "+e.getMessage();
			}
			content = error==null ? new Integer(parsed) : null;
		}
		else
			content = null;
	}

	public Object getContent()
	{
		return content;
	}
	
}
