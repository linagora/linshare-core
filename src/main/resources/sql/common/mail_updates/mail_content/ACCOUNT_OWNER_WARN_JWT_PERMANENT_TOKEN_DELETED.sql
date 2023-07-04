UPDATE mail_content SET subject='[(#{subject})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${recipient.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                      <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${recipient.firstName} AND ${recipient.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                       <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} == ${recipient.firstName} AND ${recipient.lastName} == ${owner.lastName})"
                                      data-th-utext="#{mainMsgOwner(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                     </span>
                  </p>
                      <span data-th-utext="#{endMsg}"></span>
                      <span>
                             <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="#{tokenLinkEndOfLine(${jwtTokenLink})}" th:href="@{${jwtTokenLink}}" >
                            </a>
                     </span>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
           <th:block data-th-replace="layout :: infoStandardArea(#{tokenLabel},${label})"/>
           <th:block data-th-replace="layout :: infoDateArea(#{tokenCreationDate},${creationDate})"/>
           <div data-th-if="${!#strings.isEmpty(description)}">
             <th:block data-th-replace="layout :: infoStandardArea(#{tokenDescription},${description})"/>
           </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='subject = Suppression d''''un jeton d''''accès permanent
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a supprimé un jeton d''''accès permanent: {2}, pour votre compte.
mainMsgOwner = Vous avez supprimé un jeton d''''accès permanent: {2}, pour votre compte.
tokenCreationDate = Date de création
endMsg = Vous pouvez consulter les jetons d''''accès liés à votre compte
tokenLinkEndOfLine = ici
tokenLabel = Nom
tokenDescription = Description
tokenIdentifier = Identifiant',messages_english='subject = Deletion of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has deleted a permanent authentication token: {2}, for your account.
mainMsgOwner = You have deleted a permanent authentication token: {2}, for your account.
tokenCreationDate = Creation date
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Name
tokenDescription = Description',messages_russian='subject = Удаление постоянного токена аутентификации
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has deleted a permanent authentication token: {2}, for your account.
mainMsgOwner = You have deleted a permanent authentication token: {2}, for your account.
tokenCreationDate = Дата создания
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Имя
tokenDescription = Описание' WHERE id=33;