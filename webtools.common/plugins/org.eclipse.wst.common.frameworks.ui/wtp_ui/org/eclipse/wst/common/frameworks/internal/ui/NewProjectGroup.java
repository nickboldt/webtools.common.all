/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;

public class NewProjectGroup implements IProjectCreationPropertiesNew {
	private IDataModel model;
	public Text projectNameField = null;
	protected Text locationPathField = null;
	protected Button browseButton = null;
	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 305;
	// default values
	private String defProjectNameLabel = WTPCommonUIResourceHandler.Name_; //$NON-NLS-1$
	private String defBrowseButtonLabel = WTPCommonUIResourceHandler.Browse_;//$NON-NLS-1$
	private static final String defDirDialogLabel = "Directory"; //$NON-NLS-1$

	private DataModelSynchHelper synchHelper;

	public NewProjectGroup(Composite parent, IDataModel model) {
		this.model = model;
		synchHelper = new DataModelSynchHelper(model);
		buildComposites(parent);
	}

	/**
	 * Create the controls within this composite
	 */
	public void buildComposites(Composite parent) {
		createProjectNameGroup(parent);
		createProjectLocationGroup(parent);
		projectNameField.setFocus();
	}

	private final void createProjectNameGroup(Composite parent) {
		Font font = parent.getFont();
		// project specification group
		Composite projectGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label
		Label projectLabel = new Label(projectGroup, SWT.NONE);
		projectLabel.setFont(font);
		projectLabel.setText(defProjectNameLabel);

		// new project name entry field
		projectNameField = new Text(projectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		projectNameField.setLayoutData(data);
		projectNameField.setFont(font);
		synchHelper.synchText(projectNameField, PROJECT_NAME, new Control[]{projectLabel});
	}

	/**
	 * Creates the project location specification controls.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private final void createProjectLocationGroup(Composite parent) {

		Font font = parent.getFont();
		// project specification group
		Group projectGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectGroup.setFont(font);
		projectGroup.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectContentsGroupLabel);

		final Button useDefaultsButton = new Button(projectGroup, SWT.CHECK | SWT.RIGHT);
		useDefaultsButton.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_useDefaultLabel);
		useDefaultsButton.setFont(font);
		synchHelper.synchCheckbox(useDefaultsButton, USE_DEFAULT_LOCATION, null);

		GridData buttonData = new GridData();
		buttonData.horizontalSpan = 3;
		useDefaultsButton.setLayoutData(buttonData);

		createUserSpecifiedProjectLocationGroup(projectGroup);
	}

	private void createUserSpecifiedProjectLocationGroup(Composite projectGroup) {
		Font font = projectGroup.getFont();
		// location label
		final Label locationLabel = new Label(projectGroup, SWT.NONE);
		locationLabel.setFont(font);
		locationLabel.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_locationLabel);

		// project location entry field
		locationPathField = new Text(projectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		locationPathField.setLayoutData(data);
		locationPathField.setFont(font);

		// browse button
		browseButton = new Button(projectGroup, SWT.PUSH);
		browseButton.setFont(font);
		browseButton.setText(defBrowseButtonLabel);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleLocationBrowseButtonPressed();
			}
		});

		final IDataModel localModel = model;

		class LocationListener implements ModifyListener, IDataModelListener {
			private boolean typing = false;

			public void modifyText(ModifyEvent e) {
				if (!localModel.getBooleanProperty(USE_DEFAULT_LOCATION)) {
					try {
						typing = true;
						localModel.setProperty(USER_DEFINED_LOCATION, locationPathField.getText());
					} finally {
						typing = false;
					}
				}
			}

			public void propertyChanged(DataModelEvent event) {
				boolean useDefault = localModel.getBooleanProperty(USE_DEFAULT_LOCATION);
				if (USE_DEFAULT_LOCATION.equals(event.getPropertyName())) {
					locationLabel.setEnabled(!useDefault);
					locationPathField.setEnabled(!useDefault);
					browseButton.setEnabled(!useDefault);
					if (useDefault) {
						locationPathField.setText(localModel.getStringProperty(DEFAULT_LOCATION));
					} else {
						locationPathField.setText(localModel.getStringProperty(USER_DEFINED_LOCATION));
					}
				} else if (!typing) {
					if ((useDefault && DEFAULT_LOCATION.equals(event.getPropertyName())) || (!useDefault && USER_DEFINED_LOCATION.equals(event.getPropertyName()))) {
						locationPathField.setText((String) event.getProperty());
					}
				}
			}
		}

		LocationListener listener = new LocationListener();

		listener.propertyChanged(new DataModelEvent(model, USE_DEFAULT_LOCATION, IDataModel.VALUE_CHG));

		locationPathField.addModifyListener(listener);
		model.addListener(listener);
	}

	/**
	 * Open an appropriate directory browser
	 */
	protected void handleLocationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(locationPathField.getShell());
		dialog.setMessage(defDirDialogLabel);
		String dirName = model.getStringProperty(USER_DEFINED_LOCATION);
		if (dirName.trim().length() == 0) {
			dirName = new Path(model.getStringProperty(DEFAULT_LOCATION)).removeLastSegments(1).toOSString();
		}

		if ((dirName != null) && (dirName.length() != 0)) {
			File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(dirName);
			}
		}
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			model.setProperty(USER_DEFINED_LOCATION, selectedDirectory);
		}
	}

	public void dispose() {
		model.removeListener(synchHelper);
		synchHelper.dispose();
		model = null;
	}
}