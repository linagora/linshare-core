UPDATE mail_content SET subject='[( #{subject(${actor.firstName},${actor.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${actor.firstName},${actor.lastName},${guest.firstName},${guest.lastName})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
        <th:block data-th-replace="layout :: infoStandardArea(#{guestNameTitle},${guest.mail})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a supprimé de la liste des modérateurs d''''invité
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a supprimé de la liste des modérateurs de <b>{2}</b> <b>{3}</b>.
guestNameTitle = Invité',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} deleted you from guest moderator''''s list
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> deleted you from moderators list of <b>{2}</b> <b>{3}</b>.
guestNameTitle = Guest',
messages_russian='accessToLinshareBTn = Доступ
subject = {0} {1} удалил вас из списка приглашенных модераторов
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил вас из списка приглашенных модераторов в <b>{2}</b> <b>{3}</b>.
guestNameTitle = Гость',
messages_vietnamese='accessToLinshareBTn = Truy cập
subject = {0} {1} đã xóa bạn khỏi danh sách quản trị 
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã xóa bạn khỏi danh sách quản trị của <b>{2}</b> <b>{3}</b>.
guestNameTitle = Khách' WHERE id=43;
