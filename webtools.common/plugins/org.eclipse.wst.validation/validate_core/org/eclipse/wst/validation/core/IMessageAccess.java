/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.core;


import java.util.Locale;
import java.util.List;

/**
 * <p>
 * Interface which can be used to access messages which are stored within a reporter which retains
 * its message history.
 * </p>
 */
public interface IMessageAccess {
	/**
	 * @param filter
	 *            A filter which specifies a subset of messages to retrieve. null specifies, "all
	 *            messages".
	 * @return List list of messages related to the target object
	 */
	public abstract List getMessages(MessageFilter filter);

	/**
	 * @param filter
	 *            A filter which specifies a subset of messages to retrieve. null specifies, "all
	 *            messages".
	 * @param locale
	 *            The target local to translate the messages into. null specifies, "use default
	 *            locale".
	 * @return List a list of messages related to the target object, translated into the specified
	 * locale.
	 */
	public abstract List getMessagesAsText(MessageFilter filter, Locale targetLocale);
}