-- -- Top domain (example domain)
-- -- quota : 1 To, quota_warning : 950 Go
-- -- max_file_size : 10 Go
 INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (2, 'd273f30c-825c-4379-bd21-470b0cb40da6', NOW(), NOW(), null, null, null, 0, 0, 2, null, 1, 1099511627776, 1045824536576, 10737418240, null, 'DOMAIN_QUOTA');
 INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (5, 'daa310bc-6b94-4ddc-855b-f9ef1a653f15', NOW(), NOW(), null, 2, null, 0, 0, 2, null, 1, 1099511627776, 1045824536576, 10737418240, 'USER', 'CONTAINER_QUOTA');
 INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (6, 'd91c2675-460f-4f31-b0cd-1eb82e2900ee', NOW(), NOW(), null, 2, null, 0, 0, 2, null, 1, 1099511627776, 1045824536576, 10737418240, 'WORK_GROUP', 'CONTAINER_QUOTA');


-- -- Sub domain (example domain)
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (3, '8aa8beb0-5640-488e-8854-620ef6686aa8', NOW(), NOW(), null, null, null, 0, 0, 3, null, 1, 1099511627776, 1045824536576, 10737418240, null, 'DOMAIN_QUOTA');
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (7, '5b60eeec-973f-4086-a021-76b95bfaaf85', NOW(), NOW(), null, 3, null, 0, 0, 3, null, 2, 1099511627776, 1045824536576, 10737418240, 'USER', 'CONTAINER_QUOTA');
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (8, '67d57eed-c5f9-4938-89a7-139d1bd7b32b', NOW(), NOW(), null, 3, null, 0, 0, 3, null, 2, 1099511627776, 1045824536576, 10737418240, 'WORK_GROUP', 'CONTAINER_QUOTA');


-- -- Guest domain (example domain)
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (4, '7b39a7ca-f0e8-4e6d-9d1a-3b3d076d86ea', NOW(), NOW(), null, null, null, 0, 0, 4, null, 1,1099511627776, 1045824536576, 10737418240, null, 'DOMAIN_QUOTA');
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (9, '6a7d9884-7427-46b7-9142-0625ee6ad906', NOW(), NOW(), null, 4, null, 0, 0, 4, null, 2, 1099511627776, 1045824536576, 10737418240, 'USER', 'CONTAINER_QUOTA');
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (10, 'd53ce127-0d22-47c1-96c4-3a3d308ac8b7', NOW(), NOW(), null, 4, null, 0, 0, 4, null, 2, 1099511627776, 1045824536576, 10737418240, 'WORK_GROUP', 'CONTAINER_QUOTA');



-- root domain 1, DOMAIN_QUOTA
update quota set
	current_value=1096,
	last_value=100,
	quota=2300,
	quota_warning=2000,
	max_file_size=10
	where id=1;

-- topdomain 2, DOMAIN_QUOTA
update quota set
	current_value=1096,
	last_value=500,
	quota=1900,
	quota_warning=1800,
	max_file_size=5,
	default_max_file_size=5
	where id=2;

-- topdomain 2, CONTAINER_QUOTA - USER
update quota set
	current_value=496,
	last_value=0,
	quota=1900,
	quota_warning=1300,
	max_file_size=5,
	default_max_file_size=5,
	batch_modification_date=now()
	where id=5;

-- topdomain 2, CONTAINER_QUOTA - THREAD
update quota set
	current_value=900,
	last_value=200,
	quota=2000,
	quota_warning=1500,
	max_file_size=5,
	default_max_file_size=5,
	default_account_quota=2000,
	account_quota=2000,
	batch_modification_date=now()
	where id=6;

-- topdomain 2, ACCOUNT_QUOTA - Jane (11)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	101, 'aebe1b64-39c0-11e5-9fa8-080027b8274a', NOW(), NOW(), NOW(),
	null, 5, 800, 0,
	2, 11, null, 1600,
	1480, 5, null, 'ACCOUNT_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - John (12)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	102, 'aebe1b64-39c0-11e5-9fa8-080027b8274b', NOW(), NOW(), NOW(),
	null, 5, 900, 100,
	2, 12, null, 1500,
	1000, 6, null, 'ACCOUNT_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - thread (20) cf import-tests-stat.sql
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	103, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', NOW(), NOW(), NOW(),
	null, 6, 700, 0,
	2, 20, null, 1000,
	800, 5, null, 'ACCOUNT_QUOTA');

	-- topdomain 2, ACCOUNT_QUOTA - thread (21) cf import-tests-stat.sql
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	104, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', NOW(), NOW(), NOW(),
	null, 6, 500, 200,
	2, 21, null, 1300,
	1000, 6, null, 'ACCOUNT_QUOTA');
