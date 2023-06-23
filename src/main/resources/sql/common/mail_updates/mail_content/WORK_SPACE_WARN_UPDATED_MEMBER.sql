UPDATE mail_content SET subject='[(#{subject(${workSpaceName})})]',body='<!DOCTYPE html>
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
          <span data-th-utext="#{mainMsg}"></span>
          <span>
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workSpaceName}" th:href="@{${workSpaceLink}}" >
                link </a>
          </span>
          <span data-th-utext="#{mainMsgNext}"></span>
          <span th:if="${owner.firstName} != null AND ${owner.firstName} != null" data-th-utext="#{mainMsgNextBy(${owner.firstName},${owner.lastName})}"></span>

             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceNameTitle},${workSpaceName})"/>
    <th:block th:switch="${workSpaceMember.role.name}">
      <p th:case="''WORK_SPACE_ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleAdminTitle})"/></p>
      <p th:case="''WORK_SPACE_WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleWriteTitle})"/></p>
      <p th:case="''WORK_SPACE_READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workSpaceRight}, #{workSpaceRoleReadTitle})"/></p>
    </th:block>
    <th:block th:switch="${workSpaceMember.nestedRole.name}">
      <p th:case="''ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/></p>  
      <p th:case="''CONTRIBUTOR''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/></p>
    </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{workSpaceMemberUpdatedDateTitle},${workSpaceMember.modificationDate})"/>
    <div th:if="${nbrWorkgroupsUpdated != 0}">
    <th:block data-th-replace="layout :: infoStandardArea(#{nbrWorkgoups},${nbrWorkgroupsUpdated})"/>
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul>
        <li  th:each="member : ${nestedMembers}">
              <th:block data-th-utext="${member.node.name}"/>
        </li>
        <span th:if="${nbrWorkgroupsUpdated > 3}">
             <li>...</li>
        </span>
      </ul>  
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workSpaceMemberUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le WorkSpace
mainMsgNext = et dans ses WorkGroups contenus ont été mis à jour
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Vos droits sur le WorkSpace {0} ont été mis à jour
workSpaceRight = Droit sur le WorkSpace
workGroupRight =  Droit sur le groupe de travail
workSpaceNameTitle = Nom du WorkSpace
nestedWorkGroupsList = Liste des workgoups
nbrWorkgoups = Nombre de groupe de travail mis à jours',messages_english='workSpaceMemberUpdatedDateTitle = Updated date
mainMsg = Your roles on the WorkSpace
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your roles on the WorkSpace {0} was updated.
workSpaceRight = WorkSpace right
workGroupRight = Workgroup right
workSpaceNameTitle = WorkSpace Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups',messages_russian='workSpaceMemberUpdatedDateTitle = Updated date
mainMsg = Your roles on the WorkSpace
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your roles on the WorkSpace {0} was updated.
workSpaceRight = WorkSpace right
workGroupRight = Workgroup right
workSpaceNameTitle = WorkSpace Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups' ,messages_vietnamese='workSpaceMemberUpdatedDateTitle = NGaỳ cập nhật 
mainMsg = Quyền của bạn trong workspace 
mainMsgNext= và các workgroup bên trong đã được cập nhật 
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Quyền của bạn trong WorkSpace {0} đã được cập nhật 
workSpaceRight = Quyền trong WorkSpace 
workGroupRight = Quyền trong Workgroup
workSpaceNameTitle = Tên WorkSpace
nestedWorkGroupsList = Danh sách Workgroups 
nbrWorkgoups = Số workgroup được cập nhật' WHERE id=35;
