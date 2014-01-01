Installation
============
The Portlet plugin is not yet listed in the Forge plugin repository. You need to use the git url to install it. 
In Forge type: 
	
	forge git-plugin git://github.com/jerr/portlet-forge-addon.git

That's it! The plugin will be downloaded and installed.


Setting up a portlet project
==============
From the forge prompt, create a new project and install the PortletFacet. This sets up your pom.xml.

	new-project --named HelloForge  --topLevelPackage org.demo
	portlet setup

Generating portlets
==============
Once the Facet is installed, you can create new portlet.

	portlet new-portlet --named HelloPortlet
