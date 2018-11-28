UPDATE mail_content SET subject='[( #{subject(#{displayDriveName}, ${workGroupName})})]',body='<!DOCTYPE html>
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
            <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName}, #{displayDriveName})}"></span>
            <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMainMsg(#{displayDriveName})}"></span>
            <span>
              <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
               link
             </a>
            </span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, ${threadMember.role.name})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle(#{displayDriveName})},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${threadMember.creationDate})"/>
    <div th:if="${!childMembers.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList(${childMembers[0].role.name})}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="member : ${childMembers}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="${member.node.name}"/>
          </li>
      </ul>  
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au {2} <br>
simpleMainMsg = Vous avez été ajouté au {1}
subject = Vous avez été ajouté au {0} {1}
workGroupRight = Droit par défaut 
workGroupNameTitle = Nom du {0}
nestedWorkGroupsList=Vous avez automatiquement été ajouté aux groupes de travail suivants avec un rôle <span style="text-transform:uppercase">{0}</span>:
displayDriveName =Drive',messages_english='workGroupCreationDateTitle = Creation date
displayDriveName =Drive
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the {2}<br>
simpleMainMsg = You have been added to the {0}
subject = You have been added to the {0} {1}
workGroupRight = Default right
workGroupNameTitle ={0} Name
nestedWorkGroupsList=You have been automatically added to the following workgroups with the <span style="text-transform:uppercase">{0}</span> role:' WHERE id=34;