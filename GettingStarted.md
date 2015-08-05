# How we set up our development environment in Eclipse #

_Be aware that the pPOD Mesquite Modules which use pPOD have not been released yet! So if you really want to use this stuff, you'll have to write your own client._

  1. Install Apache Tomcat http://tomcat.apache.org/
  1. Install [MySQL](http://www.mysql.com/downloads/mysql/)
  1. Get the Eclipse IDE for Java EE developers from, for example, http://www.eclipse.org/downloads/
  1. Install the [M2Eclipse](http://m2eclipse.sonatype.org/) Eclipse plugin. At a minimum you'll need:
    * m2eclipse Core
    * m2eclipse Extras
      * Maven SCM handler for Subclipse
  1. Install the [Subclipse](http://subclipse.tigris.org/) Eclipse plugin. At a minimum you'll need the required items:
    * Subclipse/Subclipse
    * Subclipse/Subversion Client Adapter
    * Subclipse/Subversion JavaHL Native Library Adapter
  1. Install the [TestNG](http://testng.org/doc/eclipse.html) Eclipse plugin
  1. Fire up Eclipse and switch over to the _SVN Repository Exploring_ perspective and create a repository at http://penn-ppod.googlecode.com/svn/ if you're not a commiter or https://penn-ppod.googlecode.com/svn/ if you're a committer
  1. Expand _http(s)://penn-ppod.googlecode.com/svn/_ and right-click (ctrl-click on Macs) on _trunk_ and choose _Check out as Maven Project_
    * Some of us like to choose the Name Template `[`artifactId`]`-`[`version`]` under _Advanced_ to give a quick indicator of which branch we're working on but that doesn't seem to work on OS X
    * Hit _Finish_
  1. Switch over to the _Java EE_ perspective

You should then be able to use the TestNG plugin to run the test suite in `ppod-cdm/src/test/resources/testng-fast.xml` by right-clicking on it and choosing `Run As...TestNG Suite`.

To run the RESTful services into MySQL, right-click on _ppod-services_ and choose _Run As...Run on Server_ and follow the instructions for running on a Tomcat server. You'll need to season hibernate.properties to taste.