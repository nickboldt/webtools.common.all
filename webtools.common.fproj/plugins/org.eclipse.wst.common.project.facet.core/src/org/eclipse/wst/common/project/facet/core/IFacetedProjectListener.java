/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 * @deprecated use the IFacetedProjectListener class from the facet.core.events package
 */

public interface IFacetedProjectListener
{
    void projectChanged();
}