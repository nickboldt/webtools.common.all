/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.snippets.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetsPlugin;
import org.eclipse.wst.common.snippets.internal.VariableItemHelper;
import org.eclipse.wst.common.snippets.internal.dnd.SnippetTextTransfer;
import org.eclipse.wst.common.snippets.internal.ui.EntrySerializer;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * An insertion implementation that supports ISnippetVariables. The content
 * string of the item can contain markers, in the form ${+variable+}, that
 * will be replaced with user-supplied values at insertion time.
 */
public class DefaultSnippetInsertion implements ISnippetInsertion {
	private IEditorPart fEditorPart;
	private ISnippetItem fItem = null;
	private Transfer[] fSupportedTransfers = null;

	public DefaultSnippetInsertion() {
		super();
	}

	protected Transfer[] createTransfers() {
		return new Transfer[]{SnippetTextTransfer.getTransferInstance(), TextTransfer.getInstance()};
	}

	/**
	 * @param editorPart
	 * @param textEditor
	 * @param document
	 * @param textSelection
	 * @throws BadLocationException
	 */
	protected void doInsert(IEditorPart editorPart, ITextEditor textEditor, IDocument document, ITextSelection textSelection) throws BadLocationException {
		String replacement = getInsertString(editorPart.getEditorSite().getShell());
		if (replacement != null && (replacement.length() > 0 || textSelection.getLength() > 0)) {
			// Update EOLs (bug 80231)
			replacement = StringUtils.replace(replacement, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			replacement = StringUtils.replace(replacement, "\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

			String preferredEOL = null;
			if (document instanceof IDocumentExtension4) {
				preferredEOL = ((IDocumentExtension4) document).getDefaultLineDelimiter();
			}
			else {
				Method getLineDelimiter = null;
				try {
					getLineDelimiter = document.getClass().getMethod("getLineDelimiter", new Class[0]); //$NON-NLS-1$
				}
				catch (NoSuchMethodException e) {
					// nothing, not unusual
				}
				if (getLineDelimiter != null) {
					try {
						preferredEOL = (String) getLineDelimiter.invoke(document, new Object[0]);
					}
					catch (IllegalAccessException e) {
						// nothing, not unusual for a non-visible method
					}
					catch (InvocationTargetException e) {
						// nothing, not unusual for a protected implementation
					}
				}

			}
			if (preferredEOL == null) {
				preferredEOL = System.getProperty("line.separator"); //$NON-NLS-1$
			}
			if (!"\n".equals(preferredEOL) && preferredEOL != null) { //$NON-NLS-1$
				replacement = StringUtils.replace(replacement, "\n", preferredEOL); //$NON-NLS-1$
			}

			document.replace(textSelection.getOffset(), textSelection.getLength(), replacement);
		}
	}

	public void dragSetData(DragSourceEvent event, ISnippetItem item) {
		boolean isSimpleText = TextTransfer.getInstance().isSupportedType(event.dataType);
		if (isSimpleText) {
			// set variable values to ""
			IWorkbenchWindow window = SnippetsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			Shell shell = null;
			if (window != null) {
				shell = window.getShell();
			}
			String content = VariableItemHelper.getInsertString(shell, item);
			// Update EOLs (bug 80231)
			String systemEOL = System.getProperty("line.separator"); //$NON-NLS-1$
			content = StringUtils.replace(content, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			content = StringUtils.replace(content, "\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			if (!"\n".equals(systemEOL) && systemEOL != null) { //$NON-NLS-1$
				content = StringUtils.replace(content, "\n", systemEOL); //$NON-NLS-1$
			}
			event.data = content;
		}
		else {
			/*
			 * All complex insertions send an XML encoded version of the item
			 * itself as the data. The drop action must use this to prompt the
			 * user for the correct insertion data
			 */
			event.data = EntrySerializer.getInstance().toXML(item);
		}
	}

	protected String getInsertString(Shell host) {
		if (getItem() == null)
			return ""; //$NON-NLS-1$
		String insertString = null;
		ISnippetItem item = getItem();
		if (item.getVariables().length > 0) {
			insertString = VariableItemHelper.getInsertString(host, item);
		}
		else {
			insertString = StringUtils.replace(item.getContentString(), "${cursor}", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return insertString;
	}

	/**
	 * Gets the Item.
	 * 
	 * @return the ISnippetItem
	 */
	public final ISnippetItem getItem() {
		return fItem;
	}

	public Transfer[] getTransfers() {
		if (fSupportedTransfers == null)
			fSupportedTransfers = createTransfers();
		return fSupportedTransfers;
	}

	public void insert(IEditorPart editorPart) {
		if (editorPart == null)
			return;
		ITextEditor textEditor = null;

		if (editorPart instanceof ITextEditor) {
			textEditor = (ITextEditor) editorPart;
		}
		if (textEditor == null) {
			// MultiPageEditorPart has no accessors for the source
			textEditor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		}
		if (textEditor == null) {
			// any errors here probably aren't really exceptional
			Method getTextEditor = null;
			try {
				getTextEditor = editorPart.getClass().getMethod("getTextEditor", new Class[0]); //$NON-NLS-1$
			}
			catch (NoSuchMethodException e) {
				// nothing, not unusual
			}
			Object editor = null;
			if (getTextEditor != null) {
				try {
					editor = getTextEditor.invoke(editorPart, new Object[0]);
				}
				catch (IllegalAccessException e) {
					// nothing, not unusual for a non-visible method
				}
				catch (InvocationTargetException e) {
					// nothing, not unusual for a protected implementation
				}
				if (editor instanceof IEditorPart && editor != editorPart && editor instanceof ITextEditor) {
					textEditor = (ITextEditor) editor;
				}
			}
		}
		if (textEditor != null) {
			insertIntoTextEditor(textEditor);
		}
	}

	private void insertIntoTextEditor(ITextEditor editor) {
		// find the text widget, its Document, and the current selection
		if (editor.isEditable()) {
			IDocumentProvider docprovider = editor.getDocumentProvider();
			ISelectionProvider selprovider = editor.getSelectionProvider();
			if (docprovider != null && selprovider != null) {
				IDocument document = docprovider.getDocument(editor.getEditorInput());
				ISelection selection = selprovider.getSelection();
				if (document != null && selection != null && selection instanceof ITextSelection) {
					ITextSelection textSel = (ITextSelection) selection;
					try {
						doInsert(editor, editor, document, textSel);
					}
					catch (Exception t) {
						Logger.logException("Could not insert " + getItem(), t); //$NON-NLS-1$
						editor.getSite().getShell().getDisplay().beep();
					}
				}
			}
		}
	}


	/**
	 * Sets the fItem.
	 * 
	 * @param fItem
	 *            The ISnippetItem to use
	 */
	public final void setItem(ISnippetItem item) {
		fItem = item;
	}

	public IEditorPart getEditorPart() {
		return fEditorPart;
	}

	public void setEditorPart(IEditorPart editorPart) {
		fEditorPart = editorPart;
	}
}