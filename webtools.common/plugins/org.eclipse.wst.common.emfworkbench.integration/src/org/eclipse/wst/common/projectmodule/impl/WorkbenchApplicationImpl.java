/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchApplicationImpl.java,v 1.1 2005/01/14 21:02:41 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.wst.common.projectmodule.ProjectModulePackage;
import org.eclipse.wst.common.projectmodule.WorkbenchApplication;
import org.eclipse.wst.common.projectmodule.WorkbenchModule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Application</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.projectmodule.impl.WorkbenchApplicationImpl#getModules <em>Modules</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchApplicationImpl extends EObjectImpl implements WorkbenchApplication {
	/**
	 * The cached value of the '{@link #getModules() <em>Modules</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModules()
	 * @generated
	 * @ordered
	 */
	protected EList modules = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchApplicationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ProjectModulePackage.eINSTANCE.getWorkbenchApplication();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getModules() {
		if (modules == null) {
			modules = new EObjectResolvingEList(WorkbenchModule.class, this, ProjectModulePackage.WORKBENCH_APPLICATION__MODULES);
		}
		return modules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ProjectModulePackage.WORKBENCH_APPLICATION__MODULES:
				return getModules();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ProjectModulePackage.WORKBENCH_APPLICATION__MODULES:
				getModules().clear();
				getModules().addAll((Collection)newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ProjectModulePackage.WORKBENCH_APPLICATION__MODULES:
				getModules().clear();
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ProjectModulePackage.WORKBENCH_APPLICATION__MODULES:
				return modules != null && !modules.isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

} //WorkbenchApplicationImpl
