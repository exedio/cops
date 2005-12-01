/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;


public class Form
{
	private final HttpServletRequest request;
	private final HashMap multipartContentParameters;

	private final HashMap fieldByName = new HashMap();
	private final HashMap fieldByKey = new HashMap();
	private final ArrayList fieldList = new ArrayList();
	
	private final ArrayList sectionList = new ArrayList();
	
	public Form(final HttpServletRequest request)
	{
		this.request = request;

		if(FileUpload.isMultipartContent(request))
		{
			final DiskFileUpload upload = new DiskFileUpload();
			final int maxSize = 100*1024; // TODO: make this configurable
			upload.setSizeThreshold(maxSize); // TODO: always save to disk
			upload.setSizeMax(maxSize);
			//upload.setRepositoryPath("");
			multipartContentParameters = new HashMap();
			try
			{
				for(Iterator i = upload.parseRequest(request).iterator(); i.hasNext(); )
				{
					final FileItem item = (FileItem)i.next();
					if (item.isFormField())
					{
						final String name = item.getFieldName();
						final String value = item.getString();
						multipartContentParameters.put(name, value);
					}
					else
					{
						final String name = item.getFieldName();
						multipartContentParameters.put(name, item);
					}
				}
			}
			catch(FileUploadException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			multipartContentParameters = null;
		}
	}
	
	final void register(final Field field)
	{
		if(fieldByName.put(field.name, field)!=null)
			throw new RuntimeException(field.name);
		if(fieldByKey.put(field.key, field)!=null)
			throw new RuntimeException(field.name);
		fieldList.add(field);
	}
	
	protected final String getParameter(final String name)
	{
		if(multipartContentParameters!=null)
		{
			return (String)multipartContentParameters.get(name);
		}
		else
			return request.getParameter(name);
	}
	
	protected final FileItem getParameterFile(final String name)
	{
		if(multipartContentParameters!=null)
		{
			return (FileItem)multipartContentParameters.get(name);
		}
		else
			return null;
	}
	
	public final Field getFieldByName(final String name)
	{
		return (Field)fieldByName.get(name);
	}
	
	public final Field getFieldByKey(final Object key)
	{
		return (Field)fieldByKey.get(key);
	}
	
	public final List getFields()
	{
		return Collections.unmodifiableList(fieldList);
	}
	
	public final void writeHiddenFields(final PrintStream out) throws IOException
	{
		Main_Jspm.writeHiddenFields(out, this);
	}
	
	public final class Section
	{
		public final String id;
		public final String name;
		
		public Section(final String id, final String name)
		{
			this.id = id;
			this.name = name;
			sectionList.add(this);
		}
	}
	
	public final List getSections()
	{
		return Collections.unmodifiableList(sectionList);
	}
	
}
