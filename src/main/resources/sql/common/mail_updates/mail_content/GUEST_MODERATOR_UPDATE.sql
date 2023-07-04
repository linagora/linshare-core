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
          <span data-th-utext="#{mainMsg(${actor.firstName},${actor.lastName},${guest.firstName},${guest.lastName},${role})}"></span>
          <!--/* Access button to guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${guestLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
           <th:block data-th-replace="layout :: infoEditedItem(#{role}, ${role.oldValue}, ${role.value})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a modifié le modérateur role
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a modifié le modérateur role pour <b>{2}</b> <b>{3}</b>.
role = Role',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} updated your moderator role
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> updated your moderator role on the guest <b>{2}</b> <b>{3}</b>.
role = Role',
messages_russian='accessToLinshareBTn = Access
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> updated your moderator role on the guest <b>{2}</b> <b>{3}.
subject = {0} {1} updated your moderator role
role = Role' WHERE id=42;