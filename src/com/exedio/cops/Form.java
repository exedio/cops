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
	private final ArrayList fieldList = new ArrayList();
	private final ArrayList hiddenFieldList = new ArrayList();
	
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
	
	public class Field
	{
		public final Object key;
		public final String name;
		public final String value;
		public String error;
		
		public Field(final Object key, final String name, final String value, final boolean hidden)
		{
			this.key = key;
			this.name = name;
			this.value = value;
			fieldMap.put(key, this);
			(hidden?hiddenFieldList:fieldList).add(this);
		}
		
		public Field(final Object key, final String value, final boolean hidden)
		{
			this.key = key;
			this.name = null;
			this.value = value;
			fieldMap.put(key, this);
			(hidden?hiddenFieldList:fieldList).add(this);
		}
		
		public final boolean isReadOnly()
		{
			return name==null;
		}
		
		public final String getName()
		{
			if(name==null)
				throw new RuntimeException();
			return name;
		}
		
		public final String getValue()
		{
			return value;
		}
		
		public final String getError()
		{
			return error;
		}
		
		// TODO make this method abstract
		public void write(final PrintStream out) throws IOException
		{
			throw new RuntimeException(name);
		}
	}
	
	public class EnumField extends Field
	{
		public final ArrayList names = new ArrayList();
		final HashMap values = new HashMap();
		
		public EnumField(final Object key, final String name, final String value, final boolean hidden)
		{
			super(key, name, value, hidden);
		}
		
		public EnumField(final Object key, final String value, final boolean hidden)
		{
			super(key, value, hidden);
		}
		
		public String getValue(final String name)
		{
			return (String)values.get(name);
		}
		
		public boolean isChecked(final String checkValue)
		{
			return value.equals(checkValue);
		}
		
		public void addOption(final String name, final String value)
		{
			names.add(name);
			values.put(name, value);
		}
		
		public void write(final PrintStream out) throws IOException
		{
			Main_Jspm.write(out, this);
		}
		
	}
	
	public class BooleanField extends Field
	{
		public static final String VALUE_ON = "on";
		
		public BooleanField(final Object key, final String name, final String value, final boolean hidden)
		{
			super(key, name, value, hidden);
		}
		
		public BooleanField(final Object key, final String value, final boolean hidden)
		{
			super(key, value, hidden);
		}
		
		public boolean isChecked()
		{
			return VALUE_ON.equals(value);
		}
		
		public void write(final PrintStream out) throws IOException
		{
			Main_Jspm.write(out, this);
		}
		
	}
	
	public final List getFields()
	{
		return Collections.unmodifiableList(fieldList);
	}
	
	public final List getHiddenFields()
	{
		return Collections.unmodifiableList(hiddenFieldList);
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
