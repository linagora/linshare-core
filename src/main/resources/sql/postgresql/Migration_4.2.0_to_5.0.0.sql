-- Migration script to upgrade from LinShare 4.2 to LinShare 4.3. 

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions 

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'5.0.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	-- TODO: CHANGE THE VERSIONS
	DECLARE version_to VARCHAR := '5.0.0';
	DECLARE version_from VARCHAR := '4.2.0';
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

SELECT ls_prechecks();
SELECT ls_check_user_connected();

SET client_min_messages = warning;


---- Here your queries
ALTER TABLE user_provider ADD COLUMN domain_discriminator VARCHAR(255);
ALTER TABLE user_provider ADD COLUMN check_external_user_id  bool;
ALTER TABLE user_provider ADD COLUMN use_email_locale_claim  bool;
ALTER TABLE user_provider ADD COLUMN use_role_claim  bool;
ALTER TABLE user_provider ADD COLUMN move_between_domain_claim bool;
ALTER TABLE user_provider ADD COLUMN use_access_claim bool;

ALTER TABLE user_provider ALTER COLUMN ldap_connection_id DROP NOT NULL;
ALTER TABLE user_provider ALTER COLUMN ldap_pattern_id DROP NOT NULL;

CREATE INDEX account_mail
  ON account (mail);
CREATE INDEX account_first_name
  ON account (first_name);
CREATE INDEX account_last_name
  ON account (last_name);

-- Ldap Drive filter
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    system,
    description,
    search_page_size,
    creation_date,
    modification_date,
    search_all_groups_query,
    search_group_query,
    group_prefix)
    SELECT 6,
    'c59078f1-2366-4360-baa0-6c089202e9a6',
    'WORK_SPACE_LDAP_PATTERN',
    'Default Ldap workSpace filter',
    true,
    'Description of default LDAP workSpace filter',
    100,
    NOW(),
    NOW(),
    'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workspace-*))");',
    'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workspace-" + pattern + "))");',
    'workspace-' WHERE NOT EXISTS (SELECT id FROM ldap_pattern WHERE id = 6);

-- Update ldap drive filter
UPDATE ldap_pattern SET pattern_type = 'WORK_SPACE_LDAP_PATTERN', description = 'Description of default LDAP workSpace filter', label = 'Default Ldap workSpace filter' WHERE uuid='c59078f1-2366-4360-baa0-6c089202e9a6';

UPDATE ldap_pattern SET pattern_type = 'WORK_SPACE_LDAP_PATTERN' WHERE pattern_type= 'DRIVE_LDAP_PATTERN';

UPDATE ldap_pattern SET group_prefix = 'workspace-', search_all_groups_query = 'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workspace-*))");', search_group_query = 'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workspace-" + pattern + "))");' WHERE pattern_type= 'WORK_SPACE_LDAP_PATTERN';

-- ldap attributes
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 22, 'mail', 'member_mail', false, true, true, false, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 22);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 23, 'givenName', 'member_firstname', false, true, true, false, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 23);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 24, 'cn', 'group_name_attr', false, true, true, true, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 24);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 25, 'member', 'extended_group_member_attr', false, true, true, true, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 25);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 26, 'sn', 'member_lastname', false, true, true, false, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 26);


-- UPGRADE_5_0_ADD_DOMAIN_TO_WORK_GROUP
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (45,
  'UNDEFINED',
  'UPGRADE_5_0_ADD_DOMAIN_TO_WORK_GROUP',
  'UPGRADE_5_0',
  45,
  'NEW',
  'REQUIRED',
  now(),
  now());
 
  -- UPGRADE_5_0_ADD_DOMAIN_TO_DRIVE
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (46,
  'UNDEFINED',
  'UPGRADE_5_0_ADD_DOMAIN_TO_DRIVE',
  'UPGRADE_5_0',
  46,
  'NEW',
  'REQUIRED',
  now(),
  now());

-- Remote server (previously ldap_connection)
ALTER TABLE ldap_connection ADD COLUMN server_type VARCHAR(255) DEFAULT 'LDAP' NOT NULL;
ALTER TABLE ldap_connection ALTER COLUMN server_type DROP DEFAULT;

ALTER TABLE ldap_connection RENAME TO remote_server;

ALTER TABLE remote_server ADD COLUMN client_id VARCHAR(255);
ALTER TABLE remote_server ADD COLUMN client_secret VARCHAR(255);

ALTER TABLE functionality_integer ADD COLUMN unlimited_value bool DEFAULT 'false' NOT NULL;
ALTER TABLE functionality_integer ADD COLUMN unlimited_value_used bool DEFAULT 'false' NOT NULL;
ALTER TABLE functionality_unit ADD COLUMN unlimited_value bool DEFAULT 'false' NOT NULL;
ALTER TABLE functionality_unit ADD COLUMN unlimited_value_used bool DEFAULT 'false' NOT NULL;

UPDATE functionality_integer SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_FILE_COUNT');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'SHARE_EXPIRATION');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION');


UPDATE functionality_unit SET integer_max_value = 900
	WHERE
		functionality_id IN (SELECT id FROM functionality WHERE identifier = 'WORK_GROUP__DOWNLOAD_ARCHIVE')
	AND
		integer_max_value = -1;
UPDATE functionality_unit SET unlimited_value = TRUE, integer_max_value = 0 WHERE integer_max_value = -1;

-- Introduce Twake user provider
ALTER TABLE user_provider ADD COLUMN twake_connection_id int8;
ALTER TABLE user_provider ADD COLUMN twake_company_id VARCHAR(255);
ALTER TABLE user_provider ADD CONSTRAINT FKuser_provi1641 FOREIGN KEY (twake_connection_id) REFERENCES remote_server (id);


-- UPGRADE_5_0_DELETE_EVENT_NOTIFICATION_COLLECTION
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (47,
  'UNDEFINED',
  'UPGRADE_5_0_DELETE_EVENT_NOTIFICATION_COLLECTION',
  'UPGRADE_5_0',
  47,
  'NEW',
  'REQUIRED',
  now(),
  now());


-- Update INTERNAL_CAN_UPLOAD functionality's identifier to INTERNAL_CAN_UPLOAD
UPDATE functionality SET identifier = 'INTERNAL_ENABLE_PERSONAL_SPACE', system = false WHERE id in (SELECT id FROM functionality WHERE identifier = 'INTERNAL_CAN_UPLOAD');
-- Update activation policy to mandatory and system
UPDATE policy SET system = true, policy = 0 WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'INTERNAL_ENABLE_PERSONAL_SPACE');
-- Update configuration policy to allowed
UPDATE policy SET policy = 1, system = false, status = true, default_status = true WHERE id IN (SELECT policy_configuration_id FROM functionality WHERE identifier = 'INTERNAL_ENABLE_PERSONAL_SPACE');

ALTER TABLE recipient_favourite ADD COLUMN expiration_date       timestamp(6);

-- Functionality : COLLECTED_EMAILS_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (336, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (337, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, param, creation_date, modification_date)
	VALUES (70, false, 'COLLECTED_EMAILS_EXPIRATION', 336, 337, NULL, 1, false, now(), now());
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (22, 0, 0), (23, 0, 0);
INSERT INTO functionality_unit(functionality_id, integer_max_value, unit_id, max_unit_id, integer_default_value, default_value_used, max_value_used)
	VALUES (70, 0, 22, 23, 8, true, false);

UPDATE functionality SET identifier='SHARED_SPACE' WHERE identifier = 'DRIVE';
UPDATE policy SET system=true, status=true, default_status=false, policy=0 WHERE id IN (SELECT policy_configuration_id FROM functionality WHERE identifier='SHARED_SPACE');
UPDATE functionality SET parent_identifier='SHARED_SPACE' WHERE parent_identifier = 'WORK_GROUP';
UPDATE functionality SET parent_identifier='SHARED_SPACE' WHERE parent_identifier = 'DRIVE';
ALTER TABLE policy ADD COLUMN delete_it BOOL DEFAULT 'FALSE';
UPDATE policy SET delete_it = true WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'WORK_GROUP');
UPDATE policy SET delete_it = true WHERE id IN (SELECT policy_configuration_id FROM functionality WHERE identifier = 'WORK_GROUP');
DELETE FROM functionality WHERE identifier = 'WORK_GROUP';
DELETE FROM policy WHERE delete_it = true;
ALTER TABLE policy DROP COLUMN delete_it;

-- functionality : remove JWT_PERMANENT_TOKEN__USER_MANAGEMENT
CREATE OR REPLACE FUNCTION ls_insert_jwt_functionality_boolean() RETURNS void AS $$
BEGIN
	DECLARE r record;
    BEGIN
        FOR r IN (SELECT id FROM functionality WHERE identifier = 'JWT_PERMANENT_TOKEN') LOOP
            INSERT INTO functionality_boolean(functionality_id, boolean_value)
                VALUES (r.id, true);
        END LOOP;
    END;
END
$$ LANGUAGE plpgsql;

ALTER TABLE policy ADD COLUMN delete_it BOOL DEFAULT 'FALSE';
UPDATE policy SET delete_it = true WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'JWT_PERMANENT_TOKEN__USER_MANAGEMENT');
UPDATE policy SET delete_it = true WHERE id IN (SELECT policy_configuration_id FROM functionality WHERE identifier = 'JWT_PERMANENT_TOKEN__USER_MANAGEMENT');
UPDATE policy SET delete_it = true WHERE id IN (SELECT policy_delegation_id FROM functionality WHERE identifier = 'JWT_PERMANENT_TOKEN__USER_MANAGEMENT');
DELETE FROM functionality WHERE identifier = 'JWT_PERMANENT_TOKEN__USER_MANAGEMENT';
DELETE FROM policy WHERE delete_it = true;
ALTER TABLE policy DROP COLUMN delete_it;

SELECT ls_insert_jwt_functionality_boolean();
UPDATE functionality SET system = false
    WHERE identifier = 'JWT_PERMANENT_TOKEN';


-- Update mail layout in order to avoid null values for UR update settings email
UPDATE mail_layout SET messages_french='common.availableUntil = Expire le
common.byYou= | Par vous
common.download= Télécharger
common.filesInShare=Fichiers joints
common.recipients = Destinataires
common.titleSharedThe= Partagé le
date.format=d MMMM, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administration
workGroupRightWirteTitle = Écriture
workGroupRightContributeTitle = Contribution
workGroupRightReadTitle = Lecture
workGroupRightContributorTitle = Contributeur
workSpaceRoleAdminTitle = WorkSpace: Administrateur
workSpaceRoleWriteTitle = WorkSpace: Auteur
workSpaceRoleReadTitle = WorkSpace: Lecteur
welcomeMessage = Bonjour {0},',messages_english='common.availableUntil = Expiry date
common.byYou= | By you
common.download= Download
common.filesInShare = Attached files
common.recipients = Recipients
common.titleSharedThe= Creation date
date.format= MMMM d, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administrator
workGroupRightWirteTitle = Writer
workGroupRightContributeTitle = Contributor
workGroupRightReadTitle = Reader
workSpaceRoleAdminTitle = WorkSpace: Administrator
workSpaceRoleWriteTitle = WorkSpace: Writer
workSpaceRoleReadTitle = WorkSpace: Reader
welcomeMessage = Hello {0},',messages_russian='common.availableUntil = Срок действия
common.byYou= | Вами
common.download= Загрузить
common.filesInShare = Прикрепленные файлы
common.recipients = Получатели
common.titleSharedThe= Дата создания
date.format= d MMMM, yyyy
productCompagny= Linagora
productName=LinShare
workGroupRightAdminTitle = Администратор
workGroupRightWirteTitle = Автор
workGroupRightContributeTitle = Редактор
workGroupRightReadTitle = Читатель
workSpaceRoleAdminTitle = WorkSpace: Administrator
workSpaceRoleWriteTitle = WorkSpace: Writer
workSpaceRoleReadTitle = WorkSpace: Reader
welcomeMessage = Здравствуйте, {0},',layout='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/* Beginning of common base layout template*/-->
<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">
  <div
    style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:''Open Sans'',arial,Helvetica,sans-serif;">
    <center>
      <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"
        style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px" width="90%">
        <tbody>
          <tr>
            <td align="center" style="border-collapse:collapse" valign="top">
              <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px"
                width="90%">
                <tbody>
                  <tr>
                    <td align="center" style="border-collapse:collapse" valign="top">
                      <table bgcolor="transparent" border="0" cellpadding="0" cellspacing="0"
                        style="background-color:transparent;border-bottom:0;padding:0px">
                        <tbody>
                          <tr>
                            <td align="center" bgcolor="#ffffff"
                              style="border-collapse:collapse;color:#202020;background-color:#ffffff;font-size:34px;font-weight:bold;line-height:100%;padding:0;text-align:center;vertical-align:middle">
                              <div align="center" style="text-align:center">
                                <a target="_blank"
                                  style="border:0;line-height:100%;outline:none;text-decoration:none;width:233px;height:57px;padding:20px 0 20px 0"
                                  data-th-href="@{${linshareURL}}">
                                  <img src="cid:logo.linshare@linshare.org"
                                    style="display:inline-block;margin-bottom:20px;margin-top:20px" width="233"
                                    alt="Logo" height="57" />
                                </a>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td align="center" style="border-collapse:collapse" valign="top">
                      <table border="0" cellpadding="0" cellspacing="0" style="width:95%;max-width:500px" width="95%">
                        <tbody>
                          <tr>
                            <td
                              style="border-collapse:collapse;border-radius:3px;font-weight:300;border:1px solid #e1e1e1;background:white;border-top:none;"
                              valign="top">
                              <table border="0" cellpadding="20" cellspacing="0" width="100%">
                                <tbody>
                                  <tr>
                                    <td style="border-collapse:collapse;padding:0px" valign="top">
                                      <div align="left"
                                        style="color:#505050;font-size:14px;line-height:150%;text-align:left">
                                        <th:block data-th-replace="${upperMainContentArea}" />
                                      </div>
                                      <table border="0" cellspacing="0" cellpadding="0" width="100%"
                                        style="background-color: #f8f8f8;">
                                        <tbody>
                                          <tr>
                                            <td width="15" style="border-top:1px solid #c9cacc;">
                                            </td>
                                            <td width="20"><img src="cid:logo.arrow@linshare.org" width="20" height="9"
                                                border="0" style="display:block;" alt="down arrow" /></td>
                                            <td style="border-top:1px solid #c9cacc;"></td>
                                          </tr>
                                        </tbody>
                                      </table>
                                      <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                        <tbody>
                                          <tr>
                                            <td>
                                              <div align="left"
                                                style="font-size:14px;padding: 0px 17px;background: #f8f8f8;text-align:left;color:#7f7f7f;line-height:20px;">
                                                <div align="left"
                                                  style="font-size:13px;line-height:20px;margin:0;padding: 15px 0 20px;">
                                                  <th:block data-th-replace="${bottomSecondaryContentArea}" />
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                        </tbody>
                                      </table>
                                      <table width="100%"
                                        style="background:#f0f0f0;text-align:left;color:#a9a9a9;line-height:20px;border-top:1px solid #e1e1e1">
                                        <tbody>
                                          <tr data-th-insert="footer :: email_footer">
                                          </tr>
                                        </tbody>
                                      </table>
                                    </td>
                                  </tr>
                                </tbody>
                              </table>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td align="center" style="border-collapse:collapse" valign="top">
                      <table bgcolor="white" border="0" cellpadding="10" cellspacing="0"
                        style="background-color:white;border-top:0" width="400">
                        <tbody>
                          <tr>
                            <td style="border-collapse:collapse" valign="top">
                              <table border="0" cellpadding="10" cellspacing="0" width="100%">
                                <tbody>
                                  <tr>
                                    <td bgcolor="#ffffff" colspan="2"
                                      style="border-collapse:collapse;background-color:#ffffff;border:0;padding: 0 8px;"
                                      valign="middle">
                                      <div align="center"
                                        style="color:#707070;font-size:12px;line-height:125%;text-align:center">
                                        <!--/* Do not remove the copyright  ! */-->
                                        <div data-th-insert="copyright :: copyright">
                                          <p
                                            style="line-height:15px;font-weight:300;margin-bottom:0;color:#b2b2b2;font-size:10px;margin-top:0">
                                            You are using the Open Source and free version of
                                            <a href="http://www.linshare.org/"
                                              style="text-decoration:none;color:#b2b2b2;"><strong>LinShare</strong>™</a>,
                                            powered by <a href="http://www.linshare.org/"
                                              style="text-decoration:none;color:#b2b2b2;"><strong>Linagora</strong></a>
                                            ©&nbsp;2009–2020. Contribute to
                                            Linshare R&amp;D by subscribing to an Enterprise offer.
                                          </p>
                                        </div>
                                      </div>
                                    </td>
                                  </tr>
                                </tbody>
                              </table>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
        </tbody>
      </table>
    </center>
  </div>
</div>
<!--/* End of common base layout template*/-->
</body>
</html>
<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoItemsToUpdate(editedInfoMsg, addedInfoMsg, deletedInfoMsg, oldValue, newValue)">
    <span>
        <th:block th:if="${oldValue != null} AND ${newValue} != null">
               <th:block data-th-replace="layout :: infoEditedItem(${editedInfoMsg}, ${oldValue}, ${newValue})"/>
        </th:block>
        <th:block th:if="${oldValue == null} AND ${newValue} != null">
               <th:block data-th-replace="layout :: infoAddedItem(${addedInfoMsg}, ${newValue})"/>
        </th:block>
        <th:block th:if="${oldValue != null} AND ${newValue} == null">
               <th:block data-th-replace="layout :: infoDeletedItem(${deletedInfoMsg}, ${oldValue})"/>
        </th:block>
    </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoEditedItem(titleInfo, oldValue, newValue)">
    <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
   <br/>
    <span>
        <th:block th:if="${oldValue != null}">
            <th:block th:replace="${oldValue}" />
            =>
        </th:block>
        <th:block th:if="${newValue != null}">
            <th:block th:replace="${newValue}" />
        </th:block>
    </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoAddedItem(titleInfo, newValue)">
    <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
   <br/>
    <span>
        <th:block th:replace="${newValue}" />
    </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoDeletedItem(titleInfo, oldValue)">
    <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
   <br/>
    <span>
        <th:block th:replace="${oldValue}" />
    </span>
</div>

<!--/* Edited  date  display settings  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoDateItemsToUpdate(editedInfoMsg, addedInfoMsg, deletedInfoMsg, oldValue, newValue)">
    <span>
        <th:block th:if="${oldValue != null} AND ${newValue} != null">
               <th:block data-th-replace="layout :: infoEditedDateItem(${editedInfoMsg}, ${oldValue}, ${newValue})"/>
        </th:block>
        <th:block th:if="${oldValue == null} AND ${newValue} != null">
               <th:block data-th-replace="layout :: infoAddedDateItem(${addedInfoMsg}, ${newValue})"/>
        </th:block>
        <th:block th:if="${oldValue != null} AND ${newValue} == null">
               <th:block data-th-replace="layout :: infoDeletedDateItem(${deletedInfoMsg}, ${oldValue})"/>
        </th:block>
    </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoEditedDateItem(titleInfo, oldValue, newValue)">
    <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
   <br/>
    <span>
        <th:block th:if="${oldValue != null}">
            <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue, df)}" />
            =>
        </th:block>
        <th:block th:if="${newValue != null}">
           <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(newValue, df)}" />
        </th:block>
    </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoAddedDateItem(titleInfo, newValue)">
    <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
   <br/>
    <span>
        <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(newValue, df)}"/>
    </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoDeletedDateItem(titleInfo, oldValue)">
    <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
   <br/>
    <span>
        <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue, df)}"/>
    </span>
</div>

<!--/* Common header template */-->
<head  data-th-fragment="header">
  <title data-th-text="${mailSubject}">Mail subject</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<!--/* Common greeting  template */-->
<div data-th-fragment="greetings(currentFirstName)">
  <p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px"
 data-th-text="#{welcomeMessage(${currentFirstName})}">
Hello Amy,</p>
</div>
<!--/* Common upper email section  template */-->
<div data-th-fragment="contentUpperSection(sectionContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;">
   <div align="left" style="padding:24px 17px 5px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;
border-top: 1px solid #e1e1e1;">
      <th:block th:replace="${sectionContent}" />
       </div>
</div>
<!--/* Common message section template */-->
<div data-th-fragment="contentMessageSection(messageTitle,messageContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;" >
          <div align="left" style="padding:24px 17px 15px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;">
<p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px">
<th:block th:replace="${messageTitle}" />
</p>
<p style="margin:0;color: #88a3b1;">
<th:block th:replace="${messageContent}" />
</p>
</div>
</div>
<!--/* Common link style */-->
<div data-th-fragment="infoActionLink(titleInfo,urlLink)"  style="margin-bottom:17px;" >
<span style="font-weight:bold;" data-th-text="${titleInfo}" >Download link title  </span>
  <br/>
<a target="_blank" style="color:#1294dc;text-decoration:none;"
                          data-th-text="${urlLink}"  th:href="@{${urlLink}}"   >Link </a>
</div>
<!--/* Common date display  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoDateArea(titleInfo,contentInfo)">
     <div data-th-if="${contentInfo != null}">
      <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
      <br/>
      <span  th:with="df=#{date.format}" data-th-text="${#dates.format(contentInfo,df)}">7th of November, 2018</span>
   </div>
</div>
<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoStandardArea(titleInfo,contentInfo)">
     <div data-th-if="${contentInfo != null}">
	   <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
       <br/>
       <th:block th:replace="${contentInfo}" />
	</div>
</div>
<!--/* Common button action style */-->
<span   data-th-fragment="actionButtonLink(labelBtn,urlLink)">
<a
style="border-radius:3px;font-size:15px;color:white;text-decoration:none;padding: 10px 7px;width:auto;max-width:50%;display:block;background-color: #42abe0;text-align: center;margin-top: 17px;"  target="_blank"
data-th-text="${labelBtn}"  th:href="@{${urlLink}}">Button label</a>
</span>
<!--/* Common recipient listing for external and internal users */-->
<div  style="margin-bottom:17px;" data-th-fragment="infoRecipientListingArea(titleInfo,arrayRecipients)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Recipients</span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="recipientData: ${arrayRecipients}">
<div data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
         <span style="color:#787878;font-size:13px"  data-th-utext="${recipientData.mail}">
        my-file-name.pdf
         </span>
</div>
<div data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">
         <span  style="color:#787878;font-size:13px">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
</div>
      </li>
   </ul>
</div>
<div data-th-if="(${!isAnonymous})">
         <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
</div>
<!--/* Lists all file links in a share   */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileLinksListingArea(titleInfo,arrayFileLinks,isAnonymous)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
<div data-th-if="(${!isAnonymous})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}" th:href="@{${shareLink.href}}">
        my-file-name.pdf
         </a>
</div>
<div data-th-if="(${isAnonymous})">
         <a style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
   </li>
</ul>
</div>
<!--/* Lists all file links in a share  and checks witch one are the recpient\s */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileListWithMyUploadRefs(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
         <a style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
<th:block  data-th-if="(${shareLink.mine})"> <span  data-th-text="#{common.byYou}">|  By You</span></th:block >
      </li>
   </ul>
</div>
<!--/* Lists all file links in a share along with their download status   */-->
<div  data-th-fragment="infoFileListUploadState(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
<li style="color:#00b800;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${shareLink.downloaded})">
 <th:block data-th-if="(${shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px ;font-weight:bold"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
 <th:block data-th-if="(${!shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
      </li>
<li style="color:#787878;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${!shareLink.downloaded})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
      </li>
   </ul>
</div>
<!--/* Lists all recpients download states per file   */-->
<div   style="margin-bottom:17px;"  data-th-fragment="infoFileListRecipientUpload(titleInfo,arrayFileLinks)">
    <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
    <th:block style="color: #787878; font-size:10px;margin-top:10px; display: inline-block;" th:each="shareLink : ${arrayFileLinks}" >
      <div style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
        <!--[if mso]>
					&nbsp;&nbsp;
				<![endif]-->
        <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
          <span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">test-file.jpg</span>
        </a>
        <span data-th-if="(${!shareLink.allDownloaded})" style="color: #787878; font-size: 22px;">&bull;</span>
        <span data-th-if="(${shareLink.allDownloaded})" style="color: #00b800; font-size: 22px;">&bull;</span>
      </div>
      <table>
        <th:block  th:each="recipientData: ${shareLink.shares}">
          <th:block data-th-if="(${!recipientData.downloaded})" >
            <tr>
              <td style="color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>

              <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
                <td>
                  <span style="color:#7f7f7f;font-size:13px;">
                    <th:block  data-th-utext="${recipientData.firstName}"/>
                    <th:block data-th-utext="${recipientData.lastName}"/>
                  </span>
                </td>
              </th:block>
              <th:block data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
                <td>
                  <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}">able.cornell@linshare.com </span>
                </td>
              </th:block>
            </tr>
          </th:block>

          <th:block data-th-if="(${recipientData.downloaded})">
            <tr>
              <td style="color:#00b800;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>

              <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
                <td>
                  <span style="color:#7f7f7f;font-size:13px;">
                    <th:block  data-th-utext="${recipientData.firstName}"/>
                    <th:block data-th-utext="${recipientData.lastName}"/>
                  </span>
                </td>
              </th:block>
              <th:block data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
                <td>
                  <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}">able.cornell@linshare.com </span>
                </td>
              </th:block>
            </tr>
          </th:block>
        </th:block>
      </table>
    </th:block>
</div>' WHERE id=1;

-- Update UPLOAD_REQUEST_UPDATED_SETTINGS.sql in order to avoid null values for UR update settings email
UPDATE mail_content SET subject='[(#{subject(${subject.value})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName}, ${subject.value})}">
                     </span>
                     <span data-th-utext="#{secondaryMsg}">
                     </span>
                  </p>
                  <!--/* If the sender has added a  customized message */-->
                  <th:block data-th-if="(${message.modified})">
                     <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                        <span id="message-title">
                        <span data-th-text="#{msgFrom}">You have a message from</span>
                        <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                        </span>
                        <span id="message-content" data-th-text="*{message.value}" style="white-space: pre-line;">
                        Hi Amy,<br>
                        As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
                        </span>
                     </div>
                  </th:block>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <span data-th-if="(${expiryDate.modified})">
               <th:block data-th-replace="layout :: infoDateItemsToUpdate(#{expiryDate}, #{expiryDateParamAdded}, #{expiryDateParamDeleted}, ${expiryDate.oldValue}, ${expiryDate.value})"/>
            </span>
            <span data-th-if="(${activationDate.modified})">
               <th:block data-th-replace="layout :: infoDateItemsToUpdate(#{activationDate}, #{activationDateParamAdded}, #{activationDateParamDeleted}, ${activationDate.oldValue}, ${activationDate.value})"/>
            </span>
            <span data-th-if="(${closureRight.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{closureRight}, #{closureRightParamAdded}, #{closureRightParamDeleted}, ${closureRight.oldValue}, ${closureRight.value})"/>
            </span>
            <span data-th-if="(${deletionRight.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{deletionRight}, #{deletionRightParamAdded}, #{deletionRightParamDeleted}, ${deletionRight.oldValue}, ${deletionRight.value})"/>
            </span>
            <span data-th-if="(${maxFileSize.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{maxFileSize}, #{maxFileSizeParamAdded}, #{maxFileSizeParamDeleted}, ${maxFileSize.oldValue}, ${maxFileSize.value})"/>
            </span>
            <span data-th-if="(${maxFileNum.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{maxFileNum}, #{maxFileNumParamAdded}, #{maxFileNumParamDeleted}, ${maxFileNum.oldValue}, ${maxFileNum.value})"/>
            </span>
            <span data-th-if="(${totalMaxDepotSize.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{depotSize}, #{totalMaxDepotSizeParamAdded}, #{totalMaxDepotSizeParamDeleted}, ${totalMaxDepotSize.oldValue}, ${totalMaxDepotSize.value})"/>
            </span>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='activationDate = Date d''activation
closureRight = Droits de clôture
deletionRight = Droits de suppression
depotSize = Taille du dépôt
expiryDate = Date de clôture
enableNotification = Activation des notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a modifié des paramètres liés à l''''Invitation de dépôt <b>{2}</b>.
maxFileNum = Nombre de Fichiers
maxFileSize = Taille autorisée
msgFrom = Nouveau message de
name = {0} {1}
nameOfDepot: Nom du dépôt
secondaryMsg = Les modifications sont listées ci-dessous.
subject = Modification des paramètres du dépôt : {0}
buttonMsg = Accès,
maxFileSizeParamAdded = Paramètre de la taille de fichier autorisée ajouté
maxFileSizeParamDeleted = Paramètre de la taille de fichier autorisée annulé, ancienne valeur
maxFileNumParamAdded = Paramètre de nombre maximal des fichiers ajouté
maxFileNumParamDeleted = Paramètre de nombre maximal des fichiers annulé, ancienne valeur
totalMaxDepotSizeParamAdded = Paramètre de la taille maximale des fichiers déposés ajouté
totalMaxDepotSizeParamDeleted = Paramètre de la taille maximale des fichiers annulé, ancienne valeur
deletionRightParamAdded = Paramètre de droit de suppression ajouté
deletionRightParamDeleted = Paramètre de droit de suppression annulé, ancienne valeur
closureRightParamAdded = Paramètre de droits de clôture ajouté
closureRightParamDeleted = Paramètre de droits de clôture annulé, ancienne valeur
activationDateParamAdded = Paramètre de date d''activation ajouté
activationDateParamDeleted = Paramètre de date d''activation annulé, ancienne valeur
expiryDateParamAdded = Paramètre d''expiration ajouté
expiryDateParamDeleted = Paramètre d''expiration annulé, ancienne valeur',messages_english='activationDate = Activation date
closureRight = Closure rights
deletionRight = Deletion rights
depotSize = Repository size
expiryDate = Closure date
enableNotification = Enable notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  has updated some settings related to the Upload Request <b>{2}</b>.
maxFileNum = File number
maxFileSize = File size
msgFrom =  New message from
name = {0} {1}
nameOfDepot: Name of the Upload Request
secondaryMsg = Updated settings are listed below.
subject = Updated Settings for Upload Request : {0}
buttonMsg = Access
maxFileSizeParamAdded = Max File size parameter added
maxFileSizeParamDeleted = Max File size parameter canceled, last value
maxFileNumParamAdded = Max File number parameter added
maxFileNumParamDeleted = Max File number parameter deleted, last value
totalMaxDepotSizeParamAdded = Max total deposite size parameter added
totalMaxDepotSizeParamDeleted = Max total deposite size parameter, last value
deletionRightParamAdded = Deletion rights parameter added
deletionRightParamDeleted = Deletion rights parameter canceled
closureRightParamAdded = Closure right parameter added
closureRightParamDeleted = Closure right parameter added, last value
activationDateParamAdded = Activation date parameter added
activationDateParamDeleted = Activation date parameter added, last value
expiryDateParamAdded = Expiration parameter added
expiryDateParamDeleted = Expiration parameter canceled, last value',messages_russian='activationDate = Дата активации
closureRight = Права закрытия
deletionRight = Права удаления
depotSize = Размер репозитория
expiryDate = Дата закрытия
enableNotification = Разрешить уведомления
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  обновил некоторые настройки запроса загрузки <b>{2}</b>.
maxFileNum = Номер файла
maxFileSize = Размер файла
msgFrom =  Новое сообщение от
name = {0} {1}
nameOfDepot: Название загрузки
secondaryMsg = Список обновленных настроек доступен ниже.
subject = Обновленные настройки для запроса загрузки {0}
buttonMsg = Access
maxFileSizeParamAdded = Max File size parameter added
maxFileSizeParamDeleted = Max File size parameter canceled, last value
maxFileNumParamAdded = Max File number parameter added
maxFileNumParamDeleted = Max File number parameter deleted, last value
totalMaxDepotSizeParamAdded = Max total deposite size parameter added
totalMaxDepotSizeParamDeleted = Max total deposite size parameter, last value
deletionRightParamAdded = Deletion rights parameter added
deletionRightParamDeleted = Deletion rights parameter canceled
closureRightParamAdded = Closure right parameter added
closureRightParamDeleted = Closure right parameter added, last value
activationDateParamAdded = Activation date parameter added
activationDateParamDeleted = Activation date parameter added, last value
expiryDateParamAdded = Expiration parameter added
expiryDateParamDeleted = Expiration parameter canceled, last value' WHERE id=23;


-- Update upgrade task table in order to hide old upgrade tasks
ALTER TABLE upgrade_task ADD COLUMN hidden bool DEFAULT 'false' NOT NULL;
UPDATE upgrade_task SET hidden = true WHERE task_group like 'UPGRADE_2%';


-- Update DRIVE functionality to WORK_SPACE
UPDATE functionality SET identifier = 'WORK_SPACE__CREATION_RIGHT' WHERE id in (SELECT id FROM functionality WHERE identifier = 'DRIVE__CREATION_RIGHT');

-- Drive renamed to WorkSpace in mail notifications
UPDATE mail_activation SET identifier = 'WORK_SPACE_WARN_NEW_MEMBER' WHERE identifier = 'DRIVE_WARN_NEW_MEMBER';
UPDATE mail_activation SET identifier = 'WORK_SPACE_WARN_UPDATED_MEMBER' WHERE identifier = 'DRIVE_WARN_UPDATED_MEMBER';
UPDATE mail_activation SET identifier = 'WORK_SPACE_WARN_DELETED_MEMBER' WHERE identifier = 'DRIVE_WARN_DELETED_MEMBER';
UPDATE mail_activation SET identifier = 'WORK_SPACE_WARN_DELETED' WHERE identifier = 'DRIVE_WARN_DELETED_DRIVE';


-- Rename driveProvider to workSpaceProvider
ALTER TABLE drive_provider RENAME TO work_space_provider;
ALTER TABLE domain_abstract RENAME COLUMN drive_provider_id TO work_space_provider_id;


-- UPGRADE_5_0_AUDIT_RENAME_DRIVE_TO_WORK_SPACE
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (48,
  'UNDEFINED',
  'UPGRADE_5_0_AUDIT_RENAME_DRIVE_TO_WORK_SPACE',
  'UPGRADE_5_0',
  48,
  'NEW',
  'MANDATORY',
  now(),
  now());

-- UPGRADE_5_0_RENAME_DRIVE_TO_WORK_SPACE
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (49,
  'UNDEFINED',
  'UPGRADE_5_0_RENAME_DRIVE_TO_WORK_SPACE',
  'UPGRADE_5_0',
  49,
  'NEW',
  'MANDATORY',
  now(),
  now());

  -- UPGRADE_5_0_RENAME_DRIVE_MEMBERS_TO_WORK_SPACE_MEMBERS
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (50,
  'UNDEFINED',
  'UPGRADE_5_0_RENAME_DRIVE_MEMBERS_TO_WORK_SPACE_MEMBERS',
  'UPGRADE_5_0',
  50,
  'NEW',
  'MANDATORY',
  now(),
  now());

---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();

COMMIT;
