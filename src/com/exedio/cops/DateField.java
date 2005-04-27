/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cops;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateField extends TextField
{
	public static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	final Date content;
	
	/**
	 * Constructs a form field with an initial value.
	 */
	public DateField(final Form form, final Object key, final String name, final boolean readOnly, final Date value, final boolean hidden)
	{
		super(form, key, name, readOnly, (value==null) ? "" : (new SimpleDateFormat(DATE_FORMAT_FULL)).format(value), hidden);

		this.content = value;
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public DateField(final Form form, final Object key, final String name, final boolean readOnly, final boolean hidden)
	{
		super(form, key, name, readOnly, hidden);

		final String value = this.value;
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
