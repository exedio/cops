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

package com.exedio.cops.example;

import com.exedio.cops.Cop;
import com.exedio.cops.Resource;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
public final class ExampleServlet extends ExampleSuperServlet
{
	@Serial
	private static final long serialVersionUID = 1l;

	static final Resource logo = new Resource("logo.png");
	@SuppressWarnings("unused")
	static final Resource someClass = new Resource("ExampleServlet.class", "application/octet-steam");

	// For ResourceTest.
	static final Resource test = new Resource("resource-test.txt");

	static final String START_SESSION = "startsession";
	static final String REPORT_EXCEPTION = "reportexception";

	private final ArrayList<String> searchSet = new ArrayList<>();

	public ExampleServlet()
	{
		for(int i = 1; i<=84; i++)
			searchSet.add("paged " + i);
	}

	@Override
	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		//System.out.println("request ---" + request.getMethod() + "---" + request.getContextPath() + "---" + request.getServletPath() + "---" + request.getPathInfo() + "---" + request.getQueryString() + "---");

		final ExampleCop cop = ExampleCop.getCop(request);
		if(cop.redirectToCanonical(request, response))
			return;

		String reportedException = null;
		if(Cop.isPost(request))
		{
			if(request.getParameter(START_SESSION)!=null)
			{
				request.getSession();
				response.sendRedirect(response.encodeRedirectURL(cop.getURL(request)));
				return;
			}
			else if(request.getParameter(REPORT_EXCEPTION)!=null)
			{
				reportedException = reportException(request, new NullPointerException("example exception for CopsServlet.reportException"));
			}
		}

		final Out out = new Out(request, response);
		Example_Jspm.write(out, cop, request, cop.pager.init(searchSet), reportedException);
		out.sendBody();
	}
}
