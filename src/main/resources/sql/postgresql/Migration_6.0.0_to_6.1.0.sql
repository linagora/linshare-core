-- Postgresql migration script template

-- Migration script to upgrade from LinShare 6.0.0 to LinShare 6.1.0.

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions 

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'6.1.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '6.1.0';
	DECLARE version_from VARCHAR := '6.0.0';
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

ALTER TABLE mail_layout ADD COLUMN messages_vietnamese text;
ALTER TABLE mail_footer ADD COLUMN messages_vietnamese text;
ALTER TABLE mail_content ADD COLUMN messages_vietnamese text;

-- mail contents

UPDATE mail_content SET subject='[(#{subject})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${recipient.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                      <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${recipient.firstName} AND ${recipient.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                       <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} == ${recipient.firstName} AND ${recipient.lastName} == ${owner.lastName})"
                                      data-th-utext="#{mainMsgOwner(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                  </p>
                      <span data-th-utext="#{endMsg}"></span>
                      <span>
                             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{tokenLinkEndOfLine(${jwtTokenLink})}" th:href="@{${jwtTokenLink}}" >
                            </a>
                     </span>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
           <th:block data-th-replace="layout :: infoStandardArea(#{tokenLabel},${label})"/>
           <th:block data-th-replace="layout :: infoDateArea(#{tokenCreationDate},${creationDate})"/>
           <div data-th-if="${!#strings.isEmpty(description)}">
             <th:block data-th-replace="layout :: infoStandardArea(#{tokenDescription},${description})"/>
           </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='subject = Création d''''un jeton d''''accès permanent
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a créé un jeton d''''accès permanent: {2}, pour votre compte.
mainMsgOwner = Vous vous avez créé un jeton d''''accès permanent : {2},pour votre compte.
tokenCreationDate = Date de création
endMsg = Vous pouvez consulter les jetons d''''accès liés à votre compte
tokenLinkEndOfLine = ici
tokenLabel = Nom
tokenDescription = Description',messages_english='subject = Creation of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a permanent authentication token: {2}, for your account.
mainMsgOwner = You have created a permanent authentication token: {2}, for your account.
tokenCreationDate = Creation date
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Name
tokenDescription = Description',messages_russian='subject = Создание постоянного токена аутентификации
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> создал постоянный токен аутентификации: {2},для вашей учетной записи.
mainMsgOwner = Вы создали постоянный токен аутентификации: {2}, для своего аккаунта.
tokenCreationDate = Дата создания
endMsg = Вы можете просмотреть все активные токены вашего аккаунта
tokenLinkEndOfLine = здесь
tokenLabel = Имя
tokenDescription = Описание' ,messages_vietnamese='subject = Tạo một mã xác thực vĩnh viễn.
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã tạo một mã xác thực vĩnh viễn: {2}, cho tài khoản của bạn.
mainMsgOwner = Bạn đã tạo một mã xác thực vĩnh viễn: {2}, cho tài khoản của bạn.
tokenCreationDate = Ngày tạo
endMsg = Bạn có thể xem lại và kích hoạt các mã xác thực của tài khoản của bạn
tokenLinkEndOfLine = ở đây
tokenLabel = Tên
tokenDescription = Mô tả' WHERE id=32;
UPDATE mail_content SET subject='[(#{subject})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${recipient.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                      <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${recipient.firstName} AND ${recipient.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                       <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} == ${recipient.firstName} AND ${recipient.lastName} == ${owner.lastName})"
                                      data-th-utext="#{mainMsgOwner(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                     </span>
                  </p>
                      <span data-th-utext="#{endMsg}"></span>
                      <span>
                             <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="#{tokenLinkEndOfLine(${jwtTokenLink})}" th:href="@{${jwtTokenLink}}" >
                            </a>
                     </span>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
           <th:block data-th-replace="layout :: infoStandardArea(#{tokenLabel},${label})"/>
           <th:block data-th-replace="layout :: infoDateArea(#{tokenCreationDate},${creationDate})"/>
           <div data-th-if="${!#strings.isEmpty(description)}">
             <th:block data-th-replace="layout :: infoStandardArea(#{tokenDescription},${description})"/>
           </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='subject = Suppression d''''un jeton d''''accès permanent
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a supprimé un jeton d''''accès permanent: {2}, pour votre compte.
mainMsgOwner = Vous avez supprimé un jeton d''''accès permanent: {2}, pour votre compte.
tokenCreationDate = Date de création
endMsg = Vous pouvez consulter les jetons d''''accès liés à votre compte
tokenLinkEndOfLine = ici
tokenLabel = Nom
tokenDescription = Description
tokenIdentifier = Identifiant',messages_english='subject = Deletion of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has deleted a permanent authentication token: {2}, for your account.
mainMsgOwner = You have deleted a permanent authentication token: {2}, for your account.
tokenCreationDate = Creation date
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Name
tokenDescription = Description',messages_russian='subject = Удаление постоянного токена аутентификации
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил постоянный токен аутентификации: {2}, для вашего аккаунта.
mainMsgOwner = Вы удалили постоянный токен аутентификации: {2}, для вашего аккаунта.
tokenCreationDate = Дата создания
endMsg = Вы можете просмотреть все активные токены вашего аккаунта
tokenLinkEndOfLine = здесь
tokenLabel = Имя
tokenDescription = Описание',messages_vietnamese='subject = Xóa mã xác thực vĩnh viễn.
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã xóa mã xác thực vĩnh viễn: {2}, cho tài khoản của bạn.
mainMsgOwner = Bạn đã xóa mã xác thực vĩnh viễn: {2}, cho tài khoản của bạn.
tokenCreationDate = Ngày tạo
endMsg = Bạn có thể xem và kích hoạt các mã của tài khoản của bạn
tokenLinkEndOfLine = tại đây
tokenLabel = Tên
tokenDescription = Mô tả' WHERE id=33;
UPDATE mail_content SET subject='[( #{subject(${document.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head  data-th-replace="layout :: header"></head>
  <body>
    <div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <section id="main-content">
      <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
        <div id="section-content">
          <!--/* Greetings */-->
            <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>
          <!--/* End of Greetings */-->
          <!--/* Main email  message content*/-->
          <p>
     <span  data-th-utext="#{beginningMainMsgInt}"></span>
            <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >
                  filename.ext
              </a>
          </span>
  <span  data-th-utext="#{endingMainMsgInt(${daysLeft})}">  </span>
           <!--/* Single download link for external recipient */-->
            <th:block   data-th-replace="layout :: actionButtonLink(#{common.download},${document.href})"/>
          </p> <!--/* End of Main email  message content*/-->
        </div><!--/* End of section-content*/-->
      </div><!--/* End of main-content container*/-->
    </section> <!--/* End of main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
      <th:block data-th-replace="layout :: infoDateArea(#{uploadedThe},${document.creationDate})"/>
      <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${document.expirationDate})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
  </body>
</html>',messages_french='beginningMainMsgInt =  Votre fichier
endingMainMsgInt = sera automatiquement supprimé dans <b> {0} jours</b> de votre Espace Personnel.
subject = Le fichier {0} va bientôt être supprimé
uploadedThe = Déposé le',messages_english='beginningMainMsgInt = Your file
endingMainMsgInt = will automatically be deleted in <b> {0} days</b> from your Personal Space.
subject = The file {0} is about to be deleted
uploadedThe = Upload date',messages_russian='beginningMainMsgInt = Ваш файл
endingMainMsgInt = будет автоматически удален через <b> {0} дней</b> из вашего личного пространства.
subject = Файл {0} будет удален
uploadedThe = Дата загрузки',messages_vietnamese='beginningMainMsgInt = Tài liệu của bạn
endingMainMsgInt = sẽ tự động bị xóa trong <b> {0} ngày </b> từ Không gian cá nhân của bạn.
subject = File {0} sắp bị xóa.
uploadedThe = Ngày tải lên' WHERE id=1;
UPDATE mail_content SET subject='[( #{subject(${creator.firstName},${creator.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${creator.firstName},${creator.lastName},#{productName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${resetLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{activationLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Activer mon compte
accountExpiryDateTitle = Date d''''''expiration
activationLinkTitle = Lien d''''initialisation
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a créé un compte invité sur <b>LinShare</b> qui vous permet de partager des fichiers de façon sécurisée. <br/> Pour vous connecter, vous devez finaliser votre inscription en créant votre mot de passe à l''''aide du lien  ci-dessous.
subject = {0}  {1} vous invite a activer votre compte
userNameTitle = Identifiant',messages_english='accessToLinshareBTn = Activate account
accountExpiryDateTitle = Account expiry date
activationLinkTitle = Initialization link
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a <b>{2}</b> guest account for you, which enables you to transfer files more securely. <br/>To log into your account, you will need to finalize your subscription by creating your password, using the following link.
subject = {0}  {1} invited you to activate your {2} account
userNameTitle = Username',messages_russian='accessToLinshareBTn = Активировать аккаунт
accountExpiryDateTitle = Срок действия аккаунта
activationLinkTitle = Ссылка активации
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> создал гостевой аккаунт <b>{2}</b>  для вас, который позволяет надежно обмениваться файлами. <br/>Для входа в ваш аккаунт, завершите процесс регистрации, используя ссылку
subject = {0}  {1} пригласил вас активировать ваш {2} аккаунт
userNameTitle = Имя пользователя',messages_vietnamese='accessToLinshareBTn = Kích hoạt tài khoản
accountExpiryDateTitle = Ngày hết hạn tài khoản
activationLinkTitle = Đường dẫn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã tạo một <b>{2}</b> tài khoản khách cho bạn, điều này cho phép bạn trao đổi tài liệu bảo mật hơn. <br/> Để đăng nhập tài khoản của bạn, bạn cần phải tạo mật khẩu bằng đường dẫn dưới đây.
subject = {0}  {1} đã mời bạn kích hoạt {2} tài khoản
userNameTitle = Tên đăng nhập' WHERE id=8;
UPDATE mail_content SET subject= '[( #{subject})]',
body= '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p data-th-utext="#{mainTitle}"><p>
          <br/>
        <span data-th-utext="#{additionalMsg}"></span>
        <br/>
          <b>NB:</b> <span data-th-utext="#{noteMsg}"></span>
        </p><br/>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block data-th-replace="layout :: actionButtonLink(#{changePasswordBtn},${resetLink})"/>
        <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{resetLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{urlExpiryDateTitle},${urlExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',
messages_french= 'urlExpiryDateTitle = Date d''''expiration de l''''URL
additionalMsg = Vous pouvez également, utiliser le formulaire de mot de passe perdu pour accomplir cette tache.
noteMsg = Ce lien est utilisable une seule fois et sera valide pendant 1 semaine.
changePasswordBtn = Réinitialiser
mainTitle = Afin de renforcer la sécurité de votre compte, vous devez changer le mot de passe de votre compte LinShare. Toute connexion sera impossible tant que cette étape ne sera pas réalisée.
resetLinkTitle = Lien de réinitialisation
subject =  Mise à jour de sécurité
userNameTitle = Identifiant',
messages_english= 'urlExpiryDateTitle = URL expiry date
additionalMsg = You can also use the reset password form to do this task.
noteMsg = This link can be used only once and will be valid for 1 week.
changePasswordBtn = Change password
mainTitle = In order to enhance the security of your account, you must change your password to your LinShare account. Any connection will be forbidden until this step is not carried out.
resetLinkTitle = LinShare reset password link
subject =  Security update
userNameTitle = Username',
messages_russian= 'urlExpiryDateTitle = Истечение срока действия URL
additionalMsg = Вы также можете использовать форму сброса пароля для выполнения этой задачи.
noteMsg = Эта ссылка может быть использована только один раз и будет действительна в течение 1 недели.
changePasswordBtn = Изменить пароль
mainTitle = Чтобы повысить уровень безопасности, вы должны изменить пароль своей учетной записи LinShare. Любое соединение будет запрещено до тех пор, пока этот шаг не будет выполнен.
resetLinkTitle = Ссылка для сброса пароля LinShare
subject =  Обновление безопасности
userNameTitle = Имя пользователя',
messages_vietnamese= 'urlExpiryDateTitle = URL ngày hết hạn
additionalMsg = Bạn cũng có thể dùng form đổi mật khẩu để thực hiện việc này.
noteMsg = Đường dẫn này chỉ được dùng một ngày và có giá trị trong 1 tuần.
changePasswordBtn = Đổi mật khẩu
mainTitle = Để nâng cao bảo bật cho tài khoản LinShare của bạn, bạn cần phải đổi mật khẩu tài khoản. Bất cứ kết nối nào cũng sẽ bị chặn cho đến khi bạn hoàn thành bước này.
resetLinkTitle = Đường dẫn đổi mật khẩu LinShare.
subject =  Cập nhật bảo mật
userNameTitle = Tên người dùng '
WHERE id= 37;
UPDATE mail_content SET subject='[( #{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p style="font-weight:bold;font-size:15px;"  data-th-utext="#{mainTile}">Did you forget your password ?</p>
        <p>
          <span data-th-utext="#{beginingMainMsg}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block data-th-replace="layout :: actionButtonLink(#{changePasswordBtn},${resetLink})"/>
          <br/>
        </p>
        <p  data-th-utext="#{endingMainMsg}"></p>
        <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{resetLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accountExpiryDateTitle = Date d''''expiration
beginingMainMsg =  Suivez le lien ci-dessous afin de réinitialiser le mot de passe de votre compte LinShare.
changePasswordBtn = Réinitialiser
endingMainMsg = Si vous n''''avez pas sollicité ce changement de mot de passe, merci d''''ignorer cet email. Votre mot de passe ne sera pas mis à jour tant que vous n''''en créé pas un nouveau, via le lien ci-dessus.
mainTile = Vous avez oublié votre mot de Passe ?
resetLinkTitle = Lien de réinitialisation
subject =  LinShare instruction de réinitialisation de mot de passe
userNameTitle = Identifiant',messages_english='accountExpiryDateTitle = Account expiry date
beginingMainMsg =  Follow the link below to reset your LinShare password account.
changePasswordBtn = Change password
endingMainMsg = If you did not request a password reset, please ignore this email. Your password will not change until you create a new one via the link above.
mainTile = Did you forget your password ?
resetLinkTitle = LinShare reset password link
subject =  LinShare reset password instructions
userNameTitle = Username',messages_russian='accountExpiryDateTitle = Дата окончания действия аккаунта
beginingMainMsg =  Используйте ссылку ниже для смены пароля к вашему аккаунту LinShare.
changePasswordBtn = Изменить пароль
endingMainMsg = Если вы не запрашивали смену пароля, пожалуйста, проигнорируйте это письмо. Ваш пароль не будет изменен пока вы не создадите новый, перейдя по ссылке.
mainTile = Забыли пароль?
resetLinkTitle = Ссылка на смену пароля LinShare
subject =  Инструкция по смену пароля LinShare
userNameTitle = Имя пользователя' ,messages_vietnamese='accountExpiryDateTitle = Ngày hết hạn tài khoản
beginingMainMsg =  Bấm vào link dưới đây để đặt lại mật khẩu cho tài khoản LinShare của bạn.
changePasswordBtn = Đổi mật khẩu
endingMainMsg = Nếu bạn không yêu cầu đổi mật khẩu, hãy bỏ qua thư này. Mật khẩu của bạn sẽ không được đổi trừ khi bạn tạo một mật khẩu mới thông qua đường dẫn trên.
mainTile = Bạn quên mật khẩu?
resetLinkTitle = Đường dẫn đổi mật khẩu
subject =  Hướng dẫn đổi mật khẩu LinShare
userNameTitle = Tên người dùng' WHERE id=9;
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
subject = {0} {1} vous a ajouté comme modérateur d''invité
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
subject = {0} {1} vous a supprimé de la liste des modérateurs d''invité
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a supprimé de la liste des modérateurs de <b>{2}</b> <b>{3}</b>.
guestNameTitle = Invité',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} deleted you from guest moderator''s list
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
           <th:block data-th-replace="layout :: infoEditedItem(#{role}, ${role.oldValue}, ${role.value})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a modifié le modérateur role
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a modifié le modérateur role pour <b>{2}</b> <b>{3}</b>.
role = Role',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} updated your moderator role
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> updated your moderator role on the guest <b>{2}</b> <b>{3}</b>.
role = Role',
messages_russian='accessToLinshareBTn = Доступ
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> обновил вашу роль модератора в гостевом <b>{2}</b> <b>{3}.
subject = {0} {1} обновил вашу роль модератора
role = Роль',
messages_vietnamese='accessToLinshareBTn = Truy cập
subject = {0} {1} cập nhật quyền quản trị của bạn
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã cập nhật quyền quản trị của bạn đối với khách <b>{2}</b> <b>{3}</b>.
role = Quyền'WHERE id=42;
UPDATE mail_content SET subject='[( #{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(#{productName},${guest.mail})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{accountCreationDateTitle},${guestCreationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accountCreationDateTitle = Date de création
accountExpiryDateTitle = Date d''''expiration
mainMsg = Le mot de passe du compte {0} <b>{1}</b> a été modifié.
subject = Votre mot de passe a été modifié',messages_english='accountCreationDateTitle = Account creation date
accountExpiryDateTitle = Account expiry date
mainMsg = The password of the account {0} <b>{1}</b> was modified.
subject = Your password has been modified',messages_russian='accountCreationDateTitle = Дата создания аккаунта
accountExpiryDateTitle = Дата окончания действия аккаунта
mainMsg = Пароль аккаунта {0} <b>{1}</b> был изменен.
subject = Ваш пароль был изменен',messages_vietnamese='accountCreationDateTitle = Ngày tạo tài khoản
accountExpiryDateTitle = Ngày tài khoản hết hạn
mainMsg = Mật khẩu của tài khoản {0} <b>{1}</b> đã được thay đổi.
subject = Mật khẩu của bạn đã được thay đổi' WHERE id=31;
UPDATE mail_content SET subject='[( #{subject(${guest.firstName},${guest.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${guest.firstName},${guest.lastName},${daysLeft})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{accountCreationDateTitle},${guestCreationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{userEmailTitle},${guest.mail})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Le compte de votre invité expire
accountCreationDateTitle = Date de création
accountExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
mainMsg = Le compte invité de : <b> {0} <span style="text-transform:uppercase">{1}</span></b> expirera dans {2} jours. Pensez à prolonger la validité du compte si besoin.
subject = Le compte invité de {0}  {1} expire bientôt
userEmailTitle = Email',messages_english='accessToLinshareBTn = Expiration account
accountCreationDateTitle = Account creation date
accountExpiryDateTitle = Account expiry date
activationLinkTitle = Initialization link
mainMsg = The  <b> {0} <span style="text-transform:uppercase">{1}</span></b> guest account is about to expire in {2} days. If this account is still needed,  postpone its expiration date.
subject = {0}  {1} guest account will expire soon.
userEmailTitle = Email',messages_russian='accessToLinshareBTn = Истечение срока действия аккаунта
accountCreationDateTitle = Дата создания аккаунта
accountExpiryDateTitle = Дата истечения срока действия аккаунта
activationLinkTitle = Ссылка активации
mainMsg = Срок действия гостевого аккаунта <b> {0} <span style="text-transform:uppercase">{1}</span></b> заканчивается через {2} дня. Если вам все еще нужен аккаунт, продлите срок его действия.
subject = {0}  {1} срок действия гостевого аккакунта скоро закончится.
userEmailTitle = Электронная почта',messages_vietnamese='accessToLinshareBTn = Tài khoản hết hạn
accountCreationDateTitle = Ngày tạo tài khoản
accountExpiryDateTitle = Ngày tài khoản hết hạn
activationLinkTitle = Đường dẫn
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> Tài khoản khách sắp hết hạn trong {2} ngày. Nếu tài khoản này vẫn cần thiết, bạn có thể gia hạn.
subject = {0}  {1} tài khoản khách sắp hết hạn
userEmailTitle = Email' WHERE id=25;
UPDATE mail_content SET subject= '[( #{subject})]',
body= '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
          <th:block data-th-replace="layout :: greetings(${shareRecipient.mail})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p style="font-weight:bold;font-size:15px;"  data-th-utext="#{mainTitle(${shareOwner.firstName} , ${shareOwner.lastName})}"></p>
        <p>
          <span data-th-utext="#{beginingMainMsg}"></span>
          <span data-th-utext="#{otherMsg}"></span>
        </p>
        <th:block data-th-replace="layout :: actionButtonLink(#{downloadBtn},${anonymousURL})"/></br>
        <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
  <th:block data-th-replace="layout :: infoStandardArea(#{passwordMessageTitle},${password})"/>
  <th:block data-th-replace="layout :: infoActionLink(#{downloadLinkTit},${anonymousURL})"/>
  <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shares[0].creationDate})"/>
  <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shares[0].expirationDate})"/>
  <th:block data-th-replace="layout :: infoFileLinksListingArea(#{common.filesInShare},${shares},${anonymous})"/>

  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',
messages_french= '
	subject=LinShare Génération d''''un nouveau mot de passe
	mainTitle= Un nouveau mot de passe vous a été généré par {0} {1}.
	beginingMainMsg= Pour des raisons de sécurité un nouveau mot de passe pour accéder à votre partage a été généré.
	otherMsg= Vous pouvez de nouveau cliquer sur le boutton ci-dessous pour télécharger le partage et saisissez le nouveau mot de passe.
	downloadBtn=Télécharger
	passwordMessageTitle= Voici le nouveau mot de passe:
	downloadLinkTit = lien de téléchargement:
	',
messages_english= '
	subject=LinShare New password Generation
	mainTitle= A new password was generated by {0} {1}
	beginingMainMsg= For a security reasons a new password was generated.
	otherMsg= You can click the button below to download the shares and enter the new generated password below.
	downloadBtn= Download
	passwordMessageTitle= Here is the new password:
	downloadLinkTit= Download link:
	',
messages_russian= '
	subject=LinShare Генерация нового пароля
	mainTitle= Новый пароль был сгенерирован {0} {1}
	beginingMainMsg= В целях безопасности был сгенерирован новый пароль.
	otherMsg= Вы можете нажать кнопку ниже, чтобы загрузить общие файлы и ввести новый сгенерированный пароль.
	downloadBtn= Загрузить
	passwordMessageTitle= Новый пароль:
	downloadLinkTit= Ссылка загрузки:
	',
messages_vietnamese= '
	subject=LinShare Tạo Mật khẩu mới
	mainTitle= Một mật khẩu mới đã được tạo bởi {0} {1}
	beginingMainMsg= Vì mục đích bảo mật một mật khẩu mới đã được tạo
	otherMsg= Bạn có thể bấm vào đường dẫn dưới đây để tải về file chia sẻ và nhập mật khẩu mới dưới đây
	downloadBtn= Tải xuống
	passwordMessageTitle= Đây là mật khẩu mới:
	downloadLinkTit= Link tải:
	'
WHERE id= 38;
UPDATE mail_content SET subject='[# th:if="${!anonymous}"]
[( #{subject(${shareRecipient.firstName},${shareRecipient.lastName},${share.name})})]
[/]
[# th:if="${anonymous}"]
[( #{subjectAnonymous(${shareRecipient.mail},${share.name})})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <th:block th:if="${!anonymous}" >
            <span data-th-utext="#{mainMsgInt(${shareRecipient.firstName},${shareRecipient.lastName})}">
            Peter WILSON has downloaded your file
            </span>
          </th:block>
          <th:block th:if="${anonymous} ">
            <span data-th-utext="#{mainMsgExt(${shareRecipient.mail})}">
              unknown@domain.com has downloaded your file
            </span>
          </th:block>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{downloadDate},${actionDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${expiryDate})"/>
     <th:block th:if="${sharesCount}  > 1 ">
         <th:block data-th-replace="layout :: infoFileListUploadState(#{common.filesInShare},${shares})"/>
   </th:block>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='downloadDate = Téléchargé le
fileNameEndOfLine = {0}.
mainMsgExt = Le destinataire externe <b>{0}</b> a téléchargé votre fichier
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a téléchargé votre fichier
subject =  {0} {1} a téléchargé {2}
subjectAnonymous = {0} a téléchargé {1}',messages_english='downloadDate = Download date
fileNameEndOfLine = {0}.
mainMsgExt = The external recipient <b>{0}</b> has downloaded your file
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has downloaded your file
subject = {0} {1} has downloaded {2}
subjectAnonymous = {0} has downloaded {1}',messages_russian='downloadDate = Дата загрузки
fileNameEndOfLine = {0}.
mainMsgExt = Внешний пользователь <b>{0}</b> скачал(а) ваш файл
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> скачал(а) ваш файл
subject = {0} {1} был скачан {2}
subjectAnonymous = {0} был скачан {1}' ,messages_vietnamese='downloadDate = Ngày tải
fileNameEndOfLine = {0}.
mainMsgExt = Người nhận ngoài <b>{0}</b> đã tải xuống file của bạn
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> đã tải xuống file của bạn
subject = {0} {1} đã tải xuống {2}
subjectAnonymous = {0} đã tải xuống {1}' WHERE id=4;
UPDATE mail_content SET subject='[( #{subject(${shareOwner.firstName},${shareOwner.lastName},${share.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${shareOwner.firstName},${shareOwner.lastName})}">
             Peter WILSON has downloaded your file
          </span>
          <span style="font-weight:bold" data-th-text="${share.name}" >
             filename.ext
          </span>.
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{deletedDate},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='deletedDate = Supprimé le
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> a supprimé le partage
subject = {0} {1} a supprimé le partage de {2}',messages_english='deletedDate = Deletion date
mainMsg = <b>{0} <span style="text-transform:uppercase">{1}</span></b> has deleted the  fileshare
subject = {0} {1} has deleted the fileshare {2}',messages_russian='deletedDate = Дата удаления
mainMsg = <b>{0} <span style="text-transform:uppercase">{1}</span></b> удалил файл рассылки
subject = {0} {1} удалил файл рассылки {2}',messages_vietnamese='deletedDate = Ngày xóa
mainMsg = <b>{0} <span style="text-transform:uppercase">{1}</span></b> đã xóa tài liệu chia sẻ
subject = {0} {1} đã xóa tài liệu chia sẻ  {2}' WHERE id=5;
UPDATE mail_content SET subject='[# th:if="${documentsCount} > 1"]
[( #{subjectPlural})]
[/]
[# th:if="${documentsCount} ==  1"]
[( #{subjectSingular})]
[/]
[# th:if="${!#strings.isEmpty(customSubject)}"]
[(${ ": " +customSubject})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(customMessage)}">
      <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFor}">You have a message from</span>
        </span>
        <span id="message-content" data-th-text="*{customMessage}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${documentsCount} > 1)" data-th-utext="#{numFilesMsgPlural(${documentsCount})}">
            Peter WILSON has  shared 4 files
            </span>
          <span data-th-if="(${documentsCount} ==  1)" data-th-utext="#{numFilesMsgSingular(${documentsCount})}">
            Peter WILSON has  shared 1 file
            </span>
          <span data-th-if="(${recipientsCount} >  1)" th:with="df=#{date.format}"
                data-th-utext="#{recipientCountMsgPlural(${#dates.format(expirationDate,df)},${recipientsCount})}">
             to 3 recipients set to expire for the 7th December 2018
            </span>
          <span data-th-if="(${recipientsCount} ==  1)" th:with="df=#{date.format}"
                data-th-utext="#{recipientCountMsgSingular(${#dates.format(expirationDate,df)},${recipientsCount})}">
            to 1 recipient set to expire for the 7th December 2018
            </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End upper of main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoRecipientListingArea(#{common.recipients},${recipients})"/>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{common.filesInShare},${documents},false)"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='numFilesMsgPlural = Vous avez partagé <b>{0} fichiers</b>
numFilesMsgSingular = Vous avez partagé <b>{0} fichier</b>
recipientCountMsgPlural = avec <b>{1} destinataires</b>. Ce partage expirera le <b>{0}</b>.
recipientCountMsgSingular = avec <b>{1} destinataire</b>. Ce partage expirera le <b>{0}</b>.
subjectPlural = Vous avez partagé des fichiers
subjectSingular = Vous avez partagé un fichier
msgFor = Votre message de partage',messages_english='numFilesMsgPlural = You have shared <b>{0} files</b>
numFilesMsgSingular = You have shared <b>{0} file</b>
recipientCountMsgPlural =   to <b>{1} recipients</b>. The fileshare will expire on : {0}.
recipientCountMsgSingular =   to <b>{1} recipient</b>. The fileshare will  expire on : {0}.
subjectPlural =  You have shared some files
subjectSingular = You have shared a file
msgFor = Your message of sharing',messages_russian='numFilesMsgPlural = Вы поделились <b>{0} files</b>
numFilesMsgSingular = Вы поделились <b>{0} file</b>
recipientCountMsgPlural =   с <b>{1} recipients</b>. Срок действия рассылки закончится: {0}.
recipientCountMsgSingular =   с <b>{1} recipient</b>. Срок действия рассылки закончится: {0}.
subjectPlural =  Вы поделились некоторыми файлами
subjectSingular =Вы поделились файлом
msgFor = Ваше сообщение рассылки',messages_vietnamese='numFilesMsgPlural = Bạn đã chia sẻ <b>{0} files</b>
numFilesMsgSingular = Bạn đã chia sẻ <b>{0} file</b>
recipientCountMsgPlural = tới <b>{1} recipients</b>. Tài liệu chia sẻ sẽ hết hạn vào : {0}.
recipientCountMsgSingular =   tới <b>{1} recipient</b>. Tài liệu chia sẻ sẽ hết hạn vào : {0}.
subjectPlural = Bạn đã chia sẻ một số tìa liệu
subjectSingular = Bạn đã chia sẻ 1 tài liệu' WHERE id=3;
UPDATE mail_content SET subject='[# th:if="${#strings.isEmpty(customSubject)}"]
[# th:if="${sharesCount} > 1"]
[( #{subjectPlural(${shareOwner.firstName},${ shareOwner.lastName})})]
[/]
[# th:if="${sharesCount} ==  1"]
[( #{subjectSingular(${shareOwner.firstName },${ shareOwner.lastName})})]
[/]
[/]
[# th:if="${!#strings.isEmpty(customSubject)}"]
[(${customSubject})]   [( #{subjectCustomAlt(${shareOwner.firstName },${shareOwner.lastName})})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(customMessage)}">
      <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${shareOwner.firstName} , ${shareOwner.lastName})}">Peter Wilson</b> :
        </span>name = {0} {1}
        <span id="message-content" data-th-text="*{customMessage}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div data-th-if="(${!anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        </div>
        <div data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.mail})"/>
        </div> <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
            <span data-th-if="(${sharesCount} ==  1)"
                  data-th-utext="#{mainMsgSingular(${shareOwner.firstName},${shareOwner.lastName},${sharesCount})}">
            Peter WILSON has shared 4 file with you
            </span>
          <span data-th-if="(${sharesCount} > 1)"
                data-th-utext="#{mainMsgPlural(${shareOwner.firstName},${shareOwner.lastName},${sharesCount})}">
            Peter WILSON has shared 4 files with you
            </span>
          <br/>
          <!--/* Check if the external user has a password protected file share */-->
          <span data-th-if="(${protected})">
       <span data-th-if="(${sharesCount} ==  1)" data-th-text="#{helpPasswordMsgSingular}">Click on the link below in order to download it     </span>
            <span data-th-if="(${sharesCount} >  1)" data-th-text="#{helpPasswordMsgPlural}">Click on the links below in order to download them </span>
            </span>
          <span data-th-if="(${!anonymous})">
            <span data-th-if="(${sharesCount} ==  1)">
              <span  data-th-utext="#{click}"></span>
                <span>
                 <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{link}" th:href="@{${filesSharesLink}}" >
                  link
                 </a>
               </span>
              <span data-th-utext="#{helpMsgSingular}"></span>
            </span>
            <span data-th-if="(${sharesCount} >  1)">
              <span  data-th-utext="#{click}"></span>
              <span>
                <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{link}" th:href="@{${filesSharesLink}}" >
                 link
               </a>
              </span>
             <span data-th-utext="#{helpMsgPlural}"></span>
            </span>
            </span>
        </p>
        <!--/* Single download link for external recipient */-->
        <div data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: actionButtonLink(#{downloadBtn},${anonymousURL})"/>
        </div>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <div data-th-if="(${protected})">
      <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
    </div>
    <div data-th-if="(${anonymous})">
      <th:block data-th-replace="layout :: infoActionLink(#{downloadLink},${anonymousURL})"/>
    </div>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shares[0].creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shares[0].expirationDate})"/>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{common.filesInShare},${shares},${anonymous})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>',messages_french='downloadBtn = Télécharger
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
link = lien',messages_english='downloadBtn = Download
downloadLink = Download link
helpMsgPlural = to access to all documents in this share.
helpMsgSingular = to access to the document in this share.
helpPasswordMsgSingular = Click on the link below in order to download it and enter the provided password.
helpPasswordMsgPlural = Click on the link below in order to download them and enter the provided password.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} files</b> with you.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} file</b> with you.
msgFrom = You have a message from
name = {0} {1}
password = Password
subjectCustomAlt =by {0} {1}
subjectPlural = {0} {1} has shared some files with you
subjectSingular = {0} {1} has shared a file with you
click = Follow this
link = link',messages_russian='downloadBtn = Загрузить
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
link = ссылке' ,messages_vietnamese='downloadBtn = Tải xuống
downloadLink = Đường dẫn tải xuống
helpMsgPlural = để truy cập tất cả tài liệu được chia sẻ
helpMsgSingular = để truy cập tất cả tài liệu được chia sẻ.
helpPasswordMsgSingular = Bấm vào đường link dưới đây để tải và nhập mật khẩu được cung cấp trước đó.
helpPasswordMsgPlural = Bấm vào đường link dưới đây để tải và nhập mật khẩu được cung cấp trước đó.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> đã chia sẻ <b>{2} files</b> với bạn .
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> đã chia sẻ <b>{2} file</b> với bạn.
msgFrom = Bạn có 1 tin nhắn từ
name = {0} {1}
password = Mật khẩu
subjectCustomAlt =by {0} {1}
subjectPlural = {0} {1} đã chia sẻ một vài tài liệu với bạn.
subjectSingular = {0} {1} đã chia sẻ một tài liệu với bạn.
click = Bấm vào
link = link' WHERE id=2;
UPDATE mail_content SET subject='[( #{subject(${share.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <b><span data-th-text="#{fileNameEndOfLine(${share.name})}"></span></b>
          <span data-th-utext="#{endingMainMsg(${shareOwner.firstName},${shareOwner.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b> a expiré et a été supprimé par le <b>système</b>.
subject = Le partage {0} a expiré
fileNameEndOfLine = {0}',messages_english='shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b> has expired and been deleted by the <b>system</b>.
subject = The fileshare {0} has expired
fileNameEndOfLine = {0}',messages_russian='shareFileTitle = Файл рассылки
shareCreationDateTitle = Дата создания
shareExpiryDateTitle = Дата срока истечения действия
activationLinkTitle = Ссылка активации
beginningMainMsg =  У файла рассылки
endingMainMsg = отправленного <b> {0} <span style="text-transform:uppercase">{1}</span></b> истек срок действия и он был удален <b>system</b>.
subject = Срок действия файла рассылки {0} истек
fileNameEndOfLine = {0}' ,messages_vietnamese='shareFileTitle = Tài liệu chia sẻ
shareCreationDateTitle = Ngày tạo
shareExpiryDateTitle = NGày hết hạn
activationLinkTitle = Đường dẫn
beginningMainMsg =  Tài liệu chia sẻ
endingMainMsg = được gửi bởi <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã hết hạn và bị xóa bởi <b>system</b>.
subject = Tài liệu chia sẻ  {0} đã hết hạn.
fileNameEndOfLine = {0}'WHERE id=27;
UPDATE mail_content SET subject='[( #{subject(${share.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-if="(${!anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        </th:block>
        <th:block data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.mail})"/>
        </th:block>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <th:block data-th-if="(${anonymous})">
            <span
              data-th-utext="#{mainMsgExt(${share.name}, ${shareOwner.firstName},${shareOwner.lastName},${daysLeft})}">
              Your share link for Peter sent by Peter WILSON, will expire in 8 days. a-shared-file.txt.
            </span>
          </th:block>
          <th:block data-th-if="(${!anonymous})">
            <span data-th-utext="#{beginningMainMsgInt}"></span>
            <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="${share.name}"
                th:href="@{${share.href}}">
                  filename.ext
             </a>
          </span>
            <span
              data-th-utext="#{endingMainMsgInt(${shareOwner.firstName},${shareOwner.lastName},${daysLeft})}">  </span>
            <!--/* Single download link for external recipient */-->
            <th:block data-th-replace="layout :: actionButtonLink(#{common.download},${share.href})"/>
          </th:block>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block
      data-th-replace="layout :: infoStandardArea(#{sharedBy},#{name(${shareOwner.firstName},${shareOwner.lastName})})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgInt = Le partage
endingMainMsgInt = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b>, va expirer dans <b>{2} jours</b>.
mainMsgExt = Le partage <b>{0}</b> émis par <b> {1} <span style="text-transform:uppercase">{2}</span></b>, va expirer dans <b>{3} jours</b>.
name = {0} {1}
sharedBy = Partagé par
subject =  Le partage {0} va bientôt expirer',messages_english='beginningMainMsgInt = The fileshare
endingMainMsgInt = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b>,  will expire in <b>{2} days</b>.
mainMsgExt = The fileshare <b>{0}</b> sent by <b> {1} <span style="text-transform:uppercase">{2}</span></b>,  will expire in <b>{3} days</b>.
name = {0} {1}
sharedBy = Shared by
subject = The fileshare for {0} is about to expire',messages_russian='beginningMainMsgInt = Срок действия файла рассылки
endingMainMsgInt = отправленного <b> {0} <span style="text-transform:uppercase">{1}</span></b>,  закончится через <b>{2} дней</b>.
mainMsgExt = Срок действия файла рассылки <b>{0}</b> sent by <b> {1} <span style="text-transform:uppercase">{2}</span></b>,  закончится через <b>{3} дней</b>.
name = {0} {1}
sharedBy = Отправлено
subject = Срок действия файла рассылки {0} заканчивается',messages_vietnamese='beginningMainMsgInt = Tài liệu chia sẻ
endingMainMsgInt = được gửi bởi <b> {0} <span style="text-transform:uppercase">{1}</span></b>,  sẽ hết hạn trong <b>{2} ngày </b>.
mainMsgExt = Tài liệu chia sẻ <b>{0}</b> được gửi bởi <b> {1} <span style="text-transform:uppercase">{2}</span></b>,  sẽ hết hạn trong <b>{3} ngày </b>.
name = {0} {1}
sharedBy = Được chia sẻ bởi
subject = Tài liệu chia sẻ cho {0} sắp hết hạn' WHERE id=6;
UPDATE mail_content SET subject='[( #{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${daysLeft},${shareRecipient.firstName},${shareRecipient.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle},#{name(${shareRecipient.firstName}, ${shareRecipient.lastName})})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Votre partage expire bientôt
shareRecipientTitle =  Destinataire
shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg =  expire dans {0} jours sans avoir été téléchargé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Votre partage expire bientôt et n''''a pas encore été téléchargé
name = {0} {1}
fileNameEndOfLine = {0}',messages_english='accessToLinshareBTn = Your share will expire soon
shareRecipientTitle = Recipient
shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg =  will expire in {0} days and has not been downloaded by the recipient <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Your share will expire soon and has not been downloaded
name = {0} {1}
fileNameEndOfLine = {0}',messages_russian='accessToLinshareBTn = Срок действия вашей рассылки скоро закончится
shareRecipientTitle = Получатель
shareFileTitle = Файл рассылки
shareCreationDateTitle = Дата создания
shareExpiryDateTitle = Дата истечения срока действия
activationLinkTitle = Ссылка активации
beginningMainMsg = Срок действия файла рассылки
endingMainMsg =  закончится через {0} дней, а файла не были скачаны получателем <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Срок действия рассылки скоро закончится, а файлы не были скачаны
name = {0} {1}
fileNameEndOfLine = {0}',messages_vietnamese='accessToLinshareBTn = Tài liệu chia sẻ của bạn sắp hết hạn
shareRecipientTitle = Người nhận
shareFileTitle = Tài liệu chia sẻ
shareCreationDateTitle = Ngày tạo
shareExpiryDateTitle = Ngày hết hạn
activationLinkTitle = Đường dẫn
beginningMainMsg =  Tài liệu chia sẻ
endingMainMsg =  sẽ hết hạn trong {0} ngày và người nhận vẫn chưa tải về <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Tài liệu chia sẻ của bạn sắp hết hạn và vẫn chưa được tải về
name = {0} {1}
fileNameEndOfLine = {0}' WHERE id=26;
UPDATE mail_content SET subject='[# th:if="${documentsCount} > 1"]
[( #{subjectPlural(${documentsCount})})]
[/]
        [# th:if="${documentsCount} ==  1"]
          [( #{subjectSingular(${documentsCount})})]
       [/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${documentsCount} ==  1)" data-th-utext="#{mainMsgSingular(${documentsCount})}">
            Some recipients have not downloaded 2 files yet. You may find further details of the recipients downloads, below.
          </span>
          <span data-th-if="(${documentsCount} >  1)" data-th-utext="#{mainMsgplural(${documentsCount})}">
            Some recipients have not downloaded 2 files yet. You may find further details of the recipients downloads, below.
          </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoFileListRecipientUpload(#{downloadStatesTile},${documents})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareGroup.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shareGroup.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='downloadStatesTile = Etat de téléchargement
mainMsgplural = Certains destinataires n''''ont pas téléchargés <b>{0} fichiers</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
mainMsgSingular = Certains destinataires n''''ont pas téléchargés <b>{0} fichier</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
subjectPlural = Rappel de non-téléchargement : {0} fichiers n''''ont pas été téléchargés.
subjectSingular = Rappel de non-téléchargement :  {0} fichier n''''a pas été téléchargé.',messages_english='downloadStatesTile = Downloads states
mainMsgplural = Some recipients have not downloaded <b>{0} files</b>. You may find further details of the recipients downloads below.
mainMsgSingular = Some recipients have not downloaded <b>{0} file</b>. You may find further details of the recipients downloads below.
subjectPlural = Undownloaded shared files alert : {0} files have not been downloaded yet.
subjectSingular = Undownloaded shared files alert : {0} file have not been downloaded yet.',messages_russian='downloadStatesTile = Статус загрузки
mainMsgplural = Некоторые получатели рассылки не скачали <b>{0} файлов</b>. Вы можете найти детали о получателях рассылки ниже.
mainMsgSingular = Некоторые получатели рассылки не скачали <b>{0} файлов</b>. Вы можете найти детали о получателях рассылки ниже.
subjectPlural = Уведомдение о не скачанных файлах: {0} файлов были не скачанны.
subjectSingular = Уведомдение о не скачанных файлах: {0} файлов были не скачанны.',messages_vietnamese='downloadStatesTile = Hiện trạng tải xuống
mainMsgplural = Một vài người nhận đã không tải  <b>{0} files</b>. Bạn có thể xem thông tin chi tiết hơn về việc tải xuống của người nhận dưới đây.
mainMsgSingular = Some recipients have not downloaded <b>{0} file</b>. Bạn có thể xem thông tin chi tiết hơn về việc tải xuống của người nhận dưới đây.
subjectPlural = Thông báo chưa tải xuống file chia sẻ : {0} files vẫn chưa được người nhận tải xuống.
subjectSingular = Thông báo chưa tải xuống file chia sẻ : {0} file vẫn chưa được người nhận tải xuống.'WHERE id=7;
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
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
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
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
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
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
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
invitationCreationDate = Date d''activation
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
               <th:block data-th-replace="layout :: infoDateArea(#{activationDate},${request.activationDate})"/>
            </div>
     <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
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
UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${document.name})})]',body='<!DOCTYPE html>
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${document.name},${subject})}">
                 Peter WILSON has deleted the file my-file.txt from the depot : subject
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
            <th:block data-th-replace="layout :: infoDateArea(#{deletionDate},${deletionDate})"/>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='closureDate = Dépôt disponible jusqu''''au
deletionDate = Fichier supprimé le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>a supprimé le fichier  <b>{2} </b> de  l''''Invitation de Dépôt : {3}
subject = {0} {1} a supprimé {2} du dépôt',messages_english='closureDate = Depot closure date
deletionDate = File deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has deleted the file <b>{2} </b>from the upload request  : {3}.
subject = {0} {1} has deleted {2} from the upload request',messages_russian='closureDate = Срок действия загрузки
deletionDate = Дата удаления
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> удалил файл <b>{2} </b> из хранилища {3}.
subject = {0} {1} удалил файл {2} из хранилища',messages_vietnamese='closureDate = Ngày đóng
deletionDate = Ngày xóa file
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> đã xóa file <b>{2} </b>từ yêu cầu tải lên  : {3}.
subject = {0} {1} đã xóa {2} từ yêu cầu tải lên' WHERE id=24;
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
  <span  data-th-utext="#{mainMsg(${requestRecipient.mail},${deleted.name},${subject})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
     <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section> <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='invitationClosureDate = Date d''''expiration
invitationCreationDate = Date d''''activation
mainMsg = <b>{0}</b> a supprimé le fichier <b> {1} </b>de votre Invitation de Dépôt <b>{2}</b>.
subject =  {0} a supprimé un fichier de votre invitation de dépôt {1}',messages_english='invitationClosureDate = Closure date
invitationCreationDate = Activation date
mainMsg = <b>{0}</b> has deleted the file <b> {1} </b>from your Upload Request <b>{2}</b>.
subject = {0} has deleted a file from the Upload Request {1}',messages_russian='invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
mainMsg = <b>{0}</b> удалил файл <b> {1} </b> из вашего запроса загрузки <b>{2}</b>.
subject = {0} удалил файл из загрузки {1}',messages_vietnamese='invitationClosureDate = Ngày đóng
invitationCreationDate = Ngày kích hoạt
mainMsg = <b>{0}</b> đã xóa file <b> {1} </b>từ yêu cầu tảu lên của bạn <b>{2}</b>.
subject = {0} đã xóa 1 file từ Yêu cầu tải lên {1}' WHERE id=15;
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
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                     </span>
                     <br/>
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
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
            <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès au dépôt
closureDate = Dépôt disponible jusqu''''au
mainMsg = <b>{0} {1}</b> a modifié le mot de passe d''''accès à l''''Invitation de Dépôt : {2}.
msgProtected = Vous trouverez ci-dessous le nouveau mot de passe ainsi que le lien d''''accès.
password = Mot de passe
subject = {0} {1} vous envoie le nouveau mot de passe du dépôt : {2}',messages_english='buttonMsg = Access to the depot
closureDate = Depot closure date
mainMsg = <b>{0} {1}</b> has changed the password of the Upload Request : {2}
msgProtected = You may find the new password below as well as the access link.
password = Password
subject = {0} {1} sent you the new password for the Upload Request: {2}',messages_russian='buttonMsg = Доступ к загрузке
closureDate = Дата закрытия загрузки
mainMsg = <b>{0} {1}</b> изменил пароль к загрузке {2}
msgProtected = Новый пароль и доступ к загрузке доступны ниже.
password = Пароль
subject = {0} {1} отправил вам новый пароль к загрузке {2}',messages_vietnamese='buttonMsg = Truy cập
closureDate = Ngày đóng
mainMsg = <b>{0} {1}</b> đã thay đổi mật khẩu của yêu cầu tải lên  : {2}
msgProtected = Bạn có thể sử dụng mật khẩu mới dưới đây khi truy cập đường dẫn.
password = Mật khẩu
subject = {0} {1} đã gửi cho bạn mật khẩu mới cho yêu cầu tải lên: {2}' WHERE id=19;
UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has deleted your access to the depot : : subject.
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
            <th:block data-th-replace="layout :: infoDateArea(#{deletionDate},${deletionDate})"/>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='deletionDate = Accès au dépôt retiré le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a retiré votre accès au dépôt de l''''invitation intitulée : {2}.
subject = {0} {1} a supprimé votre accès au dépôt : {2}',messages_english='deletionDate = Deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has removed your access to the Upload Request : {2}.
subject = {0} {1} has removed your access to the Upload Request : {2}',messages_russian='deletionDate = Дата удаления
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> закрыл ваш доступ к загрузке {2}.
subject = {0} {1} закрыл ваш доступ к загрузке {2}',messages_vietnamese='deletionDate = Ngày xóa
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> đã xóa quyền truy cập của bạn đến yêu cầu tải : {2}.
subject = {0} {1} đã xóa quyền truy cập của bạn đến yêu cầu tải : {2}' WHERE id=22;
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
                  <b data-th-text="#{name(${requestOwner.firstName}, ${requestOwner.lastName})}">Peter Wilson</b> :
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
                     Peter Wilson reminds you that he still has not received the requested files.
                     </span>
                     <span data-th-utext="#{mainMsgEnd}">
                     You can upload your files in the provided depot made available to you labeled  subject.
                     </span>
                     <!--/* Check if the external user has a password protected file share */-->
                     <br/>
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
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
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
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
mainMsg = <b>{0} {1}</b> aimerais vous rappeller de déposer vos fichiers sur: <b>{2}</b>.
mainMsgEnd =
msgFrom =  Le message de
msgUnProtected = Pour accéder au dépôt, suivez le lien ci-dessous.
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} attend toujours des fichiers de votre part',messages_english='buttonMsg = Access
closureDate = Closure date
depotSize = Size
mainMsg = <b>{0} {1}</b> kindly reminds you to upload your files on: <b>{2}</b>.
mainMsgEnd =
msgFrom = Message from
msgUnProtected = In order to upload your files, please follow the link below.
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} is still awaiting your files',messages_russian='buttonMsg = Доступ
closureDate = Дата закрытия
depotSize = Размер
mainMsg = <b>{0} {1}</b> напоминает вам о загрузке ваших файлов on: <b>{2}</b>.
mainMsgEnd =
msgFrom = Сообщение от
msgUnProtected = Для того, чтобы загрузить ваши файлы, пожалуйста, перейдите по ссылке ниже.
name = {0} {1}
recipientsOfDepot = Получатели
subject = {0} {1} ожидает ваши файлы' ,messages_vietnamese='buttonMsg = Truy cập
closureDate = Ngày đóng
depotSize = Dung lượng
mainMsg = <b>{0} {1}</b> nhắc bạn hãy tải file của bạn lên: <b>{2}</b>.
mainMsgEnd =
msgFrom = Tin nhắn từ
msgUnProtected = Để tải file của bạn, mở link dưới đây
name = {0} {1}
recipientsOfDepot = Người nhận
subject = {0} {1} đang đợi tài liệu của bạn' WHERE id=18;
UPDATE mail_content SET subject='[( #{subject(${requestRecipient.mail},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content */-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p style="font-size: 14px;font-weight: bold;color: #df5656;margin-bottom: 7px;" data-th-utext="#{mainMsgTitle}">
          You have no available space.</p>
        <p>
          <span data-th-utext="#{mainMsg(${requestRecipient.mail})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      <!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper  main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${maxDepositSize != null})">
      <th:block data-th-replace="layout :: infoStandardArea(#{maxUploadDepotSize},${maxDepositSize})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='invitationClosureDate = Date de clôture
invitationCreationDate = Date d''''activation
mainMsg =  <b>{0}</b>  n''''a pas pu déposer des fichiers dans le dépôt car il n''''y a plus d''''espace disponible dans votre Espace Personnel. Veuillez s''''il vous plait libérez de l''''espace.
mainMsgTitle = Vous n''''avez plus d''''espace disponible.
maxUploadDepotSize =  Taille total du dépôt
recipientsURequest = Destinataires
subject =  {0}  n''''a pu déposer un fichier car il n''''y a plus d''''espace disponible',messages_english='invitationClosureDate = Closure date
invitationCreationDate = Activation date
mainMsg =  <b>{0}</b> is not able to upload any file, since there is no more space available in your Personal Space. Please free up some space.
mainMsgTitle = No more space available.
maxUploadDepotSize = Maximum size of the Upload Request
recipientsURequest = Recipients
subject =  {0} could not upload a file since there is no more space available',messages_russian='invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
mainMsg =  <b>{0}</b> не может загрузить файлы, так как в вашем личном пространстве недостаточно места. Пожалуйста, удалите некоторые файлы, чтобы освободить место.
mainMsgTitle = Недостаточно свободного места.
maxUploadDepotSize = Максимальный размер загрузки
recipientsURequest = Получатели
subject =  {0} не может загрузить файл, так как недостаточно свободного места' ,messages_vietnamese='invitationClosureDate = Ngày đóng
invitationCreationDate = Ngày kích hoạt
mainMsg =  <b>{0}</b> không thể tải lên file bởi vì không còn dung lượng trống trong Personal Space của bạn. Hãy thêm dung lượng.
mainMsgTitle = Không còn dung lượng trống
maxUploadDepotSize = Dung lượng tối đa của yêu cầu tải lên.
recipientsURequest = Người nhận
subject =  {0} không thể tải file lên vì không còn dung lượng trống'WHERE id=11;
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
invitationCreationDate = Date d''activation
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
UPDATE mail_content SET subject='[# th:if="${warnOwner}"] [( #{subjectForOwner})]
[/]
[# th:if="${!warnOwner}"]
[( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName})})]
[/]
[# th:if="${!#strings.isEmpty(mailSubject)}"]
[( #{formatMailSubject(${mailSubject})})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})">
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="!(${isCollective})"   data-th-utext="#{beginningMainMsgIndividual(${subject},${remainingDays})}"></span>
            <span  data-th-if="(${isCollective})"   data-th-utext="#{beginningMainMsgCollective(${subject},${remainingDays})}"></span>
            <span data-th-if="(${documentsCount} ==  1)"   data-th-utext="#{endingMainMsgSingular}" ></span>
            <span  data-th-if="(${documentsCount} >  1)"   data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for owner of the upload request */-->
        <!--/* upper message content for recipients of the upload request */-->
        <th:block data-th-if="(${!warnOwner})" >
          <!--/* Greetings */-->
          <th:block  data-th-replace="layout :: greetings(${requestRecipient.mail})" />
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${subject},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"   data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
            <th:block   data-th-replace="layout :: actionButtonLink(#{uploadFileBtn},${requestUrl})"/>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <!--/*Lower message content for the owner of the upload request */-->
    <th:block  data-th-if="(${warnOwner})">
      <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
      <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    </th:block>
    <!--/*Lower message content for the owner of the upload request */-->
    <!--/*Lower message content for recipients of the upload request */-->
    <th:block  data-th-if="(${!warnOwner})">
      <th:block  data-th-if="(${isCollective})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="!(${isCollective})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationActivationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgForRecipient =   L''''invitation de dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b>: :  <b>{2}</b> sera clôturée dans <b>{3} jours</b>
beginningMainMsgCollective =   Votre Invitation de dépôt collective: {0}, sera clôturée dans  <b>{1} jours</b>.
beginningMainMsgIndividual =   Votre Invitation de dépôt individuelle: {0}, sera clôturée dans  <b>{1} jours</b>.
endingMainMsgPlural = Il y a un total de <b> {0} fichiers </b> dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez actuellement envoyé  <b> {0} fichiers </b> dans l''''invitation de dépôt.
endingMainMsgSingular = Il y a au total <b>1 fichier </b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez actuellement envoyé  <b>1 fichier </b> dans l''''invitation de dépôt.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationActivationDate = Date d''''activation
invitationClosureDate =  Date de clôture
recipientsURequest = Destinataires
subjectForOwner =  Votre invitation de dépôt sera bientôt clôturée
subjectForRecipient = L''''invitation au dépôt de {0} {1} sera bientôt clôturée
uploadFileBtn = Déposer un fichier',messages_english='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s upload Request:  <b>{2}</b> is about to reach it''''s end date in <b>{3} days</b>
beginningMainMsgCollective = Your collective upload request: {0}, is about to be closed in  <b>{1} days</b>.
beginningMainMsgIndividual =  Your individual upload request: {0}, is about to be closed in <b>{1} days</b>.
endingMainMsgPlural = There are a total of <b> {0} files </b> in the Upload Request.
endingMainMsgPluralForRecipient = and so far you have sent <b> {0} files </b> in the Upload Request.
endingMainMsgSingular = There is a total of <b>1 file </b> in the upload request.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationActivationDate = Activation date
invitationClosureDate = Closure date
recipientsURequest = Recipients
subjectForOwner =  Your invitation is about to be closed.
subjectForRecipient =  {0} {1}''''s  invitation is about to be closed
uploadFileBtn = Upload a file',messages_russian='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s запрос на загрузку:  <b>{2}</b> приближается к окончанию срока действия через <b>{3} дня</b>
beginningMainMsgCollective = Ваш коллективный запрос на загрузку: {0}, закроется через  <b>{1} дня</b>.
beginningMainMsgIndividual =  Ваш индивидуальный запрос на загрузку: {0}, закроется через <b>{1} дня</b>.
endingMainMsgPlural = Всего загрузка содержит <b> {0} файлов </b>.
endingMainMsgPluralForRecipient = вы отправили <b> {0} файлов </b> в загрузку.
endingMainMsgSingular = Всего в репозитории of <b>1 файл </b>.
endingMainMsgSingularForRecipient = вы отправили <b>1 файл </b> в репозиторий.
filesInURDepot = Загруженные файлы
formatMailSubject = : {0}
invitationActivationDate = Дата активации
invitationClosureDate = Дата закрытия
recipientsURequest = Получатели
subjectForOwner =  Срок действия вашего приглашения заканчивается.
subjectForRecipient =  {0} {1}''''s срок действия вашего приглашения заканчивается.
uploadFileBtn = Загрузить файл',messages_vietnamese='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s yêu cầu tải lên:  <b>{2}</b> sắp tới ngày kết thúc trong <b>{3} ngày</b>
beginningMainMsgCollective = Yêu cầu tải lên chung của bạn: {0}, sắp được đóng trong  <b>{1} ngày</b>.
beginningMainMsgIndividual =  Yêu cầu tải lên cá nhân của bạn: {0}, sắp được đóng trong <b>{1} ngày</b>.
endingMainMsgPlural = Có tổng cộng <b> {0} files </b> trong yêu cầu tải lên.
endingMainMsgPluralForRecipient = và tính đến hiện tại bạn đã gửi <b> {0} files </b> trong yêu cầu tải lên.
endingMainMsgSingular = Có tổng cộng <b>1 file </b> trong yêu cầu tải lên.
endingMainMsgSingularForRecipient = và hiện tại bạn đã taỉ lên <b>1 file </b>trong thư mục.
filesInURDepot = Files được tải lên
formatMailSubject = : {0}
invitationActivationDate = Ngày kích hoạt
invitationClosureDate = Ngày đóng
recipientsURequest = Người nhận
subjectForOwner =  Lời mời của bạn sắp được đóng.
subjectForRecipient =  Lời mời của {0} {1}''''s sắp được đóng
uploadFileBtn = Tải lên 1 file' WHERE id=12;
UPDATE mail_content SET subject='[# th:if="${warnOwner}"]
           [( #{subjectForOwner(${subject})})]
       [/]
        [# th:if="${!warnOwner}"]
           [( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName},${subject})})]
       [/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content container*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})" >
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="!(${isCollective})"   data-th-utext="#{beginningMainMsgIndividual}"></span>
            <span  data-th-if="(${isCollective})"   data-th-utext="#{beginningMainMsgCollective}"></span>
            <span data-th-if="(${documentsCount} ==  1)"   data-th-utext="#{endingMainMsgSingular}" ></span>
            <span  data-th-if="(${documentsCount} >  1)"   data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for owner of the upload request */-->
        <!--/* upper message content for recipients of the upload request */-->
        <th:block data-th-if="(${!warnOwner})" >
          <!--/* Greetings */-->
          <th:block  data-th-replace="layout :: greetings(${requestRecipient.mail})" />
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"  data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of upper main-content container*/-->
  </section><!--/* End of uppermain-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <!--/*Lower message content for the owner of the upload request */-->
    <th:block  data-th-if="(${warnOwner})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    </th:block>
    <!--/*Lower message content for the owner of the upload request */-->
    <!--/*Lower message content for recipients of the upload request */-->
    <th:block  data-th-if="(${!warnOwner})">
      <th:block  data-th-if="(${isCollective})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="!(${isCollective})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgForRecipient = L''''invitation de Dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a expiré.
beginningMainMsgCollective = Votre Invitation de Dépôt collective a expiré.
beginningMainMsgIndividual = Votre Invitation de Dépôt a expiré.
endingMainMsgPlural = et vous avez reçu un total  de <b>{0} fichiers</b>.
endingMainMsgPluralForRecipient = et vous avez  envoyé  <b> {0} fichiers </b>.
endingMainMsgSingular = et vous avez  reçu au total <b>1 fichier</b>.
endingMainMsgSingularForRecipient = et vous avez  envoyé <b>1 fichier </b>.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate = Date  de clôture
invitationCreationDate =  Date d''''activation
recipientsURequest = Destinataires
subjectForOwner = Votre Invitation de Dépôt {0} est clôturée
subjectForRecipient = L'''' Invitation de Dépôt de {0} {1} intitulée {2} est clôturée',messages_english='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Upload Request has expired
beginningMainMsgCollective = Your collective Upload Request has expired
beginningMainMsgIndividual = Your Upload Request has expired
endingMainMsgPlural = and you have received a total of <b>{0} files</b>.
endingMainMsgPluralForRecipient = and you currently have sent  <b> {0} files </b>.
endingMainMsgSingular = and you have received a total of <b>1 file</b>.
endingMainMsgSingularForRecipient = and you currently have uploaded <b>1 file </b> to the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
recipientsURequest = Recipients
subjectForOwner = Your invitation {0} is now closed
subjectForRecipient =  {0} {1}''''s  invitation {2} is now closed',messages_russian='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Срок действия загрузки закончился
beginningMainMsgCollective = Срок действия вашего группового запроса загрузки закончился
beginningMainMsgIndividual = Срок действия загрузки закончился.
endingMainMsgPlural = Вы получили <b>{0} файлов</b>.
endingMainMsgPluralForRecipient = вы отправили всего <b> {0} файлов </b>.
endingMainMsgSingular = всего вы получили <b>1 файлов</b>.
endingMainMsgSingularForRecipient = вы загрузили в репозиторий <b>1 файл </b> .
filesInURDepot = Загружено файлов
formatMailSubject = : {0}
invitationClosureDate = Дата закрытия
invitationCreationDate = Дата активации
recipientsURequest = Получатели
subjectForOwner = Ваше приглашение  {0} больше не действительно
subjectForRecipient =  {0} {1}''''s  приглешение {2} больше не действительно',messages_vietnamese='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Yêu cầu tải lên đã hết hạn.
beginningMainMsgCollective = Yêu cầu tải chung của bạn đã hết hạn
beginningMainMsgIndividual = Yêu cầu tải lên của bạn đã hết hạn
endingMainMsgPlural = và bạn nhận được tổng cộng <b>{0} files</b>.
endingMainMsgPluralForRecipient = và bạn hiện tại đã gửi  <b> {0} files </b>.
endingMainMsgSingular = và bạn nhận được tổng cộng <b>1 file</b>.
endingMainMsgSingularForRecipient = và bạn đã tải lên <b>1 file </b> vào thư mục.
filesInURDepot = Các file được tải lên
formatMailSubject = : {0}
invitationClosureDate = Ngày đóng
invitationCreationDate = Ngày kích hoạt
recipientsURequest = Người nhận
subjectForOwner = Lời mời của bạn {0} bây giờ đã được đóng
subjectForRecipient =  {0} {1}''''s  lời mời {2} đã được đóng bây giờ' WHERE id=13;
UPDATE mail_content SET subject='[( #{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
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
                      <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${member.firstName} AND ${member.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${workGroupName})}">
                      </span>
                      <span th:unless="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${member.firstName} AND ${member.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsgAdmin(${workGroupName})}">
                      </span>
                      <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null"
                                      data-th-utext="#{simpleMsg(${workGroupName})}">
                      </span>
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
</html>',messages_french='subject = Les accès au groupe de travail {0} vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du groupe de travail <b>{2}</b>
mainMsgAdmin = Vous vous êtes retiré de votre groupe de travail  <b>{0}</b>
simpleMsg = Les accès au groupe de travail <b>{0}</b> vous ont été retirés.
workGroupNameTitle = Nom du groupe de travail',messages_english='subject = Your access to the workgroup {0} was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the workgroup  <b>{2}</b>
mainMsgAdmin = You have removed yourself from your own workgroup  <b>{0}</b>
simpleMsg =  Your access to the workgroup <b>{0}</b> was withdrawn.
workGroupNameTitle = Workgroup Name',messages_russian='subject = У вас больше нет доступа к рабочей группе {0}.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил вас из рабочей группы  <b>{2}</b>
mainMsgAdmin = Вы удалили себя из созданной вами рабочей группы  <b>{0}</b>
simpleMsg =  У вас больше нет доступа к рабочей группе <b>{0}</b>.
workGroupNameTitle = Название рабочей группы',messages_vietnamese='subject = Quyền truy cập của bạn đối với workgroup {0} đã bị thu hồi
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã xóa bạn khỏi workgroup  <b>{2}</b>
mainMsgAdmin = Bạn đã tự xóa bạn ra khỏi workgroup của bạn  <b>{0}</b>
simpleMsg =  Quyền truy cập của bạn đối với workgroup <b>{0}</b> đã bị thu hồi.
workGroupNameTitle = Tên Workgroup ' WHERE id=30;
UPDATE mail_content SET subject='[( #{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <!--/* Upper main-content*/-->
    <section id="main-content">
        <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
            <div id="section-content">
                <!--/* Greetings */-->
                <th:block data-th-replace="layout :: greetings(${member.account.firstName})"/>
                <!--/* End of Greetings  */-->
                <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${workGroupName}, ${actor.firstName},${actor.lastName})}">
                     </span>
                  </p>
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
</html>',messages_french='subject = Le groupe de travail {0} a été supprimé.
mainMsg = Le groupe de travail {0} a été supprimé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Nom du groupe de travail',messages_english='subject = The workgroup {0} has been deleted.
mainMsg = The workgroup {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Workgroup Name',messages_russian='subject = Рабочая группа {0} была удалена.
mainMsg = Рабочая группа {0} была удалена <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Название рабочей группы' ,messages_vietnamese='subject = Workgroup {0} đã bị xóa.
mainMsg = Workgroup {0} đã bị xóa bởi <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Tên Workgroup' WHERE id=39;
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
</html>',messages_french='workGroupCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au groupe de travail <br>
simpleMainMsg = Vous avez été ajouté au groupe de travail
subject = Vous avez été ajouté au groupe de travail {0}
workGroupRight = Droit par défaut
workGroupNameTitle = Nom du groupe de travail',messages_english='workGroupCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the workgroup <br>
simpleMainMsg = You have been added to the workgroup
subject = You have been added to the workgroup {0}
workGroupRight = Default right
workGroupNameTitle = Workgroup Name',messages_russian='workGroupCreationDateTitle = Дата создания
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> добавил вас в рабочую группу <br>
simpleMainMsg = Вас добавили в рабочую группу
subject = Вас добавили в рабочую группу {0}
workGroupRight = Права по умолчанию
workGroupNameTitle = Название рабочей группы',messages_vietnamese='workGroupCreationDateTitle = Ngày tạo
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã thêm bạn vào workgroup <br>
simpleMainMsg = Bạn đã được thêm vào workgroup
subject =Bạn đã được thêm vào workgroup {0}
workGroupRight = Quyền mặc định
workGroupNameTitle = Tên Workgroup' WHERE id=28;
UPDATE mail_content SET subject='[( #{subject(${document.name},${workGroupMember.node.name},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${workGroupMember.account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
           <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${workGroupDocumentLink}}" >
               link
             </a>
           <span th:if="${folder.nodeType.name} != ''ROOT_FOLDER''" data-th-utext="#{folderMsg}"></span>
           <span th:if="${folder.nodeType.name} != ''ROOT_FOLDER''">
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${folder.name}" th:href="@{${workGroupFolderLink}}" >
               link
             </a>
          </span>
          <span data-th-utext="#{workgroupMsg}"></span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupMember.node.name}" th:href="@{${workGroupLink}}" >
               link
             </a>
          <!--/* Activation link for initialisation of the guest account */-->
         </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${document.creationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{DocumentSize},${document.size})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupCreationDateTitle = Date de création
DocumentSize = Taille du document
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a ajouté un nouveau document <br>
folderMsg = dans le dossier
workgroupMsg = du groupe de travail
subject = Le Document {0} a été ajouté à {1}',messages_english='workGroupCreationDateTitle = Creation date
DocumentSize = Document size
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> uploaded a new document<br>
folderMsg = into the folder
workgroupMsg = on the workgroup
subject = The document {0} was uploaded in the workgroup {1}',messages_russian='workGroupCreationDateTitle = Дата создания
DocumentSize = Размер документа
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> загрузил новый документ<br>
folderMsg = в папку
workgroupMsg = рабочей группы
subject = Документ {0} был загружен в рабочую группу {1}',messages_vietnamese='workGroupCreationDateTitle = Ngày tạo
DocumentSize = Kích cỡ tài liệu
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã tảu liên một tài liệu mới<br>
folderMsg = vào thư mục
workgroupMsg = trong workgroup
subject = Tài liệu {0} đã được tải lên workgroup {1}' WHERE id=44;
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
</html>',messages_french='workGroupUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le groupe de travail
mainMsgNext = ont été mis à jour
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Vos droits sur le groupe de travail {0} ont été mis à jour
workGroupRight =  Nouveau droit
workGroupNameTitle = Nom du groupe de travail',messages_english='workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the workgroup
mainMsgNext= have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the workgroup {0} was updated.
workGroupRight = Current right
workGroupNameTitle = Workgroup Name',messages_russian='workGroupUpdatedDateTitle = Дата обновления
mainMsg = Ваш статус в рабочей группе
mainMsgNext= был обновлен
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Ваш статус в рабочей группе {0} был обновлен.
workGroupRight = Действующий статус
workGroupNameTitle = Название рабочей группы',messages_vietnamese='workGroupUpdatedDateTitle = Ngày cập nhật
mainMsg = Quyền của bạn trong workgroup
mainMsgNext= đã được cập nhật
mainMsgNextBy= bởi <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Quyền của bạn trong workgroup {0} đã được cập nhật
workGroupRight = Quyền hiện tại
workGroupNameTitle = Tên Workgroup' WHERE id=29;
UPDATE mail_content SET subject='[( #{subject(${document.name},${workGroupMember.node.name},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${workGroupMember.account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
           <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${workGroupDocumentLink}}" >
               link
             </a>
           <span th:if="${folder.nodeType.name} != ''ROOT_FOLDER''" data-th-utext="#{folderMsg}"></span>
           <span th:if="${folder.nodeType.name} != ''ROOT_FOLDER''">
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${folder.name}" th:href="@{${workGroupFolderLink}}" >
               link
             </a>
          </span>
          <span data-th-utext="#{workgroupMsg}"></span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupMember.node.name}" th:href="@{${workGroupLink}}" >
               link
             </a>
          <span data-th-utext="#{revisionMsg}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
         </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupModificationDateTitle},${document.modificationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{DocumentSize},${document.size})"/>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupModificationDateTitle = Date de modification
DocumentSize = Taille du document
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a modifié le document <br>
folderMsg = dans le dossier
workgroupMsg = du groupe de travail
revisionMsg = en ajoutant une nouvelle révision
subject = Le Document {0} a été modifié à {1}
name: ',messages_english='workGroupModificationDateTitle = Modification date
DocumentSize = Document size
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> updated the document<br>
folderMsg = into the folder
workgroupMsg = on the workgroup
revisionMsg = by adding a new document revision
subject = The document {0} was updated in the workgroup {1}',messages_russian='workGroupModificationDateTitle = Дата изменения
DocumentSize = Размер документа
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> обновил документ<br>
folderMsg = в папке
workgroupMsg = рабочей группы
revisionMsg = путем добавления новой редакции документа
subject = Документ {0} был обновлен в рабочей группе {1}',messages_vietnamese='workGroupModificationDateTitle = NGày chỉnh sửa
DocumentSize = Kích cỡ tài liệu
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã chỉnh sửa tài liệu<br>
folderMsg = trong thư mục
workgroupMsg = trong workgroup
revisionMsg = bới thêm một phiên bản mới của tài liệu
subject = Tài liệu {0} đã được cập nhật tron workgroup {1}' WHERE id=45;
UPDATE mail_content SET subject='[( #{subject(${workSpaceName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <!--/* Upper main-content*/-->
    <section id="main-content">
        <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
            <div id="section-content">
                <!--/* Greetings */-->
                <th:block data-th-replace="layout :: greetings(${member.account.firstName})"/>
                <!--/* End of Greetings  */-->
                <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${workSpaceName}, ${actor.firstName},${actor.lastName})}">
                     </span>
                  </p>
                </p> <!--/* End of Main email  message content*/-->
            </div><!--/* End of section-content*/-->
        </div><!--/* End of main-content container*/-->
    </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
    <div th:if="${!nestedNodes.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="node : ${nestedNodes}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="#{displayNestedNodeName(${node.name})}"/>
          </li>
      </ul>
    </div>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='subject = Le Workspace {0} a été supprimé.
mainMsg = Le Workspace {0} a été supprimé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=Vous avez automatiquement été supprimé des groupes de travail suivants :
displayNestedNodeName:{0}',messages_english='subject = The Workspace {0} has been deleted.
mainMsg = The Workspace {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=You have been automatically removed from the following workgroups:
workGroupNameTitle = Workgroup Name
displayNestedNodeName:{0}',messages_russian='subject = Рабочее пространство {0} было удалено.
mainMsg = Рабочее пространство {0} было удалено <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList= Вы были автоматически удалены из следующих рабочих групп:
displayNestedNodeName:{0}',messages_vietnamese='subject = Workspace {0} đã bị xóa
mainMsg = Workspace {0} đã được xóa bởi <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=Bạn đã được tự động xóa khỏi các workgroup sau:
workGroupNameTitle = Tên Workgroup
displayNestedNodeName:{0}' WHERE id=40;
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
</html>',messages_french='subject = Les accès au workspace {0} et à ses workgroups vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du workspace <b>{2}</b>
simpleMsg = Les accès au workspace <b>{0}</b> vous ont été retirés.
workGroupNameTitle = Nom du workspace',messages_english='subject = Your access to the workspace {0}  and its workgroups was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the workspace  <b>{2}</b>
simpleMsg =  Your access to the workspace <b>{0}</b> was withdrawn.
workGroupNameTitle = Workspace Name',messages_russian='subject = Ваш доступ к рабочему пространству {0}  и его рабочим группам был отозван
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил вас из рабочего пространства  <b>{2}</b>
simpleMsg =  Ваш доступ к рабочему пространству <b>{0}</b> был отозван.
workGroupNameTitle = Название рабочего пространства', messages_vietnamese='subject = Quyền truy cập của bạn đối với workspace {0} và các workgroups bên trong đã bị thu hồi
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã xóa bạn khỏi workspace <b>{2}</b>
simpleMsg =  Quyền truy cập của bạn đối với workspace <b>{0}</b> đã bị thu hồi.
workGroupNameTitle = Tên workspace ' WHERE id=36;
UPDATE mail_content SET subject='[( #{subject(${workSpaceName})})]',body='<!DOCTYPE html>
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
              <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workSpaceName}" th:href="@{${workSpaceLink}}" >
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
    <th:block th:switch="${workSpaceMember.role.name}">
      <p th:case="''WORK_SPACE_ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleAdminTitle})"/></p>
      <p th:case="''WORK_SPACE_WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleWriteTitle})"/></p>
      <p th:case="''WORK_SPACE_READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleReadTitle})"/></p>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceNameTitle},${workSpaceName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workSpaceMemberCreationDateTitle},${workSpaceMember.creationDate})"/>
    <div th:if="${!childMembers.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="member : ${childMembers}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="#{displayWorkSpaceAndRole(${member.node.name},${member.role.name})}"/>
          </li>
      </ul>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workSpaceMemberCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au WorkSpace: <br>
simpleMainMsg = Vous avez été ajouté au WorkSpace
subject = Vous avez été ajouté au WorkSpace {0}
workSpaceRight = Droit par défaut
workSpaceNameTitle = Nom du WorkSpace
nestedWorkGroupsList=Vous avez automatiquement été ajouté aux groupes de travail suivants :
displayWorkSpaceAndRole ={0} avec un rôle <span style="text-transform:uppercase">{1}</span>',messages_english='workSpaceMemberCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the WorkSpace: <br>
simpleMainMsg = You have been added to the WorkSpace
subject = You have been added to the WorkSpace {0}
workSpaceRight = Default right
workSpaceNameTitle = WorkSpace Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayWorkSpaceAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role',messages_russian='workSpaceMemberCreationDateTitle = Дата создания
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> добавил вас в рабочее пространство: <br>
simpleMainMsg = Вы были добавлены в рабочее пространство
subject = Вы были добавлены в рабочее пространство {0}
workSpaceRight = Права по умолчанию
workSpaceNameTitle = Название рабочего пространства
nestedWorkGroupsList= Вы были автоматически добавлены в следующие рабочие группы:
displayWorkSpaceAndRole ={0} с ролью <span style="text-transform:uppercase">{1}</span>',messages_vietnamese='workSpaceMemberCreationDateTitle = Ngày tạo
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã thêm bạn vào WorkSpace: <br>
simpleMainMsg = Bạn đã được thêm vào WorkSpace
subject = Bạn đã đươcj thêm vào Workspace {0}
workSpaceRight = Quyền mặc định
workSpaceNameTitle = Tên WorkSpace
nestedWorkGroupsList=Bạn đã được tự động thêm vào các workgroup sau:
displayWorkSpaceAndRole ={0} với <span style="text-transform:uppercase">{1}</span> quyền' WHERE id=34;
UPDATE mail_content SET subject='[(#{subject(${workSpaceName})})]',body='<!DOCTYPE html>
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
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workSpaceName}" th:href="@{${workSpaceLink}}" >
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
    <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceNameTitle},${workSpaceName})"/>
    <th:block th:switch="${workSpaceMember.role.name}">
      <p th:case="''WORK_SPACE_ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleAdminTitle})"/></p>
      <p th:case="''WORK_SPACE_WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleWriteTitle})"/></p>
      <p th:case="''WORK_SPACE_READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleReadTitle})"/></p>
    </th:block>
    <th:block th:switch="${workSpaceMember.nestedRole.name}">
      <p th:case="''ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/></p>
      <p th:case="''CONTRIBUTOR''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/></p>
    </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{workSpaceMemberUpdatedDateTitle},${workSpaceMember.modificationDate})"/>
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
</html>',messages_french='workSpaceMemberUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le WorkSpace
mainMsgNext = et dans ses WorkGroups contenus ont été mis à jour
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Vos droits sur le WorkSpace {0} ont été mis à jour
workSpaceRight = Droit sur le WorkSpace
workGroupRight =  Droit sur le groupe de travail
workSpaceNameTitle = Nom du WorkSpace
nestedWorkGroupsList = Liste des workgoups
nbrWorkgoups = Nombre de groupe de travail mis à jours',messages_english='workSpaceMemberUpdatedDateTitle = Updated date
mainMsg = Your roles on the WorkSpace
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your roles on the WorkSpace {0} was updated.
workSpaceRight = WorkSpace right
workGroupRight = Workgroup right
workSpaceNameTitle = WorkSpace Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups',messages_russian='workSpaceMemberUpdatedDateTitle = Дата обновления
mainMsg = Ваши роли в рабочем пространстве
mainMsgNext= и рабочих группах внутри него были обновлены
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Ваши роли в рабочем пространстве {0} обновлены.
workSpaceRight = Права в рабочем пространстве
workGroupRight = Права в рабочей группе
workSpaceNameTitle = Название рабочего пространства
nestedWorkGroupsList = Список рабочих групп
nbrWorkgoups = Количество обновленных рабочих групп',messages_vietnamese='workSpaceMemberUpdatedDateTitle = NGaỳ cập nhật
mainMsg = Quyền của bạn trong workspace
mainMsgNext= và các workgroup bên trong đã được cập nhật
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Quyền của bạn trong WorkSpace {0} đã được cập nhật
workSpaceRight = Quyền trong WorkSpace
workGroupRight = Quyền trong Workgroup
workSpaceNameTitle = Tên WorkSpace
nestedWorkGroupsList = Danh sách Workgroups
nbrWorkgoups = Số workgroup được cập nhật' WHERE id=35;

-- mail footer

UPDATE mail_footer SET messages_french='learnMoreAbout=En savoir plus sur
productOfficialWebsite=http://www.linshare.org/',messages_english='learnMoreAbout=Learn more about
productOfficialWebsite=http://www.linshare.org/',messages_russian='learnMoreAbout=Узнать больше
productOfficialWebsite=http://www.linshare.org/',messages_vietnamese='learnMoreAbout=Tìm hiểu chi tiết về
productOfficialWebsite=http://www.linshare.org/',footer='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <body>
      <div data-th-fragment="email_footer">
         <td style="border-collapse:collapse;padding: 6px 0 4px 17px;" valign="top">
            <p style="margin: 0; font-size: 10px;">
               <span th:text="#{learnMoreAbout}">Learn more about</span>
               <a th:href="@{#{productOfficialWebsite}}"  target="_blank" style="text-decoration:none; color:#a9a9a9;">
                 <strong th:text="#{productName}">LinShare</strong>™
               </a>
            </p>
         </td>
         <td style="border-collapse:collapse; padding:  6px 17px 4px 0;"  valign="top" width="60">
            <img alt="libre-and-free" height="9"
               src="cid:logo.libre.and.free@linshare.org"
               style="line-height:100%;width:60px;height:9px;padding:0" width="60" />
         </td>
      </div>
   </body>
</html>' WHERE id=1;

-- mail layout

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
