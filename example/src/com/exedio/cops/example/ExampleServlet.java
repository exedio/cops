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

package com.exedio.cops.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.Pager;
import com.exedio.cops.Resource;

public final class ExampleServlet extends CopsServlet
{
	private static final long serialVersionUID = 1l;

	static final Resource logo = new Resource("logo.png");
	static final Resource someClass = new Resource("ExampleServlet.class", "application/octet-steam");
	
	static final String START_SESSION = "startsession";
	
	private final ArrayList<String> searchSet = new ArrayList<String>();
	
	public ExampleServlet()
	{
		for(int i = 1; i<=84; i++)
			searchSet.add("paged " + i);
	}

	private static final void writeBody(
			final StringBuilder out,
			final HttpServletResponse response)
		throws IOException
	{
		ServletOutputStream outStream = null;
		try
		{
			outStream = response.getOutputStream();
			final byte[] outBytes = out.toString().getBytes(ENCODING);
			outStream.write(outBytes);
		}
		finally
		{
			if(outStream!=null)
				outStream.close();
		}
	}
	
	private static final <E> List<E> subList(final List<E> list, final int fromIndex, final int toIndex)
	{
		final int size = list.size();
		return list.subList(fromIndex, (toIndex>size) ? size : toIndex);
	}
	
	@Override
	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		//System.out.println("request ---" + request.getMethod() + "---" + request.getContextPath() + "---" + request.getServletPath() + "---" + request.getPathInfo() + "---" + request.getQueryString() + "---");
		
		final NumberCop cop = NumberCop.getCop(request);
		
		final Pager pager = cop.pager;
		final List<String> searchResult = subList(searchSet, pager.getOffset(), pager.getOffset()+pager.getLimit());
		pager.init(searchResult.size(), searchSet.size());
		
		if(Cop.isPost(request))
		{
			if(request.getParameter(START_SESSION)!=null)
			{
				request.getSession();
				response.sendRedirect(cop.toStringNonEncoded());
				return;
			}
		}

		final StringBuilder out = new StringBuilder();
		Example_Jspm.write(out, cop, searchResult);
		writeBody(out, response);
	}
}
