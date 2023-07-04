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
        <th:block data-th-replace="layout :: infoStandardArea(#{guestNameTitle},${guest.mail})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a ajouté comme modérateur d''invité
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a ajouté comme modérateur de <b>{2}</b> <b>{3}</b> avec <b>{4}</b> role.
guestNameTitle = Invité',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} added you as guest moderator
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you as a guest moderator for <b>{2}</b> <b>{3}</b> with <b>{4}</b> role.
guestNameTitle = Guest',
messages_russian='accessToLinshareBTn = Access
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you as a guest moderator for <b>{2}</b> <b>{3}</b> with <b>{4}</b> role.
subject = {0} {1} added you as guest moderator
guestNameTitle = Guest' WHERE id=41;