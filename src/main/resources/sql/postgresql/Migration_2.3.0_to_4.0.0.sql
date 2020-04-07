-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'4.0.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	-- TODO: CHANGE THE VERSIONS
	DECLARE version_to VARCHAR := '4.0.0';
	DECLARE version_from VARCHAR := '2.3.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE nb_upgrade_tasks INT := (SELECT count(*)::int  FROM upgrade_task WHERE status != 'SUCCESS' AND status != 'SKIPPED');
	DECLARE connection_id INT := pg_backend_pid();
	DECLARE row record;
	BEGIN
		RAISE NOTICE '%', start;
		RAISE NOTICE 'Your actual version is: %', version_history_from;
		RAISE NOTICE 'Your database history is :';
		FOR row IN (SELECT * FROM version ORDER BY id DESC) LOOP
			RAISE INFO '%', row.version;
		END LOOP;
		RAISE NOTICE 'Your database system information is : %', database_info;
		IF (version_from <> version_history_from) THEN
			RAISE WARNING 'You must be in version : % to run this script. You are actually in version: %', version_from, version_history_from;
			IF EXISTS (SELECT * from version where version = version_to) THEN
				RAISE WARNING '%', error;
			END IF;
			RAISE WARNING 'We are about to abort the migration script, all the following instructions will be aborted and transaction will rollback.';
			RAISE INFO 'You should expect the following error : "query has no destination for result data".';
	--		DIRTY: did it to stop the process cause there is no clean way to do it.
	--		Expected error: query has no destination for result data.
			select error;
		END IF;
		IF (nb_upgrade_tasks > 0) THEN
			RAISE WARNING 'Can not upgrade LinShare if all upgrade tasks are not completed with success !!!!';
			RAISE WARNING 'We are about to abort the migration script, all the following instructions will be aborted and transaction will rollback.';
			RAISE INFO 'You should expect the following error : "query has no destination for result data".';
	--		DIRTY: did it to stop the process cause there is no clean way to do it.
	--		Expected error: query has no destination for result data.
			select error;
		END IF;
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_check_user_connected() RETURNS void AS $$
BEGIN
	DECLARE database VARCHAR := (SELECT current_database());
	DECLARE user_connected VARCHAR := (SELECT current_user);
	DECLARE error VARCHAR := ('You are actually connected with the user "postgres", you should be connected with your LinShare database user, we are about to stop the migration script.');
	BEGIN
		RAISE INFO 'Connected to "%" with user "%"', database, user_connected;
		IF (user_connected = 'postgres') THEN
			RAISE WARNING '%', error;
		--	DIRTY: did it to stop the process cause there is no clean way to do it.
		--	Expected error: query has no destination for result data.
			SELECT '';
		END IF;
	END;
END
$$ LANGUAGE plpgsql;


SELECT ls_check_user_connected();
SELECT ls_prechecks();

SET client_min_messages = warning;

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;
-- Here your request

ALTER TABLE mail_attachment ALTER COLUMN language DROP NOT NULL;

--Cancel NOT NULL constraint for table statistic
ALTER TABLE statistic ALTER COLUMN domain_parent_id DROP NOT NULL;

--Enable WORKGROUP__FILE_EDITION functionality
UPDATE policy SET status = TRUE, default_status = TRUE WHERE id=303;
UPDATE policy SET policy = 1 WHERE id = 303;
UPDATE policy SET system = FALSE WHERE id = 303;

UPDATE policy SET status = TRUE, default_status = TRUE WHERE id = 304;
UPDATE policy SET policy = 1 WHERE id = 304;
UPDATE policy SET system = FALSE WHERE id = 304;

UPDATE policy SET status = TRUE, default_status = TRUE WHERE id = 305;
UPDATE policy SET policy = 1 WHERE id = 305;
UPDATE policy SET system = FALSE WHERE id = 305;


--Cancel NOT NULL constraint of document_id and ls_type for upload_request_entry
ALTER TABLE upload_request_entry ALTER COLUMN ls_type DROP NOT NULL;
ALTER TABLE upload_request_entry ALTER COLUMN document_id DROP NOT NULL;


-- Group ldap pattern
WITH ldap_pattern_already_exists AS ( UPDATE ldap_pattern SET id=id WHERE id=4 RETURNING *)
INSERT INTO ldap_pattern(
	id,
	uuid,
	pattern_type,
	label,
	system,
	description,
	auth_command,
	search_user_command,
	search_page_size,
	search_size_limit,
	auto_complete_command_on_first_and_last_name,
	auto_complete_command_on_all_attributes, completion_page_size,
	completion_size_limit,
	creation_date,
	modification_date,
	search_all_groups_query,
	search_group_query,
	group_prefix)
	SELECT 4,
	'dfaa3523-51b0-423f-bb6d-95d6ecbfcd4c',
	'GROUP_LDAP_PATTERN',
	'Ldap groups',
	true,
	'default-group-pattern',
	NULL,
	NULL,
	100,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NOW(),
	NOW(),
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-*))");',
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-" + pattern + "))");',
	'workgroup-' WHERE NOT EXISTS ( SELECT * FROM ldap_pattern_already_exists);

-- ldap attributes
WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=13 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 13, 'mail', 'member_mail', false, true, true, false, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=14 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 14, 'givenName', 'member_firstname', false, true, true, false, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=15 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 15, 'cn', 'group_name_attr', false, true, true, true, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=16 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 16, 'member', 'extended_group_member_attr', false, true, true, true, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=17 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 17, 'sn', 'member_lastname', false, true, true, false, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

-- Demo ldap pattern.
WITH ldap_pattern_already_exists AS ( UPDATE ldap_pattern SET id=id WHERE id=5 RETURNING *)
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
	SELECT 5,
    'a4620dfc-dc46-11e8-a098-2355f9d6585a',
    'USER_LDAP_PATTERN',
    'default-pattern-demo',
    'This is pattern the default pattern for the OpenLdap demo structure.',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now() WHERE NOT EXISTS ( SELECT * FROM ldap_pattern_already_exists);

-- ldap_attribute
WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=18 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 18, 'user_mail', 'mail', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=19 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 19, 'user_firstname', 'givenName', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (SELECT * FROM ldap_attribute WHERE id=20)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 20, 'user_lastname', 'sn', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=21 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 21, 'user_uid', 'uid', false, true, true, 5, false WHERE NOT EXISTS (SELECT * FROM already_exists);

--Update purgeStep for deleted workGroups
UPDATE account SET purge_step = 'PURGED' where ((account_type = 5) AND (purge_step = 'IN_USE') AND (destroyed > 0));

-- Upgrade Task
-- TASK: UPGRADE_4_0_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT
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
 (31,
 'UNDEFINED',
 'UPGRADE_4_0_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT',
 'UPGRADE_4_0',
  null,
  null,
  31,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_4_0_ADD_ALL_NEW_MIME_TYPE
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
 (32,
 'UNDEFINED',
 'UPGRADE_4_0_ADD_ALL_NEW_MIME_TYPE',
 'UPGRADE_4_0',
  null,
  null,
  26,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);
  -- End Upgrade Task

-- Functionality DRIVE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (317, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (318, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param, creation_date, modification_date)
	VALUES (67, false, 'DRIVE', 317, 318, 1, false, now(), now());

-- Functionality : DRIVE__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (295, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (296, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (62, false, 'DRIVE__CREATION_RIGHT', 295, 296, 1, 'DRIVE', true, now(), now());


-- Update the mail_content
INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,34,34,'','','',NOW(),true,'','16a7001a-ee6d-11e8-bb18-ef4f3a73c249',true);

INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,35,35,'','','',NOW(),true,'','01acd058-fc92-11e8-b2b3-d7189fc47d83',true);

INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,36,36,'','','',NOW(),true,'','a9983e78-ffa9-11e8-b920-7b238822b4bb',true);

-- Update the mail_lang
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (34,0,1,34,34,true,'16a78382-ee6d-11e8-b388-13bb3e6feb85');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (35,0,1,35,35,true,'01ad9c5e-fc92-11e8-9736-ef560a979e00');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (36,0,1,36,36,true,'a9992e32-ffa9-11e8-bbfe-b32f26c4955b');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (134,1,1,34,34,true,'16a7f1aa-ee6d-11e8-9dab-3b0fd56ae1eb');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (135,1,1,35,35,true,'01ae8e66-fc92-11e8-9e2e-2b5cc9cf184f');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (136,1,1,36,36,true,'a99a4650-ffa9-11e8-b09e-83360a30f184');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (234,2,1,34,34,true,'250c2fe2-5f7c-11e9-8a15-bfaa0debac8a');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (235,2,1,35,35,true,'dbaef9ba-5f7b-11e9-909b-b73741598b74');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (236,2,1,36,36,true,'e6d0bb08-5f7b-11e9-a49e-bffcfe6b06bf');


--Set English text into message_russian (To be deleted once the Russian translation is done)
UPDATE mail_content SET messages_russian = messages_english WHERE id=34;
UPDATE mail_content SET messages_russian = messages_english WHERE id=35;
UPDATE mail_content SET messages_russian = messages_english WHERE id=36;


-- New Mails
-- Drive notification :
-- DRIVE_WARN_NEW_MEMBER
UPDATE mail_content SET subject='[( #{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
            <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
            <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMainMsg}"></span>
            <span>
              <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
               link
             </a>
            </span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, ${threadMember.role.name})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${threadMember.creationDate})"/>
    <div th:if="${!childMembers.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="member : ${childMembers}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="#{displayDriveAndRole(${member.node.name},${member.role.name})}"/>
          </li>
      </ul>  
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au drive <br>
simpleMainMsg = Vous avez été ajouté au drive
subject = Vous avez été ajouté au drive {0}
workGroupRight = Droit par défaut 
workGroupNameTitle = Nom du drive
nestedWorkGroupsList=Vous avez automatiquement été ajouté aux groupes de travail suivants :
displayDriveAndRole ={0} avec un rôle <span style="text-transform:uppercase">{1}</span>',messages_english='workGroupCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the drive <br>
simpleMainMsg = You have been added to the drive
subject = You have been added to the drive {0}
workGroupRight = Default right
workGroupNameTitle = Drive Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayDriveAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role',messages_russian='workGroupCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the drive <br>
simpleMainMsg = You have been added to the drive
subject = You have been added to the drive {0}
workGroupRight = Default right
workGroupNameTitle = Drive Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayDriveAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role' WHERE id=34;


--DRIVE warn updated member
UPDATE mail_content SET subject='[(#{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg}"></span>
          <span>
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
                link </a>
          </span>
          <span data-th-utext="#{mainMsgNext}"></span>
          <span th:if="${owner.firstName} != null AND ${owner.firstName} != null" data-th-utext="#{mainMsgNextBy(${owner.firstName},${owner.lastName})}"></span>

             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block th:switch="${threadMember.role.name}">
      <p th:case="''DRIVE_ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{workGroupRightAdminTitle})"/></p>  
      <p th:case="''DRIVE_WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{workGroupRightWirteTitle})"/></p>  
      <p th:case="''DRIVE_READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{workGroupRightReadTitle})"/></p>  
    </th:block>
    <th:block th:switch="${threadMember.nestedRole.name}">
      <p th:case="''ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/></p>  
      <p th:case="''CONTRIBUTOR''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>  
      <p th:case="''WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/></p>  
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupUpdatedDateTitle},${threadMember.creationDate})"/>
    <div th:if="${nbrWorkgroupsUpdated != 0}">
    <th:block data-th-replace="layout :: infoStandardArea(#{nbrWorkgoups},${nbrWorkgroupsUpdated})"/>
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul>
        <li  th:each="member : ${nestedMembers}">
              <th:block data-th-utext="${member.node.name}"/>
        </li>
        <span th:if="${nbrWorkgroupsUpdated > 3}">
             <li>...</li>
        </span>
      </ul>  
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le DRIVE
mainMsgNext = et dans ses WorkGroups contenus ont été mis à jour
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Vos droits sur le DRIVE {0} ont été mis à jour
driveRight = Droit sur le DRIVE
workGroupRight =  Droit sur le groupe de travail
workGroupNameTitle = Nom du DRIVE
nestedWorkGroupsList = Liste des workgoups
nbrWorkgoups = Nombre de groupe de travail mis à jours',messages_english='workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the DRIVE 
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the DRIVE {0} was updated.
driveRight = Drive right
workGroupRight = Workgroup right
workGroupNameTitle = Drive Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups',messages_russian='workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the DRIVE 
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the DRIVE {0} was updated.
driveRight = Drive right
workGroupRight = Workgroup right
workGroupNameTitle = Drive Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups' WHERE id=35;


--DRIVE warn deleted member
UPDATE mail_content SET subject='[( #{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${workGroupName})}"></span>
          <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMsg(${workGroupName})}"></span>
            
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='subject = Les accès au drive {0} et à ses workgroups vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du drive <b>{2}</b>
simpleMsg = Les accès au drive <b>{0}</b> vous ont été retirés.
workGroupNameTitle = Nom du Drive',messages_english='subject = Your access to the drive {0}  and its workgroups was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the drive  <b>{2}</b>
simpleMsg =  Your access to the drive <b>{0}</b> was withdrawn.     
workGroupNameTitle = Drive Name',messages_russian='subject = Your access to the drive {0}  and its workgroups was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the drive  <b>{2}</b>
simpleMsg =  Your access to the drive <b>{0}</b> was withdrawn.
workGroupNameTitle = Drive Name' WHERE id=36;

-- Update the body of mail content of warn updated member of a workGroup

UPDATE mail_content SET body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg}"></span>
          <span>
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
                link </a>
          </span>
          <span data-th-utext="#{mainMsgNext}"></span>
          <span th:if="${owner.firstName} != null AND ${owner.lastName} != null" data-th-utext="#{mainMsgNextBy(${owner.firstName},${owner.lastName})}"></span>

             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block th:switch="(${threadMember.role.name})">
       <p th:case="ADMIN">
          <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/>
       </p>
       <p th:case="WRITER">
          <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/>
       </p>
       <p th:case="CONTRIBUTOR">
          <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightContributeTitle})"/>
       </p>
       <p th:case="READER">
         <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/>
       </p>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupUpdatedDateTitle},${threadMember.modificationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>' WHERE id = 29;

-- Update the mail content by adding the mainMsgNextBy for English and French which was used just by Russian

UPDATE mail_content
SET messages_english='
workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the workgroup
mainMsgNext= have been updated 
mainMsgNextBy= by  <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the workgroup {0} was updated.
workGroupRight = Current right
workGroupNameTitle = Workgroup Name'
WHERE id = 29;

UPDATE mail_content
SET messages_french='
workGroupUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le groupe de travail
mainMsgNext = ont été mis à jour 
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span> </b>.
subject =  Vos droits sur le groupe de travail {0} ont été mis à jour
workGroupRight =  Nouveau droit
workGroupNameTitle = Nom du groupe de travail'
WHERE id = 29;

-- MailActivation : DRIVE_WARN_NEW_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (308, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (309, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (310, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
	VALUES(35, false, 'DRIVE_WARN_NEW_MEMBER', 308, 309, 310, 1, true);

	-- MailActivation : DRIVE_WARN_UPDATED_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (311, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (312, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (313, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
	VALUES(36, false, 'DRIVE_WARN_UPDATED_MEMBER', 311, 312, 313, 1, true);

	-- MailActivation : DRIVE_WARN_DELETED_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (314, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (315, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (316, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
	VALUES(37, false, 'DRIVE_WARN_DELETED_MEMBER', 314, 315, 316, 1, true);

-- End of your requests

-- LinShare version
SELECT ls_version();

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed != 0;
COMMIT;
