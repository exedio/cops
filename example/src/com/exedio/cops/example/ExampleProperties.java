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

import static java.lang.System.nanoTime;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unused")
public final class ExampleProperties extends Properties
{
	private final String mailFrom    = value("mail.from",    "copedemo@mailgrab.exedio.com");
	private final String mailBcc     = value("mail.bcc",     "copedemo@mailgrab.exedio.com");
	private final String errorMailTo = value("errorMail.to", "copedemo@mailgrab.exedio.com");

	private final  String smtpHost           = value("smtp.host",  "mailgrab.exedio.com");
	private final boolean smtpDebug          = value("smtp.debug", false);
	private final     int smtpConnectTimeout = value("smtp.connectTimeout", 5000, 1000);
	private final     int smtpReadTimeout    = value("smtp.readTimeout",    5000, 1000);

	private final  String ldapServerType     = value("ldap.server.type", "OpenLDAP");
	private final  String ldapServerURL      = value("ldap.server.url", "ldap://ldap.copedemo.exedio.com:389");
	private final  String ldapRootDN         = value("ldap.root.dn", "dc=exedio,dc=com");
	private final  String ldapCredentials    = value("ldap.credentials", "");
	private final  String ldapPrincipal      = value("ldap.principal", "");
	private final boolean ldapEnabledTracing = value("ldap.connection.enable.tracing", true);
	private final boolean ldapEnabledPooling = value("ldap.connection.enable.pooling", true);
	private final     int ldapConnectTimeout = value("ldap.connectTimeout", 5000, 1000);
	private final     int ldapReadTimeout    = value("ldap.readTimeout",    5000, 1000);

	private final int cartCookieMaxAgeHours = value("cartCookieMaxAgeHours", 24*30, 0); // 30 days

	private final String copaibaAdminLogin    =  value("copaiba.admin.login",    "admin");
	private final String copaibaAdminPassword =  value("copaiba.admin.password", "nimda");

	private final String stringDefault    = value("string.default", "default of string.default");
	private final String stringSet        = value("string.set", (String)null);
	private final String stringDefaultSet = value("string.default.set", "default of string.default.set");

	private final String hidden1 = valueHidden("hidden.1", "default of hidden.1");
	private final String hidden2 = valueHidden("hidden.2", (String)null);
	private final String hidden3 = valueHidden("hidden.3", (String)null);

	private final String  setToDefaultString  = value("setToDefault.string" , "value of setToDefault.string");
	private final int     setToDefaultInt     = value("setToDefault.int"    , 1234, 5);
	private final boolean setToDefaultBoolean = value("setToDefault.boolean", true);

	private ExampleProperties(final File source)
	{
		super(Sources.load(source));
	}

	ExampleProperties(final Source source)
	{
		super(source);
	}

	@Override
	@SuppressWarnings("AnonymousInnerClassMayBeStatic")
	public List<? extends Callable<?>> getTests()
	{
		final ArrayList<Callable<?>> result = new ArrayList<>();

		result.add(new Callable<Object>(){

				@Override
				public Object call()
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

			@Override
			public Object call()
			{
				return "Result";
			}

			@Override
			public String toString()
			{
				return "Ok Result";
			}
		});


		result.add(new Callable<Object>(){

				@Override
				public Object call()
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

				@Override
				public Object call()
				{
					throw new RuntimeException(new NullPointerException("zack"));
				}

				@Override
				public String toString()
				{
					return "Broken Nested";
				}
			});


		result.add(new Callable<Object>(){

				@Override
				public String call() throws InterruptedException
				{
					final long start = nanoTime();
					Thread.sleep(77);
					final long end = nanoTime();
					return "Slept " + (end - start) + "ns";
				}

				@Override
				public String toString()
				{
					return "Sleep";
				}
			});

		return result;
	}

	private static ExampleProperties instance = null;

	public static ExampleProperties instance(final File source)
	{
		if(instance!=null)
			return instance;

		instance = new ExampleProperties(source);
		return instance;
	}

	static void setInstance(final ExampleProperties instance)
	{
		ExampleProperties.instance = instance;
	}
}