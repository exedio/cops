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
import com.exedio.cope.util.Sources;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class PropertiesServlet extends CopsServlet
{
	private static final long serialVersionUID = 1l;

	static final Resource stylesheet = new Resource("properties.css");
	static final Resource script = new Resource("properties.js");
	static final Resource logo = new Resource("logo.png");
	static final Resource shortcutIcon = new Resource("shortcutIcon.png");
	static final Resource checkFalse = new Resource("checkfalse.png");
	static final Resource checkTrue  = new Resource("checktrue.png");

	static final String SET = "set";
	static final String FIELD_SELECT = "fieldSelect";
	static final String FIELD_VALUE_PREFIX = "fieldVal_";
	static final String FIELDS_RAW = "fieldsRaw";
	static final String PROBE_NUMBER = "probeNum";

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
				final HashMap<String, String> sourceMap = new HashMap<>();
				{
					final String[] selects = request.getParameterValues(FIELD_SELECT);
					if(selects!=null)
						for(final String select : selects)
							sourceMap.put(select, request.getParameter(FIELD_VALUE_PREFIX + select));
				}
				{
					final String string = request.getParameter(FIELDS_RAW);
					final java.util.Properties properties = new java.util.Properties();
					try(final StringReader stream = new StringReader(string))
					{
						properties.load(stream);
					}
					for(final Map.Entry<Object,Object> e : properties.entrySet())
						sourceMap.put((String)e.getKey(), (String)e.getValue());
				}
				if(!sourceMap.isEmpty())
				{
					final HashSet<Integer> doTestNumbers = new HashSet<>();
					final String[] doTestNumberStrings = request.getParameterValues(PROBE_NUMBER);
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
			final HashSet<Integer> doProbeNumbers)
	{
		final P properties = overridable.newProperties(Sources.cascade(
				new EditedSource(authentication, hostname, sourceMap), source));

		int probeNumber = -1;
		for(final Callable<?> probe : properties.getTests())
		{
			probeNumber++;

			if(doProbeNumbers.contains(Integer.valueOf(probeNumber)))
			{
				try
				{
					probe.call();
				}
				catch(final Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		overridable.override(properties);
	}

	private static final class EditedSource implements Properties.Source
	{
		private final String authentication;
		private final String hostname;
		private final HashMap<String, String> content;
		private final long timestamp = System.currentTimeMillis();

		EditedSource(
				final String authentication,
				final String hostname,
				final HashMap<String, String> content)
		{
			this.authentication = authentication;
			this.hostname = hostname;
			this.content = content;
		}

		@Override
		public String get(final String key)
		{
			return content.get(key);
		}

		@Override
		public String getDescription()
		{
			final StringBuilder bf = new StringBuilder();
			bf.append("(Edited ");
			bf.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(timestamp)));
			if(authentication!=null)
				bf.append(" by ").append(authentication);
			if(hostname!=null)
				bf.append(" on ").append(hostname);
			bf.append(' ');
			bf.append(content.toString());
			bf.append(')');
			return bf.toString();
		}

		@Override
		public Collection<String> keySet()
		{
			return content.keySet();
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

		final HashSet<String> result = new HashSet<>(sourceKeySet);
		for(final Properties.Field field : properties.getFields())
			result.remove(field.getKey());
		return result;
	}
}
