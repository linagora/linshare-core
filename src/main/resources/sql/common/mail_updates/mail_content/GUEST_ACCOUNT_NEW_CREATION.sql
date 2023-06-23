UPDATE mail_content SET subject='[( #{subject(${creator.firstName},${creator.lastName}, #{productName})})]',body='<!DOCTYPE html>
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
          <span data-th-utext="#{mainMsg(${creator.firstName},${creator.lastName},#{productName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${resetLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{activationLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Activer mon compte
accountExpiryDateTitle = Date d''''''expiration
activationLinkTitle = Lien d''''initialisation
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a créé un compte invité sur <b>LinShare</b> qui vous permet de partager des fichiers de façon sécurisée. <br/> Pour vous connecter, vous devez finaliser votre inscription en créant votre mot de passe à l''''aide du lien  ci-dessous.
subject = {0}  {1} vous invite a activer votre compte
userNameTitle = Identifiant',messages_english='accessToLinshareBTn = Activate account
accountExpiryDateTitle = Account expiry date
activationLinkTitle = Initialization link
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a <b>{2}</b> guest account for you, which enables you to transfer files more securely. <br/>To log into your account, you will need to finalize your subscription by creating your password, using the following link.
subject = {0}  {1} invited you to activate your {2} account
userNameTitle = Username',messages_russian='accessToLinshareBTn = Активировать аккаунт
accountExpiryDateTitle = Срок действия аккаунта
activationLinkTitle = Ссылка активации
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> создал гостевой аккаунт <b>{2}</b>  для вас, который позволяет надежно обмениваться файлами. <br/>Для входа в ваш аккаунт, завершите процесс регистрации, используя ссылку
subject = {0}  {1} пригласил вас активировать ваш {2} аккаунт
userNameTitle = Имя пользователя',messages_vietnamese='accessToLinshareBTn = Kích hoạt tài khoản
accountExpiryDateTitle = Ngày hết hạn tài khoản 
activationLinkTitle = Đường dẫn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã tạo một <b>{2}</b> tài khoản khách cho bạn, điều này cho phép bạn trao đổi tài liệu bảo mật hơn. <br/> Để đăng nhập tài khoản của bạn, bạn cần phải tạo mật khẩu bằng đường dẫn dưới đây. 
subject = {0}  {1} đã mời bạn kích hoạt {2} tài khoản
userNameTitle = Tên đăng nhập' WHERE id=8;
