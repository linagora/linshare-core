UPDATE "mail_content" SET "subject"='[# th:if="${warnOwner}"] 
           [( #{subjectForOwner(${subject})})]
       [/]
        [# th:if="${!warnOwner}"]
           [( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName},${subject})})]
       [/]',"body"='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content container*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})" >
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="(${isRestricted})"   data-th-utext="#{beginningMainMsgUnGrouped}"></span>
            <span  data-th-if="!(${isRestricted})"   data-th-utext="#{beginningMainMsgGrouped}"></span>
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
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"  data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of upper main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div   th:assert="${!#strings.isEmpty(body)}" th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section><!--/* End of uppermain-content*/-->
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
      <th:block  data-th-if="!(${isRestricted})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="(${isRestricted})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',"messages_french"='beginningMainMsgForRecipient = L''''invitation de Dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a expiré.
beginningMainMsgGrouped = Votre Invitation de Dépôt groupée a expiré.
beginningMainMsgUnGrouped = Votre Invitation de Dépôt a expiré.
endingMainMsgPlural = et vous avez reçu un total  de <b>{0} fichiers</b>.
endingMainMsgPluralForRecipient = et vous avez  envoyé  <b> {0} fichiers </b>.
endingMainMsgSingular = et vous avez  reçu au total <b>1 fichier</b>.
endingMainMsgSingularForRecipient = et vous avez  envoyé <b>1 fichier </b>.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate = Date  de clôture
invitationCreationDate =  Date d''''activation
msgTitle = Message lié à l''''Invitation de Dépôt :
recipientsURequest = Destinataires
subjectForOwner = Votre Invitation de Dépôt {0} est clôturée
subjectForRecipient = L'''' Invitation de Dépôt de {0} {1} intitulée {2} est clôturée',"messages_english"='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Upload Request has expired
beginningMainMsgGrouped = Your grouped Upload Request has expired
beginningMainMsgUnGrouped = Your Upload Request has expired
endingMainMsgPlural = and you have received a total of <b>{0} files</b>.
endingMainMsgPluralForRecipient = and you currently have sent  <b> {0} files </b>.
endingMainMsgSingular = and you have received a total of <b>1 file</b>.
endingMainMsgSingularForRecipient = and you currently have uploaded <b>1 file </b> to the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle = Upload Request''''s  attached message :
recipientsURequest = Recipients
subjectForOwner = Your invitation {0} is now closed
subjectForRecipient =  {0} {1}''''s  invitation {2} is now closed',"messages_russian"='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Upload Request has expired
beginningMainMsgGrouped = Your grouped Upload Request has expired
beginningMainMsgUnGrouped = Your Upload Request has expired
endingMainMsgPlural = and you have received a total of <b>{0} files</b>.
endingMainMsgPluralForRecipient = and you currently have sent  <b> {0} files </b>.
endingMainMsgSingular = and you have received a total of <b>1 file</b>.
endingMainMsgSingularForRecipient = and you currently have uploaded <b>1 file </b> to the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle = Upload Request''''s  attached message :
recipientsURequest = Recipients
subjectForOwner = Your invitation {0} is now closed
subjectForRecipient =  {0} {1}''''s  invitation {2} is now closed' WHERE "id"=13;