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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;

/**
 * @author Jeremie Lagarde
 */
@Alias("forge.spec.facesportlet")
@RequiresFacet({ JavaSourceFacet.class, WebResourceFacet.class, DependencyFacet.class, PortletFacet.class })
@RequiresPackagingType({ PackagingType.JAR, PackagingType.WAR, PackagingType.BUNDLE })
public class PortletFacesFacetImpl extends PortletFacetImpl implements PortletFacesFacet
{

   @Inject
   public PortletFacesFacetImpl(final DependencyInstaller installer)
   {
	  super(installer); 
   }

   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
    		  (Dependency) DependencyBuilder.create("javax.portlet:portlet-api:2.0"),
    		  (Dependency) DependencyBuilder.create("org.jboss.portletbridge:portletbridge-api:2.0.0.FINAL"),
    		  (Dependency) DependencyBuilder.create("org.jboss.portletbridge:portletbridge-impl:2.0.0.FINAL"),
    		  (Dependency) DependencyBuilder.create("javax.faces:jsf-api:1.2_13"),
    		  (Dependency) DependencyBuilder.create("javax.faces:jsf-impl:1.2_13"));
   }
}
