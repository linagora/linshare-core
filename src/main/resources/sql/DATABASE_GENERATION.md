# LinShare Generate Database data file

All data in the LinShare database are scattered on 9 different files under the
directory ${projectBaseDirectory}/src/main/resources/sql/common, to generate a
single file that contains all the data, it is necessary to use the maven goal:
`mvn generate-sources`.
The generated file will be : `target/generated-sources/import-postgresql.sql`

This plugin is automatically launched in generate-sources phase in maven lifecycle.
