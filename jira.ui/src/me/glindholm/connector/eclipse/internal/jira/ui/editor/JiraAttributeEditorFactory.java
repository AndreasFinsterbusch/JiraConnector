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

package me.glindholm.connector.eclipse.internal.jira.ui.editor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.mylyn.internal.tasks.ui.editors.CheckboxMultiSelectAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.PersonAttributeEditor;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.services.IServiceLocator;

import me.glindholm.connector.eclipse.internal.jira.core.JiraConstants;
import me.glindholm.connector.eclipse.internal.jira.core.JiraTaskDataHandler;

class JiraAttributeEditorFactory extends AttributeEditorFactory {
	private final TaskDataModel model;

	public JiraAttributeEditorFactory(TaskDataModel model, TaskRepository taskRepository, IServiceLocator serviceLocator) {
		super(model, taskRepository, serviceLocator);
		this.model = model;
	}

	@Override
	public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {
		if (JiraTaskDataHandler.isTimeSpanAttribute(taskAttribute)) {
			return new TimeSpanAttributeEditor(model, taskAttribute);
		}
//		if (JiraUtil.isCustomDateTimeAttribute(taskAttribute)) {
//			String metaType = taskAttribute.getMetaData().getValue(IJiraConstants.META_TYPE);
//			if (JiraFieldType.DATETIME.getKey().equals(metaType)) {
//				return new DateTimeAttributeEditor(model, taskAttribute, true);
//			} else if (JiraFieldType.DATE.getKey().equals(metaType)) {
//				return new DateTimeAttributeEditor(model, taskAttribute, false);
//			}
//		}
		if (TaskAttribute.TYPE_MULTI_SELECT.equals(type)) {
			CheckboxMultiSelectAttributeEditor attributeEditor = new CheckboxMultiSelectAttributeEditor(model,
					taskAttribute);
			attributeEditor.setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
			return attributeEditor;
		}
		if (JiraConstants.TYPE_NUMBER.equals(type)) {
			return new NumberAttributeEditor(model, taskAttribute);
		}

		if (TaskAttribute.TYPE_PERSON.equals(type)) {
			return new PersonAttributeEditor(model, taskAttribute) {
				@Override
				public String getValue() {
					if (isReadOnly()) {
						IRepositoryPerson repositoryPerson = getAttributeMapper().getRepositoryPerson(
								getTaskAttribute());
						if (repositoryPerson != null) {
							final String name = repositoryPerson.getName();
							if (name != null) {
								return name;
							} else {
								return repositoryPerson.getPersonId();
							}
						}
					}

					return super.getValue();
				}

				@Override
				public void createControl(Composite parent, FormToolkit toolkit) {
					super.createControl(parent, toolkit);
					IRepositoryPerson repositoryPerson = getAttributeMapper().getRepositoryPerson(getTaskAttribute());
					if (repositoryPerson != null) {
						if (isReadOnly()) {
							if (!StringUtils.isBlank(repositoryPerson.getPersonId())) {
								getControl().setToolTipText(repositoryPerson.getPersonId());
							}
						} else {
							// add tooltip with user display name for editbox in which we just display user id
							if (!StringUtils.isBlank(repositoryPerson.getName())) {
								if (getText() != null) {
									getText().setToolTipText(repositoryPerson.getName());
								}
							}
						}
					}
				}
			};
		}
		return super.createEditor(type, taskAttribute);
	}
}