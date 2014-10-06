#!/bin/sh

mysql -proot < createDatabase.sql
mysql -u linshare -plinshare linshare < createSchema.sql
mysql -u linshare -plinshare linshare < import-mysql.sql
mysql -u linshare -plinshare linshare < sample1.sql
