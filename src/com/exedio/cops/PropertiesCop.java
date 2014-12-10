/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.exedio.cops;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.Field;
import java.util.Arrays;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;

final class PropertiesCop extends Cop
{
	static final String SHOW_HIDDEN = "sh";

	private final Properties properties;
	private final HashSet<String> showHidden;

	PropertiesCop(final Properties properties, final HashSet<String> showHidden)
	{
		super("");
		this.properties = properties;
		this.showHidden = showHidden;

		if(showHidden!=null)
			for(final Field field : properties.getFields())
				if(showHidden.contains(field.getKey()))
					addParameter(SHOW_HIDDEN, field.getKey());
	}

	static PropertiesCop getCop(final Properties properties, final HttpServletRequest request)
	{
		final String[] showHiddenKeys = request.getParameterValues(SHOW_HIDDEN);
		final HashSet<String> showHidden =
				(showHiddenKeys!=null)
				? new HashSet<>(Arrays.asList(showHiddenKeys))
				: null;
		return new PropertiesCop(
				properties, showHidden);
	}

	PropertiesCop addShowHidden(final Field field)
	{
		final HashSet<String> showHidden =
				(this.showHidden!=null)
				? new HashSet<>(this.showHidden)
				: new HashSet<String>();
		showHidden.add(field.getKey());
		return new PropertiesCop(properties, showHidden);
	}

	PropertiesCop clearShowHidden()
	{
		return new PropertiesCop(properties, null);
	}

	boolean isShowHidden(final Field field)
	{
		return showHidden!=null && showHidden.contains(field.getKey());
	}

	boolean hasShowHidden()
	{
		return showHidden!=null && !showHidden.isEmpty();
	}
}
