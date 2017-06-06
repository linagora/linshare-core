UPDATE async_task SET upgrade_task_id = NULL  WHERE upgrade_task_id IS NOT NULL;
DELETE from upgrade_task;
-- TASK: UPGRADE_2_0_DOMAIN_UUID
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (1,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_UUID',
  'UPGRADE_2_0',
  null,
  null,
  1,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_DOMAIN_POLICIES_UUID
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (2,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_POLICIES_UUID',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_UUID',
  2,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (4,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_POLICIES_UUID',
  4,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (5,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS',
  5,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_ACCOUNT_QUOTA
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (6,
  'UNDEFINED',
  'UPGRADE_2_0_ACCOUNT_QUOTA',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS',
  6,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_CLEANUP_EXPIRED_GUEST
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (7,
  'UNDEFINED',
  'UPGRADE_2_0_CLEANUP_EXPIRED_GUEST',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_ACCOUNT_QUOTA',
  7,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_CLEANUP_EXPIRED_ACCOUNT
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (8,
  'UNDEFINED',
  'UPGRADE_2_0_CLEANUP_EXPIRED_ACCOUNT',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_CLEANUP_EXPIRED_GUEST',
  8,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_PURGE_ACCOUNT
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (9,
  'UNDEFINED',
  'UPGRADE_2_0_PURGE_ACCOUNT',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_CLEANUP_EXPIRED_ACCOUNT',
  9,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

  -- TASK: UPGRADE_2_0_SHA256SUM
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (10,
  'UNDEFINED',
  'UPGRADE_2_0_SHA256SUM',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_PURGE_ACCOUNT',
  10,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_UPGRADE_STORAGE
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (11,
   'UNDEFINED',
  'UPGRADE_2_0_UPGRADE_STORAGE',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_SHA256SUM',
  11,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_THREAD_TO_WORKGROUP
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (12,
   'UNDEFINED',
  'UPGRADE_2_0_THREAD_TO_WORKGROUP',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_UPGRADE_STORAGE',
  12,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);
