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
/*
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.ui.provisional.IDMExtendedPageHandler;
import org.eclipse.wst.common.frameworks.datamodel.ui.provisional.IDMExtendedWizardPage;
import org.eclipse.wst.common.frameworks.internal.ConfigurationElementWrapper;
import org.eclipse.wst.common.frameworks.ui.IExtendedPageHandler;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class DMWizardPageFactoryElement extends ConfigurationElementWrapper {

	static final String ATT_CLASS_NAME = "className"; //$NON-NLS-1$

	protected String className;
	protected DMWizardExtensionFactory wizardPageFactory;
	protected boolean isPageFactoryInitialized;
	protected String pageGroupID;

	/**
	 * @param element
	 */
	public DMWizardPageFactoryElement(IConfigurationElement element, String pageGroupID) {
		super(element);
		className = element.getAttribute(ATT_CLASS_NAME);
		this.pageGroupID = pageGroupID;
	}

	public IDMExtendedPageHandler createPageHandler(IDataModel dataModel) {
		if (!isPageFactoryInitialized)
			initPageFactory();
		if (wizardPageFactory == null)
			return null;

		IDMExtendedPageHandler handler = wizardPageFactory.createPageHandler(dataModel, pageGroupID);
		return handler;
	}

	public IDMExtendedWizardPage[] createPageGroup(IDataModel dataModel) {
		if (!isPageFactoryInitialized)
			initPageFactory();

		if (wizardPageFactory == null)
			return null;

		IDMExtendedWizardPage[] pages = wizardPageFactory.createPageGroup(dataModel, pageGroupID);
		for (int i = 0; i < pages.length; i++) {
			pages[i].setGroupID(pageGroupID);
		}
		return pages;
	}

	private void initPageFactory() {
		try {
			wizardPageFactory = (DMWizardExtensionFactory) element.createExecutableExtension(ATT_CLASS_NAME);
		} catch (CoreException e) {
			Logger.getLogger().logError("Error getting page factory: " + className); //$NON-NLS-1$ 
			Logger.getLogger().logError(e);
		} finally {
			isPageFactoryInitialized = true;
		}
	}


}