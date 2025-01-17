/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package me.glindholm.connector.eclipse.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import me.glindholm.connector.eclipse.internal.branding.ui.JiraConnectorBrandingPlugin;

public class AtlassianBundlesInfo {
	private static final String ECLIPSE_CONNECTOR_NAMESPACE = "me.glindholm.connector.eclipse.";

	public static Map<String, String> getAllInstalledBundles() {
		Map<String, String> featureToVersion = new HashMap<String, String>();
		for (Bundle bundle : JiraConnectorBrandingPlugin.getDefault().getBundle().getBundleContext().getBundles()) {
			final String symbolicName = bundle.getSymbolicName();
			if (symbolicName.startsWith(ECLIPSE_CONNECTOR_NAMESPACE)) {
				featureToVersion.put(symbolicName.substring(ECLIPSE_CONNECTOR_NAMESPACE.length()), bundle.getHeaders()
						.get(Constants.BUNDLE_VERSION)
						.toString());
			}
		}
		return featureToVersion;
	}

	public static boolean isOnlyJiraInstalled() {
		return Boolean.valueOf(System.getProperty(BrandingConstants.JIRA_INSTALLED_SYSTEM_PROPERTY));
	}
}
