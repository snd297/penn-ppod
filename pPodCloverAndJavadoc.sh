svn delete https://penn-ppod.googlecode.com/svn/site/clover -m "deleting clover report"
svn import trunk/target/site/clover/ https://penn-ppod.googlecode.com/svn/site/clover -m "clover report"
svn delete https://penn-ppod.googlecode.com/svn/site/cdm/javadoc -m "deleting cdm javadoc"
svn import trunk/cdm/target/site/apidocs/ https://penn-ppod.googlecode.com/svn/site/cdm/javadoc -m "cdm javadoc"
