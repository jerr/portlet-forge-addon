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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.InitParamType;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletType;

/**
 * @author Jeremie Lagarde
 */
@Alias("portlet")
public class PortletPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;
	   
   @Inject
   private ShellPrompt prompt;
   
   @Inject
   private Shell shell;


   @DefaultCommand
   public void status(final PipeOut out)
   {
      if (project.hasFacet(PortletFacet.class))
      {
         ShellMessages.success(out, "Portlet is installed.");
      }
      else
      {
         ShellMessages.warn(out, "Portlet is NOT installed.");
      }
   }

   @SetupCommand
   public void setup(final PipeOut out, @Option(name = "quickstart") final boolean quickstart)
	       throws Throwable
   {
      if (!project.hasFacet(WebResourceFacet.class))
      {
         request.fire(new InstallFacets(WebResourceFacet.class));
      }
      if (!project.hasFacet(PortletFacet.class))
      {
         request.fire(new InstallFacets(PortletFacet.class));
      }
      if (quickstart)
      {
    	  quickstart();
      }
      status(out);
   }
   
   @Command("new-portlet")
   public void newportlet(
       @Option(required = true, name = "named", description = "The portlet name") final String portletName,
       @Option(required = false, name = "class", description = "The portlet class") final String portletClass,
       @Option(required = false, name = "mime-type", description = "The portlet mime-type", defaultValue = "text/html") final String portletMimeType,
       @Option(required = false, name = "modes", description = "The portlet modes", defaultValue = "EDIT,HELP,VIEW") final String portletModes,
       @Option(required = false, name = "title", description = "The portlet title") final String portletTitle,
       @Option(required = false, name = "short-title", description = "The portlet short title") final String portletShortTitle,
       @Option(required = false, name = "keywords", description = "The portlet keywords") final String portletKeywords)
       throws Throwable
	{
	   final PortletFacet facet = project.getFacet(PortletFacet.class);
	   String packageName = portletClass!=null?portletClass.substring(0,portletClass.lastIndexOf('.')):null;
	   String className = portletClass!=null?portletClass.substring(portletClass.lastIndexOf('.')+1):null;
	   if ((packageName == null) || "".equals(packageName))
	   {
		   if (getPackagePortionOfCurrentDirectory() != null)
		   {  
			   packageName = getPackagePortionOfCurrentDirectory();
		   }
	       else
	       {
	    	   packageName = prompt.promptCommon(
	                  "In which package you'd like to create this portlet", PromptType.JAVA_PACKAGE);
	       
	       }
	   }
	   if ((className == null) || "".equals(className))		
	   {
		   className = portletName;
	   }


	   final PortletDescriptor portletDescriptor = facet.getConfig();
	   newPortlet(portletDescriptor, portletName, packageName+"."+className, portletMimeType, portletModes,
			   portletTitle, portletShortTitle, portletKeywords);
	   facet.saveConfig(portletDescriptor);

	   JavaClass javaClass = JavaParser.create(JavaClass.class);
	   javaClass.setPackage(packageName);
	   javaClass.setName(className);

	   javaClass.addImport("java.io.IOException");
	   javaClass.addImport("java.io.PrintWriter");
	   javaClass.addImport("javax.portlet.GenericPortlet");
	   javaClass.addImport("javax.portlet.RenderRequest");
	   javaClass.addImport("javax.portlet.RenderResponse");

	   javaClass.setSuperType("javax.portlet.GenericPortlet");
	   Method<JavaClass> doView = javaClass.addMethod("public void doView(RenderRequest request, RenderResponse response)");
	   doView.addThrows("java.io.IOException");
       doView.setBody("PrintWriter writer = response.getWriter();\nwriter.write(\"Hello Forge !\");\nwriter.close();");     

       project.getFacet(JavaSourceFacet.class).saveJavaSource(javaClass);
       shell.println("Created portlet class [" + javaClass.getQualifiedName() + "]");	   
    }

   @Command("new-faces-portlet")
   public void newfacesportlet(
       @Option(required = true, name = "named", description = "The portlet name") final String portletName,
       @Option(required = false, name = "class", description = "The portlet class", defaultValue = "javax.portlet.faces.GenericFacesPortlet") final String portletClass,
       @Option(required = false, name = "mime-type", description = "The portlet mime-type", defaultValue = "text/html") final String portletMimeType,
       @Option(required = false, name = "modes", description = "The portlet modes", defaultValue = "EDIT,HELP,VIEW") final String portletModes,
       @Option(required = false, name = "view-id", description = "The default view id") final String viewId,
       @Option(required = false, name = "edit-id", description = "The default view id") final String editId,
       @Option(required = false, name = "help-id", description = "The default view id") final String helpId,
       @Option(required = false, name = "title", description = "The portlet title") final String portletTitle,
       @Option(required = false, name = "short-title", description = "The portlet short title") final String portletShortTitle,
       @Option(required = false, name = "keywords", description = "The portlet keywords") final String portletKeywords)
       throws Throwable
	{
	   if (!project.hasFacet(PortletFacesFacet.class))
	   {
	      request.fire(new InstallFacets(PortletFacesFacet.class));
	   }
	   final PortletFacesFacet facet = project.getFacet(PortletFacesFacet.class);
	   final PortletDescriptor portletDescriptor = facet.getConfig();
	   final PortletType<PortletDescriptor> portlet = newPortlet(portletDescriptor, portletName, portletClass, portletMimeType, portletModes,
			   portletTitle, portletShortTitle, portletKeywords);
	   if(viewId!=null && viewId.length()>0)
		   getPortletInitParam(portlet, "javax.portlet.faces.defaultViewId.view").value(viewId);
	   if(editId!=null && editId.length()>0)
			getPortletInitParam(portlet, "javax.portlet.faces.defaultViewId.edit").value(editId);
	   if(helpId!=null && helpId.length()>0)
			getPortletInitParam(portlet, "javax.portlet.faces.defaultViewId.help").value(helpId);
	   facet.saveConfig(portletDescriptor);
	}
   
   
   private PortletType<PortletDescriptor> newPortlet(PortletDescriptor portletDescriptor, String portletName,String portletClass,
		   String portletMimeType, String portletModes, String portletTitle, String portletShortTitle, String portletKeywords)
       throws Throwable
	{
	   final PortletType<PortletDescriptor> portlet = portletDescriptor.createPortlet();
	   portlet.portletName(portletName).portletClass(portletClass);
	   portlet.getOrCreateSupports().mimeType(portletMimeType).portletMode(portletModes.split(","));
	   portlet.getOrCreatePortletInfo().title(portletTitle!=null?portletTitle:portletName);
	   portlet.getOrCreatePortletInfo().shortTitle(portletShortTitle!=null?portletShortTitle:portletName).keywords(portletKeywords);
	   return portlet;
    }
   
   @Command("add-init-param")
   public void addInitParam(
       @Option(required = true, name = "portlet", description = "The portlet name") final String portletName,
       @Option(required = true, name = "name", description = "The parameter name") final String paramName,
       @Option(required = true, name = "value", description = "The parameter value") final String paramValue)
    	       throws Throwable
    {
	   final PortletFacet facet = project.getFacet(PortletFacet.class);
	   final PortletDescriptor portletDescriptor = facet.getConfig();
	   final InitParamType<PortletType<PortletDescriptor>> initParam = getPortletInitParam(getPortlet(portletDescriptor, portletName), paramName);
	   initParam.value(paramValue);
	   facet.saveConfig(portletDescriptor);
	}   
    	       
	private PortletType<PortletDescriptor> getPortlet(PortletDescriptor portletDescriptor, String portletName) {
		for (PortletType<PortletDescriptor> portletType : portletDescriptor.getAllPortlet()) {
			if(portletType.getPortletName().equals(portletName))
			{
				return portletType;
			}
		}
		return portletDescriptor.createPortlet().portletName(portletName);
	}
	
	private InitParamType<PortletType<PortletDescriptor>> getPortletInitParam(PortletType<PortletDescriptor> portlet, String initParamName) {
		for (InitParamType<PortletType<PortletDescriptor>> initParam : portlet.getAllInitParam()) {
			if(initParam.getName().equals(initParamName))
			{
				return initParam;
			}
		}
		return portlet.createInitParam().name(initParamName);
	}

	private void quickstart() throws Exception {
		shell.execute("portlet new-portlet --named SimpleForgePortlet --class org.jboss.portal.portlet.samples.SimpleForgePortlet");
	}   


   /**
	* Retrieves the package portion of the current directory if it is a package, null otherwise.
	*
	* @return String representation of the current package, or null
	*/
    private String getPackagePortionOfCurrentDirectory()
    {
       for (DirectoryResource r : project.getFacet(JavaSourceFacet.class).getSourceFolders())
       {
          final DirectoryResource currentDirectory = shell.getCurrentDirectory();
          if (ResourceUtil.isChildOf(r, currentDirectory))
          {
             // Have to remember to include the last slash so it's not part of the package
             return currentDirectory.getFullyQualifiedName().replace(r.getFullyQualifiedName() + "/", "")
                      .replaceAll("/", ".");
          }
       }
       return null;
    }
}
