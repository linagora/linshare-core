-- Postgresql migration script template

-- Migration script to upgrade from LinShare 6.5.1 to LinShare 6.5.2.

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Here your queries

-- update tables
-- Update: Share file download notification template
-- Purpose: Display contact list name when recipient is from an invisible list
UPDATE mail_content
SET subject='[# th:if="${!anonymous}"]
  [# th:if="${shareRecipient.contactListName != null}"]
    [( #{subjectContactList(${shareRecipient.contactListName},${share.name})})]
  [/]
  [# th:if="${shareRecipient.contactListName == null}"]
    [( #{subject(${shareRecipient.firstName},${shareRecipient.lastName},${share.name})})]
  [/]
[/]
[# th:if="${anonymous}"]
  [( #{subjectAnonymous(${shareRecipient.mail},${share.name})})]
[/]',
    body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
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
          <th:block th:if="${!anonymous}">
            <span th:if="${shareRecipient.contactListName != null}"
                  data-th-utext="#{mainMsgContactList(${shareRecipient.contactListName})}">
              One member of the contact list has downloaded your file
            </span>
            <span th:unless="${shareRecipient.contactListName != null}"
                  data-th-utext="#{mainMsgInt(${shareRecipient.firstName},${shareRecipient.lastName})}">
              Peter WILSON has downloaded your file
            </span>
          </th:block>
          <th:block th:if="${anonymous}">
            <span data-th-utext="#{mainMsgExt(${shareRecipient.mail})}">
              unknown@domain.com has downloaded your file
            </span>
          </th:block>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}">
                  filename.ext
              </a>
          </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
<th:block th:if="${!anonymous}">
      <th:block th:if="${shareRecipient.contactListName != null}">
        <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle}, ${shareRecipient.contactListName})"/>
      </th:block>
      <th:block th:if="${shareRecipient.contactListName == null}">
        <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle}, ~{::#recipientName})">
          <span id="recipientName">
            <span th:text="${shareRecipient.firstName}"/> <span style="text-transform:uppercase" th:text="${shareRecipient.lastName}"/>
          </span>
        </th:block>
      </th:block>
    </th:block>
    <th:block th:if="${anonymous}">
      <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle},${shareRecipient.mail})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{downloadDate},${actionDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${expiryDate})"/>
    <th:block th:if="${sharesCount} > 1">
      <th:block data-th-replace="layout :: infoFileListUploadState(#{common.filesInShare},${shares})"/>
    </th:block>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',
    messages_french='downloadDate = Téléchargé le
fileNameEndOfLine = {0}.
mainMsgExt = Le destinataire externe <b>{0}</b> a téléchargé votre fichier
mainMsgInt = <b>{0} <span style="text-transform:uppercase">{1}</span></b> a téléchargé votre fichier
mainMsgContactList = Un membre de la liste de contacts <b>{0}</b> a téléchargé votre fichier
shareRecipientTitle = Destinataire
subject = {0} {1} a téléchargé {2}
subjectAnonymous = {0} a téléchargé {1}
subjectContactList = Un membre de {0} a téléchargé {1}',
    messages_english='downloadDate = Download date
fileNameEndOfLine = {0}.
mainMsgExt = The external recipient <b>{0}</b> has downloaded your file
mainMsgInt = <b>{0} <span style="text-transform:uppercase">{1}</span></b> has downloaded your file
mainMsgContactList = One member of the contact list <b>{0}</b> has downloaded your file
shareRecipientTitle = Recipient
subject = {0} {1} has downloaded {2}
subjectAnonymous = {0} has downloaded {1}
subjectContactList = A member of {0} has downloaded {1}',
    messages_russian='downloadDate = Дата загрузки
fileNameEndOfLine = {0}.
mainMsgExt = Внешний пользователь <b>{0}</b> скачал(а) ваш файл
mainMsgInt = <b>{0} <span style="text-transform:uppercase">{1}</span></b> скачал(а) ваш файл
mainMsgContactList = Один участник списка контактов <b>{0}</b> скачал(а) ваш файл
shareRecipientTitle = Получатель
subject = {0} {1} был скачан {2}
subjectAnonymous = {0} был скачан {1}
subjectContactList = Участник {0} скачал(а) {1}',
    messages_vietnamese='downloadDate = Ngày tải
fileNameEndOfLine = {0}.
mainMsgExt = Người nhận ngoài <b>{0}</b> đã tải xuống file của bạn
mainMsgInt = <b>{0} <span style="text-transform:uppercase">{1}</span></b> đã tải xuống file của bạn
mainMsgContactList = Một thành viên của danh sách liên hệ <b>{0}</b> đã tải xuống file của bạn
shareRecipientTitle = Người nhận
subject = {0} {1} đã tải xuống {2}
subjectAnonymous = {0} đã tải xuống {1}
subjectContactList = Thành viên của {0} đã tải xuống {1}'
WHERE id = 4;

-- Update: Share warn undownloaded file shares template
-- Purpose: Display contact list names with download statistics for invisible lists

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
    <div style="margin-bottom:17px;">
      <span style="font-weight:bold;" data-th-text="#{sharedFiles}">Shared files</span>
      <table>
        <th:block th:each="document : ${documents}">
          <tr>
            <td style="color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>
            <td>
              <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px"
                 th:href="@{${document.href}}" data-th-utext="${document.name}">
                document.pdf
              </a>
            </td>
          </tr>
        </th:block>
      </table>
    </div>
    <div style="margin-bottom:17px;" data-th-if="${recipients != null and !recipients.isEmpty()}">
      <span style="font-weight:bold;" data-th-text="#{common.recipients}">Recipients</span>
      <table>
        <th:block th:each="recipient : ${recipients}">
          <tr>
            <td style="color:#787878;font-size: 22px;" width="20" align="center" valign="top">&bull;</td>
            <td>
               <th:block data-th-if="${recipient.contactListName != null and !recipient.contactListName.isEmpty()}">
                <span style="color:#787878;font-size:13px;font-style:italic;">
                  <th:block data-th-text="${recipient.contactListName}">Contact List Name</th:block>
                  <th:block data-th-if="${recipient.notDownloadedCount != null and recipient.notDownloadedCount > 0}">
                    <span data-th-text="#{contactListUndownloaded(${recipient.notDownloadedCount} , ${recipient.totalMembersCount})}">
                      (2/5 members have not downloaded)
                    </span>
                  </th:block>
                </span>
              </th:block>
              <th:block data-th-if="${recipient.contactListName == null or recipient.contactListName.isEmpty()}">
                <th:block data-th-if="${!#strings.isEmpty(recipient.lastName)}">
                  <span style="color:#787878;font-size:13px;">
                    <th:block data-th-utext="${recipient.firstName}" />
                    <th:block data-th-utext="${recipient.lastName}" />
                  </span>
                </th:block>
                <th:block data-th-if="${#strings.isEmpty(recipient.lastName)}">
                  <span style="color:#787878;font-size:13px;"
                        data-th-utext="${recipient.mail}">
                    user@example.com
                  </span>
                </th:block>
              </th:block>
            </td>
          </tr>
        </th:block>
      </table>
    </div>

    <!--/* Dates */-->
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareGroup.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shareGroup.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='downloadStatesTile = Etat de téléchargement
mainMsgplural = Certains destinataires n''''ont pas téléchargés <b>{0} fichiers</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
mainMsgSingular = Certains destinataires n''''ont pas téléchargés <b>{0} fichier</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
subjectPlural = Rappel de non-téléchargement : {0} fichiers n''''ont pas été téléchargés.
subjectSingular = Rappel de non-téléchargement :  {0} fichier n''''a pas été téléchargé.
sharedFiles = Fichiers partagés
contactListUndownloaded = ({0}/{1} membres n''''ont pas téléchargé)',
                        messages_english='downloadStatesTile = Downloads states
mainMsgplural = Some recipients have not downloaded <b>{0} files</b>. You may find further details of the recipients downloads below.
mainMsgSingular = Some recipients have not downloaded <b>{0} file</b>. You may find further details of the recipients downloads below.
subjectPlural = Undownloaded shared files alert : {0} files have not been downloaded yet.
subjectSingular = Undownloaded shared files alert : {0} file have not been downloaded yet.
sharedFiles = Shared files
contactListUndownloaded = ({0}/{1} members have not downloaded)',
                        messages_russian='downloadStatesTile = Статус загрузки
mainMsgplural = Некоторые получатели рассылки не скачали <b>{0} файлов</b>. Вы можете найти детали о получателях рассылки ниже.
mainMsgSingular = Некоторые получатели рассылки не скачали <b>{0} файлов</b>. Вы можете найти детали о получателях рассылки ниже.
subjectPlural = Уведомдение о не скачанных файлах: {0} файлов были не скачанны.
subjectSingular = Уведомдение о не скачанных файлах: {0} файлов были не скачанны.
sharedFiles = Общие файлы
contactListUndownloaded = ({0}/{1} участников не скачали)',
                        messages_vietnamese='downloadStatesTile = Hiện trạng tải xuống
mainMsgplural = Một vài người nhận đã không tải  <b>{0} files</b>. Bạn có thể xem thông tin chi tiết hơn về việc tải xuống của người nhận dưới đây.
mainMsgSingular = Some recipients have not downloaded <b>{0} file</b>. Bạn có thể xem thông tin chi tiết hơn về việc tải xuống của người nhận dưới đây.
subjectPlural = Thông báo chưa tải xuống file chia sẻ : {0} files vẫn chưa được người nhận tải xuống.
subjectSingular = Thông báo chưa tải xuống file chia sẻ : {0} file vẫn chưa được người nhận tải xuống.
sharedFiles = Tập tin được chia sẻ
contactListUndownloaded = ({0}/{1} thành viên chưa tải xuống)'
WHERE id = 7;

-- Update: Share new share acknowledgement for sender template
-- Purpose: Display contact list names in share confirmation emails
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
    <span style="font-weight:bold;" data-th-text="#{common.recipients}">Recipients</span>
      <div class="recipient-info">
      <div th:each="recipient : ${recipients}">
        <span th:if="${recipient.contactListName != null}" th:text="${recipient.contactListName}"/>
        <span th:if="${recipient.contactListName == null}">
          <span th:if="${recipient.firstName != null and recipient.lastName != null}">
            <span th:text="${recipient.firstName}"/>
            <span style="text-transform:uppercase" th:text="${recipient.lastName}"/>
          </span>
          <span th:if="${recipient.firstName == null or recipient.lastName == null}" th:text="${recipient.mail}"/>
        </span>
      </div>
    </div>
    <div style="margin-bottom: 16px;"></div>
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

-- Update: Share warn sender about share expiration without download template
-- Purpose: Display contact list name for expiration warnings
UPDATE mail_content
SET subject='[(#{subject})]',
    body='<!DOCTYPE html>
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
          <span data-th-utext="#{endingMainMsgShort(${daysLeft})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <div th:if="${recipient.contactListName != null}">
      <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle}, ${recipient.contactListName})"/>
    </div>
    <div th:unless="${recipient.contactListName != null}">
      <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle},#{name(${recipient.firstName}, ${recipient.lastName})})"/>
    </div>
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',
    messages_french='accessToLinshareBTn = Votre partage expire bientôt
shareRecipientTitle =  Destinataire
shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg =  expire dans {0} jours sans avoir été téléchargé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
endingMainMsgShort = expire dans {0} jours sans avoir été téléchargé.
subject = Votre partage expire bientôt et n''''a pas encore été téléchargé
name = {0} {1}
fileNameEndOfLine = {0}',
    messages_english='accessToLinshareBTn = Your share will expire soon
shareRecipientTitle = Recipient
shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg =  will expire in {0} days and has not been downloaded by the recipient <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
endingMainMsgShort = will expire in {0} days and has not been downloaded.
subject = Your share will expire soon and has not been downloaded
name = {0} {1}
fileNameEndOfLine = {0}',
    messages_russian='accessToLinshareBTn = Срок действия вашей рассылки скоро закончится
shareRecipientTitle = Получатель
shareFileTitle = Файл рассылки
shareCreationDateTitle = Дата создания
shareExpiryDateTitle = Дата истечения срока действия
activationLinkTitle = Ссылка активации
beginningMainMsg = Срок действия файла рассылки
endingMainMsg =  закончится через {0} дней, а файла не были скачаны получателем <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
endingMainMsgShort = закончится через {0} дней, а файла не были скачаны.
subject = Срок действия рассылки скоро закончится, а файлы не были скачаны
name = {0} {1}
fileNameEndOfLine = {0}',
    messages_vietnamese='accessToLinshareBTn = Tài liệu chia sẻ của bạn sắp hết hạn
shareRecipientTitle = Người nhận
shareFileTitle = Tài liệu chia sẻ
shareCreationDateTitle = Ngày tạo
shareExpiryDateTitle = Ngày hết hạn
activationLinkTitle = Đường dẫn
beginningMainMsg =  Tài liệu chia sẻ
endingMainMsg =  sẽ hết hạn trong {0} ngày và người nhận vẫn chưa tải về <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
endingMainMsgShort = sẽ hết hạn trong {0} ngày và người nhận vẫn chưa tải về.
subject = Tài liệu chia sẻ của bạn sắp hết hạn và vẫn chưa được tải về
name = {0} {1}
fileNameEndOfLine = {0}' WHERE id=26;
---- End of your queries
-- ===================================================================
-- SUMMARY OF CHANGES:
-- 1. Updated 4 email templates to support anonymous contact list display
-- 2. Added contactListName variable handling in templates
-- 3. Implemented download statistics display for invisible lists
-- 4. Maintained backward compatibility with individual recipient display
-- 5. Added multi-language support for new features
-- ===================================================================
-- Upgrade LinShare version



COMMIT;
