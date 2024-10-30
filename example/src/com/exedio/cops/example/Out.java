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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.util.XMLEncoder;
import com.exedio.cops.BodySender;
import com.exedio.cops.Cop;
import com.exedio.cops.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	void write(final char c)
	{
		XMLEncoder.append(bf, c);
	}

	void writeStatic(final String s)
	{
		bf.append(s);
	}

	void write(final String s)
	{
		if(s==null)
			bf.append("null");
		else
			XMLEncoder.append(bf, s);
	}

	void write(final boolean b)
	{
		bf.append(b);
	}

	void write(final int i)
	{
		bf.append(i);
	}

	void write(final Charset cs)
	{
		bf.append(cs.name());
	}

	void write(final Resource resource)
	{
		bf.append(resource.getURL(request));
	}

	void writeAbsolute(final Resource resource)
	{
		bf.append(resource.getAbsoluteURL(request));
	}

	void write(final Cop cop)
	{
		bf.append(XMLEncoder.encode(response.encodeURL(cop.getURL(request))));
	}

	void writeAbsolute(final Cop cop)
	{
		bf.append(XMLEncoder.encode(response.encodeURL(cop.getAbsoluteURL(request))));
	}

	void sendBody() throws IOException
	{
		final StringBuilder bf = this.bf;
		if(bf==null)
			throw new IllegalStateException();
		this.bf = null; // prevent this instance to be used anymore

		BodySender.send(response, bf, UTF_8);
	}
}
