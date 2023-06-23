UPDATE mail_content SET subject= '[( #{subject})]',
body= '<!DOCTYPE html>
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
        <p data-th-utext="#{mainTitle}"><p>
          <br/>
        <span data-th-utext="#{additionalMsg}"></span>
        <br/>
          <b>NB:</b> <span data-th-utext="#{noteMsg}"></span>
        </p><br/>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block data-th-replace="layout :: actionButtonLink(#{changePasswordBtn},${resetLink})"/>
        <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{resetLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{urlExpiryDateTitle},${urlExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',
messages_french= 'urlExpiryDateTitle = Date d''''expiration de l''''URL
additionalMsg = Vous pouvez également, utiliser le formulaire de mot de passe perdu pour accomplir cette tache.
noteMsg = Ce lien est utilisable une seule fois et sera valide pendant 1 semaine.
changePasswordBtn = Réinitialiser
mainTitle = Afin de renforcer la sécurité de votre compte, vous devez changer le mot de passe de votre compte LinShare. Toute connexion sera impossible tant que cette étape ne sera pas réalisée.
resetLinkTitle = Lien de réinitialisation
subject =  Mise à jour de sécurité
userNameTitle = Identifiant',
messages_english= 'urlExpiryDateTitle = URL expiry date
additionalMsg = You can also use the reset password form to do this task.
noteMsg = This link can be used only once and will be valid for 1 week. 
changePasswordBtn = Change password
mainTitle = In order to enhance the security of your account, you must change your password to your LinShare account. Any connection will be forbidden until this step is not carried out.
resetLinkTitle = LinShare reset password link
subject =  Security update
userNameTitle = Username',
messages_russian= 'urlExpiryDateTitle = URL expiry date
additionalMsg = You can also use the reset password form to do this task.
noteMsg = This link can be used only once and will be valid for 1 week. 
changePasswordBtn = Change password
mainTitle = In order to enhance the security of your account, you must change your password to your LinShare account. Any connection will be forbidden until this step is not carried out.
resetLinkTitle = LinShare reset password link
subject =  Security update
userNameTitle = Username',
messages_vietnamese= 'urlExpiryDateTitle = URL ngày hết hạn 
additionalMsg = Bạn cũng có thể dùng form đổi mật khẩu để thực hiện việc này. 
noteMsg = Đường dẫn này chỉ được dùng một ngày và có giá trị trong 1 tuần. 
changePasswordBtn = Đổi mật khẩu 
mainTitle = Để nâng cao bảo bật cho tài khoản LinShare của bạn, bạn cần phải đổi mật khẩu tài khoản. Bất cứ kết nối nào cũng sẽ bị chặn cho đến khi bạn hoàn thành bước này. 
resetLinkTitle = Đường dẫn đổi mật khẩu LinShare. 
subject =  Cập nhật bảo mật 
userNameTitle = Tên người dùng '
WHERE id= 37;
