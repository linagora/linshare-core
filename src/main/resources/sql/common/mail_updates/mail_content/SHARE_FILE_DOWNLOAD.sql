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