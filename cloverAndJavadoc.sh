svn delete https://penn-ppod.googlecode.com/svn/site/clover -m "deleting clover report"
svn import /home/tomcat/hudson/jobs/pPOD-0.2-SNAPSHOT-CheckIn/workspace/trunk/target/site/clover/ https://penn-ppod.googlecode.com/svn/site/clover -m "clover report"
svn delete https://penn-ppod.googlecode.com/svn/site/cdm/javadoc -m "deleting cdm javadoc"
svn import /home/tomcat/hudson/jobs/pPOD-0.2-SNAPSHOT-CheckIn/workspace/trunk/cdm/target/site/apidocs/ https://penn-ppod.googlecode.com/svn/site/cdm/javadoc -m "cdm javadoc"
