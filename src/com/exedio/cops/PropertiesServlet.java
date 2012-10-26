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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.util.Properties;

public abstract class PropertiesServlet extends CopsServlet
{
	private static final long serialVersionUID = 1l;

	static final Resource stylesheet = new Resource("properties.css");
	static final Resource script = new Resource("properties.js");
	static final Resource logo = new Resource("logo.png");
	static final Resource checkFalse = new Resource("checkfalse.png");
	static final Resource checkTrue  = new Resource("checktrue.png");

	static final String SET = "set";
	static final String FIELD_SELECT = "fieldSelect";
	static final String FIELD_VALUE_PREFIX = "fieldVal_";
	static final String TEST_NUMBER = "testNum";

	@Override
	protected final void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		//System.out.println("request ---" + request.getMethod() + "---" + request.getContextPath() + "---" + request.getServletPath() + "---" + request.getPathInfo() + "---" + request.getQueryString() + "---");

		if(Cop.isPost(request) && request.getParameter(SET)!=null)
		{
			if(this instanceof Overridable<?>)
			{
				final HashMap<String, String> sourceMap = new HashMap<String, String>();
				{
					final String[] selects = request.getParameterValues(FIELD_SELECT);
					if(selects!=null)
						for(final String select : selects)
							sourceMap.put(select, request.getParameter(FIELD_VALUE_PREFIX + select));
				}
				if(!sourceMap.isEmpty())
				{
					final HashSet<Integer> doTestNumbers = new HashSet<Integer>();
					final String[] doTestNumberStrings = request.getParameterValues(TEST_NUMBER);
					if(doTestNumberStrings!=null)
						for(final String doTestNumberString : doTestNumberStrings)
							doTestNumbers.add(Integer.valueOf(Integer.parseInt(doTestNumberString)));

					final Principal principal = request.getUserPrincipal();
					final String authentication = principal!=null ? principal.getName() : null;
					String hostname = null;
					try
					{
						hostname = InetAddress.getLocalHost().getHostName();
					}
					catch(final UnknownHostException e)
					{
						// leave hostname==null
					}

					override(
							(Overridable<?>)this,
							getProperties().getSourceObject(),
							authentication, hostname,
							sourceMap,
							doTestNumbers);
				}
			}
		}

		final Properties properties = getProperties();

		final Out out = new Out(request);
		Properties_Jspm.write(
				out,
				PropertiesCop.getCop(properties, request),
				request,
				getDisplayCaption(),
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z (z)").format(new Date()),
				orphaned(properties),
				this instanceof Overridable<?>,
				properties);
		out.sendBody(response);
	}

	/**
	 * Helper method for using generics safely without warnings.
	 */
	private static <P extends Properties> void override(
			final Overridable<P> overridable,
			final Properties.Source source,
			final String authentication,
			final String hostname,
			final HashMap<String, String> sourceMap,
			final HashSet<Integer> doTestNumbers)
	{
		final P properties = overridable.newProperties(
				new OverrideSource(source, authentication, hostname, sourceMap));

		int testNumber = -1;
		for(final Callable<?> test : properties.getTests())
		{
			testNumber++;

			if(doTestNumbers.contains(Integer.valueOf(testNumber)))
			{
				try
				{
					test.call();
				}
				catch(final Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		overridable.override(properties);
	}

	private static final class OverrideSource implements Properties.Source
	{
		private final Properties.Source template;
		private final String authentication;
		private final String hostname;
		private final HashMap<String, String> override;
		private final long timestamp = System.currentTimeMillis();

		OverrideSource(
				final Properties.Source sourceBefore,
				final String authentication,
				final String hostname,
				final HashMap<String, String> sourceMap)
		{
			this.template = sourceBefore;
			this.authentication = authentication;
			this.hostname = hostname;
			this.override = sourceMap;
		}

		public String get(final String key)
		{
			return
				override.containsKey(key)
				? override.get(key)
				: template.get(key);
		}

		public String getDescription()
		{
			final StringBuilder bf = new StringBuilder();
			bf.append(template.getDescription());
			bf.append(" (Edited ");
			bf.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(timestamp)));
			if(authentication!=null)
				bf.append(" by ").append(authentication);
			if(hostname!=null)
				bf.append(" on ").append(hostname);
			bf.append(' ');
			bf.append(override.toString());
			bf.append(')');
			return bf.toString();
		}

		public Collection<String> keySet()
		{
			return null;
		}
	}

	protected abstract Properties getProperties();

	protected String getDisplayCaption()
	{
		return "Properties";
	}

	public interface Overridable<P extends Properties>
	{
		P newProperties(Properties.Source overrideSource);
		void override(P properties);
	}

	// TODO replace by method in class Properties
	private static final HashSet<String> orphaned(final Properties properties)
	{
		final Collection<String> sourceKeySet = properties.getSourceObject().keySet();
		if(sourceKeySet==null)
			return null;

		final HashSet<String> result = new HashSet<String>(sourceKeySet);
		for(final Properties.Field field : properties.getFields())
			result.remove(field.getKey());
		return result;
	}
}
