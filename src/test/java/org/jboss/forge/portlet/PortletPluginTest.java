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

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.junit.Test;

public class PortletPluginTest extends AbstractShellTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment()
               .addPackages(true, PortletPlugin.class.getPackage());
   }

   @Test
   public void testDefaultCommand() throws Exception
   {
      getShell().execute("portlet");
   }

   @Test
   public void testSetup() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("");
      
      // Install portlet facet
      getShell().execute("portlet setup");
      assertTrue(project.hasFacet(PortletFacet.class));
   }
   
}
