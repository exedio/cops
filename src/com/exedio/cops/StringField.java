
package com.exedio.cops;



public final class StringField extends TextField
{
	final String content;

	/**
	 * Constructs a form field with an initial value.
	 */
	public StringField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, (value==null) ? "" : value, hidden);
		
		this.content = value;
	}

	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public StringField(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		super(form, key, name, readOnly, hidden);
		
		final String value = this.value;
		content = value; // TODO: convert empty string to null
	}
	
	public Object getContent()
	{
		return content;
	}

}
