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
 (27,
 'UNDEFINED',
 'UPGRADE_2_3_MIGRATE_PERMANENT_TOKEN_ENTITY_TO_NEW_STRUCTURE',
 'UPGRADE_2_3',
  null,
  null,
  27,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_3_UPGRADE_DOCUMENT_STRUCTURE_FOR_VERSIONING
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
 (28,
 'UNDEFINED',
 'UPGRADE_2_3_UPDATE_DOCUMENT_STRUCTURE_FOR_VERSIONING',
 'UPGRADE_2_3',
  null,
  null,
  28,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_3_UPDATE_SHARED_SPACE_NODE_STRUCTURE_FOR_VERSIONING
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
 (29,
 'UNDEFINED',
 'UPGRADE_2_3_UPDATE_SHARED_SPACE_NODE_STRUCTURE_FOR_VERSIONING',
 'UPGRADE_2_3',
  null,
  null,
  29,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);
 
    -- TASK: UPGRADE_2_3_ADD_QUOTA_UUID_TO_ALL_SHARED_SPACES
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
 (30,
 'UNDEFINED',
 'UPGRADE_2_3_ADD_QUOTA_UUID_TO_ALL_SHARED_SPACES',
 'UPGRADE_2_3',
  null,
  null,
  30,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);
