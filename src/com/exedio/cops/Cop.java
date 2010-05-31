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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Cop
{
	private final String pathInfo;
	private StringBuilder url = null;
	
	public Cop(final String pathInfo)
	{
		for(final char c : FORBIDDEN_IN_PATH_INFO)
			if(pathInfo.indexOf(c)>=0)
				throw new IllegalArgumentException("cop pathInfo \"" + pathInfo + "\" must not contain character " + c);

		this.pathInfo = pathInfo;
	}
	
	private static final char[] FORBIDDEN_IN_PATH_INFO = new char[] {'?', '&', ';'};
	
	public final void addParameter(final String key, final boolean value)
	{
		if(value)
			addParameter(key, "t");
	}
	
	public final void addParameter(final String key, final int value, final int defaultValue)
	{
		if(value==defaultValue)
			return;
		
		addParameter(key, String.valueOf(value));
	}
	
	public final void addParameter(final String key, final long value, final long defaultValue)
	{
		if(value==defaultValue)
			return;
		
		addParameter(key, String.valueOf(value));
	}
	
	private static final int COMPACT_LONG_RADIX = Character.MAX_RADIX;
	
	public final void addParameterCompact(final String key, final long value, final long defaultValue)
	{
		if(value==defaultValue)
			return;
		
		addParameter(key, Long.toString(value, COMPACT_LONG_RADIX));
	}
	
	public final void addParameter(final String key, final Cop value)
	{
		if(value==null)
			return;
		
		addParameter(key, value.toString());
	}
	
	/**
	 * Does nothing, if <tt>value==null</tt>.
	 */
	public final void addParameter(final String key, final String value)
	{
		if(value==null)
			return;
		
		if(url==null)
		{
			url = new StringBuilder(pathInfo);
			url.append('?');
		}
		else
			url.append('&');
			
		url.append(key);
		url.append('=');
		try
		{
			url.append(URLEncoder.encode(value, CopsServlet.UTF8));
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Specifies, whether this cop should use http or https.
	 * Return true to use https.
	 * Return false to use any of these,
	 * which uses the previously used protocol.
	 * This default implementation returns null.
	 */
	protected boolean needsSecure()
	{
		return false;
	}
	
	private final boolean doesNotNeedSecureRedirect(final HttpServletRequest request)
	{
		return !needsSecure() || request.isSecure();
	}
	
	private static final String HOST = "Host";
	
	public final String getAbsoluteURL(final HttpServletRequest request)
	{
		final String url = this.url!=null ? this.url.toString() : pathInfo;
		
		if(request==null)
			throw new NullPointerException("request");
		
		return
			request.getScheme() + "://" +
			request.getHeader(HOST) +
			request.getContextPath() +
			request.getServletPath() +
			'/' + url;
	}
	
	/**
	 * @see #getAbsoluteURL(String)
	 */
	public static String getToken(final HttpServletRequest request)
	{
		if(request==null)
			throw new NullPointerException("request");
		
		return
			request.getHeader(HOST) +
			request.getContextPath() +
			request.getServletPath();
	}
	
	/**
	 * @see #getToken(HttpServletRequest)
	 */
	public final String getAbsoluteURL(final String token)
	{
		final String url = this.url!=null ? this.url.toString() : pathInfo;
		
		if(token==null)
			throw new NullPointerException("token");
		return EnvironmentRequest.getURL(token, needsSecure(), url);
	}
	
	public final String getURL(final HttpServletRequest request)
	{
		final String url = this.url!=null ? this.url.toString() : pathInfo;
		
		if(request==null)
			throw new NullPointerException("request");
		
		final String fullURL = request.getContextPath() + request.getServletPath() + '/' + url;
		
		if(doesNotNeedSecureRedirect(request))
			return fullURL;
		
		String host = request.getHeader(HOST);
		if(host.endsWith(":8080"))
			host = host.substring(0, host.length()-4) + "8443";
		
		return
			"https://" +
			host +
			fullURL;
	}
	
	@Override
	public final String toString()
	{
		return url!=null ? url.toString() : pathInfo;
	}
	
	private static final char NATURAL_PLACE_HOLDER = '-';
	
	public static final String encodeNaturalLanguageSegment(final String s)
	{
		if(s==null)
			return null;
		
		final int l = s.length();
		for(int i = 0; i<l; i++)
		{
			final char c = s.charAt(i);
			if(!(('0'<=c&&c<='9')||('a'<=c&&c<='z')||('A'<=c&&c<='Z')))
			{
				final StringBuilder bf = new StringBuilder(l);
				if(i>0)
					bf.append(s.substring(0, i));
				boolean skipped = false;
				for(; i<l; i++)
				{
					final char c2 = s.charAt(i);
					if(('0'<=c2&&c2<='9')||('a'<=c2&&c2<='z')||('A'<=c2&&c2<='Z'))
					{
						if(skipped)
						{
							bf.append(NATURAL_PLACE_HOLDER);
							skipped = false;
						}
						bf.append(c2);
					}
					else
					{
						skipped = true;
					}
				}
				if(bf.length()==1 && bf.charAt(0)==NATURAL_PLACE_HOLDER)
					return "";
				return bf.toString();
			}
		}
		return s;
	}
	
	public final boolean redirectToCanonical(final HttpServletRequest request, final HttpServletResponse response)
	{
		if(request==null)
			throw new NullPointerException();
		if(!"GET".equals(request.getMethod()))
			return false;
		
		final String expected = request.getContextPath() + request.getServletPath() + '/' + (this.url!=null ? this.url.toString() : pathInfo);
		final String actualRequestURI = request.getRequestURI();
		final String actualQueryString = request.getQueryString();
		final String actual = actualQueryString!=null ? (actualRequestURI + '?' + actualQueryString) : actualRequestURI;
		if(expected.equals(actual) && doesNotNeedSecureRedirect(request))
			return false;
		
		final String location = response.encodeRedirectURL(getURL(request));
		System.out.println("cops redirectToCanonical from --" + actual + "-- to --" + location + "--");
		
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", location);
		
		return true;
	}
	
	// ------------------- static helpers -------------------
	
	public static final boolean isPost(final HttpServletRequest request)
	{
		return "POST".equals(request.getMethod());
	}
	
	public static final boolean getBooleanParameter(
			final HttpServletRequest request,
			final String name)
	{
		return request.getParameter(name)!=null;
	}
	
	public static final int getIntParameter(
			final HttpServletRequest request,
			final String name,
			final int defaultValue)
	{
		final String value = request.getParameter(name);
		return (value==null) ? defaultValue : Integer.parseInt(value);
	}
	
	public static final long getLongParameter(
			final HttpServletRequest request,
			final String name,
			final long defaultValue)
	{
		final String value = request.getParameter(name);
		return (value==null) ? defaultValue : Long.parseLong(value);
	}
	
	public static final long getLongParameterCompact(
			final HttpServletRequest request,
			final String name,
			final long defaultValue)
	{
		final String value = request.getParameter(name);
		return (value==null) ? defaultValue : Long.parseLong(value, COMPACT_LONG_RADIX);
	}
	
	public static final HttpServletRequest getCopParameter(
			final HttpServletRequest request,
			final String name)
	{
		final String value = request.getParameter(name);
		return (value==null) ? null : new CopParameterRequest(request, value);
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link XMLEncoder#encode(String)} instead
	 */
	@Deprecated
	public static final String encode(final String st)
	{
		return XMLEncoder.encode(st);
	}
	
	/**
	 * @deprecated Use {@link XMLEncoder#encode(String)} instead
	 */
	@Deprecated
	public static final String encodeXml(final String st)
	{
		return XMLEncoder.encode(st);
	}
	
	/**
	 * @deprecated Use {@link BasicAuthorization#getUserAndPassword(HttpServletRequest)} instead
	 */
	@Deprecated
	public static final String[] authorizeBasic(final HttpServletRequest request)
	{
		return BasicAuthorization.getUserAndPassword(request);
	}
	
	/**
	 * @deprecated Use {@link BasicAuthorization#reject(HttpServletResponse, String)} instead
	 */
	@Deprecated
	public static final void rejectAuthorizeBasic(final HttpServletResponse response, final String realm)
	{
		BasicAuthorization.reject(response, realm);
	}
}
