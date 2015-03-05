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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;

final class ResourceStatusCop extends Cop
{
	private static final String SHOW_IMAGE = "si";
	private static final String SHOW_URL = "su";
	private static final String ABSOLUTE_URL = "au";

	final boolean showImage;
	final boolean showUrl;
	final boolean absoluteUrl;

	ResourceStatusCop(
			final boolean showImage,
			final boolean showUrl,
			final boolean absoluteUrl)
	{
		super("copsResourceStatus.html");
		this.showImage = showImage;
		this.showUrl = showUrl;
		this.absoluteUrl = absoluteUrl;

		addParameter(SHOW_IMAGE, showImage);
		addParameter(SHOW_URL, showUrl);
		addParameter(ABSOLUTE_URL, absoluteUrl);
	}

	static final ResourceStatusCop getCop(final HttpServletRequest request)
	{
		return new ResourceStatusCop(
				getBooleanParameter(request, SHOW_IMAGE),
				getBooleanParameter(request, SHOW_URL),
				getBooleanParameter(request, ABSOLUTE_URL));
	}

	ResourceStatusCop toggleShowImage()
	{
		return new ResourceStatusCop(!showImage, showUrl, absoluteUrl);
	}

	ResourceStatusCop toggleShowUrl()
	{
		return new ResourceStatusCop(showImage, !showUrl, absoluteUrl);
	}

	ResourceStatusCop toggleAbsoluteUrl()
	{
		return new ResourceStatusCop(showImage, showUrl, !absoluteUrl);
	}

	static final String SELECT = "select";
	static final String SELECT_ALL = "selectAll";
	static final String INBOX = "inbox";
	static final String OVERRIDE_HOST = "overrideHost";
	static final String TOUCH = "touchLastModified";
	static final String ACTIVATE_LOG = "activateLog";
	static final String DEACTIVATE_LOG = "deactivateLog";

	static void post(final HttpServletRequest request, final Collection<Resource> resources)
	{
		if(request.getParameter(OVERRIDE_HOST)!=null)
		{
			final String hostOverride = trim(request.getParameter(INBOX));
			for(final Resource resource : filter(request, resources))
				resource.setHostOverride(hostOverride);
		}
		else if(request.getParameter(TOUCH)!=null)
		{
			final long now = System.currentTimeMillis();
			for(final Resource resource : filter(request, resources))
				resource.setLastModified(now);
		}
		else if(request.getParameter(ACTIVATE_LOG)!=null)
		{
			for(final Resource resource : filter(request, resources))
				resource.setLog(true);
		}
		else if(request.getParameter(DEACTIVATE_LOG)!=null)
		{
			for(final Resource resource : filter(request, resources))
				resource.setLog(false);
		}
	}

	private static Collection<Resource> filter(final HttpServletRequest request, final Collection<Resource> resources)
	{
		if(request.getParameter(SELECT_ALL)!=null)
			return resources;

		final String[] selectList = request.getParameterValues(SELECT);
		if(selectList==null)
			return Collections.<Resource>emptyList();

		final HashSet<String> selects = new HashSet<>();
		for(final String select : selectList)
			selects.add(select);
		final ArrayList<Resource> result = new ArrayList<>();
		for(final Resource resource : resources)
			if(selects.contains(resource.getName()))
				result.add(resource);
		return result;
	}

	private static String trim(String s)
	{
		if(s==null)
			return null;
		s = s.trim();
		if(s.length()==0)
			return null;
		return s;
	}
}
