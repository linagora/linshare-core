UPDATE "mail_content" SET "subject"='[( #{subject(${share.name})})]',"body"='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${shareOwner.firstName},${shareOwner.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',"messages_french"='shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b> a expiré et a été supprimé par le <b>système</b>.
subject = Le partage {0} a expiré
fileNameEndOfLine = {0}',"messages_english"='shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b> has expired and been deleted by the <b>system</b>.
subject = The fileshare {0} has expired
fileNameEndOfLine = {0}',"messages_russian"='shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b> has expired and been deleted by the <b>system</b>.
subject = The fileshare {0} has expired
fileNameEndOfLine = {0}' WHERE "id"=27;