UPDATE mail_content SET subject='[( #{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <!--/* Upper main-content*/-->
    <section id="main-content">
        <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
            <div id="section-content">
                <!--/* Greetings */-->
                <th:block data-th-replace="layout :: greetings(${member.account.firstName})"/>
                <!--/* End of Greetings  */-->
                <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${workGroupName}, ${actor.firstName},${actor.lastName})}">
                     </span>
                  </p>
                </p> <!--/* End of Main email  message content*/-->
            </div><!--/* End of section-content*/-->
        </div><!--/* End of main-content container*/-->
    </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
        <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='subject = Le groupe de travail {0} a été supprimé.
mainMsg = Le groupe de travail {0} a été supprimé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Nom du groupe de travail',messages_english='subject = The workgroup {0} has been deleted.
mainMsg = The workgroup {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Workgroup Name',messages_russian='subject = The workgroup {0} has been deleted.
mainMsg = The workgroup {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Workgroup Name' ,messages_vietnamese='subject = Workgroup {0} đã bị xóa.
mainMsg = Workgroup {0} đã bị xóa bởi <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Tên Workgroup' WHERE id=39;
