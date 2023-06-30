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
          <span data-th-utext="#{revisionMsg}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
         </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupModificationDateTitle},${document.modificationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{DocumentSize},${document.size})"/>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupModificationDateTitle = Date de modification
DocumentSize = Taille du document
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a modifié le document <br>
folderMsg = dans le dossier
workgroupMsg = du groupe de travail
revisionMsg = en ajoutant une nouvelle révision 
subject = Le Document {0} a été modifié à {1}
name: ',messages_english='workGroupModificationDateTitle = Modification date
DocumentSize = Document size
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> updated the document<br>
folderMsg = into the folder
workgroupMsg = on the workgroup
revisionMsg = by adding a new document revision 
subject = The document {0} was updated in the workgroup {1}',messages_russian='workGroupModificationDateTitle = Дата изменения
DocumentSize = Размер документа
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> обновил документ<br>
folderMsg = в папке
workgroupMsg = рабочей группы
revisionMsg = путем добавления новой редакции документа
subject = Документ {0} был обновлен в рабочей группе {1}',messages_vietnamese='workGroupModificationDateTitle = NGày chỉnh sửa
DocumentSize = Kích cỡ tài liệu 
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> đã chỉnh sửa tài liệu<br>
folderMsg = trong thư mục 
workgroupMsg = trong workgroup
revisionMsg = bới thêm một phiên bản mới của tài liệu 
subject = Tài liệu {0} đã được cập nhật tron workgroup {1}' WHERE id=45;
