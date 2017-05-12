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
  '5303f31d-1c55-4395-8873-0b6c06c16ec3',
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
  '838f6b35-df62-4a5d-aafa-749581a2ee33',
  'UPGRADE_2_0_DOMAIN_POLICIES_UUID',
  'UPGRADE_2_0',
  null,
  null,
  2,
  'NEW',
  'MANDATORY',
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
  (3,
  'c4e67db6-242a-4b8a-bc4a-245c23134909',
  'UPGRADE_2_0_SHA256SUM',
  'UPGRADE_2_0',
  null,
  null,
  3,
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
  '257e56f7-810a-407a-ba71-2f10fbd3d9a0',
  'UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS',
  'UPGRADE_2_0',
  'c4e67db6-242a-4b8a-bc4a-245c23134909',
  'UPGRADE_2_0_SHA256SUM',
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
  '11df000a-dde6-4f86-9582-e46498515251',
  'UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS',
  'UPGRADE_2_0',
  '257e56f7-810a-407a-ba71-2f10fbd3d9a0',
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
  '8705ccae-84ea-493b-8c1d-ee45b49a3eca',
  'UPGRADE_2_0_ACCOUNT_QUOTA',
  'UPGRADE_2_0',
  '11df000a-dde6-4f86-9582-e46498515251',
  'UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS',
  6,
  'NEW',
  'MANDATORY',
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
  (7,
   '36b592ca-3476-44c7-a546-ce62d2c84b9b',
  'UPGRADE_2_0_THREAD_TO_WORKGROUP',
  'UPGRADE_2_0',
  '8705ccae-84ea-493b-8c1d-ee45b49a3eca',
  'UPGRADE_2_0_ACCOUNT_QUOTA',
  7,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);
