/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.environment.relative;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.environment.uri.IURI;
import org.eclipse.wst.common.environment.uri.IURIFilter;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.IURIVisitor;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.internal.environment.eclipse.Messages;


public class RelativeURI implements IURI
{
  protected String uri_;
  
  public RelativeURI( String uri )
  {
    uri_ = uri;   
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#append(org.eclipse.env.uri.URI)
   */
  public IURI append(IURI relativeURI) throws URIException
  {
    if( !relativeURI.isRelative()) 
     {      
      throw new URIException( 
          new Status( IStatus.ERROR, "id", 0,
              NLS.bind( Messages.MSG_URI_NOT_RELATIVE, relativeURI.toString() ), null ) );
      
    }
    
    String newURI = uri_ + "/" + relativeURI.toString();
    
    return getURIScheme().newURI( newURI );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#asFile()
   */
  public File asFile() 
  {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#asString()
   */
  public String asString()
  {
    return uri_;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#toString()
   */
  public String toString()
  {
    return uri_;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#asURL()
   */
  public URL asURL() throws URIException
  {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#erase()
   */
  public void erase() throws URIException
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getInputStream()
   */
  public InputStream getInputStream() throws URIException
  {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getOutputStream()
   */
  public OutputStream getOutputStream() throws URIException
  {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getURIScheme()
   */
  public IURIScheme getURIScheme()
  {
    return new RelativeScheme();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isAvailableAsFile()
   */
  public boolean isAvailableAsFile()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isAvailableAsURL()
   */
  public boolean isAvailableAsURL()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isHierarchical()
   */
  public boolean isHierarchical()
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isLeaf()
   */
  public boolean isLeaf()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isPresent()
   */
  public boolean isPresent()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isReadable()
   */
  public boolean isReadable()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isRelative()
   */
  public boolean isRelative()
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isWritable()
   */
  public boolean isWritable()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#list()
   */
  public IURI[] list() throws URIException
  {
    return new IURI[0];
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#list(org.eclipse.env.uri.URIFilter)
   */
  public IURI[] list(IURIFilter uriFilter) throws URIException
  {
    return new IURI[0];
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#parent()
   */
  public IURI parent() throws URIException
  {
    int lastSlash  = uri_.lastIndexOf( '/' );
    int firstSlash = uri_.indexOf( '/' );
    
    // If there is a parent, then it must start with a slash
    // and end with a slash.
    if( lastSlash == -1 || firstSlash == -1 ) return null;
        
    return getURIScheme().newURI( uri_.substring(0, lastSlash ) );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#rename(org.eclipse.env.uri.URI)
   */
  public void rename(IURI newURI) throws URIException
  {
    uri_ = newURI.toString();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#touchFolder()
   */
  public void touchFolder() throws URIException
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#touchLeaf()
   */
  public void touchLeaf() throws URIException
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#visit(org.eclipse.env.uri.URIVisitor, org.eclipse.env.uri.URIFilter)
   */
  public void visit(IURIVisitor uriVisitor, IURIFilter uriFilter)
    throws URIException
  {
    boolean continueVisit = true;
    
    // If the filter accepts this we will visit it.
    if( uriFilter.accepts( this ) )
    {
      continueVisit = uriVisitor.visit( this );  
    }
       
    IURI[] children  = list();
    
    for( int index = 0; index < children.length && continueVisit; index++ )
    {
      children[index].visit( uriVisitor, uriFilter );
    }  
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#visit(org.eclipse.env.uri.URIVisitor)
   */
  public void visit(IURIVisitor uriVisitor) throws URIException
  {
    boolean continueVisit = uriVisitor.visit( this );  
    
    IURI[] children  = list();
    
    for( int index = 0; index < children.length && continueVisit; index++ )
     {
      children[index].visit( uriVisitor );
    }  
  }
}
