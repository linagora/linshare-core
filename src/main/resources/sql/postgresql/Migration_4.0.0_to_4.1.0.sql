-- Postgresql migration script template

UPDATE version SET version = '4.0.0' WHERE version = '2.4.0';

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'4.1.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '4.1.0';
	DECLARE version_from VARCHAR := '4.0.0';
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

-- Activation of Upload_Request Functionality
UPDATE policy SET system = false, status = true, default_status = true WHERE id = 63;

-- Activation of Collective Upload_Request Functionality by default
UPDATE policy SET status = true, default_status = true WHERE id = 71;
UPDATE policy SET system = false, status = true, default_status = true WHERE id IN (63);

-- Add new field to Functionality_integer and Functionality_unit
-- Set the max value and max unit for the new field on functionality_unit and functionality_integer tables
INSERT INTO unit(id, unit_type, unit_value) 
	VALUES (13, 0, 2), (14, 0, 2), (15, 0, 2),(16, 1, 1), (17, 0, 2), (18, 0, 2), (19, 1, 1), (20, 1, 1), (21, 0, 0);

ALTER TABLE functionality_unit ADD integer_max_value int4 NULL;
ALTER TABLE functionality_unit ADD max_unit_id int8 NOT NULL DEFAULT 13;
ALTER TABLE functionality_integer ADD integer_max_value int4 NULL;

ALTER TABLE functionality_unit RENAME COLUMN integer_value TO integer_default_value;
ALTER TABLE functionality_integer RENAME COLUMN integer_value TO integer_default_value;
ALTER TABLE functionality_unit ADD CONSTRAINT fk3ced0169f329edc1 FOREIGN KEY (max_unit_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;

UPDATE functionality_unit SET integer_default_value = 3, integer_max_value = 4, max_unit_id = 13 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'GUESTS__EXPIRATION');      -- GUESTS__EXPIRATION
UPDATE functionality_unit SET integer_default_value = 3, integer_max_value = 4, max_unit_id = 14 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'DOCUMENT_EXPIRATION');      -- DOCUMENT_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 3, integer_max_value = 4, max_unit_id = 15 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'SHARE_EXPIRATION');      -- SHARE_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 0, integer_max_value = 900 , max_unit_id = 16 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'WORK_GROUP__DOWNLOAD_ARCHIVE'); -- WORK_GROUP__DOWNLOAD_ARCHIVE
UPDATE functionality_unit SET integer_default_value = 0, integer_max_value = -1, max_unit_id = 17 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION');     -- UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
UPDATE functionality_unit SET integer_default_value = 7, integer_max_value = 7, max_unit_id = 18 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION');      -- UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 10, integer_max_value = 20, max_unit_id = 19 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE');    -- UPLOAD_REQUEST__MAXIMUM_FILE_SIZE
UPDATE functionality_unit SET integer_default_value = 50, integer_max_value = 100, max_unit_id = 20 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE');   -- UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE
UPDATE functionality_unit SET integer_default_value = 7, integer_max_value = 7, max_unit_id = 21 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION');      -- UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION

-- Add new fields for default pwd and store original pwd of an URU
ALTER TABLE upload_request_url ADD COLUMN default_Password bool DEFAULT true NOT NULL;
ALTER TABLE upload_request_url ADD COLUMN original_password character varying(255);

--Drop upload proposition tables
DROP TABLE upload_proposition;
DROP TABLE upload_proposition_action;
DROP TABLE upload_proposition_rule;
DROP TABLE upload_proposition_filter;


--Delete UPLOAD_REQUEST__PROLONGATION functionality
DELETE FROM functionality_boolean WHERE functionality_id= 40;
DELETE FROM functionality WHERE identifier='UPLOAD_REQUEST__PROLONGATION';
DELETE FROM policy WHERE id= 89;
DELETE FROM policy WHERE id= 90;
DELETE FROM policy WHERE id= 91;


-- Delete UPLOAD_REQUEST__GROUPED_MODE functionality
DELETE FROM functionality_boolean WHERE functionality_id= 34;
DELETE FROM functionality WHERE identifier='UPLOAD_REQUEST__GROUPED_MODE';
DELETE FROM policy WHERE id= 71;
DELETE FROM policy WHERE id= 72;
DELETE FROM policy WHERE id= 73;


-- Drop not null constraint for notification date and expiry date for upload_request and upload_request_group to accept null value if func is disabled
alter table upload_request alter column notification_date drop not null;
alter table upload_request_group alter column notification_date drop not null;
alter table upload_request_group alter column expiry_date drop not null;
alter table upload_request alter column expiry_date drop not null;


-- Update mail layout css style to be interpreted by outlook
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
		<th:block style="color; #787878; font-size:10px;margin-top:10px; display: inline-block;" th:each="shareLink : ${arrayFileLinks}" >
    		<div style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
				<!--[if mso]>
					&nbsp;&nbsp;
				<![endif]-->
				<a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
    				<span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">test-file.jpg</span>
				</a>
    			<span data-th-if="(${!shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #787878;"></span>
    			<span data-th-if="(${shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #00b800;"></span>
			</div>
    		<ul style="padding: 5px 17px; margin: 0;list-style-type:disc;" >
 				<th:block  th:each="recipientData: ${shareLink.shares}">
   					<th:block data-th-if="(${!recipientData.downloaded})" >
      					<li style="color:#787878;font-size:15px;"  >
      						<th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        						<span style="color:#7f7f7f;font-size:13px;">
          							<th:block  data-th-utext="${recipientData.firstName}"/>
      								<th:block data-th-utext="${recipientData.lastName}"/>
       							</span>
     						</th:block>
      						<span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"data-th-if="(${#strings.isEmpty(recipientData.lastName)})">able.cornell@linshare.com </span>
      					</li>
   					</th:block>
					<th:block data-th-if="(${recipientData.downloaded})">
   						<li style="color:#00b800;font-size:15px;" >
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
</th:block>
</div>' WHERE id IN (select id from mail_layout);


--Fix Upload request's activation email notification

UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="${body}" style="white-space: pre-line;">
                  Hi Amy,<br>
                  As agreed,  could you send me the report. Feel free to contact me if need be. <br/>Best regards, Peter.
                  </span>
               </div>
            </th:block>
            <!--/* End of customized message */-->
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                   <th:block data-th-if="(${!request.wasPreviouslyCreated})">
                       <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                          Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                       </span>
                   </th:block>
                    <th:block data-th-if="(${request.wasPreviouslyCreated})">
                       <span data-th-text="#{msgAlt(${requestOwner.firstName} , ${requestOwner.lastName})}"> Peter Wilson''s Upload Request depot is now activated..</span>
                     </th:block>
                     <br/>
                     <!--/* Check if the external user has a password protected file share */-->
                     <span data-th-if="(${!protected})">
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                     </span>
                     <span data-th-if="(${protected})">
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                     </span>
                  </p>
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
            <div data-th-if="(${protected})">
               <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
           <div data-th-if="(${totalMaxDepotSize})">
                    <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers dans le dépôt : <b>{2}</b>.
msgAlt = L''''invitation de {0} {1} est désormais active.
msgFrom = Le message de
msgProtected = Vous pouvez déverrouiller le dépôt en suivant le lien ci-dessous et en saisissant le mot de passe fourni.
msgUnProtected = Vous pouvez y accéder en suivant le lien ci-dessous.
name = {0} {1}
password = Mot de passe
recipientsOfDepot = Destinataires
subject = {0} {1} vous invite à déposer des fichiers dans le dépôt : {2}',messages_english='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> invited you to its upload request : <b>{2}</b>.
msgFrom = Message from
msgAlt = The upload request from {0} {1} is now active.
msgProtected = Unlock it by following the link below and entering the password.
msgUnProtected = Access it by following the link below.
name = {0} {1}
password = Password
recipientsOfDepot = Recipients
subject = {0} {1} invited you to its upload request : {2}',messages_russian='buttonMsg = Доступ
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
subject = {0} {1}  пригласил вас в свой запрос загрузки {2}' WHERE  mail_content_type = 16;


-- End of your requests

-- LinShare version
SELECT ls_version();
COMMIT;
