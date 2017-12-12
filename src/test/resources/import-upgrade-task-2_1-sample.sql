  -- TASK: UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR
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
  (13,
  'UNDEFINED',
  'UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR',
  'UPGRADE_2_1',
  null,
  null,
  13,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

    -- TASK: UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS
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
  (14,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS',
  'UPGRADE_2_1',
  null,
  null,
  14,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

    -- TASK: UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS
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
  (16,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS',
  'UPGRADE_2_1',
  null,
  null,
  16,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);