/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
	final String pattern;
	
	/**
	 * Constructs a form field with an initial value.
	 */
	public DateField(final Form form, final Object key, final String name, final Date value)
	{
		this(DATE_FORMAT_FULL, form, key, name, value);
	}
	
	/**
	 * Constructs a form field with an initial value.
	 */
	public DateField(final String pattern, final Form form, final Object key, final String name, final Date value)
	{
		super(form, key, name, (value==null) ? "" : (new SimpleDateFormat(pattern)).format(value));

		this.pattern = pattern;
		this.content = value;

		if(pattern==null)
			throw new NullPointerException("pattern must not be null");
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public DateField(final Form form, final Object key, final String name)
	{
		this(DATE_FORMAT_FULL, form, key, name);
	}
	
	/**
	 * Constructs a form field with a value obtained from the submitted form.
	 */
	public DateField(final String pattern, final Form form, final Object key, final String name)
	{
		super(form, key, name);

		this.pattern = pattern;

		final String value = this.value;
		if(value.length()>0)
		{
			Date parsed = null;
			try
			{
				final SimpleDateFormat df = new SimpleDateFormat(pattern);
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
	
	@Override
	public Object getContent()
	{
		return content;
	}
	
}
