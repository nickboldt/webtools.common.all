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

package org.eclipse.wst.common.modulecore.internal.operation;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

public abstract class ComponentCreationOperation extends WTPOperation {

	public ComponentCreationOperation(ComponentCreationDataModel dataModel) {
		super(dataModel);
	}

	public ComponentCreationOperation() {
		super();
	}
}