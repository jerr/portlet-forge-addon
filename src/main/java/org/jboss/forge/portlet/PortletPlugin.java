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

import org.apache.commons.lang.StringUtils;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.SetupCommand;
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
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(PortletFacet.class))
      {
         request.fire(new InstallFacets(PortletFacet.class));
      }
      status(out);
   }

   @Command("new-portlet")
   public void newportlet(
       @Option(required = true, name = "named", description = "The portlet name") final String portletName,
       @Option(required = false, name = "class", description = "The portlet class", defaultValue = "javax.portlet.faces.GenericFacesPortlet") final String portletClass,
       @Option(required = false, name = "mime-type", description = "The portlet mime-type", defaultValue = "text/html") final String portletMimeType,
       @Option(required = false, name = "modes", description = "The portlet modes", defaultValue = "EDIT,HELP,VIEW") final String portletModes,
       @Option(required = false, name = "title", description = "The portlet title") final String portletTitle,
       @Option(required = false, name = "short-title", description = "The portlet short title") final String portletShortTitle,
       @Option(required = false, name = "keywords", description = "The portlet keywords") final String portletKeywords)
       throws Throwable
	{
	   final PortletFacet facet = project.getFacet(PortletFacet.class);
	   final PortletDescriptor portletDescriptor = facet.getConfig();
	   final PortletType<PortletDescriptor> portlet = portletDescriptor.createPortlet();
	   portlet.portletName(portletName).portletClass(portletClass);
	   portlet.getOrCreateSupports().mimeType(portletMimeType).portletMode(StringUtils.split(portletModes, ','));
	   portlet.getOrCreatePortletInfo().title(portletTitle).shortTitle(portletShortTitle).keywords(portletKeywords);
	   facet.saveConfig(portletDescriptor);
	}

   @Command("set-view-id")
   public void setViewFile(
       @Option(required = true, name = "portlet", description = "The portlet name") final String portletName,
       @Option(required = true, name = "view", description = "The default view id") final String view)
    	       throws Throwable
    {
	   addInitParam(portletName, "javax.portlet.faces.defaultViewId.view", view);
    }

   @Command("set-edit-id")
   public void setEditFile(
       @Option(required = true, name = "portlet", description = "The portlet name") final String portletName,
       @Option(required = true, name = "view", description = "The default view id") final String view)
    	       throws Throwable
    {
	   addInitParam(portletName, "javax.portlet.faces.defaultViewId.edit", view);
    }

   @Command("set-help-id")
   public void newportlet(
       @Option(required = true, name = "portlet", description = "The portlet name") final String portletName,
       @Option(required = true, name = "view", description = "The default view id") final String view)
    	       throws Throwable
    {
	   addInitParam(portletName, "javax.portlet.faces.defaultViewId.help", view);
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
	   final InitParamType<PortletType<PortletDescriptor>> initParam = getPortletInitParam(portletDescriptor, portletName, paramName);
	   initParam.value(paramValue);
	   facet.saveConfig(portletDescriptor);
	}   
    	       
	private PortletType<PortletDescriptor> getPortlet(PortletDescriptor portletDescriptor, String portletName) {
		for (PortletType<PortletDescriptor> portletType : portletDescriptor.getAllPortlet()) {
			if(portletType.getPortletName().equals(portletName)){
				return portletType;
			}
		}
		return portletDescriptor.createPortlet().portletName(portletName);
	}
	
	private InitParamType<PortletType<PortletDescriptor>> getPortletInitParam(PortletDescriptor portletDescriptor, String portletName, String initParamName) {
		final PortletType<PortletDescriptor> portlet = getPortlet(portletDescriptor, portletName);
		for (InitParamType<PortletType<PortletDescriptor>> initParam : portlet.getAllInitParam()) {
			if(initParam.getName().equals(initParamName)){
				return initParam;
			}
		}
		return portlet.createInitParam().name(initParamName);
	}
	   
   
}
