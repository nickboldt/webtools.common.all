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
package org.eclipse.wst.common.componentcore.datamodel;

import java.util.Collections;
import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.FacetProjectCreationOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class FacetProjectCreationDataModelProvider extends AbstractDataModelProvider implements IFacetProjectCreationDataModelProperties {

	public FacetProjectCreationDataModelProvider() {
		super();
	}

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(FACET_PROJECT_NAME);
		names.add(FACET_DM_LIST);
		names.add(FACET_RUNTIME);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (FACET_DM_LIST.equals(propertyName)) {
			return Collections.EMPTY_LIST;
		}
		return super.getDefaultProperty(propertyName);
	}

	public IDataModelOperation getDefaultOperation() {
		return new FacetProjectCreationOperation(model);
	}

}
