INSERT INTO operation_history (id, creation_date, operation_value, operation_type, ensemble_type, domain_id, account_id)
VALUES (1,DATEADD('DAY',-60, NOW()),-200,1, 'USER',1,11), (2,DATEADD('DAY',-90, NOW()),-300,1, 'USER',1,11), (3,DATEADD('DAY',-15, NOW()),400,0, 'USER',1,11);

INSERT INTO operation_history (id, creation_date, operation_value, operation_type, ensemble_type, domain_id, account_id)
VALUES (4,DATEADD('DAY',-10, NOW()),200,0, 'USER',2,11), (5,DATEADD('DAY',-1, NOW()),200,0, 'USER',2,11), (6,DATEADD('DAY',-2, NOW()),200,0, 'USER', 2,11), (7,DATEADD('DAY',-25, NOW()),200,0, 'USER', 2,11),
(8,DATEADD('DAY',+15, NOW()),200,0, 'USER',2,11);

INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (20, 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');

INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (21, 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');

INSERT INTO operation_history (id, creation_date, operation_value, operation_type, ensemble_type, domain_id, account_id)
VALUES (9,DATEADD('DAY',+15, NOW()),200,0, 'THREAD',1,20), (10,DATEADD('DAY',-15, NOW()),-300,1, 'THREAD',3,20), (11,DATEADD('DAY',+60, NOW()),400,0, 'USER',3,20), (12,DATEADD('DAY',-60, NOW()),400,0, 'THREAD',3,20);

INSERT INTO operation_history (id, creation_date, operation_value, operation_type, ensemble_type, domain_id, account_id)
VALUES (13,DATEADD('DAY',+15, NOW()),200,0, 'USER',4,10), (14,DATEADD('DAY',-15, NOW()),200,0, 'THREAD', 4,21), (15,DATEADD('DAY',+30, NOW()),200,0, 'USER', 4,10), (16,DATEADD('DAY',-30, NOW()),200,0, 'USER', 4,10);