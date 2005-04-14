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


public abstract class Form
{
	private final HttpServletRequest request;
	private final HashMap multipartContentParameters;

	private final HashMap fieldMap = new HashMap();
	private final ArrayList visibleFieldList = new ArrayList();
	private final ArrayList hiddenFieldList = new ArrayList();
	private final ArrayList allFieldList = new ArrayList();
	
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
	
	final void register(final Field field, final boolean hidden)
	{
		if(fieldMap.put(field.key, field)!=null)
			throw new RuntimeException(field.name);
		(hidden?hiddenFieldList:visibleFieldList).add(field);
		allFieldList.add(field);
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
	
	public final List getVisibleFields()
	{
		return Collections.unmodifiableList(visibleFieldList);
	}
	
	public final List getHiddenFields()
	{
		return Collections.unmodifiableList(hiddenFieldList);
	}
	
	public final List getAllFields()
	{
		return Collections.unmodifiableList(allFieldList);
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
