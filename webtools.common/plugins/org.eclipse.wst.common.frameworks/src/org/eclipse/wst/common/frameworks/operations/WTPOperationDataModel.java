/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/


package org.eclipse.wst.common.frameworks.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;
import org.eclipse.wst.common.frameworks.internal.operations.NullOperationHandler;
import org.eclispe.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * WTPOperationDataModel is an essential piece of both the WTP Operation and WTP Wizard frameworks.
 * WTPOPerationDataModels (DataModels) act as smart property containers used to pass various
 * properties between components. DataModels are smart property containers because they can:
 * <UL>
 * <LI>Compute default values for their properties thus saving clients from needing to populate (or
 * understand) all available properties.</LI>
 * <LI>Modify the computed default values when necessary (e.g. if the default value of property A
 * is based on property B, then A should change when B changes).</LI>
 * <LI>Notify listeners when properties change.</LI>
 * <LI>Check the validity of the of the proptery values (e.g. if a property is supposed to be an
 * Integer < 10)</LI>
 * <LI>Check the validity of the entire property set (e.g if property A is supposed to be an Iteger
 * which is a multiple of the Integer property B).</LI>
 * <LI>Supply an operation to execute.</LI>
 * <LI>Compose and decompose entire DataModels through nesting.</LI>
 * </UL>
 * 
 * <B>PropertyKeys</B>
 * Clients interact with DataModels by getting and setting properties (Objects) based with PropertyKeys. A
 * PropertyKey is a String Object uniquely identifing a particular property. The recommended
 * practice for defining PropertyKeys is to define them as static final Class level Strings and to
 * use the DataModel instance class name appended with the property name as the value (this should
 * ensure uniqueness and gives a readible value when debugging).
 * 
 * 
 * 
 * The WTP Wizard framework uses DataModels to hold all the properties displayed to the user through
 * UI controls (e.g. textboxes, comboboxes, etc.). The Wizard framework also relies on DataModels
 * for validation, default values, and the operation executed on finish.
 * 
 * The WTO Operation framework uses DataModels to pass all parameters for execution. The DataModel
 * validation is used to ensure all the properties are valid prior to operation execution.
 * 
 * @link org.eclipse.wst.common.frameworks.ui.WTPWizard for details on the WTP Wizard framework.
 * @link org.eclipse.wst.common.frameworks.operations.WTPOperation for details on the WTP Operation
 *       framework.
 */
public abstract class WTPOperationDataModel implements WTPOperationDataModelListener {
	/**
	 * An unsettable property used soley to trip validation for nested models. Clients only use this
	 * property for validation purposes and never get or set its value. Subclasses can override
	 * nested model validation by checking for this property in the doValidate method and not
	 * calling super with it.
	 */
	public static final String NESTED_MODEL_VALIDATION_HOOK = "WTPOperationDataModel.NESTED_MODEL_VALIDATION_HOOK"; //$NON-NLS-1$
	/**
	 * Optional, type boolean This boolean was added for users who wish to delay the operation from
	 * being run on a "finish". The operation will be cached in the CACHED_DELAYED_OPERATION which
	 * then leaves the user responsible for running this operation when they see fit.
	 */
	public static final String RUN_OPERATION = "WTPOperationDataModel.RUN_OPERATION"; //$NON-NLS-1$
	/**
	 * Internal, type WTPOperation
	 */
	public static final String CACHED_DELAYED_OPERATION = "WTPOperationDataModel.CACHED_DELAYED_OPERATION"; //$NON-NLS-1$
	/**
	 * Optional Operation handler to allow user to prompt on save defaults to NullOperationHandler()
	 * set to UIOperationHanlder() to add prompt
	 */

	public static final String UI_OPERATION_HANLDER = "WTPOperationDataModel.UI_OPERATION_HANLDER"; //$NON-NLS-1$

	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.wst.common.frameworks.internal", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final String PROPERTY_NOT_LOCATED_ = WTPResourceHandler.getString("20"); //$NON-NLS-1$
	private static final String NESTED_MODEL_NOT_LOCATED = WTPResourceHandler.getString("21"); //$NON-NLS-1$
	private Set validProperties = new HashSet();
	private Set validBaseProperties = new HashSet();
	private Map propertyValues = new Hashtable();
	private Map nestedModels;
	private List listeners;
	private boolean ignorePropertyChanges = false;
	private boolean notificationEnabled = true;
	private boolean locked = false;
	private boolean operationValidationEnabled = false;
	private boolean hasBeenExecutedAgainst = false;
	private boolean suspendValidation = false;
	
	private WTPOperationDataModel extendedRoot;

	public WTPOperationDataModel() {
		init_internal();
	}

	public abstract WTPOperation getDefaultOperation();

	private final void init_internal() {
		addValidBaseProperty(RUN_OPERATION);
		addValidBaseProperty(CACHED_DELAYED_OPERATION);
		addValidBaseProperty(UI_OPERATION_HANLDER);
		initValidBaseProperties();
		initNestedModels();
		init();
	}

	/**
	 * This is for subclasses to perform any required initialization other than
	 * initValidBaseProperties() and initNestedModels() both which will be called first
	 * 
	 * @see initValidBaseProperties()
	 * @see initNestedModels()
	 */
	protected void init() {
	}

	/**
	 * This is for subclasses to override to initialize any nested DataModels. This will be called
	 * before init() and after initValidBaseProperties()
	 * 
	 * @see init()
	 * @see initValidBaseProperties();
	 */
	protected void initNestedModels() {
	}

	/**
	 * This is for subclasses to invoke to initialize properties. Subclasses should call this method
	 * from initValidBaseProperties().
	 * 
	 * @param propertyName
	 *            The property name to be added.
	 * @see initValidBaseProperties();
	 */
	protected void addValidBaseProperty(String propertyName) {
		validBaseProperties.add(propertyName);
		validProperties.add(propertyName);
	}

	/**
	 * This is for subclasses to override to initialize their properties.
	 * 
	 * @see init()
	 */
	protected void initValidBaseProperties() {
	}

	public void addNestedModel(String modelName, WTPOperationDataModel dataModel) {
		if (dataModel == null)
			return;
		if (null == nestedModels) {
			validBaseProperties.add(NESTED_MODEL_VALIDATION_HOOK);
			validProperties.add(NESTED_MODEL_VALIDATION_HOOK);
			nestedModels = new Hashtable();
		}
		nestedModels.put(modelName, dataModel);
		validProperties.addAll(dataModel.validProperties);
		dataModel.addListener(this);
		WTPOperationDataModelListener extendedListener = dataModel.getExtendedSynchronizer();
		if (extendedListener != null) {
			if (this.extendedRoot == null)
				dataModel.extendedRoot = this;
			else
				dataModel.extendedRoot = this.extendedRoot;
			this.addListener(extendedListener);
		}
	}

	/**
	 * @return
	 */
	//TODO what is this???
	protected WTPOperationDataModelListener getExtendedSynchronizer() {
		return null;
	}

	public WTPOperationDataModel removeNestedModel(String modelName) {
		if (modelName == null || nestedModels == null)
			return null;
		WTPOperationDataModel model = (WTPOperationDataModel) nestedModels.remove(modelName);
		validProperties.removeAll(model.validProperties);
		model.removeListener(this);
		if (nestedModels.isEmpty()) {
			nestedModels = null;
			validBaseProperties.remove(NESTED_MODEL_VALIDATION_HOOK);
			validProperties.remove(NESTED_MODEL_VALIDATION_HOOK);
		}
		return model;
	}

	public final WTPOperationDataModel getNestedModel(String modelName) {
		WTPOperationDataModel dataModel = (WTPOperationDataModel) nestedModels.get(modelName);
		if (null == dataModel) {
			throw new RuntimeException(NESTED_MODEL_NOT_LOCATED + modelName);
		}
		return dataModel;
	}

	public final Object getProperty(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return doGetProperty(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.getProperty(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	/**
	 * Subclasses can override this method to determine if a given propertyName should be enabled
	 * for edit. Returning null indicates that there is no precedence on the enablement. Note that
	 * you can override this in an outer model since enablement may be different in the outer model.
	 * 
	 * @param propertyName
	 * @return
	 */
	public final Boolean isEnabled(String propertyName) {
		checkValidPropertyName(propertyName);
		return basicIsEnabled(propertyName);
	}

	/**
	 * Subclasses can override this method to determine if a given propertyName should be enabled
	 * for edit. Returning null indicates that there is no precedence on the enablement. Note that
	 * you can override this in an outer model since enablement may be different in the outer model.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected Boolean basicIsEnabled(String propertyName) {
		if (isBaseProperty(propertyName)) {
			return null;
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.isEnabled(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	public final WTPPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return doGetValidPropertyDescriptors(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.getValidPropertyDescriptors(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	/**
	 * Subclasses may override to provide specific valid property values for the given propertyName.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected WTPPropertyDescriptor[] doGetValidPropertyDescriptors(String propertyName) {
		return new WTPPropertyDescriptor[0];
	}

	public final WTPPropertyDescriptor getPropertyDescriptor(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return doGetPropertyDescriptor(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.doGetPropertyDescriptor(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	protected WTPPropertyDescriptor doGetPropertyDescriptor(String propertyName) {
		return new WTPPropertyDescriptor(getProperty(propertyName));
	}

	/**
	 * Return a property of type String. If the property value is null, an empty String will be
	 * returned.
	 * 
	 * @param propertyName
	 * @return
	 */
	public final String getStringProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return ""; //$NON-NLS-1$
		return (String) prop;
	}

	/**
	 * Return a property of type boolean. If the property value is null, an false will be returned.
	 * 
	 * @param propertyName
	 * @return
	 */
	public final boolean getBooleanProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return false;
		return ((Boolean) prop).booleanValue();
	}

	/**
	 * Return a property of type int. If the property value is null, a -1 will be returned.
	 * 
	 * @param propertyName
	 * @return
	 */
	public final int getIntProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return -1;
		return ((Integer) prop).intValue();
	}

	public final void setBooleanProperty(String propertyName, boolean value) {
		Boolean b = getBoolean(value);
		setProperty(propertyName, b);
	}

	protected Boolean getBoolean(boolean b) {
		if (b)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}

	public void setIntProperty(String propertyName, int value) {
		Integer i = new Integer(value);
		setProperty(propertyName, i);
	}

	protected Object doGetProperty(String propertyName) {
		if (propertyValues.containsKey(propertyName)) {
			return propertyValues.get(propertyName);
		}
		return getDefaultProperty(propertyName);
	}

	/**
	 * Override this method to compute default property values
	 * 
	 * @param propertyName
	 * @return
	 */
	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(RUN_OPERATION)) {
			return Boolean.TRUE;
		}
		if (propertyName.equals(UI_OPERATION_HANLDER)) {
			return new NullOperationHandler();
		}
		return null;
	}

	/*
	 * This assumes that checkValidPropertyName(String) has already been called.
	 */
	private boolean isBaseProperty(String propertyName) {
		if (validBaseProperties != null)
			return validBaseProperties.contains(propertyName);
		return true;
	}

	public boolean isProperty(String propertyName) {
		return validProperties.contains(propertyName);
	}

	protected void checkValidPropertyName(String propertyName) {
		if (!validProperties.contains(propertyName)) {
			throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
		}
	}

	/**
	 * unset a property by passing null for the value
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	public final void setProperty(String propertyName, Object propertyValue) {
		if (isLocked() && !isResultProperty(propertyName))
			throw new IllegalStateException(WTPResourceHandler.getString("18", new Object[]{getClass().getName()})); //$NON-NLS-1$
		if (ignorePropertyChanges)
			return; // ignoring property changes
		checkValidPropertyName(propertyName);
		boolean nestedFound = false;
		if (isBaseProperty(propertyName)) {
			internalSetProperty(propertyName, propertyValue);
			return;
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					nestedFound = true;
					dataModel.setProperty(propertyName, propertyValue);
				}
			}
		}
		if (!nestedFound) {
			throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
		}
	}

	private final void internalSetProperty(String propertyName, Object propertyValue) {
		Object oldValue = propertyValues.get(propertyName);
		if (valueChanged(propertyValue, oldValue)) {
			doSetProperty(propertyName, oldValue, propertyValue);
		}
	}

	private void doSetProperty(String propertyName, Object oldValue, Object newValue) {
		if (doSetProperty(propertyName, newValue))
			notifyListeners(propertyName, oldValue, newValue);
	}

	/*
	 * Return true to notify listeners.
	 */
	protected boolean doSetProperty(String propertyName, Object propertyValue) {
		if (null != propertyValue)
			propertyValues.put(propertyName, propertyValue);
		else if (propertyValues.containsKey(propertyName))
			propertyValues.remove(propertyName);
		return true;
	}

	/**
	 * @param propertyValue
	 * @param oldValue
	 * @return
	 */
	private boolean valueChanged(Object o1, Object o2) {
		return o1 != o2 && ((o1 != null && !o1.equals(o2)) || !o2.equals(o1));
	}

	/**
	 * @param oldValue
	 * @param propertyValue
	 */
	public void notifyListeners(String propertyName, Object oldValue, Object propertyValue) {
		notifyListeners(propertyName, PROPERTY_CHG, oldValue, propertyValue);
	}

	/**
	 * @param oldValue
	 * @param propertyValue
	 */
	protected void notifyListeners(String propertyName, int flag, Object oldValue, Object propertyValue) {
		notifyListeners(new WTPOperationDataModelEvent(this, propertyName, oldValue, propertyValue, flag));
	}

	protected void notifyListeners(WTPOperationDataModelEvent event) {
		if (notificationEnabled && listeners != null && !listeners.isEmpty()) {
			WTPOperationDataModelListener listener;
			for (int i = 0; i < listeners.size(); i++) {
				listener = (WTPOperationDataModelListener) listeners.get(i);
				listener.propertyChanged(event);
			}
		}
	}

	public void propertyChanged(WTPOperationDataModelEvent event) {
		notifyListeners(event);
	}

	public boolean isSet(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return propertyValues.containsKey(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.isSet(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	public final IStatus validateDataModel() {
		return validateDataModel(false);
	}

	public final IStatus validateDataModel(boolean stopOnFirstFailure) {
		if (suspendValidation)
			return OK_STATUS;
		IStatus status = null;
		if (validBaseProperties != null && !validBaseProperties.isEmpty()) {
			IStatus propStatus;
			String propName;
			Iterator it = validBaseProperties.iterator();
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
			return OK_STATUS;
		return status;
	}

	public void addListener(WTPOperationDataModelListener listener) {
		if (listener != null) {
			if (listeners == null) {
				listeners = new ArrayList();
				listeners.add(listener);
			} else if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	public void removeListener(WTPOperationDataModelListener listener) {
		if (listeners != null && listener != null)
			listeners.remove(listener);
	}

	/**
	 * Return true if the model doesn't have any errors.
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		IStatus status = validateDataModel(true);
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
	//TODO why is this here???
	public void setIgnorePropertyChanges(boolean aBoolean) {
		ignorePropertyChanges = aBoolean;
	}

	/**
	 * Return the status for the validation of a particular property. Subclasses should override
	 * when a specific validation is required.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected IStatus doValidateProperty(String propertyName) {
		if (NESTED_MODEL_VALIDATION_HOOK.equals(propertyName)) {
			if (nestedModels != null && !nestedModels.isEmpty()) {
				IStatus modelStatus;
				WTPOperationDataModel dataModel;
				Iterator it = nestedModels.values().iterator();
				while (it.hasNext()) {
					dataModel = (WTPOperationDataModel) it.next();
					modelStatus = dataModel.validateDataModel(true);
					if (!modelStatus.isOK()) {
						return modelStatus;
					}
				}
			}
		}
		return OK_STATUS;
	}

	public final IStatus validateProperty(String propertyName) {
		if (suspendValidation)
			return OK_STATUS;
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return doValidateProperty(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			boolean propertyFound = false;
			IStatus status = null;
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					propertyFound = true;
					status = dataModel.validateProperty(propertyName);
					if (!status.isOK()) {
						return status;
					}
				}
			}
			if (propertyFound) {
				return OK_STATUS;
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	// handles for validation enablement for model and nested children
	protected void enableValidation() {
		suspendValidation = false;
		if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				dataModel.enableValidation();
			}
		}
	}

	// handles for validation disablement for model and nested children
	protected void disableValidation() {
		suspendValidation = true;
		if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				dataModel.disableValidation();
			}
		}
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
		if (!isSet(propertyName))
			notifyListeners(propertyName, null, null);
	}

	/**
	 * This method should be called when the valid values for the given propertyName may need to be
	 * recaculated. This allows for UIs to refresh.
	 * 
	 * @param propertyName
	 */
	public void notifyValidValuesChange(String propertyName) {
		notifyListeners(propertyName, WTPOperationDataModelListener.VALID_VALUES_CHG, null, null);
	}

	protected void notifyEnablementChange(String propertyName) {
		Boolean enable = isEnabled(propertyName);
		if (enable != null)
			notifyListeners(propertyName, ENABLE_CHG, null, enable);
	}

	public void dispose() {
	}

	protected boolean isNotificationEnabled() {
		return notificationEnabled;
	}

	protected void setNotificationEnabled(boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}

	/**
	 * @return Returns the locked.
	 */
	protected boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            The locked to set.
	 */
	protected void setLocked(boolean locked) {
		this.locked = locked;
		if (locked)
			hasBeenExecutedAgainst = true;
	}

	/**
	 * By Overwriting this method, you can change the model when its executing. This is only
	 * recommened when you need to set the result of the operation in the model.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected boolean isResultProperty(String propertyName) {
		return false;
	}


	public final boolean isOperationValidationEnabled() {
		return operationValidationEnabled;
	}

	public final void setOperationValidationEnabled(boolean operationValidationEnabled) {
		this.operationValidationEnabled = operationValidationEnabled;
	}

	/**
	 * Use this method to set the property values from this model onto the otherModel for those that
	 * are valid for the otherModel.
	 * 
	 * @param otherModel
	 */
	//TODO what is this for???
	public void synchronizeValidPropertyValues(WTPOperationDataModel otherModel, String[] properties) {
		if (otherModel == null || properties == null || properties.length == 0)
			return;
		for (int i = 0; i < properties.length; i++) {
			if (isSet(properties[i]))
				otherModel.setProperty(properties[i], getProperty(properties[i]));
		}
	}

	/**
	 * Remove all propertyValues.
	 *  
	 */
	public void clearAllValues() {
		if (propertyValues != null)
			propertyValues.clear();
		if (nestedModels != null) {
			Iterator it = nestedModels.values().iterator();
			while (it.hasNext())
				((WTPOperationDataModel) it.next()).clearAllValues();
		}
	}

	/**
	 * Use this method to determine if an operation has been run using this data model.
	 * 
	 * @return
	 */
	public boolean hasBeenExecutedAgainst() {
		return hasBeenExecutedAgainst;
	}

	//TODO move to a new ExtendedDataModel class???
	private final void assertModelIsExtended() {
		if (extendedRoot == null)
			throw new IllegalStateException(WTPResourceHandler.getString("19")); //$NON-NLS-1$
	}

	//TODO move to a new ExtendedDataModel class???
	protected final Object getParentProperty(String propertyName) {
		assertModelIsExtended();
		return extendedRoot.getProperty(propertyName);
	}

	//TODO move to a new ExtendedDataModel class???
	protected final int getParentIntProperty(String propertyName) {
		assertModelIsExtended();
		return extendedRoot.getIntProperty(propertyName);
	}

	//TODO move to a new ExtendedDataModel class???
	protected final boolean getParentBooleanProperty(String propertyName) {
		assertModelIsExtended();
		return extendedRoot.getBooleanProperty(propertyName);
	}

	//TODO move to a new ExtendedDataModel class???
	protected final String getParentStringProperty(String propertyName) {
		assertModelIsExtended();
		return extendedRoot.getStringProperty(propertyName);
	}

	protected IStatus validateStringValue(String propertyName, String errorMessage) {
		String name = getStringProperty(propertyName);
		if (name == "" || name == null || name.trim().length() == 0) { //$NON-NLS-1$
			return WTPCommonPlugin.createErrorStatus(errorMessage);
		}
		return OK_STATUS;
	}

	protected IStatus validateObjectArrayValue(String propertyName, String errorMessage) {
		Object[] objects = (Object[]) getProperty(propertyName);
		if (objects == null || objects.length == 0) {
			return WTPCommonPlugin.createErrorStatus(errorMessage);
		}
		return OK_STATUS;
	}

	/**
	 * @deprecated this can be replaced with something like this:
	 *             ProjectCreationDataModel.getProjectHandleFromProjectName(getStringProperty(projectNameProperty))
	 */
	public IProject getProjectHandle(String projectNameProperty) {
		String projectName = (String) getProperty(projectNameProperty);
		return getProjectHandleFromName(projectName);
	}

	/**
	 * @deprecated see ProjectCreationDataModel.getProjectHadleFromProjectName()
	 */
	public IProject getProjectHandleFromName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);
		return (null != projectName && projectName.length() > 0 && status.isOK()) ? ResourcesPlugin.getWorkspace().getRoot().getProject(projectName) : null;
	}

	/**
	 * @deprecated this will be removed and left to subclasses to implement
	 * @return
	 */
	public IProject getTargetProject() {
		return null;
	}
}