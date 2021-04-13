-- TASK: UPGRADE_4_2_DELETE_ENTRIES_OF_ARCHIVED_DELETED_PURGED_UPLOAD_REQUESTS
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
 (36,
 'UNDEFINED',
 'UPGRADE_4_2_DELETE_ENTRIES_OF_ARCHIVED_DELETED_PURGED_UPLOAD_REQUESTS',
 'UPGRADE_4_2',
  null,
  null,
  36,
 'NEW',
 'REQUIRED',
  now(),
  now(),
  null);