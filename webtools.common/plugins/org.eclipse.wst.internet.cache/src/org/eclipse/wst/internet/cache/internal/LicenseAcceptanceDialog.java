/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.cache.internal;

import java.net.URL;
import java.util.Hashtable;

import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog that prompts the user to accept a license agreement.
 */
public class LicenseAcceptanceDialog extends IconAndMessageDialog 
{
  /**
   * Externalized string keys.
   */
  private static final String _UI_CACHE_DIALOG_LICENSE_STATEMENT1 = "_UI_CACHE_DIALOG_LICENSE_STATEMENT1";
  private static final String _UI_CACHE_DIALOG_LICENSE_STATEMENT2 = "_UI_CACHE_DIALOG_LICENSE_STATEMENT2";
  private static final String _UI_CACHE_DIALOG_AGREE_BUTTON = "_UI_CACHE_DIALOG_AGREE_BUTTON";
  private static final String _UI_CACHE_DIALOG_DISAGREE_BUTTON = "_UI_CACHE_DIALOG_DISAGREE_BUTTON";
  private static final String _UI_CACHE_DIALOG_TITLE = "_UI_CACHE_DIALOG_TITLE";

  /**
   * Holds all the dialogs that are currently displayed keyed by the license URL.
   */
  private static Hashtable dialogsInUse = new Hashtable();
  
  /**
   * The URL of the resource.
   */
  private String url;

  /**
   * The URL of the license.
   */
  private String licenseURL;
  
  /**
   * The URL of the license.
   */
  private boolean createExternalBrowser = false;
  
  /**
   * Constructor.
   * 
   * @param parent The parent of this dialog.
   * @param url The license URL.
   */
  protected LicenseAcceptanceDialog(Shell parent, String url, String licenseURL) 
  {
    super(parent);
	this.url = url;
	this.licenseURL = licenseURL;
	message = "License agreement";
  }
  
  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  protected void configureShell(Shell shell) 
  {
    super.configureShell(shell);
    shell.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_TITLE));
    shell.setImage(null);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
   */
  protected Control createButtonBar(Composite parent) 
  {
	Composite buttonBar = new Composite(parent, SWT.CENTER);
	GridLayout layout = new GridLayout();
	layout.numColumns = 0;
	layout.makeColumnsEqualWidth = true;
	buttonBar.setLayout(layout);
	GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
	buttonBar.setLayoutData(gd);
	
	Button agreeButton = createButton(buttonBar, LicenseAcceptanceDialog.OK, 
			CachePlugin.getResourceString(_UI_CACHE_DIALOG_AGREE_BUTTON), false);

	Button disagreeButton = createButton(buttonBar, LicenseAcceptanceDialog.CANCEL, 
			CachePlugin.getResourceString(_UI_CACHE_DIALOG_DISAGREE_BUTTON), false);
	
	return buttonBar;
  }

  /**
   * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
   */
  protected Control createContents(Composite parent) 
  {
	Composite composite = new Composite(parent, SWT.NULL);
	GridLayout layout = new GridLayout();
	composite.setLayout(layout);
	GridData gd = new GridData(SWT.FILL);
	gd.widthHint = 500;
	composite.setLayoutData(gd);

	// Display a statement about the license.
	Label licenseText1 = new Label(composite, SWT.NONE);
	licenseText1.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_LICENSE_STATEMENT1));
	Label urlText = new Label(composite, SWT.WRAP);
	urlText.setText(url);
	Label blankText = new Label(composite, SWT.NONE);
	Label licenseText2 = new Label(composite, SWT.NONE);
	licenseText2.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_LICENSE_STATEMENT2));
	
	// Display the license in a browser.
	try
	{
	  Browser browser = new Browser(composite, SWT.BORDER);
	  gd = new GridData(GridData.FILL_BOTH);
	  gd.heightHint = 400;
	  browser.setUrl(licenseURL);
	  browser.setLayoutData(gd);
	}
	catch(SWTException e)
	{
	  // The browser throws this exception on platforms that do not support it. 
	  // In this case we need to create an external browser.
	  try
	  {
	    CachePlugin.getDefault().getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(licenseURL));
	  }
	  catch(Exception ex)
	  {
		// Do nothing. The license will not be displayed if this occurs.
	  }
	}

	createButtonBar(composite);
		
	return composite;
  }

  /**
   * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
   */
  protected Image getImage() 
  {
	return getInfoImage();
  }

  /**
   * Prompt the user to accept the specified license. This method creates the
   * dialog and returns the result.
   * 
   * @param parent The parent of this dialog.
   * @param url The URL of the resource for which the license must be accepted.
   * @param licenseURL The license URL.
   * @return True if the license is accepted, false otherwise.
   */
  public static boolean promptForLicense(Shell parent, String url, String licenseURL) 
  {
	boolean agreedToLicense = false;
	boolean newDialog = true;
	LicenseAcceptanceDialog dialog;
	// If the dialog is already displayed for this license use it instead of 
	// displaying another dialog.
	if(dialogsInUse.containsKey(licenseURL))
	{
	  newDialog = false;
	  dialog = (LicenseAcceptanceDialog)dialogsInUse.get(licenseURL);
	}
	else
	{
	  dialog = new LicenseAcceptanceDialog(parent, url, licenseURL);
	  dialogsInUse.put(licenseURL, dialog);
	  dialog.setBlockOnOpen(true);
	  
	}
	dialog.open();
	
	if (dialog.getReturnCode() == LicenseAcceptanceDialog.OK) 
	{
	  agreedToLicense = true;
	}
	
	if(newDialog)
	{
	  dialogsInUse.remove(licenseURL);
	}
	 
	return agreedToLicense;
  }
}
