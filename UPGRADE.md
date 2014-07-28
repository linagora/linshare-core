# LinShare Upgrade Guide

NB: You must shutdown Tomcat or Jetty before upgrading LinShare WAR.


## Database

First, you need to look into the DATABASE_UPGRADE_GUIDE to known if you need to upgrade your database. 
You can find this guide in two different locations :

* The latest document is present into the repository : src/main/resources/sql/DATABASE_UPGRADE_GUIDE.md

* It is also present into the new WAR : WEB-INF/classes/sql/DATABASE_UPGRADE_GUIDE.md


## WAR

Obviously, you need to replace the running WAR by the new WAR.

For tomcat, it may be useful to delete the directory where the WAR was exploded before restarting it.


## Specfic Versions

### From 1.0.x to 1.4.x

No specific configuration is needed.


### From 1.4.x to 1.5.x

After the restart of tomcat, you need to reconfigure the way LinShare gathers data from the LDAP directory.

You should create a new model parttern and backport your configuration (filters, fields) to the new one.

Then you must edit all your domains to use this new domain pattern.
