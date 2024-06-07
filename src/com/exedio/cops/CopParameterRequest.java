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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.enumeration;
import static java.util.Objects.requireNonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

final class CopParameterRequest extends HttpServletRequestWrapper
{
	private final String pathInfo;
	private final String queryString;
	private final LinkedHashMap<String, ArrayList<String>> parameters;

	CopParameterRequest(final HttpServletRequest nested, final String value)
	{
		super(nested);
		requireNonNull(value, "value");

		final int queryPos = value.indexOf('?');
		if(queryPos<0)
		{
			pathInfo = '/' + value;
			queryString = null;
			parameters  = null;
		}
		else
		{
			pathInfo = '/' + value.substring(0, queryPos);
			queryString = value.substring(queryPos + 1);
			parameters = new LinkedHashMap<>();

			int startPos = queryPos;
			while(true)
			{
				final int equalPos = value.indexOf('=' , startPos + 1);
				if(equalPos<0)
					throw new IllegalArgumentException(value);
				final String key = value.substring(startPos + 1, equalPos);
				final int endPos = value.indexOf('&', equalPos + 1);
				final String newValue = (endPos<0) ? value.substring(equalPos + 1) : value.substring(equalPos + 1, endPos);
				parameters.computeIfAbsent(key, k -> new ArrayList<>()).add(decode(newValue));

				if(endPos<0)
					break;

				startPos = endPos;
			}
		}
	}

	private static String decode(final String s)
	{
		try
		{
			return URLDecoder.decode(s, UTF_8.name());
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPathInfo()
	{
		return pathInfo;
	}

	@Override
	public String getQueryString()
	{
		return queryString;
	}

	@Override
	public String getParameter(final String name)
	{
		requireNonNull(name, "name");
		if(parameters==null)
			return null;

		final ArrayList<String> list = parameters.get(name);
		if(list==null)
			return null;

		return list.get(0);
	}

	private static final String[] EMPTY_ARRAY = {};

	@Override
	public String[] getParameterValues(final String name)
	{
		requireNonNull(name, "name");
		if(parameters==null)
			return EMPTY_ARRAY;

		final ArrayList<String> list = parameters.get(name);
		if(list==null)
			return EMPTY_ARRAY;

		return list.toArray(EMPTY_ARRAY);
	}

	private static final Enumeration<String> EMPTY_ENUMERATION = enumeration(Collections.emptyList());

	@Override
	public Enumeration<String> getParameterNames()
	{
		if(parameters==null)
			return EMPTY_ENUMERATION;

		return enumeration(parameters.keySet());
	}

	@Override
	public Map<String,String[]> getParameterMap()
	{
		if(parameters==null)
			return Collections.emptyMap();

		final LinkedHashMap<String, String[]> result = new LinkedHashMap<>();
		for(final Map.Entry<String, ArrayList<String>> e : parameters.entrySet())
			result.put(e.getKey(), e.getValue().toArray(EMPTY_ARRAY));
		return Collections.unmodifiableMap(result);
	}
}
