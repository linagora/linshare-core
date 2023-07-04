UPDATE mail_content SET subject='[( #{subject(${workSpaceName})})]',body='<!DOCTYPE html>
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
                     <span data-th-utext="#{mainMsg(${workSpaceName}, ${actor.firstName},${actor.lastName})}">
                     </span>
                  </p>
                </p> <!--/* End of Main email  message content*/-->
            </div><!--/* End of section-content*/-->
        </div><!--/* End of main-content container*/-->
    </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
    <div th:if="${!nestedNodes.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="node : ${nestedNodes}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="#{displayNestedNodeName(${node.name})}"/>
          </li>
      </ul>  
    </div>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='subject = Le Workspace {0} a été supprimé.
mainMsg = Le Workspace {0} a été supprimé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=Vous avez automatiquement été supprimé des groupes de travail suivants :
displayNestedNodeName:{0}',messages_english='subject = The Workspace {0} has been deleted.
mainMsg = The Workspace {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=You have been automatically removed from the following workgroups:
workGroupNameTitle = Workgroup Name
displayNestedNodeName:{0}',messages_russian='subject = The Workspace {0} has been deleted.
mainMsg = The Workspace {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=You have been automatically removed from the following workgroups:
displayNestedNodeName:{0}' WHERE id=40;
