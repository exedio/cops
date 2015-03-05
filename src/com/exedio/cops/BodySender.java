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

import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class BodySender
{
	public static final void send(
			final HttpServletResponse response,
			final StringBuilder body,
			final Charset charset)
	throws IOException
	{
		send(response, body.toString().getBytes(charset));
	}

	/**
	 * @deprecated Use {@link #send(HttpServletResponse,StringBuilder,String)} instead
	 */
	@Deprecated
	public static final void send(
			final HttpServletResponse response,
			final StringBuilder body,
			final String encoding)
	throws IOException
	{
		send(response, body, Charset.forName(encoding));
	}

	public static final void send(
			final HttpServletResponse response,
			final byte[] body)
	throws IOException
	{
		response.setContentLength(body.length); // avoid chunked transfer

		try(final ServletOutputStream stream = response.getOutputStream())
		{
			stream.write(body);
		}
	}
}
