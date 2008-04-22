/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.delegates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * ValidatorDelegatesRegistry is a singleton used to store validator delegate
 * descriptors for each delegating validator.
 */
public class ValidatorDelegatesRegistry {
	
  /**
   * The one and only instance of this registry.
   */
  private static ValidatorDelegatesRegistry _instance;

  /**
   * Provides the one and only instance of this class.
   * 
   * The actual platform extension registry reading happens at this time.
   * 
   * @see ValidatorDelegatesRegistryReader
   * 
   * @return the validator delegates registry singleton instance.
   */
  public static ValidatorDelegatesRegistry getInstance() {
    if (_instance == null) {
      _instance = new ValidatorDelegatesRegistry();
      ValidatorDelegatesRegistryReader reader = new ValidatorDelegatesRegistryReader(_instance);
      reader.readRegistry();
    }

    return _instance;
  }

  /**
   * The map of target validator id to Map of delegates by id.
   */
  private Map<String, Map<String,ValidatorDelegateDescriptor>> _delegatesByTarget = 
	  new HashMap<String, Map<String,ValidatorDelegateDescriptor>>();

  /**
   * Adds a descriptor to the registry.
   * 
   * @param descriptor
   *          The descriptor to add. Must not be null.
   */
  void add(ValidatorDelegateDescriptor descriptor) {
    if (descriptor == null)return;

    String targetID = descriptor.getTargetID();

    Map<String,ValidatorDelegateDescriptor> delegates = _delegatesByTarget.get(targetID);

    if (delegates == null) {
      delegates = new HashMap<String,ValidatorDelegateDescriptor>();
      _delegatesByTarget.put(targetID, delegates);
    }

    delegates.put(descriptor.getId(), descriptor);
  }

  /**
   * Provides the default delegate ID for the given delegating validator ID.
   * 
   * @param targetID
   *          The delegating validator's ID.
   * @return a String with the ID of the default delegate.
   */
  public String getDefaultDelegate(String targetID)
  {
    Map<String,ValidatorDelegateDescriptor> delegates = getDelegateDescriptors(targetID);

    if (delegates == null)return null;

    // TODO: Implement a default attribute and use that to get the default?
    // What happens if two or more delegates claim to be the default one?
    // For now, just pick the first one in the list.

    Iterator<ValidatorDelegateDescriptor> delegatesIterator = delegates.values().iterator();

    if (!delegatesIterator.hasNext())return null;

    return delegatesIterator.next().getId();
  }

  /**
   * Retrieves a delegate instance based on the target and delegate IDs.
   * 
   * @param targetID
   *          the delegating validator's ID.
   * @param delegateID
   *          the delegate validator's ID.
   * @return an instance of the delegate validator or null if one cannot be
   *         found.
   */
  public IValidator getDelegate(String targetID, String delegateID) throws ValidationException
  {
    ValidatorDelegateDescriptor descriptor = getDescriptor(targetID, delegateID);

    if (descriptor == null)return null;

    IValidator delegate = descriptor.getValidator();

    return delegate;
  }

  /**
   * Provides a map with all delegates descriptors for the given target, keyed
   * by their ID.
   * 
   * @param targetID
   *          the target (delegating) validator id. Must not be null.
   * @return a Map <string, ValidatorDelegateDescriptor>. May be null if the ID
   *         passed in is not a delegating validator.
   */
  public Map<String,ValidatorDelegateDescriptor> getDelegateDescriptors(String targetID) {
    return _delegatesByTarget.get(targetID);
  }

  /**
   * Finds a delegate descriptor based on the target id and delegate id.
   * 
   * @param targetID
   *          the delegating validator's id
   * @param delegateID
   *          the delegate id
   * @return a ValidatorDelegateDescriptor for the given target and id or null
   *         if one cannot be found.
   */
  public ValidatorDelegateDescriptor getDescriptor(String targetID, String delegateID)
  {
    Map<String,ValidatorDelegateDescriptor> delegates = _delegatesByTarget.get(targetID);

    if (delegates == null || delegateID == null)return null;
    
    ValidatorDelegateDescriptor descriptor = (ValidatorDelegateDescriptor) delegates.get(delegateID);

    return descriptor;
  }

  /**
   * Determines if a given validator has delegates.
   * 
   * @param targetID
   *          the target validator id.
   * @return true if the target has registered delegates, false otherwise.
   */
  public boolean hasDelegates(String targetID)
  {
    Map<String,ValidatorDelegateDescriptor> delegatesByID = _delegatesByTarget.get(targetID);

    boolean hasDelegates = (delegatesByID != null && !delegatesByID.isEmpty());
    return hasDelegates;
  }
}
