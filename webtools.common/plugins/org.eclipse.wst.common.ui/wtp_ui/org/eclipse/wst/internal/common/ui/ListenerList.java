package org.eclipse.wst.internal.common.ui;

/*
 * Licensed Material - Property of IBM (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. US
 * Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP
 * Schedule Contract with IBM Corp.
 */


import java.awt.event.ActionListener;

public class ListenerList {
	private final static ActionListener[] NULL_ARRAY = new ActionListener[0];
	protected transient ActionListener[] listenerList = NULL_ARRAY;

	/**
	 * Add the listener as a listener of the specified type.
	 * 
	 * @param t
	 *            the type of the listener to be added
	 * @param l
	 *            the listener to be added
	 */
	public synchronized void add(ActionListener l) {
		if (l == null)
			return;
		if (listenerList == NULL_ARRAY) {
			// if this is the first listener added,
			// initialize the lists
			listenerList = new ActionListener[]{l};
		} else {
			// Otherwise copy the array and add the new listener
			int i = listenerList.length;
			ActionListener[] tmp = new ActionListener[i + 1];
			System.arraycopy(listenerList, 0, tmp, 0, i);

			tmp[i + 1] = l;

			listenerList = tmp;
		}
	}

	/**
	 * Return the total number of listeners for this listenerlist
	 */
	public int getListenerCount() {
		return listenerList.length;
	}

	public ActionListener[] getListenerList() {
		return listenerList;
	}

	public synchronized void remove(ActionListener l) {
		if (l == null)
			return;
		int index = -1;
		for (int i = listenerList.length - 1; i >= 0; i -= 1) {
			if (listenerList[i].equals(l)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			ActionListener[] tmp = new ActionListener[listenerList.length - 1];
			// Copy the list up to index
			System.arraycopy(listenerList, 0, tmp, 0, index);
			// Copy from two past the index, up to
			// the end of tmp (which is two elements
			// shorter than the old list)
			if (index < tmp.length)
				System.arraycopy(listenerList, index + 1, tmp, index, tmp.length - index);
			// set the listener array to the new array or null
			listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
		}
	}
}