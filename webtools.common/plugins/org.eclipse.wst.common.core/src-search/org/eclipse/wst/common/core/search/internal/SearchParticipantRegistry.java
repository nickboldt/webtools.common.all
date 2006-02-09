/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.core.search.SearchParticipant;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;

public class SearchParticipantRegistry
{

	protected Map idMap = new HashMap(); // maps searchParticipant id to a
											// searchParticipant descriptor

	public SearchParticipantRegistry()
	{
	}

	public void putSearchParticipant(String id,
			SearchParticipantDescriptor searchParticipantDescriptor)
	{
		idMap.put(id, searchParticipantDescriptor);
	}

	public String[] getSearchParticipantIds()
	{
		Set ids = idMap.keySet();
		return (String[]) ids.toArray(new String[ids.size()]);
	}

	public Collection getSearchParticipants()
	{
		return idMap.values();
	}


	public SearchParticipant getSearchParticipant(String id)
	{
		SearchParticipantDescriptor searchParticipantDescriptor = null;
		if (id != null)
		{
			searchParticipantDescriptor = (SearchParticipantDescriptor) idMap
					.get(id);
		}
		return searchParticipantDescriptor != null ? searchParticipantDescriptor
				.getSearchParticipant()
				: null;

	}

	public SearchParticipant[] getParticipants(SearchPattern pattern)
	{

		EvaluationContext evalContext = createEvaluationContext(pattern);
		List result = new ArrayList();
		for (Iterator iter = getSearchParticipants().iterator(); iter.hasNext();)
		{
			SearchParticipantDescriptor descriptor = (SearchParticipantDescriptor) iter
					.next();
			try
			{
				if (descriptor.matches(evalContext))
				{
					try
					{
						SearchParticipant participant = descriptor
								.getSearchParticipant();
						if (!SearchParticipant.class.isInstance(participant))
							throw new ClassCastException();
						if (participant.isApplicable(pattern))
						{
							result.add(participant);
						}
					} catch (ClassCastException e)
					{
						iter.remove();
					}
				}

			} catch (CoreException e)
			{
				iter.remove();
			}

		}

		return (SearchParticipant[]) result
				.toArray(new SearchParticipant[result.size()]);
	}

	private static EvaluationContext createEvaluationContext(
			SearchPattern pattern)
	{
		EvaluationContext result = new EvaluationContext(null, pattern);
		result.addVariable("pattern", pattern); //$NON-NLS-1$
		return result;
	}

}
