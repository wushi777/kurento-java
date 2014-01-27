/*
 * (C) Copyright 2013 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.kurento.kmf.media.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kurento.kmf.media.MediaApiConfiguration;

/**
 * Thread pool within Media-API.
 * 
 * @author Luis López (llopez@gsyc.es)
 * @author Ivan Gracia (igracia@gsyc.es)
 * @version 1.0.0
 */
public class MediaApiExecutorService {

	private static final Logger log = LoggerFactory
			.getLogger(MediaApiExecutorService.class);

	/**
	 * Thread pool implementation.
	 */
	private ThreadPoolExecutor executor;

	/**
	 * Autowired configuration.
	 */
	@Autowired
	private MediaApiConfiguration config;

	/**
	 * Default constructor.
	 */
	public MediaApiExecutorService() {
	}

	/**
	 * Post constructor method; instantiate thread pool.
	 * 
	 * @throws Exception
	 *             Error in the creation of the thread pool
	 */
	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		executor = new ThreadPoolExecutor(config.getPoolCoreSize(),
				config.getPoolMaxSize(), config.getPoolExecutionTimeout(),
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						config.getPoolMaxQueueSize()),
				new RejectedExecutionHandler() {

					@Override
					public void rejectedExecution(Runnable r,
							ThreadPoolExecutor executor) {
						log.warn("Execution is blocked because the thread bounds and queue capacities are reached");
					}
				});
	}

	/**
	 * Pre destroy method; shutdown the thread pool.
	 * 
	 * @throws Exception
	 *             Problem while shutting down thread
	 */
	@PreDestroy
	public void destroy() throws Exception {
		executor.shutdown();
	}

	/**
	 * Getter (accessor) of the thread pool (executor).
	 * 
	 * @return Thread pool (executor)
	 */
	public ExecutorService getExecutor() {
		return executor;
	}
}
