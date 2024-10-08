/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.UK;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serial;
import java.io.StringWriter;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CopsServlet extends HttpServlet
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final LinkedHashMap<String, Resource> resources;
	private final LinkedHashMap<String, Resource> resourcesByName;
	private final String resourcesRootPathSegment;
	private final AtomicLong resources404Count = new AtomicLong();

	protected CopsServlet()
	{
		try
		{
			@SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
			final String resourcesRootPath = getResourcesRootPath();
			if(resourcesRootPath==null)
				throw new NullPointerException    ("getResourcesRootPath must not return null");
			if(resourcesRootPath.isEmpty())
				throw new IllegalArgumentException("getResourcesRootPath must not return empty string");
			if(resourcesRootPath.indexOf('/')>=0)
				throw new IllegalArgumentException("getResourcesRootPath must not return string containing /, but was " + resourcesRootPath);
			this.resourcesRootPathSegment = '/' + resourcesRootPath + '/';

			final LinkedHashMap<String, Resource> resources       = new LinkedHashMap<>();
			final LinkedHashMap<String, Resource> resourcesByName = new LinkedHashMap<>();
			for(Class<?> clazz = getClass(); clazz!=CopsServlet.class; clazz = clazz.getSuperclass())
			{
				for(final java.lang.reflect.Field field : clazz.getDeclaredFields())
				{
					if(field.isSynthetic())
						continue;
					if((field.getModifiers() & (STATIC | FINAL)) != (STATIC | FINAL))
						continue;
					if(!Resource.class.isAssignableFrom(field.getType()))
						continue;

					field.setAccessible(true);
					final Resource resource = (Resource)field.get(null); // always static
					if(resource==null)
						continue;

					resource.init(clazz, resourcesRootPath);
					resourcesByName.put('/'+resource.name     , resource);
					resources      .put('/'+resource.getPath(), resource);
				}
			}
			this.resources       = resources      .isEmpty() ? null : resources;
			this.resourcesByName = resourcesByName.isEmpty() ? null : resourcesByName;
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init(final ServletConfig config) throws ServletException
	{
		super.init(config);
		if (!suppressPathCheck())
		{
			final Map<String, ? extends ServletRegistration> registrations=config.getServletContext().getServletRegistrations();
			for (final Map.Entry<String, ? extends ServletRegistration> entry: registrations.entrySet())
			{
				if (entry.getKey().equals(config.getServletName()))
				{
					for (final String urlPattern: entry.getValue().getMappings())
					{
						if (!urlPattern.endsWith("/*"))
						{
							throw new RuntimeException("CopsServlets must be mounted under path patterns ending with \"/*\". "
								+ "For servlet "+config.getServletName()+", this requirement is not met by pattern "+urlPattern+". "
								+ "Overwrite 'suppressPathCheck()' to disable this check."
							);
						}
					}
				}
			}
		}
	}

	/** Overwrite this method (and return true) if your CopsServlet is not mounted at ".../*" and you know what you're doing. */
	protected boolean suppressPathCheck()
	{
		return false;
	}

	/**
	 * Returns the path all resources are available under.
	 * The default implementation returns "resources".
	 * Must not return null or empty string.
	 */
	protected String getResourcesRootPath()
	{
		return "resources";
	}

	@Override
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		final String pathInfo = request.getPathInfo();

		if(pathInfo==null)
		{
			response.sendRedirect(request.getContextPath() + request.getServletPath() + '/');
			return;
		}

		if(resources!=null)
		{
			if("/copsResourceStatus.html".equals(pathInfo))
			{
				if(!request.isUserInRole("manager"))
				{
					BasicAuthorization.reject(response, "Cops Resource Status");
					return;
				}

				// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy
				response.setHeader("Content-Security-Policy",
						"default-src 'none'; " +
						"style-src 'self' 'unsafe-inline'; " +  // TODO get rid of unsafe-inline
						"script-src 'self' 'unsafe-inline'; " + // TODO get rid of unsafe-inline
						"img-src 'self' " + ResourceStatusCop.EXEDIO_LOGO + "; " + // TODO inlined images with Override Host
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

				response.setContentType("text/html; charset="+UTF_8.name());
				final DecimalFormatSymbols nfs = new DecimalFormatSymbols();
				nfs.setDecimalSeparator(',');
				nfs.setGroupingSeparator('\'');
				final DecimalFormat nf = new DecimalFormat("", nfs);

				final ResourceStatusCop cop = ResourceStatusCop.getCop(request);
				final Out out = new Out(request);
				out.absoluteUrl = cop.absoluteUrl;
				final ServletConfig config = getServletConfig();
				ResourceStatus_Jspm.write(
						out,
						cop,
						config!=null ? config.getServletName() : null, // TODO why can config be null?
						resources.values(),
						resources404Count.get(),
						getAuthentication(request),
						nf,
						CopsServlet.class.getPackage());
				out.sendBody(response);
				return;
			}
			if(pathInfo.startsWith(resourcesRootPathSegment))
			{
				{
					final Resource resource = resources.get(pathInfo);
					if(resource!=null)
					{
						resource.doGet(request, response);
						return;
					}
				}
				final int lastSlash = pathInfo.lastIndexOf('/');
				if(lastSlash>=0)
				{
					final Resource resource = resourcesByName.get(pathInfo.substring(lastSlash));
					if(resource!=null)
					{
						resource.doRedirect(request, response, false);
						return;
					}
				}
				response.setStatus(SC_NOT_FOUND);
				resources404Count.incrementAndGet();
				return;
			}
			{
				final Resource resource = resourcesByName.get(pathInfo);
				if(resource!=null)
				{
					resource.doRedirect(request, response, true);
					return;
				}
			}
		}

		doRequestPrivate(request, response);
	}

	@Override
	protected final void doPost(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		if(resources!=null && "/copsResourceStatus.html".equals(request.getPathInfo()))
		{
			if(!request.isUserInRole("manager"))
			{
				BasicAuthorization.reject(response, "Cops Resource Status");
				return;
			}

			final ResourceStatusCop cop = ResourceStatusCop.getCop(request);
			ResourceStatusCop.post(request, resources.values());
			response.sendRedirect(cop.getURL(request));
		}

		doRequestPrivate(request, response);
	}

	private void doRequestPrivate(final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding(UTF_8.name());
		response.setContentType("text/html; charset="+UTF_8.name());

		response.addHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", System.currentTimeMillis());

		doRequest(request, response);
	}

	@SuppressWarnings("RedundantThrows") // ServletException may be needed by subclasses
	protected abstract void doRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException;


	public static final String getAuthentication(final HttpServletRequest request)
	{
		final Principal principal = request.getUserPrincipal();
		return principal!=null ? principal.getName() : null;
	}

	@SuppressWarnings({"static-method", "MethodMayBeStatic"})
	public final void printException(final PrintStream out, final Exception exception)
	{
		exception.printStackTrace(out);
		if(exception instanceof ServletException)
		{
			final Throwable rootCause =
				((ServletException)exception).getRootCause();
			if(rootCause!=null)
			{
				out.println("root cause for ServletException:");
				rootCause.printStackTrace(out);
			}
			else
			{
				out.println("no root cause for ServletException");
			}
		}
		out.flush();
	}

	private static void printStackTrace(final Throwable exception, final StringBuilder out)
	{
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.flush();
		out.append(sw.getBuffer());
	}

	@SuppressWarnings({"static-method", "MethodMayBeStatic"})
	public final void printException(final StringBuilder out, final Exception exception)
	{
		printStackTrace(exception, out);
		if(exception instanceof ServletException)
		{
			final Throwable rootCause =
				((ServletException)exception).getRootCause();
			if(rootCause!=null)
			{
				out.append("root cause for ServletException:");
				printStackTrace(rootCause, out);
			}
			else
			{
				out.append("no root cause for ServletException");
			}
		}
	}

	private final Random random = new Random();

	/**
	 * Returns the id under with the exception has been reported in the log.
	 */
	public final String reportException(final HttpServletRequest request, final Exception exception)
	{
		final long idLong;
		synchronized(random)
		{
			idLong = NonNegativeRandom.nextLong(random);
		}
		final String id = String.valueOf(Math.abs(idLong));
		System.out.println("--------"+id+"-----");
		System.out.println("Date: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z (z)", UK).format(new Date()));
		if(request!=null)
			System.out.print(report(request));
		printException(System.out, exception);
		System.out.println("-------/"+id+"-----");
		return id;
	}

	@SuppressWarnings("HardcodedLineSeparator")
	public static final String report(final HttpServletRequest request)
	{
		final StringBuilder bf = new StringBuilder();

		bf.append("remoteAddr: >").append(request.getRemoteAddr()).append("<\n");
		bf.append("protocol: >").append(request.getProtocol()).append("<\n");
		bf.append("method: >").append(request.getMethod()).append("<\n");
		bf.append("remoteUser: >").append(request.getRemoteUser()).append("<\n");
		bf.append("requestURI: >").append(request.getRequestURI()).append("<\n");
		bf.append("contextPath: >").append(request.getContextPath()).append("<\n");
		bf.append("servletPath: >").append(request.getServletPath()).append("<\n");
		bf.append("pathInfo: >").append(request.getPathInfo()).append("<\n");
		bf.append("queryString: >").append(request.getQueryString()).append("<\n");
		bf.append("scheme: >").append(request.getScheme()).append("<\n");
		bf.append("secure: >").append(request.isSecure()).append("<\n");
		bf.append("userPrincipal: >").append(request.getUserPrincipal()).append("<\n");

		for(final Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements(); )
		{
			final String name = (String)e.nextElement();
			for(final Enumeration<?> ev = request.getHeaders(name); ev.hasMoreElements(); )
				bf.append("header >").append(name).append("<: >").append(((String)ev.nextElement())).append("<\n");
		}

		final String PARAMETER_PASSWORD = "(.*password)";
		for(final Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); )
		{
			final String name = (String)e.nextElement();
			for(String value : request.getParameterValues(name))
			{
				if(name.matches(PARAMETER_PASSWORD))
				{
					// mask password values
					//noinspection AssignmentToForLoopParameter
					value = "*****";
				}
				bf.append("parameter >").append(name).append("<: >").append(value).append("<\n");
			}
		}

		return bf.toString();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #reportException(HttpServletRequest, Exception)} instead.
	 */
	@Deprecated
	public final String reportException(final Exception exception)
	{
		return reportException(null, exception);
	}

	/**
	 * @deprecated Use {@link com.exedio.cope.util.CharsetName#UTF8} instead
	 */
	@Deprecated
	public static final String ENCODING = com.exedio.cope.util.CharsetName.UTF8;

	/**
	 * @deprecated Use {@link com.exedio.cope.util.CharsetName#UTF8} instead
	 */
	@Deprecated
	public static final String UTF8 = com.exedio.cope.util.CharsetName.UTF8;
}
