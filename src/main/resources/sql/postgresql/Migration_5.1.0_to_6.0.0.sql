-- Postgresql migration script template

-- Migration script to upgrade from LinShare CHANGE_ME to LinShare CHANGE_ME. 

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions 

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'6.0.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	-- TODO: CHANGE THE VERSIONS
	DECLARE version_to VARCHAR := '6.0.0';
	DECLARE version_from VARCHAR := '5.1.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE nb_upgrade_tasks INT := (SELECT count(*)::int  FROM upgrade_task WHERE status != 'SUCCESS' AND status != 'SKIPPED' AND priority != 'OPTIONAL');
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

ALTER TABLE document ADD COLUMN human_mime_type varchar(255) NOT NULL DEFAULT 'others';
ALTER TABLE document_entry ADD COLUMN human_mime_type varchar(255) NOT NULL DEFAULT 'others';
ALTER TABLE upload_request_entry ADD COLUMN human_mime_type varchar(255) NOT NULL DEFAULT 'others';
ALTER TABLE thread_entry ADD COLUMN human_mime_type varchar(255) NOT NULL DEFAULT 'others';



-- UPGRADE_5_2_ADD_STATISTIC_DATE_TO_EXISTING_STAT_RECORDS
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
    (66,
     'UNDEFINED',
     'UPGRADE_6_0_ADD_STATISTIC_DATE_TO_EXISTING_STAT_RECORDS',
     'UPGRADE_6_0',
     66,
     'NEW',
     'REQUIRED',
     now(),
     now());

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
    (67,
     'UNDEFINED',
     'UPGRADE_6_0_ADD_HUMAN_MIME_TYPE_TO_EXISTING_STAT_RECORDS',
     'UPGRADE_6_0',
     67,
     'NEW',
     'REQUIRED',
     now(),
     now());


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
          style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px"
          width="90%">
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
                                              <td width="15"
                                                style="mso-line-height-rule: exactly; line-height: 9px; border-top:1px solid #c9cacc;">
                                                &nbsp;</td>
                                              <td width="20" style="mso-line-height-rule: exactly; line-height: 9px;">
                                                <img src="cid:logo.arrow@linshare.org" width="20" height="9" border="0"
                                                  style="display:block;" alt="down arrow" />
                                              </td>
                                              <td
                                                style="mso-line-height-rule: exactly; line-height: 9px; border-top:1px solid #c9cacc;">
                                                &nbsp;</td>
                                            </tr>
                                          </tbody>
                                        </table>
                                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                          <tbody>
                                            <tr>
                                              <td
                                                style="font-size:14px;padding: 0px 17px;background: #f8f8f8;text-align:left;color:#7f7f7f;line-height:20px;">
                                                <div align="left"
                                                  style="font-size:13px;line-height:20px;margin:0;padding: 15px 0 20px;">
                                                  <th:block data-th-replace="${bottomSecondaryContentArea}" />
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
                                              ©&nbsp;2009–2022. Contribute to
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
<div style="margin-bottom:17px;"
  data-th-fragment="infoItemsToUpdate(editedInfoMsg, addedInfoMsg, deletedInfoMsg, oldValue, newValue)">
  <span>
    <th:block th:if="${oldValue != null} AND ${newValue} != null">
      <th:block data-th-replace="layout :: infoEditedItem(${editedInfoMsg}, ${oldValue}, ${newValue})" />
    </th:block>
    <th:block th:if="${oldValue == null} AND ${newValue} != null">
      <th:block data-th-replace="layout :: infoAddedItem(${addedInfoMsg}, ${newValue})" />
    </th:block>
    <th:block th:if="${oldValue != null} AND ${newValue} == null">
      <th:block data-th-replace="layout :: infoDeletedItem(${deletedInfoMsg}, ${oldValue})" />
    </th:block>
  </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoEditedItem(titleInfo, oldValue, newValue)">
  <span style="font-weight:bold;">
    <th:block th:replace="${titleInfo}" />
  </span>
  <br />
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
  <span style="font-weight:bold;">
    <th:block th:replace="${titleInfo}" />
  </span>
  <br />
  <span>
    <th:block th:replace="${newValue}" />
  </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoDeletedItem(titleInfo, oldValue)">
  <span style="font-weight:bold;">
    <th:block th:replace="${titleInfo}" />
  </span>
  <br />
  <span>
    <th:block th:replace="${oldValue}" />
  </span>
</div>

<!--/* Edited  date  display settings  style */-->
<div style="margin-bottom:17px;"
  data-th-fragment="infoDateItemsToUpdate(editedInfoMsg, addedInfoMsg, deletedInfoMsg, oldValue, newValue)">
  <span>
    <th:block th:if="${oldValue != null} AND ${newValue} != null">
      <th:block data-th-replace="layout :: infoEditedDateItem(${editedInfoMsg}, ${oldValue}, ${newValue})" />
    </th:block>
    <th:block th:if="${oldValue == null} AND ${newValue} != null">
      <th:block data-th-replace="layout :: infoAddedDateItem(${addedInfoMsg}, ${newValue})" />
    </th:block>
    <th:block th:if="${oldValue != null} AND ${newValue} == null">
      <th:block data-th-replace="layout :: infoDeletedDateItem(${deletedInfoMsg}, ${oldValue})" />
    </th:block>
  </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoEditedDateItem(titleInfo, oldValue, newValue)">
  <span style="font-weight:bold;">
    <th:block th:replace="${titleInfo}" />
  </span>
  <br />
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
  <span style="font-weight:bold;">
    <th:block th:replace="${titleInfo}" />
  </span>
  <br />
  <span>
    <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(newValue, df)}" />
  </span>
</div>

<div style="margin-bottom:17px;" data-th-fragment="infoDeletedDateItem(titleInfo, oldValue)">
  <span style="font-weight:bold;">
    <th:block th:replace="${titleInfo}" />
  </span>
  <br />
  <span>
    <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue, df)}" />
  </span>
</div>

<!--/* Common header template */-->

<head data-th-fragment="header">
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
<div data-th-fragment="contentMessageSection(messageTitle,messageContent)"
  style="margin-bottom:17px;border-top: 1px solid #e1e1e1;">
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
<div data-th-fragment="infoActionLink(titleInfo,urlLink)" style="margin-bottom:17px;">
  <span style="font-weight:bold;" data-th-text="${titleInfo}">Download link title </span>
  <br />
  <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="${urlLink}" th:href="@{${urlLink}}">Link
  </a>
</div>

<!--/* Common date display  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoDateArea(titleInfo,contentInfo)">
  <div data-th-if="${contentInfo != null}">
    <span style="font-weight:bold;" data-th-text="${titleInfo}">Shared the </span>
    <br />
    <span th:with="df=#{date.format}" data-th-text="${#dates.format(contentInfo,df)}">7th of November, 2018</span>
  </div>
</div>

<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoStandardArea(titleInfo,contentInfo)">
  <div data-th-if="${contentInfo != null}">
    <span style="font-weight:bold;">
      <th:block th:replace="${titleInfo}" />
    </span>
    <br />
    <th:block th:replace="${contentInfo}" />
  </div>
</div>

<!--/* Common button action style */-->
<span data-th-fragment="actionButtonLink(labelBtn,urlLink)">
  <a style="border-radius:3px;font-size:15px;color:white;text-decoration:none;padding: 10px 7px;width:auto;max-width:50%;display:block;background-color: #42abe0;text-align: center;margin-top: 17px;"
    target="_blank" data-th-text="${labelBtn}" th:href="@{${urlLink}}">Button label</a>
</span>

<!--/* Common recipient listing for external and internal users */-->
<div style="margin-bottom:17px;" data-th-fragment="infoRecipientListingArea(titleInfo,arrayRecipients)">
  <span style="font-weight:bold;" data-th-text="${titleInfo}">Recipients</span>
  <table>
    <th:block th:each="recipientData: ${arrayRecipients}">
      <tr>
        <td style="color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>
        <td>
          <div data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
            <span style="color:#787878;font-size:13px" data-th-utext="${recipientData.mail}">
              my-file-name.pdf
            </span>
          </div>
          <div data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="${recipientData.firstName}" />
              <th:block data-th-utext="${recipientData.lastName}" />
            </span>
          </div>
        </td>
      </tr>
    </th:block>
  </table>
</div>
<div data-th-if="(${!isAnonymous})">
  <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}"
    data-th-utext="${shareLink.name}">
    my-file-name.pdf
  </a>
</div>
</div>

<!--/* Lists all file links in a share   */-->
<div style="margin-bottom:17px;" data-th-fragment="infoFileLinksListingArea(titleInfo,arrayFileLinks,isAnonymous)">
  <span style="font-weight:bold;" data-th-text="${titleInfo}">Shared the </span>

  <table>
    <th:block th:each="shareLink : ${arrayFileLinks}">
      <tr>
        <td style="color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>
        <td>
          <div data-th-if="(${!isAnonymous})">
            <a style="color:#1294dc;text-decoration:none;font-size:13px" data-th-utext="${shareLink.name}"
              th:href="@{${shareLink.href}}">
              my-file-name.pdf
            </a>
          </div>
          <div data-th-if="(${isAnonymous})">
            <a style="color:#787878;text-decoration:none;font-size:13px" data-th-utext="${shareLink.name}">
              my-file-name.pdf
            </a>
          </div>
        </td>
      </tr>
    </th:block>
  </table>
</div>
<!--/* Lists all file links in a share  and checks witch one are the recpient\s */-->
<div style="margin-bottom:17px;" data-th-fragment="infoFileListWithMyUploadRefs(titleInfo,arrayFileLinks)">
  <span style="font-weight:bold;" data-th-text="${titleInfo}">Shared the </span>

  <table>
    <th:block th:each="shareLink : ${arrayFileLinks}">
      <tr>
        <td style="color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>
        <td>
          <a style="color:#787878;text-decoration:none;font-size:13px" data-th-utext="${shareLink.name}">
            my-file-name.pdf
          </a>
          <th:block data-th-if="(${shareLink.mine})"> <span data-th-text="#{common.byYou}">| By You</span></th:block>
        </td>
      </tr>
    </th:block>
  </table>
</div>

<!--/* Lists all file links in a share along with their download status   */-->
<div data-th-fragment="infoFileListUploadState(titleInfo,arrayFileLinks)">
  <span style="font-weight:bold;" data-th-text="${titleInfo}">Shared the </span>

  <table>
    <th:block th:each="shareLink : ${arrayFileLinks}" data-th-if="(${shareLink.downloaded})">
      <tr>
        <td style="color:#00b800;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>
        <td>
          <th:block data-th-if="(${shareLink.isDownloading})">
            <a style="color:#1294dc;text-decoration:none;font-size:13px ;font-weight:bold"
              data-th-utext="${shareLink.name}">
              my-file-name.pdf
            </a>
          </th:block>
          <th:block data-th-if="(${!shareLink.isDownloading})">
            <a style="color:#1294dc;text-decoration:none;font-size:13px" data-th-utext="${shareLink.name}">
              my-file-name.pdf
            </a>
          </th:block>
        </td>
      </tr>
    </th:block>

    <th:block th:each="shareLink : ${arrayFileLinks}" data-th-if="(${!shareLink.downloaded})">
      <tr>
        <td style=" color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>
      <td>
        <a style="color:#1294dc;text-decoration:none;font-size:13px" data-th-utext="${shareLink.name}">
          my-file-name.pdf
        </a>
      </td>
      </tr>
    </th:block>
  </table>
</div>
<!--/* Lists all recpients download states per file   */-->
<div style="margin-bottom:17px;" data-th-fragment="infoFileListRecipientUpload(titleInfo,arrayFileLinks)">
  <span style="font-weight:bold;" data-th-text="${titleInfo}">Shared the </span>
  <th:block style="color: #787878; font-size:10px;margin-top:10px; display: inline-block;"
    th:each="shareLink : ${arrayFileLinks}">
    <div style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
      <!--[if mso]>
					&nbsp;&nbsp;
				<![endif]-->
      <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
        <span align="left" style="display: inline-block; width: 96%;"
          data-th-utext="${shareLink.name}">test-file.jpg</span>
      </a>
      <span data-th-if="(${!shareLink.allDownloaded})" style="color: #787878; font-size: 22px;">&bull;</span>
      <span data-th-if="(${shareLink.allDownloaded})" style="color: #00b800; font-size: 22px;">&bull;</span>
    </div>
    <table>
      <th:block th:each="recipientData: ${shareLink.shares}">
        <th:block data-th-if="(${!recipientData.downloaded})">
          <tr>
            <td style="color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>

            <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">
              <td>
                <span style="color:#7f7f7f;font-size:13px;">
                  <th:block data-th-utext="${recipientData.firstName}" />
                  <th:block data-th-utext="${recipientData.lastName}" />
                </span>
              </td>
            </th:block>
            <th:block data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
              <td>
                <span style="color:#7f7f7f;font-size:13px;"
                  data-th-utext="${recipientData.mail}">able.cornell@linshare.com </span>
              </td>
            </th:block>
          </tr>
        </th:block>

        <th:block data-th-if="(${recipientData.downloaded})">
          <tr>
            <td style="color:#00b800;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>

            <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">
              <td>
                <span style="color:#7f7f7f;font-size:13px;">
                  <th:block data-th-utext="${recipientData.firstName}" />
                  <th:block data-th-utext="${recipientData.lastName}" />
                </span>
              </td>
            </th:block>
            <th:block data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
              <td>
                <span style="color:#7f7f7f;font-size:13px;"
                  data-th-utext="${recipientData.mail}">able.cornell@linshare.com </span>
              </td>
            </th:block>
          </tr>
        </th:block>
      </th:block>
    </table>
  </th:block>
</div>' WHERE id=1;

---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();

COMMIT;
