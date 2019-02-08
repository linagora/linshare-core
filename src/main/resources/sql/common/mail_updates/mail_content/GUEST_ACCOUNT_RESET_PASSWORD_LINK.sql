UPDATE "mail_content" SET "subject"='[( #{subject})]',"body"='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p style="font-weight:bold;font-size:15px;"  data-th-utext="#{mainTile}">Did you forget your password ?</p>
        <p>
          <span data-th-utext="#{beginingMainMsg}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block data-th-replace="layout :: actionButtonLink(#{changePasswordBtn},${resetLink})"/>
          <br/>
        </p>
        <p  data-th-utext="#{endingMainMsg}"></p>
        <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{resetLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',"messages_french"='accountExpiryDateTitle = Date d''''expiration
beginingMainMsg =  Suivez le lien ci-dessous afin de réinitialiser le mot de passe de votre compte LinShare.
changePasswordBtn = Réinitialiser
endingMainMsg = Si vous n''''avez pas sollicité ce changement de mot de passe, merci d''''ignorer cet email. Votre mot de passe ne sera pas mis à jour tant que vous n''''en créé pas un nouveau, via le lien ci-dessus.
mainTile = Vous avez oublié votre mot de Passe ?
resetLinkTitle = Lien de réinitialisation
subject =  LinShare instruction de réinitialisation de mot de passe
userNameTitle = Identifiant',"messages_english"='accountExpiryDateTitle = Account expiry date
beginingMainMsg =  Follow the link below to reset your LinShare password account.
changePasswordBtn = Change password
endingMainMsg = If you did not request a password reset, please ignore this email. Your password will not change until you create a new one via the link above.
mainTile = Did you forget your password ?
resetLinkTitle = LinShare reset password link
subject =  LinShare reset password instructions
userNameTitle = Username',"messages_russian"='accountExpiryDateTitle = Account expiry date
beginingMainMsg =  Follow the link below to reset your LinShare password account.
changePasswordBtn = Change password
endingMainMsg = If you did not request a password reset, please ignore this email. Your password will not change until you create a new one via the link above.
mainTile = Did you forget your password ?
resetLinkTitle = LinShare reset password link
subject =  LinShare reset password instructions
userNameTitle = Username' WHERE "id"=9;