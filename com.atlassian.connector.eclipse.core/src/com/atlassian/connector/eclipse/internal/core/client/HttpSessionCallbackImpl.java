/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package com.atlassian.connector.eclipse.internal.core.client;

import com.atlassian.theplugin.commons.exception.HttpProxySettingsException;
import com.atlassian.theplugin.commons.remoteapi.ServerData;
import com.atlassian.theplugin.commons.remoteapi.rest.AbstractHttpSession;
import com.atlassian.theplugin.commons.remoteapi.rest.HttpSessionCallback;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of HttpSessionCallback that can handle setting the HttpClient information on a per-server basis
 * 
 * @author Shawn Minto
 * @author Wojciech Seliga
 */
public class HttpSessionCallbackImpl implements HttpSessionCallback {

	private final Map<ServerData, HttpClient> httpClients = new HashMap<ServerData, HttpClient>();

	private final Map<String, ServerData> locations = new HashMap<String, ServerData>();

	private final IdleConnectionTimeoutThread idleConnectionTimeoutThread = new IdleConnectionTimeoutThread();

	public HttpSessionCallbackImpl() {
		idleConnectionTimeoutThread.start();
	}

	public synchronized HttpClient getHttpClient(ServerData server) throws HttpProxySettingsException {
		HttpClient httpClient = httpClients.get(server);

		// TODO handle the case where we dont have a client initialized
		assert (httpClient != null);

		return httpClient;
	}

	public void configureHttpMethod(AbstractHttpSession session, HttpMethod method) {
		session.adjustHttpHeader(method);
	}

	public synchronized void removeClient(ServerData server) {
		HttpClient client = httpClients.remove(server);
		if (client != null) {
			shutdown(client);
		}
	}

	public synchronized void removeClient(AbstractWebLocation location) {
		ServerData server = locations.remove(location.getUrl());
		if (server != null) {
			removeClient(server);
		}
	}

	public synchronized void updateHostConfiguration(AbstractWebLocation location, ServerData serverCfg) {
		HttpClient httpClient = httpClients.get(serverCfg);
		if (httpClient == null) {
			httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
			httpClients.put(serverCfg, httpClient);
			locations.put(location.getUrl(), serverCfg);
		}
		setupHttpClient(location, httpClient);
	}

	private void setupHttpClient(AbstractWebLocation location, HttpClient httpClient) {
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location,
				new NullProgressMonitor());
		httpClient.setHostConfiguration(hostConfiguration);
		httpClient.getParams().setAuthenticationPreemptive(true);
		idleConnectionTimeoutThread.addConnectionManager(httpClient.getHttpConnectionManager());
	}

	@Override
	protected void finalize() throws Throwable {
		for (HttpClient httpClient : httpClients.values()) {
			shutdown(httpClient);
		}
	}

	public void shutdown(HttpClient httpClient) {
		((MultiThreadedHttpConnectionManager) httpClient.getHttpConnectionManager()).shutdown();
		idleConnectionTimeoutThread.removeConnectionManager(httpClient.getHttpConnectionManager());
	}

	public void clear() {
		locations.clear();
		httpClients.clear();
	}
}