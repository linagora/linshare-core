INSERT INTO operation_history (
	id, uuid, creation_date, operation_value, 
	operation_type, container_type, domain_id, account_id)
VALUES 
--  Operations WORKGROUP (20)
	(15, '4c9c59e6-163a-48ab-919a-ceba6dc408f7', DATEADD(day, -1, now()), 200,
	0, 'WORK_GROUP', @my_sub_domain_id, @workgroup_20_id),
	(16, '4c9c59e6-163a-48ab-919a-ceba6dc408f8', DATEADD(day, -1, now()), -300,
	1, 'WORK_GROUP', @my_sub_domain_id, @workgroup_20_id),
	(17, '4c9c59e6-163a-48ab-919a-ceba6dc408f9', DATEADD(day, -1, now()), 400,
	0, 'WORK_GROUP', @my_sub_domain_id, @workgroup_20_id),
	(18, '4c9c59e6-163a-48ab-919a-ceba6dc409f0', '2042-09-13', 400,
	0, 'WORK_GROUP', @my_sub_domain_id, @workgroup_20_id),
	(19, '4c9c59e6-163a-48ab-919a-ceba6dc409f3', '2042-10-16', 200,
	0, 'WORK_GROUP', @my_sub_domain_id, @workgroup_20_id),
--  Operations WORKGROUP (21)
	(20, '4c9c59e6-163a-48ab-919a-ceba6dc409f1', DATEADD(day, -1, now()), 200,
	0, 'WORK_GROUP', @guest_domain_id, @workgroup_21_id),
	(21, '4c9c59e6-163a-48ab-919a-ceba6dc409f2', DATEADD(day, -1, now()), 200,
	0, 'WORK_GROUP', @guest_domain_id, @workgroup_21_id);