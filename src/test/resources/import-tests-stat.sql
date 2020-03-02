-- statistics
---- USERS
------ DAILY STAT FOR USER Jane [ 7 insertions]
INSERT INTO statistic (
	id, creation_date, statistic_date, operation_count, 
	delete_operation_count, create_operation_count, create_operation_sum, delete_operation_sum, 
	diff_operation_sum, actual_operation_sum, domain_id, account_id, 
	domain_parent_id, statistic_type)
VALUES 
	(4,'2042-11-18', '2042-11-17', 5,
	2, 3, 100,-50, 
	50, 50, 1, @jane_simth_id, 
	null, 'USER_DAILY_STAT'),
	
	(5,'2042-11-17', '2042-11-16', 4, 
	1, 3, 200, -50,
	150, 150, 1, @jane_simth_id, 
	null, 'USER_DAILY_STAT'),
	
	(6,'2042-10-16', '2042-10-14', 5,
	2, 3, 100, -50, 
	50, 50, 2, @jane_simth_id, 
	1, 'USER_DAILY_STAT'),
	
	(7,'2042-10-15', DATEADD(DAY, -2, now()),5,
	2, 3, 100, -50,
	50, 50, 2, @jane_simth_id,
	1, 'USER_DAILY_STAT'),
	
	(8,'2042-09-14', DATEADD(DAY, -2, now()), 10,
	4, 6, 1700,-1000, 
	700, 700, 2, @jane_simth_id, 
	1, 'USER_DAILY_STAT'),
	
	(9,'2042-09-13', '2042-09-12', 12,
	7, 5, 1500, -1000, 
	500, 500, 1, @jane_simth_id, 
	null, 'USER_DAILY_STAT'),
	
	(10,'2042-11-12', '2042-11-11', 12,
	7, 5, 1500, -1000,
	500, 500, 1, @jane_simth_id, 
	null, 'USER_DAILY_STAT');
	
------ WEEKLY STAT FOR USER Jane 
INSERT INTO statistic (
	id, creation_date, statistic_date, operation_count, 
	delete_operation_count, create_operation_count, create_operation_sum, delete_operation_sum,
	diff_operation_sum, actual_operation_sum, domain_id, account_id, 
	domain_parent_id, statistic_type)
VALUES 
	(1,'2042-11-25', '2042-11-24', 9,
	4, 5, 1500,-1000, 
	500, 500, 1, @jane_simth_id, 
	null, 'USER_WEEKLY_STAT'),
	 
	(2,'2042-10-15', '2042-09-14', 22,
	10, 12, 2700, -2000,
	700, 700, 2, @jane_simth_id, 
	1, 'USER_WEEKLY_STAT'),
	
	(3,'2042-09-18', '2042-09-17', 9, 
	4, 5, 1500, -1000,
	500, 500, 1, @jane_simth_id, 
	null, 'USER_WEEKLY_STAT');

	
---- WORKGROUPS	
INSERT INTO statistic (
	id, creation_date, statistic_date, operation_count, 
	delete_operation_count, create_operation_count, create_operation_sum, delete_operation_sum, 
	diff_operation_sum, actual_operation_sum, domain_id, account_id, 
	domain_parent_id, statistic_type)
	
------- WEEKLY STAT FOR WORKGROUP (22 & 23) [ 4 insertions]
VALUES 
	(15,'2042-11-29', '2042-11-28', 5,
	2, 3, 100, -50, 
	50, 50, @my_domain_id, @workgroup_23_id, 
	1, 'WORK_GROUP_WEEKLY_STAT'),
	
	(16,'2042-11-22','2042-11-21', 4,
	1, 3, 200,-50, 
	150, 150, 4, @workgroup_22_id, 
	3, 'WORK_GROUP_WEEKLY_STAT'),
	
	(17,'2042-11-15', '2042-11-14', 5,
	2, 3, 100, -50, 
	50, 50, 4, @workgroup_23_id, 
	3, 'WORK_GROUP_WEEKLY_STAT'),
------- DAILY STAT FOR WORKGROUP (22 && 23)	[7 insertions]
	(18,'2042-11-08', '2042-11-07', 5,
	2, 3, 100,-50,
	50, 50, 4, @workgroup_22_id,
	3, 'WORK_GROUP_DAILY_STAT'),
	 
	(19,'2042-11-01', '2042-10-31', 5,
	2, 3, 100, -50,
	50, 50, 3, @workgroup_23_id,
	2, 'WORK_GROUP_DAILY_STAT'),

	(25,'2042-11-06', '2042-11-05', 5,
	2, 3, 100, -50,
	50, 50, @my_domain_id, @workgroup_23_id, 
	1, 'WORK_GROUP_DAILY_STAT'),
	
	(26,'2042-11-05','2042-11-04', 4, 
	1, 3, 200, -50, 
	150, 150, 4, @workgroup_22_id, 
	3, 'WORK_GROUP_DAILY_STAT'),
	
	(27,'2042-11-04', '2042-11-03', 5, 
	2, 3, 100, -50, 
	50, 50, 4, @workgroup_23_id, 
	3, 'WORK_GROUP_DAILY_STAT'),
	
	(28,'2042-11-03', '2042-11-02',5, 
	2, 3, 100, -50, 
	50, 50, 4, @workgroup_22_id, 
	3, 'WORK_GROUP_DAILY_STAT'),
	
	(29,'2042-11-01', '2042-10-31',5, 
	2, 3, 100, -50, 
	50, 50, 3, @workgroup_23_id, 
	2, 'WORK_GROUP_DAILY_STAT');
	
-- DOMAINS	
------ DAILY STAT FOR DOMAIN  [ 4 insertions]
INSERT INTO statistic ( 
	id, creation_date, statistic_date, operation_count,
	delete_operation_count, create_operation_count, create_operation_sum, delete_operation_sum,
	diff_operation_sum, actual_operation_sum, domain_id, account_id, 
	domain_parent_id, statistic_type)
VALUES 
	(11,'2042-11-15', '2042-11-14', 5,
	2, 3, 100, -50,
	50, 50, @my_domain_id, null,
	1,'DOMAIN_DAILY_STAT'),
	
	(12,'2042-11-14', '2042-11-13',4,
	1, 3, 200, -50, 
	150, 150, @my_domain_id, null,
	1, 'DOMAIN_DAILY_STAT'),
	(13,'2042-10-13','2042-10-12',5,
	2, 3, 100, -50,
	50, 50, @my_domain_id, null,
	1, 'DOMAIN_DAILY_STAT'),
	
	(14,'2042-09-12','2042-09-11',5, 
	2, 3, 100, -50,
	50, 50, @my_domain_id, null,
	1, 'DOMAIN_DAILY_STAT'),
	
---------WEEKLY STAT FOR DOMAIN	[ 5 insertions]
	(20,'2042-11-15', '2042-11-14', 5, 
	2, 3, 100, -50, 
	50, 50, @my_domain_id, null, 
	1, 'DOMAIN_WEEKLY_STAT'),
	
	(21,'2042-10-15', now(), 4, 
	1, 3, 200, -50, 
	150, 150, @my_domain_id, null, 
	1, 'DOMAIN_WEEKLY_STAT'),
	
	(22,'2042-09-15', now(), 5, 
	2, 3, 100,-50, 
	50, 50, @my_domain_id, null, 
	1, 'DOMAIN_WEEKLY_STAT'),
	
	(23,'2042-09-08', '2042-09-07', 5, 
	2, 3, 100,-50, 
	50, 50, @my_domain_id, null, 
	1, 'DOMAIN_WEEKLY_STAT'),
	
	(24,'2042-12-22', '2042-12-21', 5, 
	2, 3, 100,-50,
	50, 50, @my_domain_id, null,
	1, 'DOMAIN_WEEKLY_STAT');
	