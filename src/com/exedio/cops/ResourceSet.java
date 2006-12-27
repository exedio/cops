/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ResourceSet
{
	private final Class resourceLoader;
	private final ArrayList<Resource> resourceList = new ArrayList<Resource>();
	private final HashMap<String, Resource> resources = new HashMap<String, Resource>();
	
	public ResourceSet(final Class resourceLoader)
	{
		if(resourceLoader==null)
			throw new NullPointerException();
		
		this.resourceLoader = resourceLoader;
	}
	
	void add(final Resource r)
	{
		resourceList.add(r);
	}
	
	/**
	 * to be called in {@link javax.servlet.GenericServlet#init()}.
	 */
	public final void init()
	{
		for(final Resource resource : resourceList)
		{
			resource.init(resourceLoader);
			resources.put('/'+resource.name, resource);
		}
	}
	
	public final boolean doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		final String pathInfo = request.getPathInfo();
		if(pathInfo==null)
			return false;
		
		final Resource resource = resources.get(pathInfo);
		if(resource==null)
			return false;
		
		resource.doGet(request, response);
		return true;
	}
}
