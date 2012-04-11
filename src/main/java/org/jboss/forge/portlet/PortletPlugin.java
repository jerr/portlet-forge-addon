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

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.SetupCommand;

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

}
