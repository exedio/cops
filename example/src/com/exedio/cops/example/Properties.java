/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.exedio.cops.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.ServletContext;

@SuppressWarnings("unused")
public final class Properties extends com.exedio.cope.util.Properties
{
	private final StringField mailFrom    = new StringField("mail.from",    "copedemo@mailgrab.exedio.com");
	private final StringField mailBcc     = new StringField("mail.bcc",     "copedemo@mailgrab.exedio.com");
	private final StringField errorMailTo = new StringField("errorMail.to", "copedemo@mailgrab.exedio.com");
	
	private final  StringField smtpHost           = new  StringField("smtp.host",  "mailgrab.exedio.com");
	private final BooleanField smtpDebug          = new BooleanField("smtp.debug", false);
	private final     IntField smtpConnectTimeout = new     IntField("smtp.connectTimeout", 5000, 1000);
	private final     IntField smtpReadTimeout    = new     IntField("smtp.readTimeout",    5000, 1000);
	
	private final  StringField ldapServerType     = new  StringField("ldap.server.type", "OpenLDAP");
	private final  StringField ldapServerURL      = new  StringField("ldap.server.url", "ldap://ldap.copedemo.exedio.com:389");
	private final  StringField ldapRootDN         = new  StringField("ldap.root.dn", "dc=exedio,dc=com");
	private final  StringField ldapCredentials    = new  StringField("ldap.credentials", "");
	private final  StringField ldapPrincipal      = new  StringField("ldap.principal", "");
	private final BooleanField ldapEnabledTracing = new BooleanField("ldap.connection.enable.tracing", true);
	private final BooleanField ldapEnabledPooling = new BooleanField("ldap.connection.enable.pooling", true);
	private final     IntField ldapConnectTimeout = new     IntField("ldap.connectTimeout", 5000, 1000);
	private final     IntField ldapReadTimeout    = new     IntField("ldap.readTimeout",    5000, 1000);
	
	private final IntField cartCookieMaxAgeHours = new IntField("cartCookieMaxAgeHours", 24*30, 0); // 30 days
	
	private final StringField copaibaAdminLogin    =  new StringField("copaiba.admin.login",    "admin");
	private final StringField copaibaAdminPassword =  new StringField("copaiba.admin.password", "nimda");
	
	private Properties(final ServletContext context)
	{
		super(new Source(){

			public String get(final String key)
			{
				return context.getInitParameter(key);
			}

			public String getDescription()
			{
				return context.getServletContextName();
			}

			public Collection<String> keySet()
			{
				return null;
			}
		}, null);
	}
	
	@Override
	public List<? extends Callable<?>> getTests()
	{
		final ArrayList<Callable<?>> result = new ArrayList<Callable<?>>();
		
		result.add(new Callable<Object>(){

				public Object call() throws Exception
				{
					return null;
				}
				
				@Override
				public String toString()
				{
					return "Ok";
				}
			});
		
		
		result.add(new Callable<Object>(){

				public Object call() throws Exception
				{
					throw new NullPointerException("zack");
				}
				
				@Override
				public String toString()
				{
					return "Broken";
				}
			});
		
		result.add(new Callable<Object>(){

				public Object call() throws Exception
				{
					throw new RuntimeException(new NullPointerException("zack"));
				}
				
				@Override
				public String toString()
				{
					return "Broken2";
				}
			});
		
		return result;
	}
	
	private static Properties instance = null;
	
	public static final Properties instance(final ServletContext context)
	{
		if(instance!=null)
			return instance;
		
		instance = new Properties(context);
		return instance;
	}
}
