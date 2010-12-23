#!/bin/bash

echo "-- adding 40 days in expiry time" >> import.sql
echo "INSERT INTO linshare_parameter(parameter_id, file_size_max, user_available_size, active_mimetype,active_signature,active_encipherment,active_doc_time_stamp,user_expiry_time,user_expiry_time_unit_id, default_expiry_time,default_expiry_time_unit_id)  VALUES (1,10240000,51200000,'false','false','false','false','40','0', '100', '0');" >> import.sql

echo "" >> import.sql

echo "-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'" >> import.sql
echo "INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (1, 0, 'root@localhost.localdomain', 'Administrator', 'LinShare', 'root@localhost.localdomain', '2009-01-01', 1, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', '2019-01-01', 'true','true');" >> import.sql
echo "INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (2, 0, 'system', '', '', 'system@localhost', '2009-01-01', 2, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', '2019-01-01', 'true','true');" >> import.sql

echo "" >> import.sql

for locale in en_0 fr_1 nl_2
do
  lang=`echo $locale | cut -f 1 -d '_'`
  index=`echo $locale | cut -f 2 -d '_'`

  echo "-- LOCALE $lang" >> import.sql

  # Adding mail subjects
  echo "-- Mail subjects" >> i18n_$lang.sql
  cat templates_mail_subjects_$lang.pot \
	| tr '\n' '|'  \
	| sed -e "s/\"|msgstr/:/g" \
	| tr '|' '\n' \
	| sed -e "s/msgid \"\(.*\)_\([0-9]\+\): \"\(.*\)\"/INSERT INTO linshare_\1 (parameter_id, subject_id, content, language_id) VALUES (1, \2, '\3', $index);/g" \
	| sed -e "s/’/''/g" \
	| sed -e "s/^# /-- /g" >> i18n_$lang.sql

  # Adding mail templates
  echo "-- Mail templates" >> i18n_$lang.sql
  cat templates_mail_templates_$lang.pot \
	| tr '\n' '|' \
	| sed -e "s/|\?# \([^|]\+\)|msgid \"\([^|]\+\)_\([0-9]\+\)_html\"|msgstr \"\([^|]\+\)\"||\?# \([^|]\+\)|msgid \"\([^|]\+\)_\([0-9]\+\)_txt\"|msgstr \"\([^|]\+\)\"|/INSERT INTO linshare_\2 (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, \3, \"\4\", \"\8\", $index);|/g" \
	| tr '|' '\n' \
	| tr '\"' '|' \
	| tr "'" '\"' \
	| tr '|' "'" \
	| sed -e "s/’/''/g" \
	| sed -e "s/^# /-- /g" >> i18n_$lang.sql


  # Adding welcome texts
  echo "-- Welcome texts" >> i18n_$lang.sql
  cat templates_welcome_texts_$lang.pot \
	| tr '\n' '|'  \
	| sed -e "s/\"|msgstr/:/g" \
	| tr '|' '\n' \
	| sed -e "s/msgid \"\(.*\)_\([0-9]\+\): \"\(.*\)\"/INSERT INTO linshare_\1 (parameter_id , welcome_text, user_type_id, language_id) VALUES (1, '\3', \2, $index);|/g" \
	| tr '|' '\n' \
	| sed -e "s/’/''/g" \
	| sed -e "s/^# /-- /g" >> i18n_$lang.sql


  # Computing global i18n.sql file
  cat i18n_$lang.sql >> import.sql
done
