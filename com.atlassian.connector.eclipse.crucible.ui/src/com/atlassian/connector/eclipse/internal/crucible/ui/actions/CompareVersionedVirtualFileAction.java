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

package com.atlassian.connector.eclipse.internal.crucible.ui.actions;

import com.atlassian.connector.eclipse.internal.crucible.ui.CrucibleUiPlugin;
import com.atlassian.connector.eclipse.internal.crucible.ui.IReviewAction;
import com.atlassian.connector.eclipse.internal.crucible.ui.IReviewActionListener;
import com.atlassian.connector.eclipse.internal.crucible.ui.annotations.CrucibleCompareAnnotationModel;
import com.atlassian.connector.eclipse.ui.team.ICompareAnnotationModel;
import com.atlassian.connector.eclipse.ui.team.TeamUiUtils;
import com.atlassian.theplugin.commons.VersionedVirtualFile;
import com.atlassian.theplugin.commons.crucible.api.model.CrucibleFileInfo;
import com.atlassian.theplugin.commons.crucible.api.model.Review;
import com.atlassian.theplugin.commons.crucible.api.model.VersionedComment;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.PlatformUI;

import java.lang.reflect.InvocationTargetException;

/**
 * Action to open the compare editor given a crucible file with 2 revisions
 * 
 * @author Shawn Minto
 */
public class CompareVersionedVirtualFileAction extends Action implements IReviewAction {

	private final CrucibleFileInfo crucibleFile;

	private IReviewActionListener actionListener;

	private final Review review;

	private final VersionedComment versionedComment;

	private IPartListener editorOpenedListener;

	public CompareVersionedVirtualFileAction(CrucibleFileInfo crucibleFile, VersionedComment versionedComment,
			Review review) {
		this.crucibleFile = crucibleFile;
		this.review = review;
		this.versionedComment = versionedComment;
	}

	public CompareVersionedVirtualFileAction(CrucibleFileInfo crucibleFile, Review review) {
		this(crucibleFile, null, review);
	}

	@Override
	public final void run() {
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					final VersionedVirtualFile newVirtualFile = crucibleFile.getFileDescriptor();

					final VersionedVirtualFile oldVirtualFile = crucibleFile.getOldFileDescriptor();

					final ICompareAnnotationModel annotationModel = new CrucibleCompareAnnotationModel(crucibleFile,
							review, versionedComment);

					TeamUiUtils.openCompareEditor(newVirtualFile.getRepoUrl(), newVirtualFile.getUrl(),
							oldVirtualFile.getRevision(), newVirtualFile.getRevision(), annotationModel, monitor);
				}

			});
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(IStatus.ERROR, CrucibleUiPlugin.PLUGIN_ID, e.getMessage(), e));
		} catch (InterruptedException e) {
			StatusHandler.log(new Status(IStatus.ERROR, CrucibleUiPlugin.PLUGIN_ID, e.getMessage(), e));
		} catch (OperationCanceledException e) {
			// ignore since the user requested a cancel
		}
		if (actionListener != null) {
			actionListener.actionRan(this);
		}
	}

	public void setActionListener(IReviewActionListener listener) {
		this.actionListener = listener;
	}
}