

-- MyDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, quota_type, current_value_for_subdomains) VALUES ( 2, '164783e8-b9d1-11e5-87e9-bfc0aac925c2', NOW(), NOW(), null, 0, 0, 2, 1, 1000000000000, false, 1000000000000, 1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)


-- 'CONTAINER_QUOTA', 'USER' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 3, '37226d66-b9d2-11e5-b4d8-f7b730449724', NOW(), NOW(), null, 2, 0, 0, 2, 1, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 100000000000, false, 10000000000, false, 100000000000, false, 'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 4, '6a442450-b9d2-11e5-8c67-5b2367500fc4', NOW(), NOW(), null, 2, 0, 0, 2, 1, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 400000000000, false, 10000000000, false, 400000000000, false, 'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup



-- MySubDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, quota_type, current_value_for_subdomains) VALUES ( 5, 'b69b9d1a-b9d2-11e5-aab9-e337a9ab2b58', NOW(), NOW(), null, 0, 0, 3, 2, 1000000000000, false, 1000000000000, 1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)



-- 'CONTAINER_QUOTA', 'USER' for MySubDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 6, 'f8733bd0-b9d2-11e5-a247-2b9505cfdddf', NOW(), NOW(), null, 5, 0, 0, 3, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 100000000000, false, 10000000000, false, 100000000000, false, 'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MySubDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 7, '002310d0-b9d3-11e5-9413-d3f63c53e650', NOW(), NOW(), null, 5, 0, 0, 3, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 400000000000, false, 10000000000, false, 400000000000, false, 'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup


-- GuestDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, quota_type, current_value_for_subdomains) VALUES ( 8, '0b866494-b9d4-11e5-be35-afca154efca0', NOW(), NOW(), null, 0, 0, 4, 2, 1000000000000, false, 1000000000000, 1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)



-- 'CONTAINER_QUOTA', 'USER' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 9, '1515e6e2-b9d4-11e5-997e-0b5792ea886a', NOW(), NOW(), null, 8, 0, 0, 4, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 100000000000, false, 10000000000, false, 100000000000, false, 'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 10, '1f468522-b9d4-11e5-916d-a713a67dd225', NOW(), NOW(), null, 8, 0, 0, 4, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 400000000000, false, 10000000000, false, 400000000000, false, 'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
