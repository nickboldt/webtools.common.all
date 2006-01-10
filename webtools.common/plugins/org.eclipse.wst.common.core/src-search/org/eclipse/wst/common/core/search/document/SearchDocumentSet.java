/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.document;

// The class is used to manage a set of search documents
// that have been constructed by various participants
//
public abstract class SearchDocumentSet
{
  public abstract SearchDocument getSearchDocument(String resourcePath, String participantId);
  public abstract SearchDocument[] getSearchDocuments(String participantId);
  public abstract void putSearchDocument(String participantId, SearchDocument document);
  public abstract SearchDocument _tempGetSearchDocumetn(String resourcePath); 
}
