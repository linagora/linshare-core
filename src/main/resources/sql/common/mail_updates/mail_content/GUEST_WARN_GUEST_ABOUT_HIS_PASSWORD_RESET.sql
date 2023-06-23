UPDATE mail_content SET subject='[( #{subject})]',body='<!DOCTYPE html>
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
        <p>
          <span data-th-utext="#{mainMsg(#{productName},${guest.mail})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{accountCreationDateTitle},${guestCreationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accountCreationDateTitle = Date de création
accountExpiryDateTitle = Date d''''expiration
mainMsg = Le mot de passe du compte {0} <b>{1}</b> a été modifié.
subject = Votre mot de passe a été modifié',messages_english='accountCreationDateTitle = Account creation date
accountExpiryDateTitle = Account expiry date
mainMsg = The password of the account {0} <b>{1}</b> was modified.
subject = Your password has been modified',messages_russian='accountCreationDateTitle = Дата создания аккаунта
accountExpiryDateTitle = Дата окончания действия аккаунта
mainMsg = Пароль аккаунта {0} <b>{1}</b> был изменен.
subject = Ваш пароль был изменен',messages_vietnamese='accountCreationDateTitle = Ngày tạo tài khoản 
accountExpiryDateTitle = Ngày tài khoản hết hạn 
mainMsg = Mật khẩu của tài khoản {0} <b>{1}</b> đã được thay đổi. 
subject = Mật khẩu của bạn đã được thay đổi' WHERE id=31;
