
package com.exedio.cops;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CopsServlet extends HttpServlet
{
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		doRequest(request, response);
	}

	protected final void doPost(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		doRequest(request, response);
	}

	protected abstract void doRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException;


	public final void printException(final PrintStream out, final Exception exception)
	{
		exception.printStackTrace(out);
		if(exception instanceof ServletException)
		{
			final Throwable rootCause =
				((ServletException)exception).getRootCause();
			if(rootCause!=null)
			{
				out.println("root cause for ServletException:");
				rootCause.printStackTrace(out);
			}
			else
			{
				out.println("no root cause for ServletException");
			}
		}
		out.flush();
	}
	
	private final Random random = new Random();

	/**
	 * Returns the id under with the exception has been reported in the log.
	 */
	public final String reportException(final Exception exception)
	{
		final long idLong;
		synchronized(random)
		{
			idLong = random.nextLong();
		}
		final String id = String.valueOf(Math.abs(idLong));
		System.out.println("--------I"+id+"-----");
		printException(System.out, exception);
		System.out.println("--------O"+id+"-----");
		return id;
	}

}
