/*
 * Copyright 2014 Jeremie Lagarde.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.portlet;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;

/**
 * If installed, this {@link Project} supports features from the Portlet 2.0 specification.
 * 
 * @author Jeremie Lagarde
 */
public class PortletFacetImpl_2_0 extends AbstractPortletFacetImpl<PortletDescriptor> implements PortletFacet_2_0
{

   private static final Dependency JAVAX_PORTLET_API = DependencyBuilder
            .create("javax.portlet:portlet-api");

   @Inject
   public PortletFacetImpl_2_0(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return new SingleVersion("2.0");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new HashMap<Dependency, List<Dependency>>();
      result.put(JAVAX_PORTLET_API, Arrays.asList(JAVAX_PORTLET_API));
      return result;
   }

   /*
    * Facet Methods
    */
   @Override
   public PortletDescriptor getConfig()
   {
      FileResource<?> configFile = getConfigFile();
      PortletDescriptor descriptor;
      if (configFile.exists())
      {

         DescriptorImporter<PortletDescriptor> importer = Descriptors.importAs(PortletDescriptor.class);
         InputStream inputStream = configFile.getResourceInputStream();
         try
         {
            descriptor = importer.fromStream(inputStream);
         }
         finally
         {
            Streams.closeQuietly(inputStream);
         }
      }
      else
      {
         descriptor = Descriptors.create(PortletDescriptor.class).version("2.0");
         configFile.setContents(descriptor.exportAsString());
      }
      return descriptor;
   }

   @Override
   public void saveConfig(final PortletDescriptor descriptor)
   {
      FileResource<?> configFile = getConfigFile();
      String output = descriptor.exportAsString();
      configFile.setContents(output);
   }
}
