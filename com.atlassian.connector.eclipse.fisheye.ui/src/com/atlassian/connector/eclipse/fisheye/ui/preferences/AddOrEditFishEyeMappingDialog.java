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

package com.atlassian.connector.eclipse.fisheye.ui.preferences;

import com.atlassian.connector.eclipse.internal.fisheye.core.FishEyeClientManager;
import com.atlassian.connector.eclipse.internal.fisheye.core.FishEyeCorePlugin;
import com.atlassian.connector.eclipse.internal.fisheye.core.client.FishEyeClient;
import com.atlassian.connector.eclipse.internal.fisheye.core.client.FishEyeClientData;
import com.atlassian.connector.eclipse.internal.fisheye.ui.FishEyeImages;
import com.atlassian.connector.eclipse.internal.fisheye.ui.FishEyeUiPlugin;
import com.atlassian.connector.eclipse.ui.dialogs.ProgressDialog;
import com.atlassian.connector.eclipse.ui.team.TeamUiUtils;
import com.atlassian.theplugin.commons.util.MiscUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AddOrEditFishEyeMappingDialog extends ProgressDialog {

	private final class OnShowHandler extends ShellAdapter {
		public void shellActivated(ShellEvent event) {
			updateOkButtonState();
			try {
				run(true, false, runnable);
			} catch (InvocationTargetException e) {
				StatusHandler.log(new Status(IStatus.ERROR, FishEyeUiPlugin.PLUGIN_ID,
						"Cannot collect data about workspace SCM repositories", e.getCause()));
				setErrorMessage("Cannot collect data about workspace SCM repositories");
				return;
			} catch (InterruptedException e) {
				StatusHandler.log(new Status(IStatus.ERROR, FishEyeUiPlugin.PLUGIN_ID,
						"Cannot collect data about workspace SCM repositories", e));
				setErrorMessage("Cannot collect data about workspace SCM repositories");
				return;
			}
		}
	}

	private ComboViewer fishEyeServerCombo;

	private ComboViewer fishEyeRepoCombo;

	private Button okButton;

	private FishEyeMappingConfiguration cfg;

	private Text scmPathEdit;

	private final Set<TaskRepository> taskRepositories;

	private final FishEyeClientManager fishEyeClientManager;

	private Collection<String> repositories;

	private final IRunnableWithProgress runnable = new IRunnableWithProgress() {

		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Collecting data about workspace SCM repositories", IProgressMonitor.UNKNOWN);
			repositories = TeamUiUtils.getRepositories(monitor);
			monitor.done();
		}
	};

	/**
	 * Creates dialog in "edit" mode - with initial selections
	 */
	public AddOrEditFishEyeMappingDialog(Shell parentShell, FishEyeMappingConfiguration initialConfiguration,
			Collection<TaskRepository> taskRepositories, FishEyeClientManager fishEyeClientManager) {
		super(parentShell);
		this.fishEyeClientManager = fishEyeClientManager;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		cfg = initialConfiguration;
		this.taskRepositories = new HashSet<TaskRepository>(taskRepositories);

	}

	/**
	 * Creates dialog in "add" mode
	 */
	public AddOrEditFishEyeMappingDialog(Shell parentShell, Collection<TaskRepository> taskRepositories,
			FishEyeClientManager fishEyeClientManager) {
		this(parentShell, null, taskRepositories, fishEyeClientManager);
	}

	public FishEyeMappingConfiguration getCfg() {
		return cfg;
	}

	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	@Override
	protected void okPressed() {
		final TaskRepository server = getSelectedServer();
		final String repo = getSelectedRepo();
		if (server == null || repo == null) {
			return;
		}
		cfg = new FishEyeMappingConfiguration(scmPathEdit.getText(), server.getUrl(), repo);
		super.okPressed();
	}

	private TaskRepository getSelectedServer() {
		final Object server = ((IStructuredSelection) fishEyeServerCombo.getSelection()).getFirstElement();
		if (server instanceof TaskRepository) {
			return (TaskRepository) server;
		}
		return null;
	}

	private String getSelectedRepo() {
		final Object repo = ((IStructuredSelection) fishEyeRepoCombo.getSelection()).getFirstElement();
		if (repo instanceof String) {
			return (String) repo;
		}
		return null;

	}

	/**
	 * for easy testing
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Display display = new Display();
		final Shell shell = new Shell(display);
		final TaskRepository stac = new TaskRepository(FishEyeCorePlugin.CONNECTOR_KIND,
				"https://studio.atlassian.com/source");
		stac.setRepositoryLabel("StAC");
		stac.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("user", "pass"), false);

		final TaskRepository localFe = new TaskRepository(FishEyeCorePlugin.CONNECTOR_KIND, "http://localhost:8060");
		localFe.setRepositoryLabel("Local FishEye 1.6.4");
		localFe.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("wseliga", "wseliga"),
				false);
		FishEyeMappingConfiguration cfg = new FishEyeMappingConfiguration("http://demo", localFe.getUrl(), "TST");
		new AddOrEditFishEyeMappingDialog(shell, cfg, MiscUtil.buildArrayList(stac, localFe), new FishEyeClientManager(
				File.createTempFile("elipseconnector", "tmp"))).open();
	}

	@Override
	protected Control createPageControls(Composite parent) {
		getShell().setText((cfg == null ? "Add" : "Edit") + " FishEye Mapping");
		setTitle("SCM to FishEye Mapping");
		setMessage("Define how locally checked-out projects map to FishEye server and repository");
		GridLayout layout = new GridLayout(3, false);
		layout.makeColumnsEqualWidth = false;
		parent.setLayout(layout);
		createLabel(parent, "SCM Path");
		scmPathEdit = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1).hint(400, SWT.DEFAULT).applyTo(scmPathEdit);
		final Button scmButton = new Button(parent, SWT.PUSH);
		scmButton.setText("...");
		GridDataFactory.fillDefaults().grab(false, false).span(1, 1).applyTo(scmButton);
		scmButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				final ListDialog ld = new ListDialog(getShell());
				ld.setAddCancelButton(true);
				ld.setContentProvider(new ArrayContentProvider());
				ld.setLabelProvider(new LabelProvider());
				if (repositories != null) {
					ld.setInput(repositories.toArray());
					ld.setTitle("Select SCM Repository");
					if (ld.open() == Window.OK) {
						final Object[] result = ld.getResult();
						if (result != null && result.length > 0) {
							scmPathEdit.setText(result[0].toString());
						}
					}
				}

			}

		});

		createLabel(parent, "FishEye Server");
		fishEyeServerCombo = new ComboViewer(new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY));
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(fishEyeServerCombo.getControl());

		createLabel(parent, "FishEye Repository");
		fishEyeRepoCombo = new ComboViewer(new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY));
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(fishEyeRepoCombo.getControl());
		fishEyeRepoCombo.setContentProvider(new ArrayContentProvider());
		fishEyeRepoCombo.setLabelProvider(new LabelProvider());

		final Button updateServerDataButton = new Button(parent, SWT.PUSH);
		updateServerDataButton.setText("Refresh");
		GridDataFactory.fillDefaults().grab(false, false).span(3, 1).align(SWT.RIGHT, SWT.FILL).applyTo(
				updateServerDataButton);

		fishEyeServerCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof TaskRepository) {
					TaskRepository taskRepository = (TaskRepository) element;
					return taskRepository.getRepositoryLabel() + " (" + taskRepository.getUrl() + ")";
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				return FishEyeImages.getImage(FishEyeImages.FISHEYE_ICON);
			}

		});
		fishEyeServerCombo.setContentProvider(new ArrayContentProvider());
		fishEyeServerCombo.setInput(taskRepositories.toArray());

		if (cfg != null) {
			scmPathEdit.setText(cfg.getScmPath());
			if (cfg.getFishEyeServer() != null) {
				final TaskRepository repository = findByUrl(cfg.getFishEyeServer());
				if (repository != null) {
					fishEyeServerCombo.setSelection(new StructuredSelection(repository));
					Set<String> cachedRepositories = fishEyeClientManager.getClient(repository)
							.getClientData()
							.getCachedRepositories();
					// handling the case where the repository could have been deleted or hasn't been yet fetched by the client 
					if (cfg != null && cfg.getFishEyeRepo() != null) {
						if (!cachedRepositories.contains(cfg.getFishEyeRepo())) {
							cachedRepositories = new HashSet<String>(cachedRepositories);
							cachedRepositories.add(cfg.getFishEyeRepo());
						}
					}
					final Object[] cachedRepositoriesArray = cachedRepositories.toArray();
					Arrays.sort(cachedRepositoriesArray);
					fishEyeRepoCombo.setInput(cachedRepositoriesArray);
				}
			}
		}

		// only now listeners - as previously they could have been triggered by restoring default selection (edit mode)
		fishEyeServerCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection.getFirstElement() instanceof TaskRepository) {
						final TaskRepository taskRepository = (TaskRepository) selection.getFirstElement();
						final FishEyeClientData clientData = fishEyeClientManager.getClient(taskRepository)
								.getClientData();
						fishEyeRepoCombo.setInput(clientData.getCachedRepositories().toArray());
					}
				}
				updateOkButtonState();
			}
		});

		if (cfg != null && cfg.getFishEyeRepo() != null) {
			fishEyeRepoCombo.setSelection(new StructuredSelection(cfg.getFishEyeRepo()));
		}

		fishEyeRepoCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateOkButtonState();
			}
		});

		updateServerDataButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (fishEyeServerCombo.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) fishEyeServerCombo.getSelection();
					if (selection.getFirstElement() instanceof TaskRepository) {
						final TaskRepository taskRepository = (TaskRepository) selection.getFirstElement();
						updateServerData(taskRepository);
					}
				}
				updateOkButtonState();
			}
		});

		getShell().addShellListener(new OnShowHandler());
		return parent;
	}

	private TaskRepository findByUrl(String url) {
		for (TaskRepository taskRepository : taskRepositories) {
			if (taskRepository.getUrl().equals(url)) {
				return taskRepository;
			}
		}
		return null;
	}

	private void updateOkButtonState() {
		boolean isEnabled = (getSelectedRepo() != null && getSelectedServer() != null && scmPathEdit.getText().length() > 0);
		okButton.setEnabled(isEnabled);
	}

	private void updateServerData(final TaskRepository taskRepository) {
		try {
			fishEyeRepoCombo.getControl().setEnabled(false);
			fishEyeServerCombo.getControl().setEnabled(false);
			final Button button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(false);
			}
			//	getButton(IDialogConstants.OK_ID).setEnabled(false);
			run(true, true, new IRunnableWithProgress() {

				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					final FishEyeClient client = fishEyeClientManager.getClient(taskRepository);
					try {
						client.updateRepositoryData(monitor, taskRepository);
					} catch (final CoreException e) {
						StatusHandler.log(new Status(IStatus.ERROR, FishEyeUiPlugin.PLUGIN_ID, e.getMessage(), e));
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (!monitor.isCanceled()) {
									setErrorMessage(e.getMessage());
								}
							}
						});
						return;
					}
					final FishEyeClientData clientData = client.getClientData();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (!monitor.isCanceled()) {
								final ISelection oldSelection = fishEyeRepoCombo.getSelection();
								fishEyeRepoCombo.setInput(clientData.getCachedRepositories().toArray());
								fishEyeRepoCombo.setSelection(oldSelection);
								setErrorMessage(null);
							}
						}
					});

				}

			});
		} catch (InvocationTargetException e) {
			if (e.getCause() != null) {
				setErrorMessage(e.getCause().getMessage());
			}
			StatusHandler.log(new Status(IStatus.ERROR, FishEyeUiPlugin.PLUGIN_ID, e.getMessage(), e));
		} catch (Exception e) {
			setErrorMessage(e.getMessage() != null ? e.getMessage() : "Exception:  " + e.getClass().getName());
			StatusHandler.log(new Status(IStatus.ERROR, FishEyeUiPlugin.PLUGIN_ID, e.getMessage(), e));
		} finally {
			fishEyeRepoCombo.getControl().setEnabled(true);
			fishEyeServerCombo.getControl().setEnabled(true);
		}

	}

	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

}