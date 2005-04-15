
package com.exedio.cops;


public class DoubleField extends TextField
{
	final Double content;
	
	/**
	 * Constructs a form field with an inital value.
	 */
	public DoubleField(final Form form, final Object key, final String name, final boolean readOnly, final Double value, final boolean hidden)
	{
		super(form, key, name, readOnly, (value==null) ? "" : String.valueOf(value), hidden);

		this.content = value;
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public DoubleField(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		super(form, key, name, readOnly, hidden);

		final String value = this.value;
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
