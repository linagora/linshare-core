UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(body)}">
      <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
        </span>
        <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection(~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div>
          <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
        </div>
          <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
                 <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                </span>
 <span th:with="df=#{customDate}" data-th-text="${#dates.format(request.activationDate,df)}">7th of November, 2018</span>
        </p>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
  <div data-th-if="${!#strings.isEmpty(request.activationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{activationDate},${request.activationDate})"/>
            </div>
     <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
       <div data-th-if="(${totalMaxDepotSize})">
               <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
         </div>
  <div data-th-if="!(${isRestricted})">
         <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>',messages_french='activationDate = Ouverture du dépôt le
closureDate = Date de clôture
customDate= d MMMM yyyy.
depotSize = Taille autorisée
mainMsg = <b>{0} {1}</b> a créé une Invitation de dépôt, qui sera ouverte le
msgFrom = Le message de
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} vous a créé une Invitation de Dépôt',messages_english='activationDate = Activation date
closureDate = Closure date
customDate= MMMM d, yyyy.
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> has invited you to access to his Upload Request, sets to open
msgFrom = Message from
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} has sent an invitation to access to his Upload Request.',messages_russian='activationDate = Дата активации
closureDate = Дата закрытия
customDate= MMMM d, yyyy.
depotSize = Допустимый размер
mainMsg = <b>{0} {1}</b> открыл для вас доступ к его запросу загрузки, созданному
msgFrom = Сообщение от
name = {0} {1}
recipientsOfDepot = Получатели
subject = {0} {1} открыл для вас доступ к его запросу загрузки.' WHERE id=20;