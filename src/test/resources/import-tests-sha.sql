INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, domain_quota, ensemble_quota,current_value, last_value, domain_id, account_id, parent_domain_id, quota, quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (2, 'aebe1b64-39c0-11e5-9fa8-080027b8274b', NOW(), NOW(), NOW(), null, null,1096,500,2,null,null, 99991900, 1800, 9999999999, null, 'DOMAIN_QUOTA'),
(3, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', NOW(), NOW(), NOW(), 2, null,496,0,2,null,1, 99991900, 1300, 9999999999, 'USER', 'ENSEMBLE_QUOTA'),
(6, 'aebe1b64-39c0-11e5-9fa8-080027b8274e', NOW(), NOW(), NOW(), 2, null,900,200,2,null,1, 99992000, 1500, 9999999999, 'THREAD', 'ENSEMBLE_QUOTA'),
(1, 'aebe1b64-39c0-11e5-9fa8-080027b8274a', NOW(), NOW(), NOW(), null, 3,800,0,2,11,null, 9999000, 1480, 9999999999, null, 'ACCOUNT_QUOTA'),
(5, 'aebe1b64-39c0-11e5-9fa8-080027b8274f', NOW(), NOW(), NOW(), null, 3,900,100,2,12,null, 99991500, 1000, 6, null, 'ACCOUNT_QUOTA'),
(4, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', NOW(), NOW(), NOW(), null, null, 1096,100, null, null, null, 99992300, 2000, 9999999999, null, 'PLATFORM_QUOTA');