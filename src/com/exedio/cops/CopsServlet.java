/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CopsServlet extends HttpServlet
{
	static final ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();
	static final ThreadLocal<HttpServletResponse> responses = new ThreadLocal<HttpServletResponse>();
	
	public static final String ENCODING = "utf-8";
	
	private final LinkedHashMap<String, Resource> resources;
	
	protected CopsServlet()
	{
		try
		{
			final LinkedHashMap<String, Resource> resources = new LinkedHashMap<String, Resource>();
			final Class<?> clazz = getClass(); // TODO go to super class as well until CopsServlet
			for(final java.lang.reflect.Field field : clazz.getDeclaredFields())
			{
				if((field.getModifiers() & (STATIC | FINAL)) != (STATIC | FINAL))
					continue;
				if(!Resource.class.isAssignableFrom(field.getType()))
					continue;

				field.setAccessible(true);
				final Resource resource = (Resource)field.get(null); // always static
				if(resource==null)
					continue;
				
				resource.init(clazz);
				resources.put('/'+resource.name, resource);
			}
			this.resources = resources.isEmpty() ? null : resources;
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	static final String INLINE = "inline";

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
				
				response.setContentType("text/html; charset="+ENCODING);
				final Principal principal = request.getUserPrincipal();
				final String authentication = principal!=null ? principal.getName() : null;
				final DecimalFormatSymbols nfs = new DecimalFormatSymbols();
				nfs.setDecimalSeparator(',');
				nfs.setGroupingSeparator('\'');
				final DecimalFormat nf = new DecimalFormat("", nfs);
				final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
				try
				{
					assert requests.get()==null;
					requests.set(request);
					final ServletConfig config = getServletConfig();
					ResourceStatus_Jspm.write(
							out,
							config!=null ? config.getServletName() : null, // TODO why can config be null?
							resources.values(),
							authentication,
							request.getParameter(INLINE)!=null,
							nf,
							new SimpleDateFormat("yyyy/MM/dd'&nbsp;'HH:mm:ss'<small>'.S'</small>'"),
							new SimpleDateFormat("yyyy/MM/dd'&nbsp;'HH:mm:ss.SSS Z (z)"),
							CopsServlet.class.getPackage());
				}
				finally
				{
					requests.remove();
				}
				out.close();
				return;
			}
			final Resource resource = resources.get(pathInfo);
			if(resource!=null)
			{
				resource.doGet(request, response);
				return;
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
		doRequestPrivate(request, response);
	}
	
	private final void doRequestPrivate(final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding(ENCODING);
		response.setContentType("text/html; charset="+ENCODING);

		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Cache-Control", "max-age=0");
		response.addHeader("Cache-Control", "must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", System.currentTimeMillis());
		
		try
		{
			requests.set(request);
			responses.set(response);
			doRequest(request, response);
		}
		finally
		{
			requests.remove();
			responses.remove();
		}
	}

	protected abstract void doRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException;


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
	
	private static final void printStackTrace(final Throwable exception, final StringBuilder out)
	{
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.flush();
		out.append(sw.getBuffer());
	}
	
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
			idLong = random.nextLong();
		}
		final String id = String.valueOf(Math.abs(idLong));
		System.out.println("--------"+id+"-----");
		System.out.println("Date: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z (z)").format(new Date()));
		if(request!=null)
			System.out.print(report(request));
		printException(System.out, exception);
		System.out.println("-------/"+id+"-----");
		return id;
	}
	
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
		
		for(final Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); )
		{
			final String name = (String)e.nextElement();
			for(final String value : request.getParameterValues(name))
				bf.append("parameter >").append(name).append("<: >").append(value + "<\n");
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
}
