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
package org.eclipse.wst.common.internal.emfworkbench.edit;



import org.eclipse.emf.common.command.Command;
import org.eclipse.wst.common.internal.emfworkbench.integration.AbstractEditModelCommand;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelCommand;

/**
 * Insert the type's description here. Creation date: (05/22/01 8:57:56 AM)
 * 
 * @author: Administrator
 */
public class ChildCommand extends EditModelCommand {
	private ParentCommand parent;
	private EditModelRetriever modelRetriever;

	public ChildCommand(ParentCommand parentCmd, Command targetCmd, EditModelRetriever retriever) {
		super(targetCmd);
		parent = parentCmd;
		modelRetriever = retriever;
	}

	public boolean canExecute() {
		return true;
	}

	/**
	 * Does nothing
	 */
	public void execute() {
		//does nothing
	}

	protected void executeInModel(AbstractEditModelCommand cmd) {
		EditModel model = modelRetriever.getEditModelForWrite(this);
		try {
			model.getCommandStack().execute(cmd);
			model.saveIfNecessary(this);
		} finally {
			model.releaseAccess(this);
		}
	}

	/**
	 * Insert the method's description here. Creation date: (05/22/01 9:35:36 AM)
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getEditModelKey() {
		return modelRetriever.getEditModelID();
	}

	/**
	 * Insert the method's description here. Creation date: (05/22/01 9:35:36 AM)
	 * 
	 * @return com.ibm.etools.j2ee.workbench.ParentCommand
	 */
	public ParentCommand getParent() {
		return parent;
	}

	public void redo() {
		redoInModel();
		getParent().redoFrom(this);
	}

	protected void redoInModel() {
		EditModel model = modelRetriever.getEditModelForWrite(this);
		try {
			getTarget().redo();
			model.saveIfNecessary(this);
		} finally {
			model.releaseAccess(this);
		}
	}

	public void undo() {
		undoInModel();
		getParent().undoFrom(this);
	}

	protected void undoInModel() {
		EditModel model = modelRetriever.getEditModelForWrite(this);
		try {
			getTarget().undo();
			model.saveIfNecessary(this);
		} finally {
			model.releaseAccess(this);
		}
	}
}
