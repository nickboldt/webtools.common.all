/******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

/**
 * The compiled form of a version expression. A version expression is used to
 * specify one or more versions.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IVersionExpr
{
    /**
     * Evaluates the version expression against the specified version. Returns
     * <code>true</code> if and only if the version expression matches the
     * specified version.
     * 
     * @param version the version string to check against the version expression
     * @return <code>true</code> if and only if the version expression matches
     *   the specified version
     */
    
    boolean evaluate( String version );
    
    /**
     * Returns human-readable form of the version expression that uses
     * descriptive terms rather than symbols.
     * 
     * @return human-readable form of the version expression
     */
    
    String toDisplayString();
    
}
