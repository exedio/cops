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

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CopsServlet extends HttpServlet
{
	static final ThreadLocal<HttpServletResponse> responses = new ThreadLocal<HttpServletResponse>();
	
	public static final String ENCODING = "utf-8";
	
	private final HashMap<String, Resource> resources = new HashMap<String, Resource>();
	
	protected CopsServlet()
	{
		try
		{
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
				this.resources.put('/'+resource.name, resource);
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
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
		
		final Resource resource = resources.get(pathInfo);
		if(resource!=null)
		{
			resource.doGet(request, response);
			return;
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
			responses.set(response);
			doRequest(request, response);
		}
		finally
		{
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
	
	private final Random random = new Random();

	/**
	 * Returns the id under with the exception has been reported in the log.
	 */
	public final String reportException(final Exception exception)
	{
		final long idLong;
		synchronized(random)
		{
			idLong = random.nextLong();
		}
		final String id = String.valueOf(Math.abs(idLong));
		System.out.println("--------"+id+"-----");
		printException(System.out, exception);
		System.out.println("-------/"+id+"-----");
		return id;
	}
}
