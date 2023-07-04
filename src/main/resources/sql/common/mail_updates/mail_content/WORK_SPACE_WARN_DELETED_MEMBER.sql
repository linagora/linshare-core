UPDATE mail_content SET subject='[( #{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
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
          <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${workGroupName})}"></span>
          <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMsg(${workGroupName})}"></span>
            
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
</html>',messages_french='subject = Les accès au workspace {0} et à ses workgroups vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du workspace <b>{2}</b>
simpleMsg = Les accès au workspace <b>{0}</b> vous ont été retirés.
workGroupNameTitle = Nom du workspace',messages_english='subject = Your access to the workspace {0}  and its workgroups was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the workspace  <b>{2}</b>
simpleMsg =  Your access to the workspace <b>{0}</b> was withdrawn.
workGroupNameTitle = Workspace Name',messages_russian='subject = Your access to the workspace {0}  and its workgroups was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the workspace  <b>{2}</b>
simpleMsg =  Your access to the workspace <b>{0}</b> was withdrawn.
workGroupNameTitle = Workspace Name', messages_vietnamese='subject = Quyền truy cập của bạn đối với workspace {0} và các workgroups bên trong đã bị thu hồi
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã xóa bạn khỏi workspace <b>{2}</b>
simpleMsg =  Quyền truy cập của bạn đối với workspace <b>{0}</b> đã bị thu hồi.
workGroupNameTitle = Tên workspace ' WHERE id=36;
