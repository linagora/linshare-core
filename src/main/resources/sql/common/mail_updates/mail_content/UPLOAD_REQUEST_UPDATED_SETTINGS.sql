UPDATE mail_content SET subject='[(#{subject(${subject.value})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName}, ${subject.value})}">
                     </span>
                     <span data-th-utext="#{secondaryMsg}">
                     </span>
                  </p>
                  <!--/* If the sender has added a  customized message */-->
                  <th:block data-th-if="(${message.modified})">
                     <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                        <span id="message-title">
                        <span data-th-text="#{msgFrom}">You have a message from</span>
                        <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                        </span>
                        <span id="message-content" data-th-text="*{message.value}" style="white-space: pre-line;">
                        Hi Amy,<br>
                        As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
                        </span>
                     </div>
                  </th:block>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <span data-th-if="(${expiryDate.modified})">
               <th:block data-th-replace="layout :: infoDateItemsToUpdate(#{expiryDate}, #{expiryDateParamAdded}, #{expiryDateParamDeleted}, ${expiryDate.oldValue}, ${expiryDate.value})"/>
            </span>
            <span data-th-if="(${activationDate.modified})">
               <th:block data-th-replace="layout :: infoDateItemsToUpdate(#{activationDate}, #{activationDateParamAdded}, #{activationDateParamDeleted}, ${activationDate.oldValue}, ${activationDate.value})"/>
            </span>
            <span data-th-if="(${closureRight.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{closureRight}, #{closureRightParamAdded}, #{closureRightParamDeleted}, ${closureRight.oldValue}, ${closureRight.value})"/>
            </span>
            <span data-th-if="(${deletionRight.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{deletionRight}, #{deletionRightParamAdded}, #{deletionRightParamDeleted}, ${deletionRight.oldValue}, ${deletionRight.value})"/>
            </span>
            <span data-th-if="(${maxFileSize.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{maxFileSize}, #{maxFileSizeParamAdded}, #{maxFileSizeParamDeleted}, ${maxFileSize.oldValue}, ${maxFileSize.value})"/>
            </span>
            <span data-th-if="(${maxFileNum.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{maxFileNum}, #{maxFileNumParamAdded}, #{maxFileNumParamDeleted}, ${maxFileNum.oldValue}, ${maxFileNum.value})"/>
            </span>
            <span data-th-if="(${totalMaxDepotSize.modified})">
               <th:block data-th-replace="layout :: infoItemsToUpdate(#{depotSize}, #{totalMaxDepotSizeParamAdded}, #{totalMaxDepotSizeParamDeleted}, ${totalMaxDepotSize.oldValue}, ${totalMaxDepotSize.value})"/>
            </span>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='activationDate = Date d''''activation
closureRight = Droits de clôture
deletionRight = Droits de suppression
depotSize = Taille du dépôt
expiryDate = Date de clôture
enableNotification = Activation des notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a modifié des paramètres liés à l''''Invitation de dépôt <b>{2}</b>.
maxFileNum = Nombre de Fichiers
maxFileSize = Taille autorisée
msgFrom = Nouveau message de
name = {0} {1}
nameOfDepot: Nom du dépôt
secondaryMsg = Les modifications sont listées ci-dessous.
subject = Modification des paramètres du dépôt : {0}
buttonMsg = Accès,
maxFileSizeParamAdded = Paramètre de la taille de fichier autorisée ajouté
maxFileSizeParamDeleted = Paramètre de la taille de fichier autorisée annulé, ancienne valeur
maxFileNumParamAdded = Paramètre de nombre maximal des fichiers ajouté
maxFileNumParamDeleted = Paramètre de nombre maximal des fichiers annulé, ancienne valeur
totalMaxDepotSizeParamAdded = Paramètre de la taille maximale des fichiers déposés ajouté
totalMaxDepotSizeParamDeleted = Paramètre de la taille maximale des fichiers annulé, ancienne valeur
deletionRightParamAdded = Paramètre de droit de suppression ajouté
deletionRightParamDeleted = Paramètre de droit de suppression annulé, ancienne valeur
closureRightParamAdded = Paramètre de droits de clôture ajouté
closureRightParamDeleted = Paramètre de droits de clôture annulé, ancienne valeur
activationDateParamAdded = Paramètre de date d''''activation ajouté
activationDateParamDeleted = Paramètre de date d''''activation annulé, ancienne valeur
expiryDateParamAdded = Paramètre d''''expiration ajouté
expiryDateParamDeleted = Paramètre d''''expiration annulé, ancienne valeur',messages_english='activationDate = Activation date
closureRight = Closure rights
deletionRight = Deletion rights
depotSize = Repository size
expiryDate = Closure date
enableNotification = Enable notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  has updated some settings related to the Upload Request <b>{2}</b>.
maxFileNum = File number
maxFileSize = File size
msgFrom =  New message from
name = {0} {1}
nameOfDepot: Name of the Upload Request
secondaryMsg = Updated settings are listed below.
subject = Updated Settings for Upload Request : {0}
buttonMsg = Access
maxFileSizeParamAdded = Max File size parameter added
maxFileSizeParamDeleted = Max File size parameter canceled, last value
maxFileNumParamAdded = Max File number parameter added
maxFileNumParamDeleted = Max File number parameter deleted, last value
totalMaxDepotSizeParamAdded = Max total deposite size parameter added
totalMaxDepotSizeParamDeleted = Max total deposite size parameter, last value
deletionRightParamAdded = Deletion rights parameter added
deletionRightParamDeleted = Deletion rights parameter canceled
closureRightParamAdded = Closure right parameter added
closureRightParamDeleted = Closure right parameter added, last value
activationDateParamAdded = Activation date parameter added
activationDateParamDeleted = Activation date parameter added, last value
expiryDateParamAdded = Expiration parameter added
expiryDateParamDeleted = Expiration parameter canceled, last value',messages_russian='activationDate = Дата активации
closureRight = Права закрытия
deletionRight = Права удаления
depotSize = Размер репозитория
expiryDate = Дата закрытия
enableNotification = Разрешить уведомления
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  обновил некоторые настройки запроса загрузки <b>{2}</b>.
maxFileNum = Номер файла
maxFileSize = Размер файла
msgFrom =  Новое сообщение от
name = {0} {1}
nameOfDepot: Название загрузки
secondaryMsg = Список обновленных настроек доступен ниже.
subject = Обновленные настройки для запроса загрузки {0}
buttonMsg = Доступ
maxFileSizeParamAdded = Добавлен параметр максимального размера файла
maxFileSizeParamDeleted = Параметр максимального размера файла удален, последнее значение
maxFileNumParamAdded = Добавлен параметр максимального количества файлов
maxFileNumParamDeleted = Параметр максимального количества файлов удален, последнее значение
totalMaxDepotSizeParamAdded = Добавлен параметр максимального общего размера депозита
totalMaxDepotSizeParamDeleted = Параметр максимального общего размера депозита удален, последнее значение
deletionRightParamAdded = Добавлен параметр прав на удаление
deletionRightParamDeleted = Параметр прав на удаление отменен
closureRightParamAdded = Добавлен параметр прав на закрытие
closureRightParamDeleted = Параметр прав на закрытие удален
activationDateParamAdded = Добавлен параметр даты активации
activationDateParamDeleted = Добавлен параметр даты активации, последнее значение
expiryDateParamAdded = Добавлен параметр срока действия
expiryDateParamDeleted = Параметр срока действия удален, последнее значение ',messages_vietnamese='activationDate = Ngày kích hoạt
closureRight = Quyền đóng 
deletionRight = Quyền xóa 
depotSize = kích cỡ thư mục 
expiryDate = NGày đóng 
enableNotification = Bật thông báo 
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  đã cập nhật một số cài đặt liên quan đến yêu cầu tải  <b>{2}</b>.
maxFileNum = Số filte 
maxFileSize = Khích thước size 
msgFrom =  Tin nhắn mới từ 
name = {0} {1}
nameOfDepot: Tên yêu cầu tải
secondaryMsg = Các chỉnh sửa cài đặt được liệt kê dưới đây. 
subject = Các cài đặt của yêu cầu tải đã được chỉnh sửa: {0}
buttonMsg = Truy cập
maxFileSizeParamAdded = Tham số dung lượng file tối đa đã được thêm vào 
maxFileSizeParamDeleted = Tham số dung lượng file tối đa đa bị hủy, giá trị cuối cùng
maxFileNumParamAdded = Tham số số lượng file tối đa đã được thêm vào 
maxFileNumParamDeleted = Tham số số lượng file tối đa đã bị hủy, giá trị cuối cùng
totalMaxDepotSizeParamAdded = Tham số tổng dung lượng file tối đa đã được thêm vào 
totalMaxDepotSizeParamDeleted = Tham số tổng dung lượng file tối đa đã bị hủy, giá trị cuối cùng
deletionRightParamAdded = Tham số quyền xóa đã được thêm vào 
deletionRightParamDeleted = Tham số quyền xóa đã bị hủy 
closureRightParamAdded = Tham số quyền đóng đã được thêm vào 
closureRightParamDeleted = Tham số quyền đóng đã bị hủy, last value
activationDateParamAdded = Tham số ngày kích hoạt đã được thêm vào 
activationDateParamDeleted = Tham số ngày kích hoạt đã bị hủy, giá trị cuối cùng 
expiryDateParamAdded = Tham số ngày hết hạn đã được thêm vào
expiryDateParamDeleted = Tham số ngày hết hạn đã bị hủy, giá trị cuối cùng' WHERE id=23;
