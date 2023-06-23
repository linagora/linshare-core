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
