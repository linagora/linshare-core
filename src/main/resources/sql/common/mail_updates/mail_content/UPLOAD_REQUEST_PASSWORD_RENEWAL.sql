UPDATE mail_content SET subject='[( #{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                     </span>
                     <br/>
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès au dépôt
closureDate = Dépôt disponible jusqu''''au
mainMsg = <b>{0} {1}</b> a modifié le mot de passe d''''accès à l''''Invitation de Dépôt : {2}.
msgProtected = Vous trouverez ci-dessous le nouveau mot de passe ainsi que le lien d''''accès.
password = Mot de passe
subject = {0} {1} vous envoie le nouveau mot de passe du dépôt : {2}',messages_english='buttonMsg = Access to the depot
closureDate = Depot closure date
mainMsg = <b>{0} {1}</b> has changed the password of the Upload Request : {2}
msgProtected = You may find the new password below as well as the access link.
password = Password
subject = {0} {1} sent you the new password for the depot: {2}' WHERE id=19;