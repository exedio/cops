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

package com.exedio.cops;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Cop
{
	private final String name;
	private StringBuilder url = null;
	
	public Cop(final String name)
	{
		for(final char c : FORBIDDEN_IN_NAME)
			if(name.indexOf(c)>=0)
				throw new IllegalArgumentException("cop name \"" + name + "\" must not contain character " + c);

		this.name = name;
	}
	
	private static final char[] FORBIDDEN_IN_NAME = new char[] {'?', '&', ';'};
	
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
			url = new StringBuilder(name);
			url.append('?');
		}
		else
			url.append('&');
			
		url.append(key);
		url.append('=');
		try
		{
			url.append(URLEncoder.encode(value, CopsServlet.ENCODING));
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
		final String url = this.url!=null ? this.url.toString() : name;
		
		final HttpServletResponse response = CopsServlet.responses.get();
		if(response==null)
			throw new IllegalStateException("no response available");
		if(response instanceof EnvironmentResponse)
		{
			final HttpServletRequest request = CopsServlet.requests.get();
			if(request==null)
				throw new IllegalStateException("no request available");
			final Boolean needsSecure = needsSecure();
			final boolean secure = needsSecure!=null && needsSecure.booleanValue();
			
			String environment = ((EnvironmentRequest)request).environment;
			if(secure)
			{
				final int pos = environment.indexOf(":8080/");
				if(pos>0)
					environment = environment.substring(0, pos) + ":8443" + environment.substring(pos+5);
			}
			else
			{
				final int pos = environment.indexOf(":8443/");
				if(pos>0)
					environment = environment.substring(0, pos) + ":8080" + environment.substring(pos+5);
			}
			
			return
				(secure ? "https://" : "http://") +
				environment +
				'/' + url;
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
		return XMLEncoder.encode(toStringNonEncoded());
	}
	
	/**
	 * Use this method for instance with {@link HttpServletResponse#sendRedirect(String)}.
	 */
	public final String toStringNonEncoded()
	{
		final String url = this.url!=null ? this.url.toString() : name;
		
		final HttpServletRequest  request  = CopsServlet.requests.get();
		final HttpServletResponse response = CopsServlet.responses.get();
		if(request==null)
			throw new IllegalStateException("no request available");
		if(response==null)
			throw new IllegalStateException("no response available");
		
		if(request instanceof EnvironmentRequest)
		{
			final Boolean needsSecure = needsSecure();
			final boolean secure = needsSecure!=null && needsSecure.booleanValue();
			
			String environment = ((EnvironmentRequest)request).environment;
			if(secure)
			{
				final int pos = environment.indexOf(":8080/");
				if(pos>0)
					environment = environment.substring(0, pos) + ":8443" + environment.substring(pos+5);
			}
			else
			{
				final int pos = environment.indexOf(":8443/");
				if(pos>0)
					environment = environment.substring(0, pos) + ":8080" + environment.substring(pos+5);
			}
				
			return
				(secure ? "https://" : "http://") +
				environment +
				'/' + url;
		}
		
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
	
	private static final String BASIC = "Basic ";
	private static final int BASIC_LENGTH = BASIC.length();
	
	public static final String[] authorizeBasic(final HttpServletRequest request)
	{
		final String authorization = request.getHeader("Authorization");
		//System.out.println("authorization:"+authorization);
		if(authorization==null||!authorization.startsWith(BASIC))
			return null;
		
		final String basicCookie = authorization.substring(BASIC_LENGTH);
		//System.out.println("basicCookie:"+basicCookie);
		
		final String basicCookiePlain = new String(Base64.decode(basicCookie));
		//System.out.println("basicCookiePlain:"+basicCookiePlain);
		
		final int colon = basicCookiePlain.indexOf(':');
		if(colon<=0 || colon+1>=basicCookiePlain.length())
			return null;
		
		final String userid = basicCookiePlain.substring(0, colon);
		final String password = basicCookiePlain.substring(colon+1);
		//System.out.println("userid:"+userid);
		//System.out.println("password:"+password);
		return new String[]{userid, password};
	}
	
	public static final void rejectAuthorizeBasic(final HttpServletResponse response, final String realm)
	{
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + '"');
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
}
