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