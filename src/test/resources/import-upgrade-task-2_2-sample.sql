  -- TASK: UPGRADE_2_2_MIGRATE_HISTORY_TO_MONGO_AUDIT;
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
  (19,
  'UNDEFINED',
  'UPGRADE_2_2_MIGRATE_UPLOAD_REQUEST_HISTORY_TO_MONGO_AUDIT',
  'UPGRADE_2_2',
  null,
  null,
  19,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

  -- TASK: UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_FILTER_TO_MONGO_DATABASE
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
  (20,
  'UNDEFINED',
  'UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_FILTER_TO_MONGO_DATABASE',
  'UPGRADE_2_2',
  null,
  null,
  20,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_TO_MONGO_DATABASE
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
  (21,
  'UNDEFINED',
  'UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_TO_MONGO_DATABASE',
  'UPGRADE_2_2',
  null,
  null,
  21,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);
