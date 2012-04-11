/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.portlet;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;

/**
 * @author Jeremie Lagarde
 */
@Alias("forge.spec.portlet")
@RequiresFacet({ JavaSourceFacet.class, WebResourceFacet.class, DependencyFacet.class })
@RequiresPackagingType({ PackagingType.JAR, PackagingType.WAR, PackagingType.BUNDLE })
public class PortletFacetImpl extends BaseFacet implements PortletFacet
{

   private final DependencyInstaller installer;

   @Inject
   public PortletFacetImpl(final DependencyInstaller installer)
   {
      this.installer = installer;
   }

   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList((Dependency) DependencyBuilder
               .create("javax.portlet:portlet-api:2.0"));
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         FileResource<?> descriptor = getConfigFile();
         if (!descriptor.exists())
         {

            PortletDescriptor descriptorContents = Descriptors.create(PortletDescriptor.class)
                     .version("2.0");
            descriptor.setContents(descriptorContents.exportAsString());
         }
      }
      for (Dependency requirement : getRequiredDependencies()) {
         if (!installer.isInstalled(project, requirement))
         {
            installer.install(project, requirement, ScopeType.PROVIDED);
         }
      }
      return true;
      
   }

   @Override
   public boolean isInstalled()
   {
      return getConfigFile().exists();
   }

   @Override
   public PortletDescriptor getConfig()
   {
      DescriptorImporter<PortletDescriptor> importer = Descriptors.importAs(PortletDescriptor.class);
      PortletDescriptor descriptor = importer.from(getConfigFile().getResourceInputStream());
      return descriptor;
   }

   @Override
   public void saveConfig(final PortletDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile().setContents(output);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      WebResourceFacet resources = project.getFacet(WebResourceFacet.class);
      return (FileResource<?>) resources.getWebRootDirectory().getChild("WEB-INF" + File.separator + "portlet.xml");
   }

}
