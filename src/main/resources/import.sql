-- adding 40 days in expiry time
INSERT INTO linshare_parameter(parameter_id, file_size_max, user_available_size, active_mimetype,active_signature,active_encipherment,user_expiry_time,user_expiry_time_unit_id, default_expiry_time,default_expiry_time_unit_id)  VALUES (1,10240000,51200000,'false','true','false','40','0', '100', '0');


-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'
INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (1, 0, 'root@localhost.localdomain', 'admin', 'LinShare', 'root@localhost.localdomain', '01-01-2009', 1, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', '01-01-2109', 'true','true');
INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (2, 0, 'system', '', '', 'system@localhost', '01-01-2009', 2, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', '01-01-2109', 'true','true');

insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Welcome to LinShare, the Open Source secure files sharing system.', 0, 0);
insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.', 0, 1);
insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Welcome to LinShare, the Open Source secure files sharing system.', 1, 0);
insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.', 1, 1);


SELECT setval('hibernate_sequence', 100);
