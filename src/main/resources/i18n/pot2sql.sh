#!/bin/bash

for locale in en_0 fr_1
do
  lang=`echo $locale | cut -f 1 -d '_'`
  index=`echo $locale | cut -f 2 -d '_'`

  echo "-- LOCALE $lang\n" >> i18n.sql

  # Adding mail templates
  echo "-- Mail templates \n" >> i18n_$lang.sql
  cat templates_mail_templates_$lang.pot \
	| tr '\n' '|'  \
	| sed -e "s/\"|msgstr/:/g" \
	| tr '|' '\n' \
	| sed -e "s/msgid \"\(.*\)_\([0-9]\+\): \"\(.*\)\"/INSERT INTO linshare_\1 values (1, \2, '\3', $index)/g" -e "s/^# /-- /g" >> i18n_$lang.sql

  # Adding mail subjects
  echo "-- Mail subjects \n" >> i18n_$lang.sql
  cat templates_mail_subjects_$lang.pot \
	| tr '\n' '|' \
	| sed -e "s/|\?# \([^|]\+\)|msgid \"\([^|]\+\)_\([0-9]\+\)_html\"|msgstr \"\([^|]\+\)\"||\?# \([^|]\+\)|msgid \"\([^|]\+\)\"|msgstr \"\([^|]\+\)\"|/INSERT INTO linshare_\2 VALUES (1, \3, '\4', '\5', $index)|/g" \
	| tr '|' '\n' \
	| sed -e "s/^# /-- /g" >> i18n_$lang.sql

  # Computing global i18n.sql file
  cat i18n_$lang.sql >> i18n.sql
  echo "\n" >> i18n.sql
done
