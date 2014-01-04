/*
 * Copyright 2014 Jeremie Lagarde.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.portlet.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.portlet.PortletFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Forge command to configure project with portlet specification.
 * 
 * @author Jeremie Lagarde
 */
public class PortletSetupWisard extends AbstractJavaEECommand
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata
               .from(super.getMetadata(context), getClass())
               .name("Portlet: Setup")
               .description("Setup Portlet in your project")
               .category(
                        Categories.create(super.getMetadata(context)
                                 .getCategory(), "Portlet"));
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "Portlet Version", defaultValue = "2.0")
   private UISelectOne<PortletFacet> version;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      version.setItemLabelConverter(new Converter<PortletFacet, String>()
      {
         @Override
         public String convert(PortletFacet source)
         {
            return source.getSpecVersion().toString();
         }
      });

      for (PortletFacet choice : version.getValueChoices())
      {
         if (version.getValue() == null || choice.getSpecVersion().compareTo(version.getValue().getSpecVersion()) >= 1)
         {
            version.setDefaultValue(choice);
         }
      }

      builder.add(version);
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      if (facetFactory.install(getSelectedProject(context), version.getValue()))
      {
         return Results.success("Portlet has been installed.");
      }
      return Results.fail("Could not install portlet.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}