UPDATE "mail_content" SET "subject"='[(#{subject(${subject})})]',"body"='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${request.subject})}">
                      Your Upload Request repository labeled $subject is now activated.
                     </span>
                     <span data-th-text="#{msgLink}">In order to access it click the link below.</span>
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
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
             <div data-th-if="(${totalMaxDepotSize})">
                   <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',"messages_french"='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille autorisée
mainMsg = Votre dépôt intitulé <b>{0}</b> est désormais actif.
msgLink = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
recipientsOfDepot = Destinataires
subject = Votre invitation de dépôt {0} est désormais active',"messages_english"='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = Your Upload Request labeled <b>{0}</b> is now active.
msgLink = Access it by following the link below.
recipientsOfDepot = Recipients
subject = Your Upload Request : {0}, is now active',"messages_russian"='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = Your Upload Request labeled <b>{0}</b> is now active.
msgLink = Access it by following the link below.
recipientsOfDepot = Recipients
subject = Your Upload Request : {0}, is now active' WHERE "id"=17;