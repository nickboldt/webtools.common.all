/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.ui.internal.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.internal.ui.SearchPreferencePage;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

public class SearchResultTableContentProvider implements IStructuredContentProvider, IFileSearchContentProvider {
	
	private final Object[] EMPTY_ARR= new Object[0];
	
	private SearchResultPage fPage;
	private AbstractTextSearchResult fResult;

	public SearchResultTableContentProvider(SearchResultPage page) {
		fPage= page;
	}
	
	public void dispose() {
		// nothing to do
	}
	
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SearchResult) {
			Object[] elements= ((SearchResult)inputElement).getElements();
			int tableLimit= SearchPreferencePage.getTableLimit();
			if (SearchPreferencePage.isTableLimited() && elements.length > tableLimit) {
				Object[] shownElements= new Object[tableLimit];
				System.arraycopy(elements, 0, shownElements, 0, tableLimit);
				return shownElements;
			}
			return elements;
		}
		return EMPTY_ARR;
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof SearchResult) {
			fResult= (SearchResult) newInput;
		}
	}
	
	public void elementsChanged(Object[] updatedElements) {
		TableViewer viewer= getViewer();
		boolean tableLimited= SearchPreferencePage.isTableLimited();
		for (int i= 0; i < updatedElements.length; i++) {
			if (fResult.getMatchCount(updatedElements[i]) > 0) {
				if (viewer.testFindItem(updatedElements[i]) != null)
					viewer.update(updatedElements[i], null);
				else {
					if (!tableLimited || viewer.getTable().getItemCount() < SearchPreferencePage.getTableLimit())
						viewer.add(updatedElements[i]);
				}
			} else
				viewer.remove(updatedElements[i]);
		}
	}

	private TableViewer getViewer() {
		return (TableViewer) fPage.getViewer();
	}
	
	public void clear() {
		getViewer().refresh();
	}
}
