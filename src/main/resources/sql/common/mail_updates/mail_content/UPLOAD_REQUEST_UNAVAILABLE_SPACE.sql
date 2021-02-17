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
subject =  {0} не может загрузить файл, так как недостаточно свободного места' WHERE id=11;