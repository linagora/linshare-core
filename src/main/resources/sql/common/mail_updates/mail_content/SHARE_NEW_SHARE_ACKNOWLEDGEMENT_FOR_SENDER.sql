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
