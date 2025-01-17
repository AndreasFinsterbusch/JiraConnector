/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package me.glindholm.connector.eclipse.internal.jira.core.model;

import org.eclipse.osgi.util.NLS;

public class JiraMessages extends NLS {

	private static final String BUNDLE_NAME = "me.glindholm.connector.eclipse.internal.jira.core.model.messages"; //$NON-NLS-1$
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, JiraMessages.class);
	}

	public static String SecurityLevel_None;

	private JiraMessages() {
	}

}
