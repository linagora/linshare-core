INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (20, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');
INSERT INTO thread (account_id, name) VALUES (20, 'Ministère de l''intérieur');

INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (21, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');
INSERT INTO thread (account_id, name) VALUES (21, 'RATP');

INSERT INTO operation_history (id, uuid, creation_date, operation_value, operation_type, container_type, domain_id, account_id)
VALUES (1,'4c9c59e6-163a-48ab-919a-ceba6dc407f0', '2042-10-23',-200,1, 'USER',2,11),
(2, '4c9c59e6-163a-48ab-919a-ceba6dc408f0', DATEADD(day, -1,now()),-300,1, 'USER',2,11),
(3, '4c9c59e6-163a-48ab-919a-ceba6dc408f1', DATEADD(day, -1,now()),400,0, 'USER',2,11),
(4, '4c9c59e6-163a-48ab-919a-ceba6dc408f2', DATEADD(day, -1,now()),200,0, 'USER',2,11),
(5, '4c9c59e6-163a-48ab-919a-ceba6dc408f3', '2042-10-15',200,0, 'USER',2,11),
(6, '4c9c59e6-163a-48ab-919a-ceba6dc408f4', '2042-10-15',200,0, 'USER', 2,11),
(7, '4c9c59e6-163a-48ab-919a-ceba6dc408f5', '2042-10-16',200,0, 'USER', 2,11),
(8, '4c9c59e6-163a-48ab-919a-ceba6dc408f6', '2042-10-16',200,0, 'USER',2,11),
(9, '4c9c59e6-163a-48ab-919a-ceba6dc408f7', DATEADD(day, -1,now()),200,0, 'WORK_GROUP',3,20),
(10, '4c9c59e6-163a-48ab-919a-ceba6dc408f8', DATEADD(day, -1,now()),-300,1, 'WORK_GROUP',3,20),
(11, '4c9c59e6-163a-48ab-919a-ceba6dc408f9', DATEADD(day, -1,now()),400,0, 'WORK_GROUP',3,20),
(12, '4c9c59e6-163a-48ab-919a-ceba6dc409f0', '2042-09-13',400,0, 'WORK_GROUP',3,20),
(14, '4c9c59e6-163a-48ab-919a-ceba6dc409f1', DATEADD(day, -1,now()),200,0, 'WORK_GROUP', 4,21),
(25, '4c9c59e6-163a-48ab-919a-ceba6dc409f2', DATEADD(day, -1,now()),200,0, 'WORK_GROUP', 4,21),
(26, '4c9c59e6-163a-48ab-919a-ceba6dc409f3', '2042-10-16',200,0, 'WORK_GROUP',1,20),
(16, '4c9c59e6-163a-48ab-919a-ceba6dc409f4', '2042-10-14',450,0, 'USER',1,10),
(17, '4c9c59e6-163a-48ab-919a-ceba6dc409f5', '2042-10-15',270,0, 'USER',2,10),
(18, '4c9c59e6-163a-48ab-919a-ceba6dc409f6', '2042-10-15',220,0, 'USER',2,10),
(19, '4c9c59e6-163a-48ab-919a-ceba6dc409f7', '2042-10-15',220,0, 'USER', 2,10),
(20, '4c9c59e6-163a-48ab-919a-ceba6dc409f8', '2042-10-16',270,0, 'USER', 2,10),
(21, '4c9c59e6-163a-48ab-919a-ceba6dc409f9', '2042-10-16',21,0, 'USER',2,10);
