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
uploadFileBtn = Upload a file',messages_russian='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s upload Request:  <b>{2}</b> is about to reach it''''s end date in <b>{3} days</b>
beginningMainMsgCollective = Your collective upload request: {0}, is about to be closed in  <b>{1} days</b>.
beginningMainMsgIndividual =  Your individual upload request: {0}, is about to be closed in <b>{1} days</b>.
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
uploadFileBtn = Загрузить файл' WHERE id=12;