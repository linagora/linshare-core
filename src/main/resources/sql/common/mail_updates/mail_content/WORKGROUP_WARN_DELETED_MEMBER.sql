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
                <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
                <!--/* End of Greetings  */-->
                <!--/* Main email  message content*/-->
                <p>
                      <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${member.firstName} AND ${member.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${workGroupName})}">
                      </span>
                      <span th:unless="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${member.firstName} AND ${member.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsgAdmin(${workGroupName})}">
                      </span>
                      <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null"
                                      data-th-utext="#{simpleMsg(${workGroupName})}">
                      </span>
                    <!--/* Activation link for initialisation of the guest account */-->
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
</html>',messages_french='subject = Les accès au groupe de travail {0} vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du groupe de travail <b>{2}</b>
mainMsgAdmin = Vous vous êtes retiré de votre groupe de travail  <b>{0}</b>
simpleMsg = Les accès au groupe de travail <b>{0}</b> vous ont été retirés.
workGroupNameTitle = Nom du groupe de travail',messages_english='subject = Your access to the workgroup {0} was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the workgroup  <b>{2}</b>
mainMsgAdmin = You have removed yourself from your own workgroup  <b>{0}</b>
simpleMsg =  Your access to the workgroup <b>{0}</b> was withdrawn.     
workGroupNameTitle = Workgroup Name',messages_russian='subject = У вас больше нет доступа к рабочей группе {0}.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> удалил вас из рабочей группы  <b>{2}</b>
mainMsgAdmin = Вы удалили себя из созданной вами рабочей группы  <b>{0}</b>
simpleMsg =  У вас больше нет доступа к рабочей группе <b>{0}</b>.
workGroupNameTitle = Название рабочей группы' WHERE id=30;