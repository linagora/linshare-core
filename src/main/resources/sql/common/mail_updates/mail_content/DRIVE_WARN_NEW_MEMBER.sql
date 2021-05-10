UPDATE mail_content SET subject='[( #{subject(${driveName})})]',body='<!DOCTYPE html>
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
            <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
            <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMainMsg}"></span>
            <span>
              <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${driveName}" th:href="@{${driveLink}}" >
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
       <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, ${driveMember.role.name})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{driveNameTitle},${driveName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{driveCreationDateTitle},${driveMember.creationDate})"/>
    <div th:if="${!childMembers.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="member : ${childMembers}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="#{displayDriveAndRole(${member.node.name},${member.role.name})}"/>
          </li>
      </ul>  
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='driveCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au drive <br>
simpleMainMsg = Vous avez été ajouté au drive
subject = Vous avez été ajouté au drive {0}
driveRight = Droit par défaut 
driveNameTitle = Nom du drive
nestedWorkGroupsList=Vous avez automatiquement été ajouté aux groupes de travail suivants :
displayDriveAndRole ={0} avec un rôle <span style="text-transform:uppercase">{1}</span>',messages_english='driveCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the drive <br>
simpleMainMsg = You have been added to the drive
subject = You have been added to the drive {0}
driveRight = Default right
driveNameTitle = Drive Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayDriveAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role',messages_russian='driveCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the drive <br>
simpleMainMsg = You have been added to the drive
subject = You have been added to the drive {0}
driveRight = Default right
driveNameTitle = Drive Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayDriveAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role' WHERE id=34;