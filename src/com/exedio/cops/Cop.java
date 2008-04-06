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
	
	private static final char[] FORBIDDEN_IN_NAME = new char[] {'/', '?', '&', ';'};
	
	protected final void addParameter(final String key, final String value)
	{
		if(url==null)
		{
			url = new StringBuilder(name);
			url.append('?');
		}
		else
			url.append('&');
			
		url.append(key);
		url.append('=');
		url.append(value);
	}
	
	protected Boolean needsSecure()
	{
		return null;
	}
	
	@Override
	public final String toString()
	{
		final String url = this.url!=null ? this.url.toString() : name;
		
		final HttpServletResponse response = CopsServlet.responses.get();
		final String encodedURL = response!=null ? response.encodeURL(url) : url;
		
		final Boolean needsSecure = needsSecure();
		if(needsSecure==null)
			return encodedURL;
		
		final HttpServletRequest request = CopsServlet.requests.get();
		if(request==null)
			return encodedURL;
		
		if(needsSecure.booleanValue()==request.isSecure())
			return encodedURL;
		
		String host = request.getHeader("Host");
		if(host.endsWith(":8080"))
			host = host.substring(0, host.length()-4) + "8443";
		else if(host.endsWith(":8443"))
			host = host.substring(0, host.length()-4) + "8080";
		
		return
			(needsSecure?"https://":"http://") +
			host +
			request.getContextPath() +
			request.getServletPath() +
			'/' + encodedURL;
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
	
	public static final String encodeXml(final String st)
	{
		if(st==null)
			return null;
		
		return
			st.replaceAll("&",  "&amp;").
				replaceAll("<",  "&lt;").
				replaceAll(">",  "&gt;").
				replaceAll("\"", "&quot;").
				replaceAll("'",  "&apos;");
	}
	
	public static final int getIntParameter(
			final HttpServletRequest request,
			final String name,
			final int defaultValue)
	{
		final String value = request.getParameter(name);
		return (value==null) ? defaultValue : Integer.parseInt(value);
	}
}
