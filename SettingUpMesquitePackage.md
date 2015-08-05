## How to set up the pPOD Mesquite Package project under Eclipse to talk to a pPOD server ##

  1. Check out from the branch you're interested in. Trunk is at https://dbappserv.cis.upenn.edu/svnroot/develop/pPOD/mesquite/trunk/. You'll need to get a dbgroup Subversion account to access that repository. We'll assume that you call your project _pPODMesquitePackage_. We have this project checked in with the Eclipse config files, so (w/ Subclipse) you should just be able to do _Checkout..._ followed by _Check out as a project in the workspace_ and end up with a working Eclipse project.
  1. Assuming you already have Mesquite installed, add these entries to Mesquite's _classpath.xml_ file:
```
<classpath>../../pPODMesquitePackage/Extras/config</classpath>  
<classpath>../../pPODMesquitePackage/Mesquite_Folder</classpath>
```
  1. Now we need to tell the pPOD Mesquite package about the pPOD server. There is an empty file _pPODMesquitePackage/Extras/ppod.properties_ that you can copy and modify. Here's an example _ppod.properties_ file:
```
ppod.uri=https://someserver.edu/ppod-services
ppod.username=myname
ppod.password=mypassword
```
> Copy the example _ppod.properties_ into _pPODMesquitePackage/Extras/config_ and modify it with your values. Once in the _config_ folder, it will be on Mesquite's classpath owing to step 2. It should be noted that the _config_ folder has an _svn:ignore=`*`_ property on it, so _ppod.properties_ (or anything else you put in there) will not be committed to the repository. Which is a good thing both to prevent config conflicts with others and to preserve your password's secrecy. So this directory makes a good place to put any other config files that don't make sense to put into Subversion.

And that should be that.