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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

final class PropertiesCop extends Cop
{
	private final HttpServletRequest request;

	private PropertiesCop(final HttpServletRequest request)
	{
		super("");
		this.request = request;
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static PropertiesCop getCop(final HttpServletRequest request)
	{
		return new PropertiesCop(
				request);
	}

	static void addShowHidden(final Field<?> field, final HttpServletRequest request)
	{
		final HttpSession session = request.getSession(true);
		final Object valueObject = session.getAttribute(SHOW_HIDDEN_SESSION);
		if(valueObject instanceof final Set<?> valueWildcard) // implies !=null
		{
			@SuppressWarnings("unchecked")
			final Set<String> value = (Set<String>)valueWildcard;
			value.add(field.getKey());
		}
		else
		{
			final HashSet<String> value = new HashSet<>();
			value.add(field.getKey());
			session.setAttribute(SHOW_HIDDEN_SESSION, value);
		}
	}

	static void clearShowHidden(final HttpServletRequest request)
	{
		final HttpSession session = request.getSession(false);
		if(session!=null)
			session.removeAttribute(SHOW_HIDDEN_SESSION);
	}

	boolean isHidden(final Field<?> field)
	{
		if(!field.hasHiddenValue())
			return false;

		final HttpSession session = request.getSession(false);
		if(session==null)
			return true;
		final Object valueObject = session.getAttribute(SHOW_HIDDEN_SESSION);
		return !(
				valueObject instanceof final Set<?> value && // implies !=null
				value.contains(field.getKey())
		);
	}

	boolean hasShowHidden()
	{
		final HttpSession session = request.getSession(false);
		return
				session!=null &&
				session.getAttribute(SHOW_HIDDEN_SESSION)!=null;
	}

	private static final String SHOW_HIDDEN_SESSION = PropertiesServlet.class.getName() + "#showHidden";

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static Object reloadedNewValue(
			final Field<?> field,
			final Properties reloaded,
			final ArrayList<Field<?>> reloadedFields)
	{
		if(reloaded==null)
			return null;

		final Field<?> reloadedField = reloaded.getField(field.getKey());
		if(reloadedField==null)
			return null;

		final Object reloadedValue = reloadedField.getValue();
		if(field.getValue().equals(reloadedValue))
			return null;

		reloadedFields.add(reloadedField);
		return reloadedValue;
	}
}
