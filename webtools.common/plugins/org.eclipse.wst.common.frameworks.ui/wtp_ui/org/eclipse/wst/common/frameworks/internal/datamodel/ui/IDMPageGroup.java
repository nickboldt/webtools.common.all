/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public interface IDMPageGroup 
{
  public String getWizardID();
  
  public String getPageGroupID();
  
  public String getPageGroupInsertionID();
  
  public boolean getAllowsExtendedPages();
  
  public String getRequiredDataOperationToRun();
  
  public String getDataModelID();
  
  public DataModelWizardPage[] getExtendedPages( IDataModel dataModel );
  
  public IDMPageHandler getPageHandler( IDataModel dataModel );
  
  public IDMPageGroupHandler getPageGroupHandler( IDataModel dataModel );
}
