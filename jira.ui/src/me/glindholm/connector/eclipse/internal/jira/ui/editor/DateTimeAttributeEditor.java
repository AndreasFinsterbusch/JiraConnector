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

package me.glindholm.connector.eclipse.internal.jira.ui.editor;

import java.text.DateFormat;
import java.text.ParseException;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import me.glindholm.connector.eclipse.internal.jira.core.service.JiraLocalConfiguration;
import me.glindholm.connector.eclipse.internal.jira.core.util.JiraUtil;

/**
 * @author Steffen Pingel
 */
public class DateTimeAttributeEditor extends AbstractAttributeEditor {

    private Text text;

    private final DateFormat format;

    public DateTimeAttributeEditor(TaskDataModel model, TaskAttribute taskAttribute, boolean includeTime) {
        super(model, taskAttribute);
        JiraLocalConfiguration configuration = JiraUtil.getLocalConfiguration(model.getTaskRepository());
        if (includeTime) {
            this.format = configuration.getDateTimeFormat();
        } else {
            this.format = configuration.getDateFormat();
        }
    }

    protected Text getText() {
        return text;
    }

    @Override
    public void createControl(Composite parent, FormToolkit toolkit) {
        if (isReadOnly()) {
            text = new Text(parent, SWT.FLAT | SWT.READ_ONLY);
            text.setFont(JFaceResources.getDefaultFont());
            text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
            text.setText(getValue());
        } else {
            text = toolkit.createText(parent, getValue(), SWT.FLAT);
            text.setFont(JFaceResources.getDefaultFont());
            text.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    setValue(text.getText());
                    //EditorUtil.ensureVisible(text);
                }
            });
        }
        toolkit.adapt(text, false, false);
        setControl(text);
    }

    public String getValue() {
        return format.format(getAttributeMapper().getDateValue(getTaskAttribute()));
    }

    public void setValue(String text) {
        try {
            getAttributeMapper().setDateValue(getTaskAttribute(), format.parse(text));
        } catch (ParseException e) {
            // XXX ignore
        }
        attributeChanged();
    }
}
