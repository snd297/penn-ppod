rm -rf /var/www/html/ppod/*
cp -r trunk/target/site/clover/ /var/www/html/ppod/clover/
mkdir /var/www/html/ppod/cdm/
cp -r trunk/cdm/target/site/apidocs/ /var/www/html/ppod/cdm/javadoc/