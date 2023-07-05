UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has deleted your access to the depot : : subject.
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
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='deletionDate = Accès au dépôt retiré le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a retiré votre accès au dépôt de l''''invitation intitulée : {2}.
subject = {0} {1} a supprimé votre accès au dépôt : {2}',messages_english='deletionDate = Deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has removed your access to the Upload Request : {2}.
subject = {0} {1} has removed your access to the Upload Request : {2}',messages_russian='deletionDate = Дата удаления
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> закрыл ваш доступ к загрузке {2}.
subject = {0} {1} закрыл ваш доступ к загрузке {2}' WHERE id=22;