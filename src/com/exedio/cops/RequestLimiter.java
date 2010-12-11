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

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class RequestLimiter
{
	private static final String STATUS_PATH_INFO = "/copsRequestLimiterStatus.html";
	static final String THRESHOLD = "copsRequestLimiter.threshold";
	static final String INTERVAL  = "copsRequestLimiter.interval";

	private volatile int threshold;
	private volatile int interval;
	private final String denyMessage;
	private final byte[] denyBody;

	/**
	 * @param threshold
	 *        the number of request allowed in one interval,
	 *        until further requests in that interval are denied
	 * @param interval
	 *        the length of the interval in milliseconds
	 */
	public RequestLimiter(final int threshold, final int interval, final String denyMessage)
	{
		this(threshold, interval, denyMessage, null);
	}

	/**
	 * @param threshold
	 *        the number of request allowed in one interval,
	 *        until further requests in that interval are denied
	 * @param interval
	 *        the length of the interval in milliseconds
	 */
	public RequestLimiter(final int threshold, final int interval, final String denyMessage, final String denyBody)
	{
		if(threshold<=0)
			throw new IllegalArgumentException("threshold must be greater than zero, but was " + threshold);
		if(interval<=0)
			throw new IllegalArgumentException("interval must be greater than zero, but was " + interval);

		this.threshold = threshold;
		this.interval = interval;
		this.denyMessage = denyMessage;
		if(denyBody!=null)
		{
			try
			{
				this.denyBody = denyBody.getBytes(CopsServlet.UTF8);
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			this.denyBody = null;
		}
	}

	public void init(final ServletConfig config)
	{
		setParameters(
				config.getInitParameter(THRESHOLD),
				config.getInitParameter(INTERVAL));
	}

	private void setParameters(final String threshold, final String interval)
	{
		if(threshold!=null)
			this.threshold = getIntParameter(threshold, "threshold");
		if(interval!=null)
		{
			this.interval = getIntParameter(interval, "interval");
			this.lastInterval = Long.MIN_VALUE;
		}
	}

	private static int getIntParameter(final String s, final String name)
	{
		final int i = Integer.parseInt(s);
		if(i<=0)
			throw new IllegalArgumentException(name + " must be greater than zero, but was " + i);
		return i;
	}

	private volatile long lastInterval = Long.MIN_VALUE;
	private volatile int requestsInInterval = 1;
	private volatile long deniedRequests = 0;

	@SuppressWarnings("deprecation")
	private static final void setStatus503WithBody(final HttpServletResponse response, final String denyMessage)
	{
		response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE, denyMessage);
	}

	/**
	 * @return whether this call has handled the request.
	 *         Do the following at the beginning of processing the request:
	 *         <tt>if(requestLimiter.doRequest(request, response)) return;</tt>
	 */
	public boolean doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
	throws IOException
	{
		if(STATUS_PATH_INFO.equals(request.getPathInfo()))
		{
			doStatus(request, response);
			return true;
		}
		else
		{
			final long nowInterval = System.currentTimeMillis() / interval;

			if(nowInterval<=lastInterval)
			{
				if(requestsInInterval>=threshold && request.getSession(false)==null)
				{
					if(denyBody!=null)
					{
						setStatus503WithBody(response, denyMessage);
						BodySender.send(response, denyBody);
					}
					else
					{
						response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, denyMessage);
					}
					deniedRequests++;
					//System.out.println("copsRequestLimiter denied " + deniedRequests);
					return true;
				}
				else
				{
					requestsInInterval++;
					//System.out.println("copsRequestLimiter passed " + requestsInInterval);
					return false;
				}
			}
			else
			{
				//System.out.println("copsRequestLimiter new interval " + lastInterval + "-->" + nowInterval + " got " + requestsInInterval);
				lastInterval = nowInterval;
				requestsInInterval = 1;
				return false;
			}
		}
	}

	private void doStatus(
			final HttpServletRequest request,
			final HttpServletResponse response)
	throws IOException
	{
		if(!request.isUserInRole("manager"))
		{
			BasicAuthorization.reject(response, "Cops Request Limiter");
			return;
		}

		final String path = request.getContextPath() + STATUS_PATH_INFO;

		if(Cop.isPost(request))
		{
			setParameters(
					request.getParameter(THRESHOLD),
					request.getParameter(INTERVAL));
			response.sendRedirect(path);
		}
		else
		{
			ServletOutputStream outStream = null;
			PrintStream out = null;
			try
			{
				outStream = response.getOutputStream();
				out = new PrintStream(outStream, false, CopsServlet.UTF8);
				RequestLimiter_Jspm.write(out, path, threshold, interval, deniedRequests);
			}
			finally
			{
				if(out!=null)
					out.close();
				if(outStream!=null)
					outStream.close();
			}
		}
	}
}
