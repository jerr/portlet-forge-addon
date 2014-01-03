/*
 * Copyright 2014 Jeremie Lagarde.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.portlet;

import java.io.File;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * Abstract facet for the Portlet 2.0 specification.
 * 
 * @author Jeremie Lagarde
 */
public abstract class AbstractPortletFacetImpl<DESCRIPTOR extends Descriptor> extends AbstractJavaEEFacet implements
         PortletFacet<DESCRIPTOR>
{

   public AbstractPortletFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getFaceted();
      DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "portlet.xml");
   }

}
