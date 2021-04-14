# LinShare Generate Database data file

All data in the LinShare database are scattered on 9 different files under the
directory ${projectBaseDirectory}/src/main/resources/sql/common, to generate a
single file that contains all the data, it is necessary to use the maven goal:
`mvn process-resources`.
The generated files will be:
 * `target/classes/sql/postgresql/import-postgresql.sql`
 * `target/classes/sql/postgresql/import-all-emails.sql`

This plugin is automatically launched in process-resources phase in maven lifecycle.
