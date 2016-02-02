INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (20, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');
INSERT INTO thread (account_id, name) VALUES (20, 'Ministère de l''intérieur');

INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (21, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');
INSERT INTO thread (account_id, name) VALUES (21, 'RATP');

INSERT INTO operation_history (id, creation_date, operation_value, operation_type, ensemble_type, domain_id, account_id)
VALUES (1,'2042-10-23',-200,1, 'USER',2,11),
(2,DATEADD(day, -1,now()),-300,1, 'USER',2,11),
(3,DATEADD(day, -1,now()),400,0, 'USER',2,11),
(4,DATEADD(day, -1,now()),200,0, 'USER',2,11),
(5,'2042-10-15',200,0, 'USER',2,11),
(6,'2042-10-15',200,0, 'USER', 2,11),
(7,'2042-10-16',200,0, 'USER', 2,11),
(8,'2042-10-16',200,0, 'USER',2,11),
(9,DATEADD(day, -1,now()),200,0, 'THREAD',3,20),
(10,DATEADD(day, -1,now()),-300,1, 'THREAD',3,20),
(11,DATEADD(day, -1,now()),400,0, 'THREAD',3,20),
(12,'2042-09-13',400,0, 'THREAD',3,20),
(14,DATEADD(day, -1,now()),200,0, 'THREAD', 4,21),
(25,DATEADD(day, -1,now()),200,0, 'THREAD', 4,21),
(26,'2042-10-16',200,0, 'THREAD',1,20),
(16,'2042-10-14',450,0, 'USER',1,10),
(17,'2042-10-15',270,0, 'USER',2,10),
(18,'2042-10-15',220,0, 'USER',2,10),
(19,'2042-10-15',220,0, 'USER', 2,10),
(20,'2042-10-16',270,0, 'USER', 2,10),
(21,'2042-10-16',21,0, 'USER',2,10);