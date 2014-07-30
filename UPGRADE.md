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


### From 1.5.x to 1.6.x

The LinShare 1.6.0 is the first release of LinShare without the administration UI.
Now it is a standalone application built using AngularJS. This new application is called
linshare-ui-admin (2.0.0), store in a dedicated repository.
The deployment is a little bit more complicated, you need a new Apache virtual host for this new
 application like below :

<VirtualHost *:443>
	...
	ServerName linshare-admin.yourdomain.com
	<Location /linshare>
		ProxyPass http://127.0.0.1:8080/linshare/webservice/rest/admin
		ProxyPassReverse http://127.0.0.1:8080/linshare/webservice/rest/admin
	</Location>
	DocumentRoot /var/www/linshare-ui-admin-2.0.0
	...
</VirtualHost>

Look at the quick install guide for more informations.
