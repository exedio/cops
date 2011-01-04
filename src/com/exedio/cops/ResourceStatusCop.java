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
}
