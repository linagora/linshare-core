-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'2.3.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '2.3.0';
	DECLARE version_from VARCHAR := '2.2.0';
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

-- Updates default values for LDAP synchronization
-- Group ldap pattern
UPDATE ldap_pattern 
SET search_all_groups_query = 'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-*))");',
search_group_query = 'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-" + pattern + "))");'
WHERE system = true ;


-- Patch 1 : Prevent creation conflict with id > 1000
DELETE FROM ldap_attribute WHERE id = 1060 AND system = TRUE;
DELETE FROM ldap_attribute WHERE id = 1061 AND system = TRUE;
DELETE FROM ldap_attribute WHERE id = 1062 AND system = TRUE;
DELETE FROM ldap_attribute WHERE id = 1063 AND system = TRUE;
DELETE FROM ldap_attribute WHERE id = 1064 AND system = TRUE;

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
	'workgroup-' WHERE NOT EXISTS (SELECT * FROM ldap_pattern_already_exists);


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

-- Patch 2 : New Demo ldap pattern.

WITH ldap_pattern_already_exists AS (UPDATE ldap_pattern SET id=id WHERE id=5 RETURNING *)
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
    now() WHERE NOT EXISTS (SELECT * FROM ldap_pattern_already_exists);


WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=18 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 18, 'user_mail', 'mail', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=19 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 19, 'user_firstname', 'givenName', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=12 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 20, 'user_lastname', 'sn', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=21 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 21, 'user_uid', 'uid', false, true, true, 5, false WHERE NOT EXISTS (SELECT * FROM already_exists);

	-- Functionality : WORK_GROUP__FILE_VERSIONING
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (297, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (298, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (299, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (63, false, 'WORK_GROUP__FILE_VERSIONING', 297, 298, 299, 1, 'WORK_GROUP', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (63, true);

	-- Functionality : WORK_GROUP__FILE_EDITION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (303, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (304, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (305, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (65, false, 'WORK_GROUP__FILE_EDITION', 303, 304, 305, 1, 'WORK_GROUP', true, now(), now());
INSERT INTO functionality_string(functionality_id, string_value) 
	VALUES (65, 'http://editor.linshare.local');
	
	-- Functionality : WORK_GROUP__DOWNLOAD_ARCHIVE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (306, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (307, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (66, false, 'WORK_GROUP__DOWNLOAD_ARCHIVE', 306, 307, 1, 'WORK_GROUP', true, now(), now());
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (12, 1, 1);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) 
	VALUES (66, 900, 12);

	--Welcome messages
CREATE OR REPLACE FUNCTION update_wm() RETURNS void AS $$
BEGIN
	DECLARE welcmessage record;
	DECLARE language VARCHAR := 'ru';
	DECLARE j BIGINT;
BEGIN
		FOR welcmessage IN 
		SELECT * FROM welcome_messages LOOP
					IF language NOT IN (SELECT lang FROM welcome_messages_entry where welcome_messages_id = welcmessage.id) THEN
					    BEGIN
						j := (SELECT nextVal('hibernate_sequence'));
						 INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id)
							VALUES (j, 'ru', '<h2>Добро пожаловать в LinShare</h2><p>Добро пожаловать в LinShare - открытое приложение для надежного обмена файлами.</p>', welcmessage.id);
					    END;
					END IF;
		END LOOP;
END;
END
$$ LANGUAGE plpgsql;

UPDATE welcome_messages_entry SET value = '<h2>Chào mừng bạn đến với LinShare</h2><p>Chào mừng bạn đến với LinShare, phần mềm nguồn mở chia sẻ file bảo mật.</p>' WHERE id = 3;
-- End of your requests

-- Upgrade Task
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
  26,
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

-- End Upgrade Task

-- Mail content
UPDATE mail_content SET messages_french='downloadBtn = Télécharger
downloadLink = Lien de téléchargement
helpMsgSingular =  pour visualiser le document partagé.
helpMsgPlural =pour visualiser tous les documents du partage.
helpPasswordMsgSingular = Cliquez sur le lien pour le télécharger et saisissez le mot de passe fourni ici.
helpPasswordMsgPlural = Cliquez sur le lien pour les télécharger et saisissez le mot de passe fourni.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>a partagé {2} fichiers avec vous.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a partagé {2} fichier  avec vous.
msgFrom = Vous avez un message de
name = {0} {1}
password = Mot de passe
subjectCustomAlt =de {0} {1}
subjectPlural =  {0} {1} vous a partagé des fichiers
subjectSingular =  {0} {1} vous a partagé un fichier
click = Cliquez sur ce
link = lien' WHERE id=2;
-- End mail content

-- Mail layout
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
welcomeMessage = Hello {0},',layout='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/* Beginning of common base layout template*/-->
<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">
  <div style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:''Open Sans'',arial,Helvetica,sans-serif;">
    <center>
      <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"
             style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px"
             width="90%">
        <tbody>
        <tr>
          <td align="center" style="border-collapse:collapse" valign="top">
            <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px" width="90%">
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
                              style="display:inline-block;margin-bottom:20px;margin-top:20px" width="233" alt="Logo"
                              height="57"/>
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
                                <th:block data-th-replace="${upperMainContentArea}"/>
                              </div>
                              <table border="0" cellspacing="0" cellpadding="0" width="100%"
                                     style="background-color: #f8f8f8;">
                                <tbody>
                                <tr>
                                  <td width="15" style="border-top:1px solid #c9cacc;">
                                  </td>
                                  <td width="20"><img src="cid:logo.arrow@linshare.org"
                                    width="20" height="9" border="0" style="display:block;" alt="down arrow"/></td>
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
                                        <th:block data-th-replace="${bottomSecondaryContentArea}"/>
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
                                    ©&nbsp;2009–2019. Contribute to
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
<div style="margin-bottom:17px;" data-th-fragment="infoEditedItem(titleInfo,oldValue,newValue)">
     <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
    <br/>
      <th:block th:replace="${oldValue}" />  -> <th:block th:replace="${newValue}" />
</div>
<!--/* Edited  date  display settings  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoEditedDateArea(titleInfo,oldValue,newValue)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
    <br/>
 <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue,df)}"/> -> 
 <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(newValue,df)}"/>
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
      </li>
   </ul>
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
         <span style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
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
   <ul style="padding: 5px 0px; margin: 0;list-style-type:none;">
<li style="color:#787878;font-size:10px;margin-top:10px;"  th:each="shareLink : ${arrayFileLinks}" >
    <span style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
  <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
    <span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">
test-file.jpg</span></a>
    <span data-th-if="(${!shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #787878;"></span>
    <span data-th-if="(${shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #00b800;"></span>
    </span>
    <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;" >
 <th:block  th:each="recipientData: ${shareLink.shares}">
   <th:block data-th-if="(${!recipientData.downloaded})" >
      <li style="color:#787878;font-size:15px"  >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
      <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"
           data-th-if="(${#strings.isEmpty(recipientData.lastName)})">able.cornell@linshare.com </span>
      </li>
   </th:block>
<th:block data-th-if="(${recipientData.downloaded})">
   <li style="color:#00b800;font-size:15px" >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span  style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
<th:block  data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
  <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"> able.cornell@linshare.com </span>
  </th:block>
      </li>
   </th:block>
</th:block>
    </ul>
</li>
   </ul>
</div>' WHERE id=1;
--end mail layout

--Mail content
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
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${threadMember.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>' WHERE id=28;

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
          <span th:if="${owner.firstName} != null AND ${owner.firstName} != null" data-th-utext="#{mainMsgNextBy(${owner.firstName},${owner.lastName})}"></span>

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
--end mail content

-- Add russian mail message to mail tables
ALTER TABLE mail_content ADD COLUMN messages_russian text;
ALTER TABLE mail_footer ADD COLUMN messages_russian text;
ALTER TABLE mail_layout ADD COLUMN messages_russian text;
--End

-- Update the all mail_content tables by adding the russian messages
UPDATE mail_content
SET messages_russian='beginningMainMsgInt = Ваш файл
endingMainMsgInt = будет автоматически удален через <b> {0} дней</b> из вашего личного пространства.
subject = Файл {0} будет удален
uploadedThe = Дата загрузки'
WHERE id = 1;

UPDATE mail_content
SET messages_russian='downloadBtn = Загрузить
downloadLink = Загрузить по ссылке
helpMsgPlural = , чтобы получить доступ ко всем документам рассылки.
helpMsgSingular = , чтобы получить доступ ко всем документам рассылки.
helpPasswordMsgSingular = Перейдите по ссылке ниже, чтобы загрузить файлы и ввести пароль.
helpPasswordMsgPlural = Перейдите по ссылке ниже, чтобы загрузить файлы и ввести пароль.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> поделился с вами файлами <b>{2} файлов</b>.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> поделился с вами  <b>{2} файлами</b>.
msgFrom = Вы получили сообщение от
name = {0} {1}
password = Пароль
subjectCustomAlt =by {0} {1}
subjectPlural = {0} {1} поделился с вами файлами
subjectSingular = {0} {1} поделился с вами файлами
click = Перейдите по
link = ссылке'
WHERE id = 2;

UPDATE mail_content
SET messages_russian='numFilesMsgPlural = Вы поделились <b>{0} files</b>
numFilesMsgSingular = Вы поделились <b>{0} file</b>
recipientCountMsgPlural =   с <b>{1} recipients</b>. Срок действия рассылки закончится: {0}.
recipientCountMsgSingular =   с <b>{1} recipient</b>. Срок действия рассылки закончится: {0}.
subjectPlural =  Вы поделились некоторыми файлами
subjectSingular =Вы поделились файлом
msgFor = Ваше сообщение рассылки'
WHERE id = 3;

UPDATE mail_content
SET messages_russian='downloadDate = Дата загрузки
fileNameEndOfLine = {0}.
mainMsgExt = Внешний пользователь <b>{0}</b> скачал(а) ваш файл
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> скачал(а) ваш файл
subject = {0} {1} был скачан {2}
subjectAnonymous = {0} был скачан {1}'
WHERE id = 4;

UPDATE mail_content
SET messages_russian='deletedDate = Дата удаления
mainMsg = <b>{0} <span style="text-transform:uppercase">{1}</span></b> удалил файл рассылки
subject = {0} {1} удалил файл рассылки {2}'
WHERE id = 5;

UPDATE mail_content
SET messages_russian='beginningMainMsgInt = Срок действия файла рассылки
endingMainMsgInt = отправленного <b> {0} <span style="text-transform:uppercase">{1}</span></b>,  закончится через <b>{2} дней</b>.
mainMsgExt = Срок действия файла рассылки <b>{0}</b> sent by <b> {1} <span style="text-transform:uppercase">{2}</span></b>,  закончится через <b>{3} дней</b>.
name = {0} {1}
sharedBy = Отправлено
subject = Срок действия файла рассылки {0} заканчивается'
WHERE id = 6;

UPDATE mail_content
SET messages_russian='downloadStatesTile = Статус загрузки
mainMsgplural = Некоторые получатели рассылки не скачали <b>{0} файлов</b>. Вы можете найти детали о получателях рассылки ниже.
mainMsgSingular = Некоторые получатели рассылки не скачали <b>{0} файлов</b>. Вы можете найти детали о получателях рассылки ниже.
subjectPlural = Уведомдение о не скачанных файлах: {0} файлов были не скачанны.
subjectSingular = Уведомдение о не скачанных файлах: {0} файлов были не скачанны.'
WHERE id = 7;

UPDATE mail_content
SET messages_russian='accessToLinshareBTn = Активировать аккаунт
accountExpiryDateTitle = Срок действия аккаунта
activationLinkTitle = Ссылка активации
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> создал гостевой аккаунт <b>{2}</b>  для вас, который позволяет надежно обмениваться файлами. <br/>Для входа в ваш аккаунт, завершите процесс регистрации, используя ссылку
subject = {0}  {1} пригласил вас активировать ваш {2} аккаунт
userNameTitle = Имя пользователя'
WHERE id = 8;

UPDATE mail_content
SET messages_russian='accountExpiryDateTitle = Дата окончания действия аккаунта
beginingMainMsg =  Используйте ссылку ниже для смены пароля к вашему аккаунту LinShare.
changePasswordBtn = Изменить пароль
endingMainMsg = Если вы не запрашивали смену пароля, пожалуйста, проигнорируйте это письмо. Ваш пароль не будет изменен пока вы не создадите новый, перейдя по ссылке.
mainTile = Забыли пароль?
resetLinkTitle = Ссылка на смену пароля LinShare
subject =  Инструкция по смену пароля LinShare
userNameTitle = Имя пользователя'
WHERE id = 9;

UPDATE mail_content
SET messages_russian='endingMainMsg = в вашем запросе загрузки
fileSize = Размер файла
fileUploadedThe = Дата загрузки
invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
beginningMainMsg =  <b> {0} </b> загрузил файл
endingMainMsg = в ваш запрос загрузки.
numFilesInDepot = Всего загруженных файлов
subject =  {0}  загрузил {1}  в ваш запрос загрузки
uploadedOverTotal = {0} / {1} файлы
totalUploaded = {0} файлы'
WHERE id = 10;

UPDATE mail_content
SET messages_russian='invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
mainMsg =  <b>{0}</b> не может загрузить файлы, так как в вашем личном пространстве недостаточно места. Пожалуйста, удалите некоторые файлы, чтобы освободить место.
mainMsgTitle = Недостаточно свободного места.
maxUploadDepotSize = Максимальный размер загрузки
msgTitle = Сообщение загрузки:
recipientsURequest = Получатели
subject =  {0} не может загрузить файл, так как недостаточно свободного места'
WHERE id = 11;

UPDATE mail_content
SET messages_russian='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Действие запроса на загрузку закончится через <b>{2} дней</b>
beginningMainMsgGrouped = Действие вашего приглашения закончится через <b>{0} дней</b>.
beginningMainMsgUnGrouped =  Действие вашего приглашения закончится через <b>{0} дней</b>.
endingMainMsgPlural =  вы получили <b>{0} файлов</b>.
endingMainMsgPlural = Всего загрузка содержит <b> {0} файлов </b>.
endingMainMsgPluralForRecipient = вы отправили <b> {0} файлов </b> в загрузку.
endingMainMsgSingular =   вы получили <b>1 файл</b>.
endingMainMsgSingular = Всего в репозитории of <b>1 файл </b>.
endingMainMsgSingularForRecipient = вы отправили <b>1 файл </b> в репозиторий.
filesInURDepot = Загруженные файлы
formatMailSubject = : {0}
invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
msgTitle =  Сообщение запроса загрузки:
recipientsURequest = Получатели
subjectForOwner =  Срок действия вашего приглашения заканчивается.
subjectForRecipient =  {0} {1}''''s срок действия вашего приглашения заканчивается.
uploadFileBtn = Загрузить файл'
WHERE id = 12;

UPDATE mail_content
SET messages_russian='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Срок действия загрузки закончился
beginningMainMsgGrouped = Срок действия вашего группового запроса загрузки закончился
beginningMainMsgUnGrouped = Срок действия загрузки закончился.
endingMainMsgPlural = Вы получили <b>{0} файлов</b>.
endingMainMsgPluralForRecipient = вы отправили всего <b> {0} файлов </b>.
endingMainMsgSingular = всего вы получили <b>1 файлов</b>.
endingMainMsgSingularForRecipient = вы загрузили в репозиторий <b>1 файл </b> .
filesInURDepot = Загружено файлов
formatMailSubject = : {0}
invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
msgTitle = Сообщение загрузки
recipientsURequest = Получатели
subjectForOwner = Ваше приглашение  {0} больше не действительно
subjectForRecipient =  {0} {1}''''s  приглешение {2} больше не действительно'
WHERE id = 13;

UPDATE mail_content
SET messages_russian='endingMainMsgPlural = Всего в хранилище <b> {0} файлов </b>.
endingMainMsgSingular =  Всего в хранилище <b>1 файл </b.
filesInURDepot = Файлы загружены
fileSize =  Общий размер файла
groupedBeginningMainMsg = <b>{0}</b> закрыл ваше групповое хранилище для файлов запроса загрузки.
invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
msgTitle =  Запрос загрузки содержит сообщение:
numFilesInDepot = Всего загруженных файлов
recipientsURequest = Получатели
subject =  {0} закрыл ваше хранилище для файлов запроса загрузки {1}
ungroupedBeginningMainMsg  = <b>{0}</b> закрыл ваше хранилище для файлов запроса загрузки.
uploadedOverTotal = {0} / {1} файлов
totalUploaded = {0} файлов'
WHERE id = 14;

UPDATE mail_content
SET messages_russian='invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
mainMsg = <b>{0}</b> удалил файл <b> {1} </b> из вашего запроса загрузки.
msgTitle = Загрузка содержит сообщение:
subject = {0} удалил файл из загрузки {1}'
WHERE id = 15;

UPDATE mail_content
SET messages_russian='buttonMsg = Доступ
closureDate = Дата закрытия
depotSize = Допустимый размер
mainMsg = <b>{0} {1}</b> пригласил вас в свой запрос загрузки <b>{2}</b>.
msgFrom = Сообщение от
msgAlt = Репозиторий {0} {1} активен.
msgProtected = Разблокируйте его, перейдя по ссылке ниже и введя пароль.
msgUnProtected = Получите доступ, перейдя по ссылке ниже.
name = {0} {1}
password = Пароль
recipientsOfDepot = Получатель
subject = {0} {1}  пригласил вас в свой запрос загрузки {2}'
WHERE id = 16;

UPDATE mail_content
SET messages_russian='buttonMsg = Доступ
closureDate = Дата закрытия
depotSize = Допустимый размер
mainMsg = Ваш запрос загрузки <b>{0}</b> активен.
msgLink = Получите доступ к нему, перейдя по ссылке ниже.
recipientsOfDepot = Получатель
subject = Ваш запрос загрузки {0} активен'
WHERE id = 17;

UPDATE mail_content
SET messages_russian='buttonMsg = Доступ
closureDate = Дата закрытия
depotSize = Размер
mainMsg = <b>{0} {1}</b> напоминает вам о загрузке ваших файлов.
mainMsgEnd =
msgFrom = Сообщение от
msgUnProtected = Для того, чтобы загрузить ваши файлы, пожалуйста, перейдите по ссылке ниже.
name = {0} {1}
recipientsOfDepot = Получатели
subject = {0} {1} ожидает ваши файлы'
WHERE id = 18;

UPDATE mail_content
SET messages_russian='buttonMsg = Доступ к загрузке
closureDate = Дата закрытия загрузки
mainMsg = <b>{0} {1}</b> изменил пароль к загрузке {2}
msgProtected = Новый пароль и доступ к загрузке доступны ниже.
password = Пароль
subject = {0} {1} отправил вам новый пароль к загрузке {2}'
WHERE id = 19;

UPDATE mail_content
SET messages_russian='activationDate = Дата активации
closureDate = Дата закрытия
customDate= MMMM d, yyyy.
depotSize = Допустимый размер
mainMsg = <b>{0} {1}</b> открыл для вас доступ к его запросу загрузки, созданному
msgFrom = Сообщение от
name = {0} {1}
recipientsOfDepot = Получатели
subject = {0} {1} открыл для вас доступ к его запросу загрузки.'
WHERE id = 20;

UPDATE mail_content
SET messages_russian='closureDate = Дата закрытия
filesInURDepot = Файлы
mainMsg = <b>{0} {1}</b> закрыл запрос загрузки {2}.
recipientsOfDepot = Получатели
subject = {0} {1} закрыл запрос загрузки {2}'
WHERE id = 21;

UPDATE mail_content
SET messages_russian='deletionDate = Дата удаления
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> закрыл ваш доступ к загрузке {2}.
subject = {0} {1} закрыл ваш доступ к загрузке {2}'
WHERE id = 22;

UPDATE mail_content
SET messages_russian='activationDate = Дата активации
closureRight = Права закрытия
deletionRight = Права удаления
depotSize = Размер репозитория
expiryDate = Дата закрытия
local = Локальный
enableNotification = Разрешить уведомления
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  обновил некоторые настройки запроса загрузки.
maxFileNum = Номер файла
maxFileSize = Размер файла
msgFrom =  Новое сообщение от
name = {0} {1}
nameOfDepot: Название загрузки
secondaryMsg = Список обновленных настроек доступен ниже.
subject = Обновленные настройки для запроса загрузки {0}'
WHERE id = 23;

UPDATE mail_content
SET messages_russian='closureDate = Срок действия загрузки
deletionDate = Дата удаления
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> удалил файл <b>{2} </b> из хранилища {3}.
subject = {0} {1} удалил файл {2} из хранилища'
WHERE id = 24;

UPDATE mail_content
SET messages_russian='accessToLinshareBTn = Истечение срока действия аккаунта
accountCreationDateTitle = Дата создания аккаунта
accountExpiryDateTitle = Дата истечения срока действия аккаунта
activationLinkTitle = Ссылка активации
mainMsg = Срок действия гостевого аккаунта <b> {0} <span style="text-transform:uppercase">{1}</span></b> заканчивается через {2} дня. Если вам все еще нужен аккаунт, продлите срок его действия.
subject = {0}  {1} срок действия гостевого аккакунта скоро закончится.
userEmailTitle = Электронная почта'
WHERE id = 25;

UPDATE mail_content
SET messages_russian='accessToLinshareBTn = Срок действия вашей рассылки скоро закончится
shareRecipientTitle = Получатель
shareFileTitle = Файл рассылки
shareCreationDateTitle = Дата создания
shareExpiryDateTitle = Дата истечения срока действия
activationLinkTitle = Ссылка активации
beginningMainMsg = Срок действия файла рассылки
endingMainMsg =  закончится через {0} дней, а файла не были скачаны получателем <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Срок действия рассылки скоро закончится, а файлы не были скачаны
name = {0} {1}
fileNameEndOfLine = {0}'
WHERE id = 26;

UPDATE mail_content
SET messages_russian='shareFileTitle = Файл рассылки
shareCreationDateTitle = Дата создания
shareExpiryDateTitle = Дата срока истечения действия
activationLinkTitle = Ссылка активации
beginningMainMsg =  У файла рассылки
endingMainMsg = отправленного <b> {0} <span style="text-transform:uppercase">{1}</span></b> истек срок действия и он был удален <b>system</b>.
subject = Срок действия файла рассылки {0} истек
fileNameEndOfLine = {0}'
WHERE id = 27;

UPDATE mail_content
SET messages_russian='workGroupCreationDateTitle = Дата создания
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> добавил вас в рабочую группу <br>
simpleMainMsg = Вас добавили в рабочую группу
subject = Вас добавили в рабочую группу {0}
workGroupRight = Права по умолчанию
workGroupNameTitle = Название рабочей группы'
WHERE id = 28;

UPDATE mail_content
SET messages_russian='workGroupUpdatedDateTitle = Дата обновления
mainMsg = Ваш статус в рабочей группе
mainMsgNext= был обновлен
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Ваш статус в рабочей группе {0} был обновлен.
workGroupRight = Действующий статус
workGroupNameTitle = Название рабочей группы'
WHERE id = 29;

UPDATE mail_content
SET messages_russian='subject = У вас больше нет доступа к рабочей группе {0}.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил вас из рабочей группы  <b>{2}</b>
simpleMsg =  У вас больше нет доступа к рабочей группе <b>{0}</b>.
workGroupNameTitle = Название рабочей группы'
WHERE id = 30;

UPDATE mail_content
SET messages_russian='accountCreationDateTitle = Дата создания аккаунта
accountExpiryDateTitle = Дата окончания действия аккаунта
mainMsg = Пароль аккаунта {0} <b>{1}</b> был изменен.
subject = Ваш пароль был изменен'
WHERE id=31;

UPDATE mail_content
SET messages_russian='subject = Создание постоянного токена аутентификации
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> создал постоянный токен аутентификации для вашей учетной записи.
tokenCreationDate = Дата создания
tokenLabel = Имя
tokenDescription = Описание'
WHERE id=32;

UPDATE mail_content
SET messages_russian='subject = Удаление постоянного токена аутентификации
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил постоянный токен аутентификации для вашего аккаунта.
tokenCreationDate = Дата создания
tokenLabel = Имя
tokenDescription = Описание'
WHERE id=33;
--End 

--Update the mail footer with the russian message
UPDATE mail_footer SET messages_russian='learnMoreAbout=Узнать больше
productOfficialWebsite=http://www.linshare.org/',footer='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <body>
    <div data-th-fragment="email_footer">
                                <td style="border-collapse:collapse;padding: 6px 0 4px 17px;" valign="top">
                                  <p style="margin: 0; font-size: 10px;"><span th:text="#{learnMoreAbout}">En savoir plus sur</span>
<a   th:href="@{#{productOfficialWebsite}}"  target="_blank"style="text-decoration:none; color:#a9a9a9;"><strong th:text="#{productName}">LinShare</strong>™</a>
                                  </p>
                                </td>
                                <td style="border-collapse:collapse; padding:  6px 17px 4px 0;"  valign="top" width="60">
                                  <img alt="libre-and-free" height="9"
                                       src="cid:logo.libre.and.free@linshare.org"
                                       style="line-height:100%;width:60px;height:9px;padding:0" width="60" />
 </td>
   </div>
 </body>
 </html>'
WHERE id=1;
--End

--Update mail layout with the new russian message
UPDATE mail_layout SET messages_russian='common.availableUntil = Срок действия
common.byYou= | Вами
common.download= Загрузить
common.filesInShare = Прикрепленные файлы
common.recipients = Получатели
common.titleSharedThe= Дата создания
date.format=d MMMM, yyyy
productCompagny= Linagora
productName=LinShare
workGroupRightAdminTitle = Администратор
workGroupRightWirteTitle = Автор
workGroupRightContributeTitle = Редактор
workGroupRightReadTitle = Читатель
welcomeMessage = Здравствуйте, {0}'
WHERE id=1;
--End

--Set the messages to be not null on database.
ALTER TABLE mail_content ALTER COLUMN messages_english SET NOT NULL;
ALTER TABLE mail_content ALTER COLUMN messages_french SET NOT NULL;
ALTER TABLE mail_content ALTER COLUMN messages_russian SET NOT NULL;
ALTER TABLE mail_footer ALTER COLUMN messages_russian SET NOT NULL;
ALTER TABLE mail_footer ALTER COLUMN messages_english SET NOT NULL;
ALTER TABLE mail_footer ALTER COLUMN messages_french SET NOT NULL;
ALTER TABLE mail_layout ALTER COLUMN messages_english SET NOT NULL;
ALTER TABLE mail_layout ALTER COLUMN messages_french SET NOT NULL;
ALTER TABLE mail_layout ALTER COLUMN messages_russian SET NOT NULL;
--End

-- Add mail_content_lang for russian

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (201,2,1,1,1,true,'28e5855a-c0e7-40fc-8401-9cf25eb53f30');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (202,2,1,2,2,true,'41d0f03d-57dd-420e-84b0-7908179c8392');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (203,2,1,3,3,true,'72c0fff4-4638-4e98-8223-df27f8f8eab8');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (204,2,1,4,4,true,'6fbabf1a-58c0-49b9-859e-d24b0af38c78');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (205,2,1,5,5,true,'b85fc62f-d9eb-454b-9289-fec5eab51a67');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (206,2,1,6,6,true,'25540d2d-b3b8-46a9-811b-0549ad300f0e');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (207,2,1,7,7,true,'6580009b-36fd-472d-9937-41d0097ead19');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (208,2,1,8,8,true,'86fdc43c-5fd7-4aba-b01a-90fccbfb5444');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (209,2,1,9,9,true,'f9455b1d-3582-4998-8675-bc0a8137fd25');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (210,2,1,10,10,true,'e5a9f689-c005-47c2-958f-b68071b1b666');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (211,2,1,11,11,true,'2daaea2a-1b13-48b4-89a6-032f7e034a3s');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (212,2,1,12,12,true,'8f579a8a-e352-11e4-99b3-08002722e5de');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (213,2,1,13,13,true,'fa7a23cb-f545-45b4-b9dc-c39586cb2ggg');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (214,2,1,14,14,true,'44bc0912-cf91-4fc0-b376-f0ebb82acmmm');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (215,2,1,15,15,true,'cccb263e-1c24-4eb9-bff7-298713cc3854');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (216,2,1,16,16,true,'9f02736e-60e7-11e7-bf58-080027146bb7');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (217,2,1,17,17,true,'9f05d3ec-60e7-11e7-98a3-080027146bb7');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (218,2,1,18,18,true,'9f08b468-60e7-11e7-87e7-080027146bb7');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (219,2,1,19,19,true,'9f0c0672-60e7-11e7-ba0a-080027146bb7');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (220,2,1,20,20,true,'9f0f3ea0-60e7-11e7-a25e-080027146rr8');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (221,2,1,21,21,true,'9f12e0f0-60e7-11e7-8c20-080027146rr8');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (222,2,1,22,22,true,'9f164a06-60e7-11e7-998e-080027146rr8');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (223,2,1,23,23,true,'9f199652-60e7-11e7-a9cf-080027146rr8');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (224,2,1,24,24,true,'9f1c879a-60e7-11e7-95d8-080027146rr8');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (225,2,1,25,25,true,'82ce572e-b968-11e7-9f2c-8b110ac9988y');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (226,2,1,26,26,true,'4376471e-c3ca-11e7-96f0-df378884dyur');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (227,2,1,27,27,true,'935a7b10-c53c-11e7-8ce9-17fe85e6bhji');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (228,2,1,28,28,true,'cd33d42c-c617-11e7-979a-6bf962f5cii9');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (229,2,1,29,29,true,'a4efd518-c619-11e7-8cdf-13a90ce64aaz');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (230,2,1,30,30,true,'4740d3f8-c61a-11e7-8d5a-3f431ce96zza');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (231,2,1,31,31,true,'d5c55f44-d6b5-11e7-b521-4f65da9d0zaz');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (232,2,1,32,32,true,'dbf12958-8389-11e8-964e-6b7eef81d99z');

INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (233,2,1,33,33,true,'dbf23f1e-8389-11e8-b430-a3d498f96z88');

-- end

-- add mail_footer_lang for russian
INSERT INTO mail_footer_lang (id,language,mail_config_id,mail_footer_id,readonly,uuid) VALUES (3,2,1,1,true,'a6c8ee84-b5a8-4c96-b148-43301fbccde8');
-- end

--create mail_attachment
  CREATE TABLE mail_attachment (
  id                  int8 NOT NULL,
  uuid               varchar(255) NOT NULL,
  ressource_uuid               varchar(255) NOT NULL,
  enable           bool DEFAULT 'true' NOT NULL,
  enable_for_all           bool DEFAULT 'true' NOT NULL,
  language          varchar(255) NOT NULL,
  description        text  NOT NULL,
  name               varchar(255),
  size                  int8 NOT NULL,
  creation_date                   timestamp(6) NOT NULL,
  modification_date               timestamp(6) NOT NULL,
  mime_type         varchar(255) NOT NULL,
  sha256sum     varchar(255) NOT NULL,
  mail_config_id    int8 NOT NULL,
  cid               varchar(255) NOT NULL,
  bucket_uuid       varchar(255),
  PRIMARY KEY (id));
ALTER TABLE mail_attachment ADD CONSTRAINT FKmail_attachment35169 FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);


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
