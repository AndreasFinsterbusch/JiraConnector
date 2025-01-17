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

package me.glindholm.connector.eclipse.jira.tests.util;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;

import me.glindholm.connector.eclipse.internal.jira.core.model.Attachment;
import me.glindholm.connector.eclipse.internal.jira.core.model.Component;
import me.glindholm.connector.eclipse.internal.jira.core.model.IssueField;
import me.glindholm.connector.eclipse.internal.jira.core.model.IssueType;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraAction;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraFilter;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraIssue;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraStatus;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraVersion;
import me.glindholm.connector.eclipse.internal.jira.core.model.NamedFilter;
import me.glindholm.connector.eclipse.internal.jira.core.model.Priority;
import me.glindholm.connector.eclipse.internal.jira.core.model.Project;
import me.glindholm.connector.eclipse.internal.jira.core.model.ProjectRole;
import me.glindholm.connector.eclipse.internal.jira.core.model.Resolution;
import me.glindholm.connector.eclipse.internal.jira.core.model.SecurityLevel;
import me.glindholm.connector.eclipse.internal.jira.core.model.ServerInfo;
import me.glindholm.connector.eclipse.internal.jira.core.model.Version;
import me.glindholm.connector.eclipse.internal.jira.core.model.filter.FilterDefinition;
import me.glindholm.connector.eclipse.internal.jira.core.model.filter.IssueCollector;
import me.glindholm.connector.eclipse.internal.jira.core.service.JiraClient;
import me.glindholm.connector.eclipse.internal.jira.core.service.JiraClientCache;
import me.glindholm.connector.eclipse.internal.jira.core.service.JiraException;
import me.glindholm.connector.eclipse.internal.jira.core.service.JiraLocalConfiguration;

public class MockJiraClient extends JiraClient {

	public static Component createComponent(String id, String name) {
		Component component = new Component();
		component.setId(id);
		component.setName(name);
		return component;
	}

	public static IssueType createIssueType(String id, String name) {
		return new IssueType(id, name, null, null);
	}

	public static Priority createPriority(String id, String name) {
		return new Priority(id, name, null, null, null);
	}

	public static Project createProject() {
		Project project = new Project();
		project.setId(JiraTestUtil.PROJECT1);
		project.setKey("PRONE");
		project.setName("Prone");

		Component[] components = new Component[] { createComponent("1", "component1"),
				createComponent("2", "component2"), createComponent("3", "component3"),
				createComponent("4", "component4"), };
		project.setComponents(components);

		Version[] versions = new Version[] { createVersion("1", "1.0"), createVersion("2", "2.0"),
				createVersion("3", "3.0"), createVersion("4", "4.0"), };
		project.setVersions(versions);

		return project;
	}

	public static Version createVersion(String id, String name) {
		return new Version(id, name);
	}

	private JiraClientCache cache;

	static class MockWebLocation extends WebLocation {
		MockWebLocation(String baseUrl) {
			super(baseUrl);
			setCredentials(AuthenticationType.REPOSITORY, "username", "password");
		}
	}

	public MockJiraClient(String baseUrl) {
		super(new MockWebLocation(baseUrl), new JiraLocalConfiguration(), new MockJiraRestClientAdapter(baseUrl, null));
		this.cache = super.getCache();
	}

//	@Override
//	public void addCommentToIssue(String issueKey, Comment comment, IProgressMonitor monitor) throws JiraException {
//		// ignore
//	}

	@Override
	public void addCommentToIssue(String issueKey, String comment, IProgressMonitor monitor) throws JiraException {
		// ignore
	}

	@Override
	public void advanceIssueWorkflow(JiraIssue issue, String actionKey, String comment, IProgressMonitor monitor)
			throws JiraException {
		// ignore
	}

	@Override
	public void assignIssueTo(JiraIssue issue, String user, String comment, IProgressMonitor monitor)
			throws JiraException {
		// ignore
	}

	@Override
	public void addAttachment(JiraIssue issue, String comment, String filename, byte[] content, IProgressMonitor monitor)
			throws JiraException {
		// ignore
	}

	@Override
	public JiraIssue createIssue(JiraIssue issue, IProgressMonitor monitor) throws JiraException {
		// ignore
		return null;
	}

	@Override
	public void deleteIssue(JiraIssue issue, IProgressMonitor monitor) throws JiraException {
		// ignore
	}

//	@Override
//	public void executeNamedFilter(NamedFilter filter, IssueCollector collector, IProgressMonitor monitor)
//			throws JiraException {
//		// ignore
//	}

	@Override
	public void findIssues(FilterDefinition filterDefinition, IssueCollector collector, IProgressMonitor monitor)
			throws JiraException {
		// ignore
	}

	@Override
	public List<IssueField> getActionFields(String issueKey, String actionId, IProgressMonitor monitor)
			throws JiraException {
		return null;
	}

	@Override
	public List<JiraAction> getAvailableActions(String issueKey, IProgressMonitor monitor) throws JiraException {
		return null;
	}

	@Override
	public JiraClientCache getCache() {
		return this.cache;
	}

//	@Override
//	public Component[] getComponents(String key, IProgressMonitor monitor) throws JiraException {
//		return null;
//	}

//	@Override
//	public CustomField[] getCustomAttributes(IProgressMonitor monitor) throws JiraException {
//		return null;
//	}

//	@Override
//	public IssueField[] getEditableAttributes(String issueKey, IProgressMonitor monitor) throws JiraException {
//		return null;
//	}

	public JiraIssue getIssueById(String issue) throws JiraException {
		return null;
	}

	@Override
	public JiraIssue getIssueByKey(String issueKey, IProgressMonitor monitor) throws JiraException {
		// ignore
		return null;
	}

	@Override
	public IssueType[] getIssueTypes(IProgressMonitor monitor) throws JiraException {
		return new IssueType[0];
	}

//	@Override
//	public String getKeyFromId(String issueId, IProgressMonitor monitor) throws JiraException {
//		return null;
//	}

	@Override
	public NamedFilter[] getNamedFilters(IProgressMonitor monitor) throws JiraException {
		return null;
	}

	@Override
	public Priority[] getPriorities(IProgressMonitor monitor) throws JiraException {
		return new Priority[0];
	}

	@Override
	public Project[] getProjects(IProgressMonitor monitor) throws JiraException {
		return new Project[0];
	}

	@Override
	public ServerInfo getServerInfo(IProgressMonitor monitor) throws JiraException {
		ServerInfo si = new ServerInfo();
		si.setVersion(JiraVersion.JIRA_3_13.toString());
		return si;
	}

	@Override
	public SecurityLevel[] getAvailableSecurityLevels(final String projectKey, IProgressMonitor monitor)
			throws JiraException {
		return new SecurityLevel[0];
	}

//	@Override
//	public IssueType[] getSubTaskIssueTypes(final String projectId, IProgressMonitor monitor) throws JiraException {
//		return new IssueType[0];
//	}
//
//	@Override
//	public IssueType[] getIssueTypes(String projectId, IProgressMonitor monitor) throws JiraException {
//		return new IssueType[0];
//	}
//
//	@Override
//	public IssueType[] getSubTaskIssueTypes(IProgressMonitor monitor) throws JiraException {
//		return new IssueType[0];
//	}

//	@Override
//	public Version[] getVersions(String key, IProgressMonitor monitor) throws JiraException {
//		return null;
//	}

//	@Override
//	public void login(IProgressMonitor monitor) throws JiraException {
//	}

	@Override
	public void logout(IProgressMonitor monitor) {
	}

//	@Override
//	public void quickSearch(String searchString, IssueCollector collector, IProgressMonitor monitor)
//			throws JiraException {
//		// ignore
//	}

	@Override
	public InputStream getAttachment(JiraIssue issue, Attachment attachment, IProgressMonitor monitor)
			throws JiraException {
		// ignore
		return null;
	}

	@Override
	public void search(JiraFilter query, IssueCollector collector, IProgressMonitor monitor) throws JiraException {
		// ignore
	}

	public void setCache(JiraClientCache cache) {
		this.cache = cache;
	}

	@Override
	public void updateIssue(JiraIssue issue, String comment, boolean updateEstimate, IProgressMonitor monitor)
			throws JiraException {
		// ignore
	}

//	@Override
//	public JiraWorkLog[] getWorklogs(String issueKey, IProgressMonitor monitor) throws JiraException {
//		return new JiraWorkLog[0];
//	}

	@Override
	public ProjectRole[] getProjectRoles(IProgressMonitor monitor) throws JiraException {
		return new ProjectRole[0];
	}

	@Override
	public Resolution[] getResolutions(IProgressMonitor monitor) throws JiraException {
		return new Resolution[0];
	}

	@Override
	public JiraStatus[] getStatuses(IProgressMonitor monitor) throws JiraException {
		return new JiraStatus[0];
	}

}
