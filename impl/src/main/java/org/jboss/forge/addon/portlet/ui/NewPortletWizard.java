/*
 * Copyright 2014 Jeremie Lagarde.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.portlet.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.JavaSourceFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.portlet.PortletFacet_2_0;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletType;

/**
 * Forge command to create Portlet.
 * 
 * @author Jeremie Lagarde
 */
public class NewPortletWizard extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Portlet name", description = "The portlet name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Target class")
   private UIInput<String> targetClass;

   @Inject
   @WithAttributes(label = "Mime type", defaultValue = "text/html")
   private UIInput<String> mimeType;

   @Inject
   @WithAttributes(label = "Modes")
   private UIInputMany<String> modes;

   @Inject
   @WithAttributes(label = "Title")
   private UIInput<String> title;

   @Inject
   @WithAttributes(label = "Short title")
   private UIInput<String> shortTitle;

   @Inject
   @WithAttributes(label = "Keywords")
   private UIInput<String> keywords;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private JavaSourceFactory javaSourceFactory;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata
               .from(super.getMetadata(context), getClass())
               .name("Portlet: New Portlet")
               .description("Create a new portlet")
               .category(
                        Categories.create(super.getMetadata(context)
                                 .getCategory().getName(), "Portlet"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      if (project == null)
      {
         UISelection<FileResource<?>> currentSelection = builder
                  .getUIContext().getInitialSelection();
         if (!currentSelection.isEmpty())
         {
            FileResource<?> resource = currentSelection.get();
            if (resource instanceof DirectoryResource)
            {
               targetLocation
                        .setDefaultValue((DirectoryResource) resource);
            }
            else
            {
               targetLocation.setDefaultValue(resource.getParent());
            }
         }
      }
      else if (project.hasFacet(JavaSourceFacet.class))
      {
         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         targetLocation.setDefaultValue(facet.getSourceDirectory())
                  .setEnabled(false);
         targetPackage.setValue(project.getFacet(MetadataFacet.class)
                  .getTopLevelPackage() + ".portlet");
      }
      modes.setDefaultValue(Arrays.asList("EDIT", "HELP", "VIEW"));
      builder.add(targetLocation);
      builder.add(targetPackage).add(named).add(targetClass).add(mimeType)
               .add(modes).add(title).add(shortTitle).add(keywords);
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

   @SuppressWarnings("unused")
   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {

      Project project = getSelectedProject(context);
      PortletFacet_2_0 facet = project.getFacet(PortletFacet_2_0.class);
      PortletDescriptor config = facet.getConfig();

      JavaClass javaClass = createJavaClass();

      List<String> modes = new ArrayList<String>();
      for (String mode : this.modes.getValue())
      {
         modes.add(mode);
      }

      newPortlet(config, named.getValue(), javaClass.getName(), mimeType.getValue(),
               Strings.join(modes.toArray(), ","), title.getValue(), shortTitle.getValue(), keywords.getValue());

      DirectoryResource targetDir = targetLocation.getValue();
      JavaResource javaResource;
      if (project == null)
      {
         javaResource = getJavaResource(targetDir, javaClass.getName());
         javaResource.setContents(javaClass);
      }
      else
      {
         JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
         javaResource = java.saveJavaSource(javaClass);
      }

      facet.saveConfig(config);
      context.getUIContext().setSelection(javaResource);
      return Results.success("Portlet " + javaResource + " created");
   }

   private JavaClass createJavaClass()
   {
      String className = targetClass.getValue();
      String packageName = targetPackage.getValue();

      if (Strings.isNullOrEmpty(className))
         className = Strings.capitalize(named.getValue());

      JavaClass javaClass = javaSourceFactory.create(JavaClass.class)
               .setName(className).setPublic();

      if (Strings.isNullOrEmpty(packageName))
      {
         javaClass.setPackage(packageName);
      }

      javaClass.addImport("java.io.IOException");
      javaClass.addImport("java.io.PrintWriter");
      javaClass.addImport("javax.portlet.GenericPortlet");
      javaClass.addImport("javax.portlet.RenderRequest");
      javaClass.addImport("javax.portlet.RenderResponse");

      javaClass.setSuperType("javax.portlet.GenericPortlet");
      Method<JavaClass> doView = javaClass
               .addMethod("public void doView(RenderRequest request, RenderResponse response)");
      doView.addThrows("java.io.IOException");
      doView.setBody("PrintWriter writer = response.getWriter();\nwriter.write(\"Hello Forge !\");\nwriter.close();");

      return javaClass;
   }

   private JavaResource getJavaResource(final DirectoryResource sourceDir,
            final String relativePath)
   {
      String path = relativePath.trim().endsWith(".java") ? relativePath
               .substring(0, relativePath.lastIndexOf(".java")) : relativePath;
      path = path.replace(".", "/") + ".java";
      JavaResource target = sourceDir
               .getChildOfType(JavaResource.class, path);
      return target;
   }

   private PortletType<PortletDescriptor> newPortlet(
            PortletDescriptor portletDescriptor, String portletName,
            String portletClass, String portletMimeType, String portletModes,
            String portletTitle, String portletShortTitle,
            String portletKeywords)
   {
      final PortletType<PortletDescriptor> portlet = portletDescriptor
               .createPortlet();
      portlet.portletName(portletName).portletClass(portletClass);
      portlet.getOrCreateSupports().mimeType(portletMimeType)
               .portletMode(portletModes.split(","));
      portlet.getOrCreatePortletInfo().title(
               portletTitle != null ? portletTitle : portletName);
      portlet.getOrCreatePortletInfo()
               .shortTitle(
                        portletShortTitle != null ? portletShortTitle
                                 : portletName).keywords(portletKeywords);
      return portlet;
   }
}
