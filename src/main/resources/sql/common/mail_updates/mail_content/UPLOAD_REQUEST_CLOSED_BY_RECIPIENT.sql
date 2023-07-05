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
totalUploaded = {0} файлов' WHERE id=14;