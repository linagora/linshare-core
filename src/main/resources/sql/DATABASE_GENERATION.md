# LinShare Generate Database data file

All data in the LinShare database are scattered on 9 different files under the directory ${projectBaseDirectory}/src/main/resources/sql/common, to generate a single file that contains all the data, it is necessary to use the plugin "org.zcore.maven:merge-maven-plugin".

To do this it is enough to run the following command: `org.zcore.maven:merge-maven-plugin:merge`.
Once the build is completed successfully, you will find a new file that has been generated called import-posgresql.sql, whose complete path is ${projectBaseDirectory}/target/generated-sources/import-postgresql.sql

This plugin will be automatically launched in pre-packaged phase in maven lifecycle