
package com.exedio.cops;


public class IntegerField extends TextField
{
	final Integer content;
	
	/**
	 * Constructs a form field with an inital value.
	 */
	public IntegerField(final Form form, final Object key, final String name, final boolean readOnly, final Integer value, final boolean hidden)
	{
		super(form, key, name, readOnly, (value==null) ? "" : String.valueOf(value), hidden);
		
		this.content = value;
	}

	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public IntegerField(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		super(form, key, name, readOnly, hidden);

		final String value = this.value;
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
