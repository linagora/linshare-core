UPDATE mail_content SET subject='[( #{subject(${document.name},${workGroupMember.node.name},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${workGroupMember.account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
           <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${workGroupDocumentLink}}" >
               link
             </a>
           <span th:if="${folder.nodeType.name} != ''ROOT_FOLDER''" data-th-utext="#{folderMsg}"></span>
           <span th:if="${folder.nodeType.name} != ''ROOT_FOLDER''">
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${folder.name}" th:href="@{${workGroupFolderLink}}" >
               link
             </a>
          </span>
          <span data-th-utext="#{workgroupMsg}"></span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupMember.node.name}" th:href="@{${workGroupLink}}" >
               link
             </a>
          <!--/* Activation link for initialisation of the guest account */-->
         </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${document.creationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{DocumentSize},${document.size})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupCreationDateTitle = Date de création
DocumentSize = Taille du document
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a ajouté un nouveau document <br>
folderMsg = dans le dossier
workgroupMsg = du groupe de travail
subject = Le Document {0} a été ajouté à {1}',messages_english='workGroupCreationDateTitle = Creation date
DocumentSize = Document size
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> uploaded a new document<br>
folderMsg = into the folder
workgroupMsg = on the workgroup
subject = The document {0} was uploaded in the workgroup {1}',messages_russian='workGroupCreationDateTitle = Creation date
DocumentSize = Document size
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> uploaded a new document<br>
folderMsg = into the folder
workgroupMsg = of the workgroup
subject = The document {0} was uploaded in the workgroup {1}' WHERE id=44;