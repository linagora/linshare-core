INSERT INTO statistique (id, creation_date, operation_count, delete_operation_count, add_operation_count, add_operation_sum, delete_operation_sum, diff_operation_sum, actual_operation_sum, domain_id, account_id, parent_domain_id, statistique_type)
VALUES (1,DATEADD('DAY',-30, NOW()),9,4,5,1500,-1000, 500, 500,1,11, null, 'USER_WEEKLY_STAT'), (2,DATEADD('DAY',-30, NOW()),22,10,12,2700,-2000, 700, 700,2,11,1, 'USER_WEEKLY_STAT'),
(3,DATEADD('DAY',+30, NOW()),9,4,5,1500,-1000, 500, 500,1,11, null, 'USER_WEEKLY_STAT');

INSERT INTO statistique (id, creation_date, operation_count, delete_operation_count, add_operation_count, add_operation_sum, delete_operation_sum, diff_operation_sum, actual_operation_sum, domain_id, account_id, parent_domain_id, statistique_type)
VALUES (4,DATEADD('DAY',-7, NOW()),5,2,3,100,-50, 50, 50,1,11, null, 'USER_DAILY_STAT'), (5,DATEADD('DAY',-7, NOW()),4,1,3,200,-50, 150, 150,1,11, null, 'USER_DAILY_STAT'), (6,DATEADD('DAY',+7, NOW()),5,2,3,100,-50, 50, 50,1,11, null, 'USER_DAILY_STAT'),
(7,DATEADD('DAY',-30, NOW()),5,2,3,100,-50, 50, 50,1,11, null, 'USER_DAILY_STAT'), (8,DATEADD('DAY',-14, NOW()),10,4,6,1700,-1000, 700, 700,2,11, null, 'USER_DAILY_STAT'),
(9,DATEADD('DAY',+7, NOW()),12,7,5,1500,-1000, 500, 500,1,11, null, 'USER_DAILY_STAT'), (10,DATEADD('DAY',+7, NOW()),12,7,5,1500,-1000, 500, 500,1,11, null, 'USER_DAILY_STAT');

INSERT INTO statistique (id, creation_date, operation_count, delete_operation_count, add_operation_count, add_operation_sum, delete_operation_sum, diff_operation_sum, actual_operation_sum, domain_id, account_id, parent_domain_id, statistique_type)
VALUES (11,DATEADD('DAY',-7, NOW()),5,2,3,100,-50, 50, 50,3, null, 2, 'DOMAIN_DAILY_STAT'), (12,DATEADD('DAY',-7, NOW()),4,1,3,200,-50, 150, 150,2, null, 1, 'DOMAIN_DAILY_STAT'), (13,DATEADD('DAY',-7, NOW()),5,2,3,100,-50, 50, 50,3, null, 2, 'DOMAIN_DAILY_STAT'), (14,DATEADD('DAY',-14, NOW()),5,2,3,100,-50, 50, 50,3, null, 2, 'DOMAIN_DAILY_STAT');

INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (22, 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274e', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');

INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) 
VALUES (23, 5, 'aebe1b64-39c0-11e5-9fa8-080027b8274f', now(), now(), 0, 'en', 'en', 'en', true, null, false, 2, 'IN_USE');

INSERT INTO statistique (id, creation_date, operation_count, delete_operation_count, add_operation_count, add_operation_sum, delete_operation_sum, diff_operation_sum, actual_operation_sum, domain_id, account_id, parent_domain_id, statistique_type)
VALUES (15,DATEADD('DAY',-30, NOW()),5,2,3,100,-50, 50, 50,2, 23, 1, 'THREAD_WEEKLY_STAT'), (16,DATEADD('DAY',-30, NOW()),4,1,3,200,-50, 150, 150,4, 22, 3, 'THREAD_WEEKLY_STAT'), (17,DATEADD('DAY',+2, NOW()),5,2,3,100,-50, 50, 50,4, 23, 3, 'THREAD_WEEKLY_STAT'), (18,DATEADD('DAY',+2, NOW()),5,2,3,100,-50, 50, 50,4, 22, 3, 'THREAD_DAILY_STAT'), (19,DATEADD('DAY',-7, NOW()),5,2,3,100,-50, 50, 50,3, 23, 2, 'THREAD_DAILY_STAT');

INSERT INTO statistique (id, creation_date, operation_count, delete_operation_count, add_operation_count, add_operation_sum, delete_operation_sum, diff_operation_sum, actual_operation_sum, domain_id, account_id, parent_domain_id, statistique_type)
VALUES (20,DATEADD('DAY',-30, NOW()),5,2,3,100,-50, 50, 50,3, null, 2, 'DOMAIN_WEEKLY_STAT'), (21,DATEADD('DAY',-30, NOW()),4,1,3,200,-50, 150, 150,2, null, 1, 'DOMAIN_WEEKLY_STAT'), (22,DATEADD('DAY',-30, NOW()),5,2,3,100,-50, 50, 50,3, null, 2, 'DOMAIN_WEEKLY_STAT'), (23,DATEADD('DAY',-60, NOW()),5,2,3,100,-50, 50, 50,3, null, 2, 'DOMAIN_WEEKLY_STAT'), (24,DATEADD('DAY',-60, NOW()),5,2,3,100,-50, 50, 50,4, null, 2, 'DOMAIN_WEEKLY_STAT');