-- Postgresql migration script template

-- Migration script to upgrade from LinShare 6.3.0 to LinShare 6.3.1.

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'6.4.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '6.4.0';
	DECLARE version_from VARCHAR := '6.3.0';
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

-- update tables

UPDATE mail_layout SET messages_french='common.availableUntil = Expire le
common.byYou= | Par vous
common.download= Télécharger
common.filesInShare=Fichiers joints
common.recipients = Destinataires
common.titleSharedThe= Partagé le
date.format=d MMMM, yyyy
date.formatWithHours=d MMMM, yyyy HH:mm
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
date.formatWithHours= MMMM d, yyyy HH:mm
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
date.formatWithHours= d MMMM, yyyy HH:mm
productCompagny= Linagora
productName=LinShare
workGroupRightAdminTitle = Администратор
workGroupRightWirteTitle = Автор
workGroupRightContributeTitle = Редактор
workGroupRightReadTitle = Читатель
workSpaceRoleAdminTitle = WorkSpace: Администратор
workSpaceRoleWriteTitle = WorkSpace: Автор
workSpaceRoleReadTitle = WorkSpace: Читатель
welcomeMessage = Здравствуйте {0},',messages_vietnamese='common.availableUntil = Ngày hết hạn
common.byYou= Bởi bạn
common.download= Tải xuống
common.filesInShare = Tài liệu đính kèm
common.recipients = Người nhận
common.titleSharedThe= NGày tạo
date.format= d MMMM, yyyy
date.formatWithHours= d MMMM, yyyy HH:mm
productCompagny= Linagora
productName=LinShare
workGroupRightAdminTitle = Quản trị viên
workGroupRightWirteTitle = Người viết
workGroupRightContributeTitle = Người đóng góp
workGroupRightReadTitle = Người đọc
workSpaceRoleAdminTitle = WorkSpace: Quản trị viên
workSpaceRoleWriteTitle = WorkSpace: Người viết
workSpaceRoleReadTitle = WorkSpace: Người đọc
welcomeMessage = Xin chào {0},',layout='<!DOCTYPE html>
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

<!--/* Common date with hours display style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoDateAreaWithHours(titleInfo,contentInfo)">
  <div data-th-if="${contentInfo != null}">
    <span style="font-weight:bold;" data-th-text="${titleInfo}">Shared the </span>
    <br />
   <span th:with="dfwh=#{date.formatWithHours}" data-th-text="${#dates.format(contentInfo,dfwh)}">7th of November, 2018 at 10:30</span>
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

UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(body)}">
      <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
        </span>
        <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
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
                 <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                </span>
 <span th:with="df=#{customDate}" data-th-text="${#dates.format(request.activationDate,df)}">7th of November, 2018</span>
        </p>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
  <div data-th-if="${!#strings.isEmpty(request.activationDate)}">
               <th:block data-th-replace="layout :: infoDateAreaWithHours(#{activationDate},${request.activationDate})"/>
            </div>
     <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateAreaWithHours(#{closureDate},${request.expirationDate})"/>
            </div>
       <div data-th-if="(${totalMaxDepotSize})">
               <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
         </div>
  <div data-th-if="(${isCollective})">
         <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>',messages_french='activationDate = Ouverture du dépôt le
closureDate = Date de clôture
customDate= d MMMM yyyy.
depotSize = Taille autorisée
mainMsg = <b>{0} {1}</b> a créé une Invitation de dépôt <b>{2}</b>, qui sera ouverte le
msgFrom = Le message de
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} vous a créé une Invitation de Dépôt',messages_english='activationDate = Activation date
closureDate = Closure date
customDate= MMMM d, yyyy.
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> has invited you to access to his Upload Request <b>{2}</b>, sets to open
msgFrom = Message from
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} has sent an invitation to access to his Upload Request.',messages_russian='activationDate = Дата активации
closureDate = Дата закрытия
customDate= MMMM d, yyyy.
depotSize = Допустимый размер
mainMsg = <b>{0} {1}</b> открыл для вас доступ к его запросу загрузки <b>{2}</b>, созданному
msgFrom = Сообщение от
name = {0} {1}
recipientsOfDepot = Получатели
subject = {0} {1} открыл для вас доступ к его запросу загрузки.',messages_vietnamese='activationDate = Ngày kích hoạt
closureDate = Ngày đóng
customDate= MMMM d, yyyy.
depotSize = Dung lượng cho phép
mainMsg = <b>{0} {1}</b> đã mời bạn truy cập Yêu cầu tải lên của anh ấy <b>{2}</b>, sẽ được mở vào
msgFrom = Tin nhắn từ
name = {0} {1}
recipientsOfDepot = Người nhận
subject = {0} {1} đã gửi lời mời truy cập Yêu cầu tải lên của anh ấy.' WHERE id=20;

UPDATE mail_content SET subject='[( #{subject(${requestRecipient.mail},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <section id="main-content">
    <!--/* Upper main-content*/-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${isCollective})" data-th-utext="#{collectiveBeginningMainMsg(${requestRecipient.mail},${subject})}"></span>
          <span data-th-if="!(${isCollective})"
                data-th-utext="#{individualBeginningMainMsg(${requestRecipient.mail},${subject})}"></span>
          <span data-th-if="(${documentsCount} == 1)" data-th-utext="#{endingMainMsgSingular}"></span>
          <span data-th-if="(${documentsCount} > 1)" data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${isCollective})">
       <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{fileSize}, ${totalSize})"/>
    <th:block data-th-if="(${request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{uploadedOverTotal(${documentsCount},${request.authorizedFiles})})"/>
    </th:block>
    <th:block data-th-if="(${!request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{totalUploaded(${documentsCount})})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoDateAreaWithHours(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateAreaWithHours(#{invitationClosureDate},${request.expirationDate})"/>
  </section> <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='endingMainMsgPlural =  Il y a <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = Il y a  <b>1 fichier </b> dans le dépôt.
filesInURDepot =  Fichiers déposés
fileSize =  Taille
collectiveBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt <b>{1}</b>.
invitationClosureDate = Date de clôture
invitationCreationDate = Date d''''activation
numFilesInDepot = Nombre de fichiers déposés
recipientsURequest = Destinataires
subject = {0} a clôturé votre invitation de dépôt : {1}
individualBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt <b>{1}</b>.
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} files',messages_english='endingMainMsgPlural = There are a total of <b> {0} files </b> in the upload request.
endingMainMsgSingular =  There is <b>1 file </b> in the upload request.
filesInURDepot = Files uploaded
fileSize =  Total filesize
collectiveBeginningMainMsg = <b>{0}</b> has closed your collective Upload Request <b>{1}</b>.
invitationClosureDate = Closure date
invitationCreationDate = Activation date
numFilesInDepot = Total uploaded files
recipientsURequest = Recipients
subject =  {0} has closed your Upload Request: {1}
individualBeginningMainMsg  = <b>{0}</b> has closed your Upload Request <b>{1}</b>.
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files',messages_russian='endingMainMsgPlural = Всего в хранилище <b> {0} файлов </b>.
endingMainMsgSingular =  Всего в хранилище <b>1 файл </b.
filesInURDepot = Файлы загружены
fileSize =  Общий размер файла
collectiveBeginningMainMsg = <b>{0}</b> закрыл ваше групповое хранилище для файлов запроса загрузки <b>{1}</b>.
invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
numFilesInDepot = Всего загруженных файлов
recipientsURequest = Получатели
subject =  {0} закрыл ваше хранилище для файлов запроса загрузки {1}
individualBeginningMainMsg  = <b>{0}</b> закрыл ваше хранилище для файлов запроса загрузки <b>{1}</b>.
uploadedOverTotal = {0} / {1} файлов
totalUploaded = {0} файлов' ,messages_vietnamese='endingMainMsgPlural = Có tổng số <b> {0} files </b> trong yêu cầu tải lên.
endingMainMsgSingular = Có <b>1 file </b> in trong yêu cầu tải lên.
filesInURDepot = Files được tải lên
fileSize =  Tổng dung lượng file.
collectiveBeginningMainMsg = <b>{0}</b> đã đóng yêu cầu tải lên chung của bạn <b>{1}</b>.
invitationClosureDate = Ngày đóng
invitationCreationDate = Ngày kích hoạt
numFilesInDepot = Tổng số file đã tải lên
recipientsURequest = Người nhận
subject =  {0} đã đóng yêu cầu tải của bạn: {1}
individualBeginningMainMsg  = <b>{0}</b> đã đóng yêu cầu tải của bạn <b>{1}</b>.
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id=14;

UPDATE mail_content SET subject='[( #{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has closed prematurely his Upload Request Depot labeled : subject.
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
               <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
            <div data-th-if="(${isCollective})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateAreaWithHours(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='closureDate = Date de clôture
filesInURDepot = Fichiers
mainMsg = <b>{0} {1}</b> a fermé son invitation de dépôt : {2}.
recipientsOfDepot = Destinataires
subject = {0} {1} a fermé l''''invitation de dépôt : {2}',messages_english='closureDate = Closure date
filesInURDepot = Files
mainMsg = <b>{0} {1}</b> has closed the upload request labeled : {2}.
recipientsOfDepot = Recipients
subject = {0} {1} has closed his upload request : {2}',messages_russian='closureDate = Дата закрытия
filesInURDepot = Файлы
mainMsg = <b>{0} {1}</b> закрыл запрос загрузки {2}.
recipientsOfDepot = Получатели
subject = {0} {1} закрыл запрос загрузки {2}',messages_vietnamese='closureDate = Ngày đóng
filesInURDepot = Files
mainMsg = <b>{0} {1}</b> đã đóng yêu cầu tải được dán nhãn : {2}.
recipientsOfDepot = Recipients
subject = {0} {1} đã đóng yêu cầu tải của anh  : {2}' WHERE id=21;

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
                       <span data-th-text="#{msgAlt(${subject} , ${requestOwner.firstName} , ${requestOwner.lastName})}"> Peter Wilson''s Upload Request is now activated..</span>
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
               <th:block data-th-replace="layout :: infoDateAreaWithHours(#{closureDate},${request.expirationDate})"/>
            </div>
           <div data-th-if="(${totalMaxDepotSize})">
                    <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="(${isCollective})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers via cette Invitation de Dépôt: <b>{2}</b>.
msgAlt = L''''invitation de dépôt {0} de {1} {2} est désormais active.
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
msgAlt = The upload request {0} from {1} {2} is now active.
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
msgAlt = Репозиторий {0} из {1} {2} теперь активен.
msgProtected = Разблокируйте его, перейдя по ссылке ниже и введя пароль.
msgUnProtected = Получите доступ, перейдя по ссылке ниже.
name = {0} {1}
password = Пароль
recipientsOfDepot = Получатель
subject = {0} {1}  пригласил вас в свой запрос загрузки {2}',messages_vietnamese='buttonMsg = Truy cập
closureDate = Ngày đóng
depotSize = Dung lượng cho phép
mainMsg = <b>{0} {1}</b> đã mời bạn vào yêu cầu tải : <b>{2}</b>.
msgFrom = Tin nhắn từ
msgAlt = Yêu cầu tải {0} từ {1} {2} được kích hoạt bây giờ.
msgProtected = mở khóa bằng việc mở đường dẫn dưới đây và điền mật khẩu
msgUnProtected = Truy cập bằng việc mở đường dẫn dưới đây
name = {0} {1}
password = Mật khẩu
recipientsOfDepot = Người nhận
subject = {0} {1} đã mời bạn vào yêu cầu tải : {2}' WHERE id=16;

UPDATE mail_content SET subject='[(#{subject(${subject})})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${request.subject})}">
                      Your Upload Request repository labeled $subject is now activated.
                     </span>
                     <span data-th-text="#{msgLink}">In order to access it click the link below.</span>
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
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateAreaWithHours(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="(${isCollective})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
             <div data-th-if="(${totalMaxDepotSize})">
                   <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille autorisée
mainMsg = Votre dépôt intitulé <b>{0}</b> est désormais actif.
msgLink = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
recipientsOfDepot = Destinataires
subject = Votre invitation de dépôt {0} est désormais active',messages_english='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = Your Upload Request labeled <b>{0}</b> is now active.
msgLink = Access it by following the link below.
recipientsOfDepot = Recipients
subject = Your Upload Request : {0}, is now active',messages_russian='buttonMsg = Доступ
closureDate = Дата закрытия
depotSize = Допустимый размер
mainMsg = Ваш запрос загрузки <b>{0}</b> активен.
msgLink = Получите доступ к нему, перейдя по ссылке ниже.
recipientsOfDepot = Получатель
subject = Ваш запрос загрузки {0} активен',messages_vietnamese='buttonMsg = Truy cập
closureDate = Ngày đóng
depotSize = Dung lượng cho phép
mainMsg = Yêu cầu tải của bạn với tên <b>{0}</b> được kích hoạt bây giờ.
msgLink = Truy cập bằng cách mở đường dẫn dưới đây.
recipientsOfDepot = Người nhận
subject = Yêu cầu tải của bạn: {0}, được kích hoạt ' WHERE id=17;


---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();


COMMIT;
