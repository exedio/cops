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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cops.BodySender;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.Resource;
import com.exedio.cops.XMLEncoder;

final class Out
{
	private StringBuilder bf = new StringBuilder();
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	
	Out(final HttpServletRequest request, final HttpServletResponse response)
	{
		assert request !=null;
		assert response!=null;
		
		this.request  = request;
		this.response = response;
	}
	
	void append(final char c)
	{
		bf.append(c);
	}
	
	void append(final String s)
	{
		bf.append(s);
	}
	
	void append(final int i)
	{
		bf.append(i);
	}
	
	void append(final Resource resource)
	{
		bf.append(resource.getURL(request));
	}

	void appendAbsolute(final Resource resource)
	{
		bf.append(resource.getAbsoluteURL(request));
	}
	
	void append(final Cop cop)
	{
		bf.append(XMLEncoder.encode(response.encodeURL(cop.getURL(request))));
	}

	void appendAbsolute(final Cop cop)
	{
		bf.append(XMLEncoder.encode(response.encodeURL(cop.getAbsoluteURL(request))));
	}
	
	void writeBody() throws IOException
	{
		final StringBuilder bf = this.bf;
		if(bf==null)
			throw new IllegalStateException();
		this.bf = null; // prevent this instance to be used anymore
		
		BodySender.send(response, bf, CopsServlet.UTF8);
	}
}
