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
	
	protected final void addParameter(final String key, final boolean value)
	{
		if(value)
			addParameter(key, "t");
	}
	
	protected final void addParameter(final String key, final int value, final int defaultValue)
	{
		if(value==defaultValue)
			return;
		
		addParameter(key, String.valueOf(value));
	}
	
	protected final void addParameter(final String key, final long value, final long defaultValue)
	{
		if(value==defaultValue)
			return;
		
		addParameter(key, String.valueOf(value));
	}
	
	private static final int COMPACT_LONG_RADIX = Character.MAX_RADIX;
	
	protected final void addParameterCompact(final String key, final long value, final long defaultValue)
	{
		if(value==defaultValue)
			return;
		
		addParameter(key, Long.toString(value, COMPACT_LONG_RADIX));
	}
	
	/**
	 * Does nothing, if <tt>value==null</tt>.
	 */
	protected final void addParameter(final String key, final String value)
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
	 * Return TRUE to use https.
	 * Return FALSE to use http.
	 * Return null to use any of these,
	 * which uses the previously used protocol.
	 * This default implementation returns null.
	 */
	protected Boolean needsSecure()
	{
		return null;
	}
	
	public final String toAbsolute()
	{
		final String url = this.url!=null ? this.url.toString() : pathInfo;
		
		final HttpServletResponse response = CopsServlet.responses.get();
		if(response==null)
			throw new IllegalStateException("no response available");
		if(response instanceof EnvironmentResponse)
		{
			final HttpServletRequest request = CopsServlet.requests.get();
			if(request==null)
				throw new IllegalStateException("no request available");
			return ((EnvironmentRequest)request).getURL(needsSecure(), url);
		}
		final String encodedURL = response.encodeURL(url);
		
		final HttpServletRequest request = CopsServlet.requests.get();
		if(request==null)
			throw new IllegalStateException("no request available");
		return
			request.getScheme() + "://" +
			request.getHeader("Host") +
			request.getContextPath() +
			request.getServletPath() +
			'/' + encodedURL;
	}
	
	@Override
	public final String toString()
	{
		return toStringNonEncoded();
	}
	
	/**
	 * Use this method for instance with {@link HttpServletResponse#sendRedirect(String)}.
	 */
	public final String toStringNonEncoded()
	{
		final String url = this.url!=null ? this.url.toString() : pathInfo;
		
		final HttpServletRequest  request  = CopsServlet.requests.get();
		final HttpServletResponse response = CopsServlet.responses.get();
		if(request==null)
			throw new IllegalStateException("no request available");
		if(response==null)
			throw new IllegalStateException("no response available");
		
		if(request instanceof EnvironmentRequest)
			return ((EnvironmentRequest)request).getURL(needsSecure(), url);
		
		final String encodedURL = request.getContextPath() + request.getServletPath() + '/' + response.encodeURL(url);
		
		final Boolean needsSecure = needsSecure();
		if(needsSecure==null)
			return encodedURL;
		
		final boolean isSecure = request.isSecure();
		if(needsSecure.booleanValue()==isSecure)
			return encodedURL;
		
		String host = request.getHeader("Host");
		if(!isSecure)
		{
			if(host.endsWith(":8080"))
				host = host.substring(0, host.length()-4) + "8443";
		}
		else
		{
			if(host.endsWith(":8443"))
				host = host.substring(0, host.length()-4) + "8080";
		}
		
		return
			(needsSecure?"https://":"http://") +
			host +
			encodedURL;
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
	
	public final boolean redirectToCanonical()
	{
		final HttpServletRequest request = CopsServlet.requests.get();
		if(request==null)
			throw new IllegalStateException("no request available");
		if(request instanceof EnvironmentRequest)
			throw new RuntimeException("redirectToCanonical not implemented for setEnvironment");
		if(!"GET".equals(request.getMethod()))
			return false;
		
		final String expected = request.getContextPath() + request.getServletPath() + '/' + (this.url!=null ? this.url.toString() : pathInfo);
		final String actualRequestURI = request.getRequestURI();
		final String actualQueryString = request.getQueryString();
		final String actual = actualQueryString!=null ? (actualRequestURI + '?' + actualQueryString) : actualRequestURI;
		if(expected.equals(actual))
		{
			final Boolean needsSecure = needsSecure();
			if(needsSecure==null)
				return false;
			
			final boolean isSecure = request.isSecure();
			if(needsSecure.booleanValue()==isSecure)
				return false;
		}
		
		final String location = toStringNonEncoded();
		System.out.println("cops redirectToCanonical from --" + actual + "-- to --" + location + "--");
		
		final HttpServletResponse response = CopsServlet.responses.get();
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", location);
		
		return true;
	}
	
	public static String getEnvironment()
	{
		final HttpServletRequest request  = CopsServlet.requests.get();
		
		if(request==null)
			throw new IllegalStateException("no request available");
		
		if(request instanceof EnvironmentRequest)
			return ((EnvironmentRequest)request).environment;
		
		return
			request.getHeader("Host") +
			request.getContextPath() +
			request.getServletPath();
	}
	
	public static void setEnvironment(final String environment)
	{
		if(CopsServlet.requests.get()!=null)
			throw new IllegalStateException("environment already available");
		assert CopsServlet.responses.get()==null;
		
		CopsServlet.requests.set(new EnvironmentRequest(environment));
		CopsServlet.responses.set(new EnvironmentResponse());
	}
	
	public static void removeEnvironment()
	{
		CopsServlet.requests.remove();
		CopsServlet.responses.remove();
	}
	
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
	 * @deprecated use {@link #toAbsolute()} instead.
	 */
	@Deprecated
	public final String toAbsolute(final HttpServletRequest request)
	{
		assert request==CopsServlet.requests.get();
		return toAbsolute();
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
