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

package me.glindholm.connector.eclipse.internal.jira.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

import me.glindholm.connector.eclipse.internal.jira.core.model.JiraComponent;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraIssueType;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraPriority;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraProject;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraResolution;
import me.glindholm.connector.eclipse.internal.jira.core.model.JiraVersion;
import me.glindholm.connector.eclipse.internal.jira.core.service.JiraClient;
import me.glindholm.connector.eclipse.internal.jira.core.util.JiraUtil;

/**
 * @author Steffen Pingel
 */
public class JiraAttributeMapper extends TaskAttributeMapper implements ITaskAttributeMapper2 {

    private final JiraClient client;

    public JiraAttributeMapper(TaskRepository taskRepository, JiraClient client) {
        super(taskRepository);
        this.client = client;
    }

    @Override
    public Date getDateValue(TaskAttribute attribute) {
        if (JiraUtil.isCustomDateTimeAttribute(attribute)) {
            try {
                //				return JiraRssHandler.getOffsetDateTimeFormat().parse(attribute.getValue());
                return client.getDateTimeFormat().parse(attribute.getValue());
            } catch (ParseException e) {
                return null;
            }
        } else if (JiraUtil.isCustomDateAttribute(attribute)) {
            try {
                //				return JiraRssHandler.getOffsetDateTimeFormat().parse(attribute.getValue());
                return client.getDateFormat().parse(attribute.getValue());
            } catch (ParseException e) {
                return null;
            }
        } else {
            return super.getDateValue(attribute);
        }
    }

    @Override
    public void setDateValue(TaskAttribute attribute, Date date) {
        if (JiraUtil.isCustomDateTimeAttribute(attribute)) {
            attribute.setValue(date != null ? new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.US).format(date) : "");
        } else {
            super.setDateValue(attribute, date);
        }
    }

    @Override
    public String getValueLabel(TaskAttribute taskAttribute) {
        if (JiraTaskDataHandler.isTimeSpanAttribute(taskAttribute)) {
            return JiraUtil.getTimeFormat(getTaskRepository()).format(getLongValue(taskAttribute));
        }
        return super.getValueLabel(taskAttribute);
    }

    @Override
    public List<String> getValueLabels(TaskAttribute taskAttribute) {
        if (JiraTaskDataHandler.isTimeSpanAttribute(taskAttribute)) {
            return Collections.singletonList(JiraUtil.getTimeFormat(getTaskRepository()).format(
                    getLongValue(taskAttribute)));
        }
        return super.getValueLabels(taskAttribute);
    }

    @Override
    public String mapToRepositoryKey(TaskAttribute parent, String key) {
        if (TaskAttribute.COMPONENT.equals(key)) {
            return JiraAttribute.COMPONENTS.id();
        } else if (TaskAttribute.TASK_KIND.equals(key)) {
            return JiraAttribute.TYPE.id();
        } else if (TaskAttribute.DATE_DUE.equals(key)) {
            return JiraAttribute.DUE_DATE.id();
        } else if (TaskAttribute.VERSION.equals(key)) {
            return JiraAttribute.AFFECTSVERSIONS.id();
        }
        return super.mapToRepositoryKey(parent, key);
    }

    @Override
    public Map<String, String> getOptions(TaskAttribute attribute) {
        Map<String, String> options = getRepositoryOptions(attribute);
        return options != null ? options : super.getOptions(attribute);
    }

    @Override
    public Map<String, String> getRepositoryOptions(TaskAttribute attribute) {
        if (client.getCache().hasDetails()) {
            Map<String, String> options = new LinkedHashMap<>();
            if (JiraAttribute.PROJECT.id().equals(attribute.getId())) {
                JiraProject[] jiraProjects = client.getCache().getProjects();
                for (JiraProject jiraProject : jiraProjects) {
                    options.put(jiraProject.getId(), jiraProject.getName());
                }
                return options;
            } else if (JiraAttribute.RESOLUTION.id().equals(attribute.getId())) {
                JiraResolution[] jiraResolutions = client.getCache().getResolutions();
                for (JiraResolution resolution : jiraResolutions) {
                    options.put(resolution.getId(), resolution.getName());
                }
                return options;
            } else if (JiraAttribute.PRIORITY.id().equals(attribute.getId())) {
                JiraPriority[] jiraPriorities = client.getCache().getPriorities();
                for (JiraPriority priority : jiraPriorities) {
                    options.put(priority.getId(), priority.getName());
                }
                return options;
            } else {
                TaskAttribute projectAttribute = attribute.getTaskData()
                        .getRoot()
                        .getMappedAttribute(JiraAttribute.PROJECT.id());
                if (projectAttribute != null) {
                    JiraProject project = client.getCache().getProjectById(projectAttribute.getValue());
                    if (project != null && project.hasDetails()) {
                        if (JiraAttribute.COMPONENTS.id().equals(attribute.getId())) {
                            for (JiraComponent component : project.getComponents()) {
                                options.put(component.getId(), component.getName());
                            }
                            return options;
                        } else if (JiraAttribute.AFFECTSVERSIONS.id().equals(attribute.getId())) {
                            for (JiraVersion version : project.getVersions()) {
                                if (!version.isArchived() || attribute.getValues().contains(version.getId())) {
                                    options.put(version.getId(), version.getName());
                                }
                            }
                            return options;
                        } else if (JiraAttribute.FIXVERSIONS.id().equals(attribute.getId())) {
                            for (JiraVersion version : project.getVersions()) {
                                if (!version.isArchived() || attribute.getValues().contains(version.getId())) {
                                    options.put(version.getId(), version.getName());
                                }
                            }
                            return options;
                        } else if (JiraAttribute.TYPE.id().equals(attribute.getId())) {
                            boolean isSubTask = JiraTaskDataHandler.hasSubTaskType(attribute);
                            JiraIssueType[] jiraIssueTypes = project.getIssueTypes();
                            if (jiraIssueTypes == null) {
                                jiraIssueTypes = client.getCache().getIssueTypes();
                            }
                            for (JiraIssueType issueType : jiraIssueTypes) {
                                if (!isSubTask || issueType.isSubTaskType()) {
                                    options.put(issueType.getId(), issueType.getName());
                                }
                            }
                            return options;
                        }
                    }
                }
            }
        }
        return null;
    }

}