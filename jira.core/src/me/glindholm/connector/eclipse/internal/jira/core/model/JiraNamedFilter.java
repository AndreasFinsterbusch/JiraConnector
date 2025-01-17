/*******************************************************************************
 * Copyright (c) 2004, 2008 Brock Janiczak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brock Janiczak - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package me.glindholm.connector.eclipse.internal.jira.core.model;

import java.io.Serializable;

/**
 * @author Brock Janiczak
 */
public class JiraNamedFilter implements JiraFilter, Serializable {
	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String description;

	private String author;

	private String jql;

	private String viewUrl;

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}

	public void setJql(String jql) {
		this.jql = jql;
	}

	public String getJql() {
		return jql;
	}

	public void setViewUrl(String url) {
		this.viewUrl = url;
	}

	public String getViewUrl() {
		return viewUrl;
	}

}
