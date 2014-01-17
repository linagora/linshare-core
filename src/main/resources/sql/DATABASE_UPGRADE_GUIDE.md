# LinShare upgrade guide

## LinShare Versions

LinShare version number are named like X.Y.Z.

* X : Major release
A major version brings important changes in used technologies
that could break compatibility between to version of LinShare. 

* Y : Minor release
A minor version brings new functionalities and database upgrade.

* Z : maintenance release
Only bug fixes. No database upgrade.



## Migration scripts

Migration scripts are specific for each database, you have one directory by supported database.
All scripts are named like "Migration_X.A.0_to_X.B.0.sql"

In order to upgrade LinShare from 1.1 to 1.4 you ALWAYS need to run all scripts : 
	1. Migration_1.1.0_to_1.2.0.sql
	2. Migration_1.2.0_to_1.3.0.sql
	3. Migration_1.2.0_to_1.4.0.sql


## How To

1. Stop LinShare

> service tomcat7 stop

2. Backup your database

	* postgresql	: pg_dump

	* mysql		: mysqldump

3. upgrade database

	* postgresql :

> psql -U linshare -d linshare
> \i Migration_1.1.0_to_1.2.0.sql
> \i ...

	* mysql	:

> mysql -u linshare -p linshare < Migration_1.1.0_to_1.2.0.sql


## Migration with particularity

### 1.4.0 to 1.5.0

* postgresql :
This migration script must be run with user postgres because we used plpgpsql language.

For postgresql 8+, you may need to comment "DROP EXTENSION IF EXISTS plpgsql;" command from the migration script
because this version do not support it.

* mysql :
Nothing particular.
