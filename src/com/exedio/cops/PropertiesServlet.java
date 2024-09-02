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

import static java.util.Locale.UK;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.ProbeAbortedException;
import com.exedio.cope.util.Sources;
import java.io.IOException;
import java.io.Serial;
import java.io.StringReader;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
public abstract class PropertiesServlet extends CopsServlet
{
	@Serial
	private static final long serialVersionUID = 1l;

	static final Resource stylesheet = new Resource("properties.css");
	static final Resource script = new Resource("properties.js");
	static final Resource logo = new Resource("logo.png");
	static final Resource shortcutIcon = new Resource("shortcutIcon.png");
	static final Resource checkFalse = new Resource("checkfalse.png");
	@SuppressWarnings("unused") // used by properties.js
	static final Resource checkTrue  = new Resource("checktrue.png");
	static final Resource nodeFalse = new Resource("nodefalse.png");
	@SuppressWarnings("unused") // used by properties.js
	static final Resource nodeTrue  = new Resource("nodetrue.png");

	static final String SET = "set";
	static final String FIELD_SELECT = "fieldSelect";
	static final String FIELD_VALUE_PREFIX = "fieldVal_";
	static final String FIELDS_RAW = "fieldsRaw";
	static final String PROBE_NUMBER = "probeNum";
	static final String DRY_RUN = "dryRun";

	@Override
	protected final void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		//System.out.println("request ---" + request.getMethod() + "---" + request.getContextPath() + "---" + request.getServletPath() + "---" + request.getPathInfo() + "---" + request.getQueryString() + "---");

		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy
		response.setHeader("Content-Security-Policy",
				"default-src 'none'; " +
				"style-src 'self'; " +
				"script-src 'self' 'unsafe-inline'; " + // TODO get rid of unsafe-inline
				"img-src 'self'; " +
				"frame-ancestors 'none'; " +
				"block-all-mixed-content; " +
				"base-uri 'none'");

		// Do not leak information to external servers, not even the (typically private) hostname.
		// We need the referer within the servlet, because typically there is a StrictRefererValidationFilter.
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Referrer-Policy
		response.setHeader("Referrer-Policy", "same-origin");

		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
		response.setHeader("X-Content-Type-Options", "nosniff");

		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options
		response.setHeader("X-Frame-Options", "deny");

		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-XSS-Protection
		response.setHeader("X-XSS-Protection", "1; mode=block");

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
				final HashSet<Integer> doProbeNumbers = new HashSet<>();
				final String[] doProbeNumberStrings = request.getParameterValues(PROBE_NUMBER);
				if(doProbeNumberStrings!=null)
					for(final String doProbeNumberString : doProbeNumberStrings)
						doProbeNumbers.add(Integer.parseInt(doProbeNumberString));

				final Principal principal = request.getUserPrincipal();
				final String authentication = principal!=null ? principal.getName() : null;

				override(
						(Overridable<?>)this,
						getProperties().getSourceObject().reload(),
						authentication,
						sourceMap,
						doProbeNumbers,
						request.getParameter(DRY_RUN)!=null);
			}
		}

		final Properties properties = getProperties();

		Properties reloaded = null;
		RuntimeException reloadFailure = null;
		if(this instanceof Overridable<?>)
		{
			try
			{
				reloaded = ((Overridable<?>)this).newProperties(properties.getSourceObject().reload());
			}
			catch(final RuntimeException e)
			{
				reloadFailure = e;
			}
		}

		final ArrayList<EditedSource> edited = new ArrayList<>();
		for(final Properties.Source s : Sources.decascade(properties.getSourceObject()))
			if(s instanceof EditedSource)
				edited.add((EditedSource)s);

		final Out out = new Out(request);
		Properties_Jspm.write(
				out,
				PropertiesCop.getCop(properties, request),
				request,
				getDisplayCaption(),
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z (z)", UK).format(new Date()),
				properties.getOrphanedKeys(),
				this instanceof Overridable<?>,
				edited,
				properties,
				reloaded,
				reloadFailure);
		out.sendBody(response);
	}

	/**
	 * Helper method for using generics safely without warnings.
	 */
	private static <P extends Properties> void override(
			final Overridable<P> overridable,
			final Properties.Source source,
			final String authentication,
			final HashMap<String, String> sourceMap,
			final HashSet<Integer> doProbeNumbers,
			final boolean dryRun)
	{
		final P properties = overridable.newProperties(
				sourceMap.isEmpty()
				? source
				: Sources.cascade(
						new EditedSource(authentication, sourceMap),
						source)
		);

		int probeNumber = -1;
		for(final Callable<?> probe : properties.getProbes())
		{
			probeNumber++;

			if(doProbeNumbers.contains(probeNumber))
			{
				try
				{
					probe.call();
				}
				catch(final ProbeAbortedException ignored)
				{
				}
				catch(final Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		if(!dryRun)
			overridable.override(properties);
	}

	static final class EditedSource implements Properties.Source
	{
		final String authentication;
		private final HashMap<String, String> content;
		private final long timestamp = System.currentTimeMillis();

		EditedSource(
				final String authentication,
				final HashMap<String, String> content)
		{
			this.authentication = authentication;
			//noinspection AssignmentOrReturnOfFieldWithMutableType OK: no reference is held anywhere else
			this.content = content;
		}

		String timestamp()
		{
			return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", UK).format(new Date(timestamp));
		}

		Map<String,String> content()
		{
			return Collections.unmodifiableMap(content);
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
			bf.append("(Transiently changed ");
			bf.append(timestamp());
			if(authentication!=null)
				bf.append(" by ").append(authentication);
			bf.append(' ');
			bf.append(content);
			bf.append(')');
			return bf.toString();
		}

		@Override
		public Collection<String> keySet()
		{
			return content.keySet();
		}
	}

	/**
	 * @deprecated Override {@link PropertiesInstanceServlet} instead.
	 */
	@Deprecated
	protected abstract Properties getProperties();

	protected String getDisplayCaption()
	{
		return "Properties";
	}

	public interface Overridable<P extends Properties>
	{
		/**
		 * @deprecated Override {@link PropertiesInstanceServlet} instead.
		 */
		@Deprecated
		P newProperties(Properties.Source overrideSource);

		/**
		 * @deprecated Override {@link PropertiesInstanceServlet} instead.
		 */
		@Deprecated
		void override(P properties);
	}
}
