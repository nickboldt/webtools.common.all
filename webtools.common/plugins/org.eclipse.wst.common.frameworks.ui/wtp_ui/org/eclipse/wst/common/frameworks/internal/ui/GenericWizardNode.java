/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.graphics.Point;

/**
 * @version 1.0
 * @author
 */
public abstract class GenericWizardNode implements IWizardNode {

	protected IWizard wizard;

	/**
	 * Constructor for GenericWizardNode.
	 */
	public GenericWizardNode() {
		super();
	}

	/*
	 * @see IWizardNode#dispose()
	 */
	public void dispose() {
		if (wizard != null)
			wizard.dispose();
	}

	/*
	 * @see IWizardNode#getContents()
	 */
	public Point getContents() {
		return null;
	}

	public final IWizard getWizard() {
		if (wizard == null)
			wizard = createWizard();
		return wizard;
	}

	/**
	 * Subclasses must override to create the wizard
	 */
	protected abstract IWizard createWizard();

	/*
	 * @see IWizardNode#isContentCreated()
	 */
	public boolean isContentCreated() {
		return wizard != null;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizardNode#getExtent()
	 */
	public Point getExtent() {
		return null;
	}

}