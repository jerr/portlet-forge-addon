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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
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
   
   @Test
   public void testCreatePortlet() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("");
      
      // Install portlet facet
      getShell().execute("portlet setup");
      assertTrue(project.hasFacet(PortletFacet.class));

      // Install faces facet
      // getShell().execute("faces setup");
      // assertTrue(project.hasFacet(FacesFacet.class));
      
      // Create new helloportlet portlet whith init-param
      getShell().execute("portlet new-faces-portlet --named helloportlet --title \"My forge portlet\" "+
    		  "--short-title ForgePortlet --keywords \"demo,forge,portlet\" "+
    		  "--view-id \"/home.xhtml\" --edit-id \"/edit.xhtml\" --edit-id \"/help.xhtml\"");
      getShell().execute("portlet add-init-param --portlet helloportlet --name javax.portlet.faces.renderPolicy --value NEVER_DELEGATE");

      // Create new hiportlet portlet
      getShell().execute("portlet new-portlet --named hiportlet --modes \"VIEW,HELP\"  --class demo.hiportlet");

      // Verify portlet.xml file
      PortletDescriptor config = project.getFacet(PortletFacesFacet.class).getConfig();
      String portletXmlOriginal = getResourceContents("src/test/resources/portlet.xml.original");
      String portletXmlGenerated = config.exportAsString();
      assertEquals(portletXmlOriginal, portletXmlGenerated);
   
      // Verify pom.xml file
      Resource pom = project.getProjectRoot().getChild("pom.xml");
      String pomOriginal = getResourceContents("src/test/resources/pom.xml.original");
      String pomGenerated = getResourceContents(pom.getFullyQualifiedName());
      assertEquals(pomOriginal, pomGenerated);
   }
   
   private String getResourceContents(String resource) throws Exception
   {
      assert resource != null && resource.length() > 0 : "Resource must be specified";
      final BufferedReader reader = new BufferedReader(new FileReader(resource));
      final StringBuilder builder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null)
      {
         builder.append(line);
         builder.append("\n");
      }
      return builder.toString();
   }
}
