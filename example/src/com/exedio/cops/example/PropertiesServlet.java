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

package com.exedio.cops.example;

import com.exedio.cope.util.Properties;
import com.exedio.cops.PropertiesServlet.Overridable;
import java.io.File;

public final class PropertiesServlet extends com.exedio.cops.PropertiesServlet
implements Overridable<ExampleProperties>
{
	private static final long serialVersionUID = 1l;

	private File source;

	@Override
	public void init()
	{
		source = new File(getServletContext().getRealPath("WEB-INF/" + getInitParameter("source")));
	}

	@Override
	public void destroy()
	{
		source = null;
	}

	@Override
	protected ExampleProperties getProperties()
	{
		return ExampleProperties.instance(source);
	}

	@Override
	protected String getDisplayCaption()
	{
		return "Display Caption";
	}

	@Override
	public ExampleProperties newProperties(final Properties.Source overrideSource)
	{
		return new ExampleProperties(overrideSource);
	}

	@Override
	public void override(final ExampleProperties properties)
	{
		ExampleProperties.setInstance(properties);
	}
}
