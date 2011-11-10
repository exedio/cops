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

import static com.exedio.cops.CopsServlet.UTF8;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.util.XMLEncoder;

final class Out
{
	private StringBuilder bf = new StringBuilder();
	private final HttpServletRequest request;
	boolean absoluteUrl = false;

	private final long now = System.currentTimeMillis();
	private final SimpleDateFormat dateFormatFull  = new SimpleDateFormat("yyyy/MM/dd'&nbsp;'HH:mm:ss'<small>'.SSS'</small>'");
	private final SimpleDateFormat dateFormatYear  = new SimpleDateFormat(     "MM/dd'&nbsp;'HH:mm:ss'<small>'.SSS'</small>'");
	private final SimpleDateFormat dateFormatToday = new SimpleDateFormat(                  "HH:mm:ss'<small>'.SSS'</small>'");

	Out(final HttpServletRequest request)
	{
		assert request !=null;
		this.request = request;
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

	void write(final int i)
	{
		bf.append(i);
	}

	void writeNow()
	{
		bf.append(dateFormatFull.format(new Date(now)));
	}

	void write(final Date d)
	{
		if(d==null)
			return;

		final long millis = d.getTime();
		final SimpleDateFormat df;
		if( (now-deltaToday) < millis && millis < (now+deltaToday) )
			df = dateFormatToday;
		else if( (now-deltaYear) < millis && millis < (now+deltaYear) )
			df = dateFormatYear;
		else
			df = dateFormatFull;

		bf.append(df.format(d));
	}

	private static final long deltaYear  = 1000l * 60 * 60 * 24 * 90; // 90 days
	private static final long deltaToday = 1000l * 60 * 60 * 6; // 6 hours


	void write(final Resource resource)
	{
		bf.append(absoluteUrl ? resource.getAbsoluteURL(request) : resource.getURL(request));
	}

	void write(final Cop cop)
	{
		bf.append(cop.getURL(request));
	}

	void sendBody(final HttpServletResponse response) throws IOException
	{
		final StringBuilder bf = this.bf;
		if(bf==null)
			throw new IllegalStateException();
		this.bf = null; // prevent this instance to be used anymore

		BodySender.send(response, bf, UTF8);
	}
}
