/*
 * Copyright 2014 Jeremie Lagarde.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.portlet;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.Configurable;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * If installed, this {@link Project} supports features from the Portlet specification.
 * 
 * @author Jeremie Lagarde
 */
@FacetConstraints({ @FacetConstraint(value = WebResourcesFacet.class, type = FacetConstraintType.REQUIRED) })
public interface PortletFacet<DESCRIPTOR extends Descriptor> extends JavaEEFacet, Configurable<DESCRIPTOR>
{
}