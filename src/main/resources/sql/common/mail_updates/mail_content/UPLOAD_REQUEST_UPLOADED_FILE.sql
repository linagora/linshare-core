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
buttonLabel = View
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
