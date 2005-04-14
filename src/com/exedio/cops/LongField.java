
package com.exedio.cops;


public class LongField extends TextField
{
	final Long content;
	
	/**
	 * Constructs a form field with an inital value.
	 */
	public LongField(final Form form, final Object key, final String name, final boolean readOnly, final Long value, final boolean hidden)
	{
		super(form, key, name, readOnly, (value==null) ? "" : String.valueOf(value), hidden);
		content = value;
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public LongField(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		super(form, key, name, readOnly, hidden);

		final String value = this.value;
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
