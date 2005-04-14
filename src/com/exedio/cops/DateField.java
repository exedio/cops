
package com.exedio.cops;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateField extends TextField
{
	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	final Date content;
	
	public DateField(final Form form, final Object key, final String name, final boolean readOnly, final String value, final boolean hidden)
	{
		super(form, key, name, readOnly, value, hidden);

		if(value.length()>0)
		{
			Date parsed = null;
			try
			{
				final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
				parsed = df.parse(value);
			}
			catch(ParseException e)
			{
				error = "bad date: "+e.getMessage();
			}
			content = error==null ? parsed : null;
		}
		else
			content = null;
	}
	
	public Object getContent()
	{
		return content;
	}
	
}
