/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;

import java.util.Map;

import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;


public interface IEditModelFactory {

	public abstract EditModel createEditModelForRead(String editModelID, EMFWorkbenchContext context);

	public abstract EditModel createEditModelForRead(String editModelID, EMFWorkbenchContext context, Map params);

	public abstract EditModel createEditModelForWrite(String editModelID, EMFWorkbenchContext context);

	public abstract EditModel createEditModelForWrite(String editModelID, EMFWorkbenchContext context, Map params);

	public String getCacheID(String editModelID, Map params);

	public void setLoadKnownResourcesAsReadOnly(boolean value);
}
