/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.internal.ui.IExtendedPageHandler;
import org.eclipse.wst.common.frameworks.internal.ui.IExtendedWizardPage;


/**
 * This interface is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class WizardExtensionFactory {

	public WizardExtensionFactory() {
		super();
	}

	public abstract IExtendedWizardPage[] createPageGroup(WTPOperationDataModel dataModel, String pageGroupID);

	/*
	 * this is optional
	 */
	public IExtendedPageHandler createPageHandler(WTPOperationDataModel dataModel, String pageGroupID) {
		return null;
	}
}