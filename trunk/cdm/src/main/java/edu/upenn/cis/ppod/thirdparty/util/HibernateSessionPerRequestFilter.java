/*
 * Copyright (c) 2005, Christian Bauer <christian@hibernate.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the original author nor the names of contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES of MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT of
 * SUBSTITUTE GOODS OR SERVICES; LOSS of USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY of LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT of THE USE of THIS SOFTWARE, EVEN IF ADVISED of
 * THE POSSIBILITY of SUCH DAMAGE.
 */
package edu.upenn.cis.ppod.thirdparty.util;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.SessionFactory;
import org.hibernate.context.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.upenn.cis.ppod.util.PPodCoreFactory;

public final class HibernateSessionPerRequestFilter implements Filter {
	private static Logger logger = LoggerFactory
			.getLogger(HibernateSessionPerRequestFilter.class);

	private final PPodCoreFactory pPodCoreFactory = new PPodCoreFactory();
// private final Provider<PPodVersionInfoInterceptor>
	// versionInfoInterceptorProvider = pPodCoreFactory
	// .getProvider(PPodVersionInfoInterceptor.class);
	private SessionFactory sf;

	public void destroy() {}

	public void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		final long inTime = new Date().getTime();
		org.hibernate.classic.Session currentSession;

		try {

			logger.debug("Opening Session");
			currentSession = sf.openSession();

			logger.debug("Binding the current Session");
			ManagedSessionContext.bind(currentSession);

			logger.debug("Starting a database transaction");
			currentSession.beginTransaction();

			logger.debug("Processing the event");
			chain.doFilter(request, response);

			logger.debug("Committing the database transaction");

			// The transactions should have been committed by the resteasy
			// interceptor
			if (currentSession.getTransaction().isActive()) {
				logger
						.warn("The transaction has not already been committed as we expect it to be - is the HibernateCommitInterceptor configured? Committing the transaction now...");
				currentSession.getTransaction().commit();
				logger.warn("transaction committed");
			}

		} catch (final Throwable ex) {
			try {
				if (sf.getCurrentSession().getTransaction().isActive()) {
					logger
							.debug("Trying to rollback database transaction after exception");
					sf.getCurrentSession().getTransaction().rollback();
				}
			} catch (final Throwable rbEx) {
				logger.error("Could not rollback transaction after exception!",
						rbEx);
			}
			// Let others handle it... maybe another interceptor for exceptions?

			throw new ServletException(ex);

		} finally {
			logger.debug("Unbinding Session");
			currentSession = ManagedSessionContext.unbind(sf);
			logger.debug("Closing Session");
			currentSession.close();
			logger.info("RESPONSE TIME: "
					+ ((new Date().getTime() - inTime) / 1000F) + " seconds");
		}
	}

	public void init(final FilterConfig filterConfig) throws ServletException {
		logger.debug("Initializing filter...");
		logger.debug("Obtaining SessionFactory from HibernateUtil");
		sf = HibernateUtil.getSessionFactory();
	}
}
