  -- TASK: UPGRADE_2_3_ADD_ALL_NEW_MIME_TYPE
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
 (26,
 'UNDEFINED',
 'UPGRADE_2_3_ADD_ALL_NEW_MIME_TYPE',
 'UPGRADE_2_3',
  null,
  null,
  26,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);