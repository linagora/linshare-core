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
link = ссылке' WHERE id=2;