-- Postgresql migration script template

-- Migration script to upgrade from LinShare 6.1.0 to LinShare 6.1.1.

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions 

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'6.1.1', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '6.1.1';
	DECLARE version_from VARCHAR := '6.1.0';
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

ALTER TABLE domain_policy ADD COLUMN creation_date timestamp without time zone;
ALTER TABLE domain_policy ADD COLUMN modification_date timestamp without time zone;

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
</html>',messages_french='activationDate = Date d''''activation
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
activationDateParamAdded = Paramètre de date d''''activation ajouté
activationDateParamDeleted = Paramètre de date d''''activation annulé, ancienne valeur
expiryDateParamAdded = Paramètre d''''expiration ajouté
expiryDateParamDeleted = Paramètre d''''expiration annulé, ancienne valeur',messages_english='activationDate = Activation date
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
buttonMsg = Доступ
maxFileSizeParamAdded = Добавлен параметр максимального размера файла
maxFileSizeParamDeleted = Параметр максимального размера файла удален, последнее значение
maxFileNumParamAdded = Добавлен параметр максимального количества файлов
maxFileNumParamDeleted = Параметр максимального количества файлов удален, последнее значение
totalMaxDepotSizeParamAdded = Добавлен параметр максимального общего размера депозита
totalMaxDepotSizeParamDeleted = Параметр максимального общего размера депозита удален, последнее значение
deletionRightParamAdded = Добавлен параметр прав на удаление
deletionRightParamDeleted = Параметр прав на удаление отменен
closureRightParamAdded = Добавлен параметр прав на закрытие
closureRightParamDeleted = Параметр прав на закрытие удален
activationDateParamAdded = Добавлен параметр даты активации
activationDateParamDeleted = Добавлен параметр даты активации, последнее значение
expiryDateParamAdded = Добавлен параметр срока действия
expiryDateParamDeleted = Параметр срока действия удален, последнее значение ',messages_vietnamese='activationDate = Ngày kích hoạt
closureRight = Quyền đóng
deletionRight = Quyền xóa
depotSize = kích cỡ thư mục
expiryDate = NGày đóng
enableNotification = Bật thông báo
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  đã cập nhật một số cài đặt liên quan đến yêu cầu tải  <b>{2}</b>.
maxFileNum = Số filte
maxFileSize = Khích thước size
msgFrom =  Tin nhắn mới từ
name = {0} {1}
nameOfDepot: Tên yêu cầu tải
secondaryMsg = Các chỉnh sửa cài đặt được liệt kê dưới đây.
subject = Các cài đặt của yêu cầu tải đã được chỉnh sửa: {0}
buttonMsg = Truy cập
maxFileSizeParamAdded = Tham số dung lượng file tối đa đã được thêm vào
maxFileSizeParamDeleted = Tham số dung lượng file tối đa đa bị hủy, giá trị cuối cùng
maxFileNumParamAdded = Tham số số lượng file tối đa đã được thêm vào
maxFileNumParamDeleted = Tham số số lượng file tối đa đã bị hủy, giá trị cuối cùng
totalMaxDepotSizeParamAdded = Tham số tổng dung lượng file tối đa đã được thêm vào
totalMaxDepotSizeParamDeleted = Tham số tổng dung lượng file tối đa đã bị hủy, giá trị cuối cùng
deletionRightParamAdded = Tham số quyền xóa đã được thêm vào
deletionRightParamDeleted = Tham số quyền xóa đã bị hủy
closureRightParamAdded = Tham số quyền đóng đã được thêm vào
closureRightParamDeleted = Tham số quyền đóng đã bị hủy, last value
activationDateParamAdded = Tham số ngày kích hoạt đã được thêm vào
activationDateParamDeleted = Tham số ngày kích hoạt đã bị hủy, giá trị cuối cùng
expiryDateParamAdded = Tham số ngày hết hạn đã được thêm vào
expiryDateParamDeleted = Tham số ngày hết hạn đã bị hủy, giá trị cuối cùng' WHERE id=23;

UPDATE mail_content SET subject='[( #{subject(${requestRecipient.mail},${document.name},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg(${requestRecipient.mail})}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >
                  filename.ext
             </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${requestRecipient.mail})}"></span>
          <th:block   data-th-replace="layout :: actionButtonLink(#{buttonLabel},${requestUrl})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{fileUploadedThe},${document.creationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{fileSize},${document.size})"/>
 <th:block data-th-if="(${request.authorizedFiles})">
      <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
         #{uploadedOverTotal(${request.uploadedFilesCount},${request.authorizedFiles})})"/>
 </th:block>
 <th:block data-th-if="(${!request.authorizedFiles})">
      <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
         #{totalUploaded(${request.uploadedFilesCount})})"/>
 </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='endingMainMsg = dans votre Invitation de Dépôt.
fileSize =  Taille du fichier
buttonLabel = Voir
fileUploadedThe= Fichier déposé le
invitationClosureDate = Date de clôture
invitationCreationDate = Date d''''activation
beginningMainMsg = <b> {0} </b> vous a déposé le fichier
numFilesInDepot = Nombre de fichiers déposés
subject =  {0}  vous a déposé {1}  dans votre Invitation de Dépôt
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} fichiers',messages_english='endingMainMsg = in your Upload Request
fileSize =  File size
buttonLabel = View
fileUploadedThe = Upload date
invitationClosureDate = Closure date
invitationCreationDate = Activation date
beginningMainMsg =  <b> {0} </b> has uploaded the file
numFilesInDepot = Total uploaded files
subject =  {0}  has uploaded {1}  in your Upload Request
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files',messages_russian='endingMainMsg = в вашем запросе загрузки
fileSize = Размер файла
buttonLabel = Просмотр
fileUploadedThe = Дата загрузки
invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
beginningMainMsg =  <b> {0} </b> загрузил файл
numFilesInDepot = Всего загруженных файлов
subject =  {0}  загрузил {1}  в ваш запрос загрузки
uploadedOverTotal = {0} / {1} файлы
totalUploaded = {0} файлы',messages_vietnamese='endingMainMsg = trong yêu cầu tải lên của bạn
fileSize =  Kích cỡ file
buttonLabel = Xem
fileUploadedThe = Ngày tải lên
invitationClosureDate = Ngày đóng
invitationCreationDate = Ngày kích hoạt
beginningMainMsg =  <b> {0} </b> đã tải lên file
numFilesInDepot = Tổng số file tải lên
subject =  {0}  đã tải lên {1}  trong yêu cầu tải của bạn
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id=10;

UPDATE mail_content SET subject='[( #{subject(${actor.firstName},${actor.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${actor.firstName},${actor.lastName},${guest.firstName},${guest.lastName},${role})}"></span>
          <!--/* Access button to guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${guestLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
        <th:block data-th-replace="layout :: infoStandardArea(#{guestNameTitle},${guest.mail})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a ajouté comme modérateur d''''invité
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a ajouté comme modérateur de <b>{2}</b> <b>{3}</b> avec <b>{4}</b> role.
guestNameTitle = Invité',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} added you as guest moderator
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you as a guest moderator for <b>{2}</b> <b>{3}</b> with <b>{4}</b> role.
guestNameTitle = Guest',
messages_russian='accessToLinshareBTn = Доступ
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> добавил вас в качестве приглашенного модератора в <b>{2}</b> <b>{3}</b> с ролью <b>{4}</b>.
subject = {0} {1} добавил вас в качестве приглашенного модератора
guestNameTitle = Гость',
messages_vietnamese='accessToLinshareBTn = Truy cập
subject = {0} {1} đã thêm bạn làm người giám sát tài khoản khách
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã thêm bạn làm quản trị viên của khách <b>{2}</b> <b>{3}</b> với <b>{4}</b> quyền.
guestNameTitle = Khách' WHERE id=41;

UPDATE mail_content SET subject='[( #{subject(${actor.firstName},${actor.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${actor.firstName},${actor.lastName},${guest.firstName},${guest.lastName})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
        <th:block data-th-replace="layout :: infoStandardArea(#{guestNameTitle},${guest.mail})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a supprimé de la liste des modérateurs d''''invité
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a supprimé de la liste des modérateurs de <b>{2}</b> <b>{3}</b>.
guestNameTitle = Invité',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} deleted you from guest moderator''''s list
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> deleted you from moderators list of <b>{2}</b> <b>{3}</b>.
guestNameTitle = Guest',
messages_russian='accessToLinshareBTn = Доступ
subject = {0} {1} удалил вас из списка приглашенных модераторов
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил вас из списка приглашенных модераторов в <b>{2}</b> <b>{3}</b>.
guestNameTitle = Гость',
messages_vietnamese='accessToLinshareBTn = Truy cập
subject = {0} {1} đã xóa bạn khỏi danh sách quản trị
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã xóa bạn khỏi danh sách quản trị của <b>{2}</b> <b>{3}</b>.
guestNameTitle = Khách' WHERE id=43;

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
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
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


---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();

COMMIT;
