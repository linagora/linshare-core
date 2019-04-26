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
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has deleted the file <b>{2} </b>from the depot  : {3}.
subject = {0} {1} has deleted {2} from the depot',messages_russian='closureDate = Срок действия загрузки
deletionDate = Дата удаления
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> удалил файл <b>{2} </b> из хранилища {3}.
subject = {0} {1} удалил файл {2} из хранилища' WHERE id=24;