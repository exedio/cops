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

package com.exedio.cops.webtest;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class ResourceTest extends AbstractWebTest
{
	private String schemeAndHost;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		schemeAndHost = "http://localhost:" + System.getProperty("tomcat.port.http");
	}

	public void testError() throws Exception
	{
		final String prefix = "/cops/";
		final String textRelative = prefix + "resources/9e4cd71daa5a10b9dde41b944e0f185c/resource-test.txt";
		final URL text = new URL(schemeAndHost + textRelative);

		final long textLastModified = assertURL(text);
		assertEquals(textLastModified, assertURL(text));
		assertEquals(textLastModified, assertURL(text, textLastModified-1, false));
		assertEquals(textLastModified, assertURL(text, textLastModified, true));
		assertEquals(textLastModified, assertURL(text, textLastModified+5000, true));

		assertMoved   (prefix + "resources/X/resource-test.txt", textRelative);
		assertMoved   (prefix + "resources//resource-test.txt" , textRelative);
		assertMoved   (prefix + "resources/resource-test.txt"  , textRelative);
		assertMoved   (prefix + "resource-test.txt"            , textRelative);
		assertNotFound(prefix + "resources/X/Xresource-test.txt");
		assertNotFound(textRelative + "?param=value");
	}

	private static long assertURL(final URL url) throws IOException
	{
		return assertURL(url, -1, false);
	}

	private static long assertURL(final URL url, final long ifModifiedSince, final boolean expectNotModified) throws IOException
	{
		return assertURL(url, "text/plain", ifModifiedSince, expectNotModified);
	}

	private static long assertURL(final URL url, final String contentType, final long ifModifiedSince, final boolean expectNotModified) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		if(ifModifiedSince>=0)
			conn.setIfModifiedSince(ifModifiedSince);
		conn.connect();
		assertEquals(expectNotModified ? HttpURLConnection.HTTP_NOT_MODIFIED : HttpURLConnection.HTTP_OK, conn.getResponseCode());
		assertEquals(expectNotModified ? "Not Modified" : "OK", conn.getResponseMessage());
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		final long lastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(lastModified));
		assertTrue("This sometimes fails because the request takes too long or so.", (date+1000)>=lastModified); // TODO
		assertEquals(expectNotModified ? null : contentType, conn.getContentType());
		//System.out.println("Expires: "+new Date(conn.getExpiration()));
		assertEquals(0, conn.getExpiration());
		assertEquals("max-age=31363200", conn.getHeaderField("Cache-Control"));
		assertEquals(expectNotModified ? -1 : (41 + (2*System.getProperty("line.separator").length())), conn.getContentLength());

		try(BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream())))
		{
			if(!expectNotModified)
			{
				assertEquals("This is the test file", is.readLine());
				assertEquals("for the ResourceTest", is.readLine());
				assertEquals(null, is.readLine());
			}
		}

		return lastModified;
	}

	private void assertMoved(final String url, final String target) throws IOException
	{
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.connect();
		assertEquals(HTTP_MOVED_PERM, conn.getResponseCode());
		assertEquals("Moved Permanently", conn.getResponseMessage());
		assertEquals(target, conn.getHeaderField("Location"));
		assertEquals(null, conn.getContentType());
		assertEquals(0, conn.getContentLength());
	}

	private void assertNotFound(final String url) throws IOException
	{
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.connect();
		assertEquals(HTTP_NOT_FOUND, conn.getResponseCode());
		assertEquals("Not Found", conn.getResponseMessage());
	}
}
