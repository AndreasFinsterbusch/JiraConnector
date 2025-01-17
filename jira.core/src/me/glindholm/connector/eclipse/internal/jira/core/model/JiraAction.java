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

package me.glindholm.connector.eclipse.internal.jira.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steffen Pingel
 * @author Jacek Jaroczynski
 */
public class JiraAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String id;

	private final String name;

	private final List<JiraIssueField> fields = new ArrayList<JiraIssueField>();

	public JiraAction(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<JiraIssueField> getFields() {
		return fields;
	}

}
