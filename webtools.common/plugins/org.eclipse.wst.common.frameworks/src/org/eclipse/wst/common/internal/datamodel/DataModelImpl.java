/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.wst.common.frameworks.datamodel.provisional.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.provisional.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelListener;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;

public class DataModelImpl implements IDataModel, IDataModelListener {

	private static final String PROPERTY_NOT_LOCATED_ = WTPResourceHandler.getString("20"); //$NON-NLS-1$
	private static final String NESTED_MODEL_NOT_LOCATED = WTPResourceHandler.getString("21"); //$NON-NLS-1$
	private static final String NESTED_MODEL_DUPLICATE = WTPResourceHandler.getString("33"); //$NON-NLS-1$

	private Set allPropertyNames = new HashSet();
	private Set basePropertyNames = new HashSet();
	private Map propertyValues = new Hashtable();
	private Map nestedModels;
	private Set nestingModels;
	private List listeners;
	private boolean ignorePropertyChanges = false;
	private boolean notificationEnabled = true;
	private boolean operationValidationEnabled = false;
	private boolean hasBeenExecutedAgainst = false;

	private IDataModelProvider provider;

	public DataModelImpl(IDataModelProvider dataModelProvider) {
		init(dataModelProvider);
	}

	private void init(IDataModelProvider dataModelProvider) {
		this.provider = dataModelProvider;
		dataModelProvider.setDataModel(this);
		String[] propertyNames = dataModelProvider.getPropertyNames();
		for (int i = 0; null != propertyNames && i < propertyNames.length; i++) {
			addBaseProperty(propertyNames[i]);
		}
		dataModelProvider.init();
	}

	private void addBaseProperty(String propertyName) {
		basePropertyNames.add(propertyName);
		allPropertyNames.add(propertyName);
	}

	public boolean isBaseProperty(String propertyName) {
		if (basePropertyNames != null)
			return basePropertyNames.contains(propertyName);
		return true;
	}

	public boolean isProperty(String propertyName) {
		return allPropertyNames.contains(propertyName);
	}

	private void checkValidPropertyName(String propertyName) {
		if (!allPropertyNames.contains(propertyName)) {
			throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
		}
	}

	private DataModelImpl getOwningDataModel(String propertyName) {
		checkValidPropertyName(propertyName);
		return searchNestedModels(propertyName);
	}

	private DataModelImpl searchNestedModels(String propertyName) {
		if (isBaseProperty(propertyName)) {
			return this;
		} else if (nestedModels != null) {
			DataModelImpl dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (DataModelImpl) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.searchNestedModels(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	public Object getProperty(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		if (dataModel.propertyValues.containsKey(propertyName)) {
			return dataModel.propertyValues.get(propertyName);
		}
		return dataModel.provider.getDefaultProperty(propertyName);
	}

	public Object getDefaultProperty(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.provider.getDefaultProperty(propertyName);
	}

	public int getIntProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return -1;
		return ((Integer) prop).intValue();
	}

	public boolean getBooleanProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return false;
		return ((Boolean) prop).booleanValue();
	}

	public String getStringProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return ""; //$NON-NLS-1$
		return (String) prop;
	}

	public boolean isPropertySet(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.propertyValues.containsKey(propertyName);
	}

	public boolean isPropertyEnabled(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.provider.isPropertyEnabled(propertyName);
	}


	public void setProperty(String propertyName, Object propertyValue) {
		// TODO add locking, result & ignore back???
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		dataModel.internalSetProperty(propertyName, propertyValue);
	}

	private void internalSetProperty(String propertyName, Object propertyValue) {
		Object oldValue = propertyValues.get(propertyName);
		if (valueChanged(propertyValue, oldValue)) {
			if (null != propertyValue)
				propertyValues.put(propertyName, propertyValue);
			else if (propertyValues.containsKey(propertyName))
				propertyValues.remove(propertyName);
			if (provider.setProperty(propertyName, propertyValue)) {
				notifyListeners(propertyName, DataModelEvent.PROPERTY_CHG);
			}
		}
	}

	private boolean valueChanged(Object o1, Object o2) {
		return o1 != o2 && ((o1 != null && !o1.equals(o2)) || !o2.equals(o1));
	}

	public void setIntProperty(String propertyName, int value) {
		setProperty(propertyName, new Integer(value));
	}

	public void setBooleanProperty(String propertyName, boolean value) {
		setProperty(propertyName, (value) ? Boolean.TRUE : Boolean.FALSE);
	}

	public void setStringProperty(String propertyName, String value) {
		setProperty(propertyName, value);
	}

	public void addNestedModel(String modelName, IDataModel dataModel) {
		if (null == nestedModels) {
			nestedModels = new Hashtable();
		}
		DataModelImpl nestedDataModel = (DataModelImpl) dataModel;
		if (null == nestedDataModel.nestingModels) {
			nestedDataModel.nestingModels = new HashSet();
		}
		if (nestedDataModel.nestingModels.contains(this)) {
			throw new RuntimeException(NESTED_MODEL_DUPLICATE);
		}
		nestedDataModel.nestingModels.add(this);

		nestedModels.put(modelName, nestedDataModel);

		addNestedProperties(nestedDataModel.allPropertyNames);
		nestedDataModel.addListener(this);
	}

	private void addNestedProperties(Set nestedProperties) {
		boolean propertiesAdded = allPropertyNames.addAll(nestedProperties);
		// Pass the new properties up the nesting chain
		if (propertiesAdded && nestingModels != null) {
			Iterator iterator = nestingModels.iterator();
			while (iterator.hasNext()) {
				((DataModelImpl) iterator.next()).addNestedProperties(nestedProperties);
			}
		}
	}

	public IDataModel[] getNestedModels() {
		return (IDataModel[]) nestedModels.values().toArray();
	}

	public IDataModel[] getNestingModels() {
		return (IDataModel[]) nestingModels.toArray();
	}

	public IDataModel removeNestedModel(String modelName) {
		if (modelName == null || nestedModels == null)
			return null;
		DataModelImpl model = (DataModelImpl) nestedModels.remove(modelName);
		model.nestingModels.remove(this);
		removeNestedProperties(model.allPropertyNames);
		model.removeListener(this);
		if (nestedModels.isEmpty()) {
			nestedModels = null;
		}
		return model;
	}

	private void removeNestedProperties(Set nestedProperties) {
		Iterator iterator = nestedProperties.iterator();
		String property = null;
		boolean keepProperty = false;
		Set nestedPropertiesToRemove = null;
		while (iterator.hasNext()) {
			keepProperty = false;
			property = (String) iterator.next();
			if (basePropertyNames.contains(property)) {
				keepProperty = true;
			}
			if (!keepProperty && nestedModels != null) {
				Iterator nestedModelsIterator = nestedModels.values().iterator();
				while (!keepProperty && nestedModelsIterator.hasNext()) {
					DataModelImpl nestedModel = (DataModelImpl) nestedModelsIterator.next();
					if (nestedModel.isProperty(property)) {
						keepProperty = true;
					}
				}
			}
			if (!keepProperty) {
				if (null == nestedPropertiesToRemove) {
					nestedPropertiesToRemove = new HashSet();
				}
				nestedPropertiesToRemove.add(property);
			}
		}

		if (null != nestedPropertiesToRemove) {
			allPropertyNames.removeAll(nestedPropertiesToRemove);
			if (nestingModels != null) {
				Iterator nestingModelsIterator = nestingModels.iterator();
				while (nestingModelsIterator.hasNext()) {
					((DataModelImpl) nestingModelsIterator.next()).removeNestedProperties(nestedPropertiesToRemove);
				}
			}
		}
	}


	public IDataModel getNestedModel(String modelName) {
		IDataModel dataModel = (IDataModel) nestedModels.get(modelName);
		if (null == dataModel) {
			throw new RuntimeException(NESTED_MODEL_NOT_LOCATED + modelName);
		}
		return dataModel;
	}

	public IDataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.provider.getValidPropertyDescriptors(propertyName);
	}

	public IDataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		IDataModelPropertyDescriptor descriptor = dataModel.provider.getPropertyDescriptor(propertyName);
		return descriptor == null ? new DataModelPropertyDescriptor(getProperty(propertyName)) : descriptor;
	}

	/**
	 * Convenience method to create a WTPOperationDataModelEvent.PROPERTY_CHG event and notify
	 * listeners.
	 * 
	 * @param propertyName
	 * @see #notifyListeners(DataModelEvent)
	 */
	public void notifyListeners(String propertyName) {
		notifyListeners(propertyName, DataModelEvent.PROPERTY_CHG);
	}

	/**
	 * Convenience method to create a WTPOperationDataModelEvent event of the specified type and
	 * notify listeners.
	 * 
	 * @param propertyName
	 * @see DataModelEvent for the list of valid flag values
	 */
	public void notifyListeners(String propertyName, int flag) {
		notifyListeners(new DataModelEvent(this, propertyName, flag));
	}

	/**
	 * Notifies all registerd WTPOperationDataModelListeners of the specified
	 * WTPOperationDataModelEvent.
	 * 
	 * @param event
	 */
	protected void notifyListeners(DataModelEvent event) {
		if (notificationEnabled && listeners != null && !listeners.isEmpty()) {
			IDataModelListener listener;
			for (int i = 0; i < listeners.size(); i++) {
				listener = (IDataModelListener) listeners.get(i);
				if (listener != event.getDataModel()) {
					listener.propertyChanged(event);
				}
			}
		}
	}

	public void propertyChanged(DataModelEvent event) {
		notifyListeners(event);
	}

	public IStatus validate() {
		return validate(false);
	}

	public IStatus validate(boolean stopOnFirstFailure) {
		IStatus status = null;
		if (basePropertyNames != null && !basePropertyNames.isEmpty()) {
			IStatus propStatus;
			String propName;
			Iterator it = basePropertyNames.iterator();
			while (it.hasNext()) {
				propName = (String) it.next();
				propStatus = validateProperty(propName);
				if (status == null || status.isOK())
					status = propStatus;
				else {
					if (status.isMultiStatus())
						((MultiStatus) status).merge(propStatus);
					else {
						MultiStatus multi = new MultiStatus("org.eclipse.wst.common.frameworks.internal", 0, "", null); //$NON-NLS-1$ //$NON-NLS-2$
						multi.merge(status);
						multi.merge(propStatus);
						status = multi;
					}
				}
				if (stopOnFirstFailure && status != null && !status.isOK() && status.getSeverity() == IStatus.ERROR)
					return status;
			}
		}
		if (status == null)
			return IDataModelProvider.OK_STATUS;
		return status;
	}

	public void addListener(IDataModelListener listener) {
		if (listener != null) {
			if (listeners == null) {
				listeners = new ArrayList();
				listeners.add(listener);
			} else if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	public void removeListener(IDataModelListener listener) {
		if (listeners != null && listener != null)
			listeners.remove(listener);
	}

	/**
	 * Return true if the model doesn't have any errors.
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		IStatus status = validate(true);
		if (status.isOK())
			return true;
		if (status.getSeverity() == IStatus.ERROR)
			return false;
		return true;
	}

	/**
	 * Use this method when the model should ignore any property set calls. Remember to always reset
	 * the value in a finally block.
	 * 
	 * @param aBoolean
	 */
	public void setIgnorePropertyChanges(boolean aBoolean) {
		ignorePropertyChanges = aBoolean;
	}

	public boolean isPropertyValid(String propertyName) {
		return validateProperty(propertyName).isOK();
	}

	public IStatus validateProperty(String propertyName) {
		DataModelImpl dataModel = getOwningDataModel(propertyName);
		return dataModel.provider.validateProperty(propertyName);
	}

	/**
	 * This method should be called from doSetProperty(String, Object) when a change to a
	 * propertyName will cause default values within the model to change. The passed propertyName is
	 * another property that may need to have its default value recomputed. This allows for UIs to
	 * refresh.
	 * 
	 * @param propertyName
	 */
	public void notifyDefaultChange(String propertyName) {
		if (!isPropertySet(propertyName))
			notifyListeners(propertyName, DataModelEvent.PROPERTY_CHG);
	}

	/**
	 * This method should be called when the valid values for the given propertyName may need to be
	 * recaculated. This allows for UIs to refresh.
	 * 
	 * @param propertyName
	 */
	public void notifyValidValuesChange(String propertyName) {
		notifyListeners(propertyName, DataModelEvent.VALID_VALUES_CHG);
	}

	public void notifyEnablementChange(String propertyName) {
		notifyListeners(propertyName, DataModelEvent.ENABLE_CHG);
	}


	protected boolean isNotificationEnabled() {
		return notificationEnabled;
	}

	protected void setNotificationEnabled(boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}

	public final boolean isOperationValidationEnabled() {
		return operationValidationEnabled;
	}

	public final void setOperationValidationEnabled(boolean operationValidationEnabled) {
		this.operationValidationEnabled = operationValidationEnabled;
	}

	public List getExtendedContext() {
		List extendedContext = provider.getExtendedContext();
		return extendedContext == null ? Collections.EMPTY_LIST : extendedContext;
	}

	public void dispose() {
	}

	public IDataModelOperation getDefaultOperation() {
		return provider.getDefaultOperation();
	}

	public String[] getBaseProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getNestedProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAllProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNestedProperty(String propertyName) {
		// TODO Auto-generated method stub
		return false;
	}
}