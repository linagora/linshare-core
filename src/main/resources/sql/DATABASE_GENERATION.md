# LinShare Generate Database data file

All data in the LinShare database are scattered on 10 different files, to generate a single file that contains all the data, it is necessary to use the plugin "org.zcore.maven:merge-maven-plugin".

To do this it is enough to run the following command: `org.zcore.maven:merge-maven-plugin:merge`.
Once the build is completed successfully, you will find a new file that has been generated called import-posgresql.sql, whose complete path is ${projectBaseDirectory}/src/main/resources/sql/postgresql/import-postgresql.sql
