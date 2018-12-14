
UPDATE mail_layout SET messages_french='common.availableUntil = Expire le
common.byYou= | Par vous
common.download= Télécharger
common.filesInShare=Fichiers joints
common.recipients = Destinataires
common.titleSharedThe= Partagé le
date.format=d MMMM, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administrateur
workGroupRightWirteTitle = Écriture
workGroupRightReadTitle = Lecture
workGroupRightContributorTitle = Contributeur
welcomeMessage = Bonjour {0},',messages_english='common.availableUntil = Expiry date
common.byYou= | By you
common.download= Download
common.filesInShare = Attached files
common.recipients = Recipients
common.titleSharedThe= Creation date
date.format= MMMM d, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administrator
workGroupRightWirteTitle = Writer
workGroupRightReadTitle = Reader
workGroupRightContributorTitle = Contributor
welcomeMessage = Hello {0}',layout='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/* Beginning of common base layout template*/-->
<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">
  <div style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:''Open Sans'',arial,Helvetica,sans-serif;">
    <center>
      <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"
             style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px"
             width="90%">
        <tbody>
        <tr>
          <td align="center" style="border-collapse:collapse" valign="top">
            <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px" width="90%">
              <tbody>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table bgcolor="transparent" border="0" cellpadding="0" cellspacing="0"
                         style="background-color:transparent;border-bottom:0;padding:0px">
                    <tbody>
                    <tr>
                      <td align="center" bgcolor="#ffffff"
                          style="border-collapse:collapse;color:#202020;background-color:#ffffff;font-size:34px;font-weight:bold;line-height:100%;padding:0;text-align:center;vertical-align:middle">
                        <div align="center" style="text-align:center">
                          <a target="_blank"
                             style="border:0;line-height:100%;outline:none;text-decoration:none;width:233px;height:57px;padding:20px 0 20px 0"
                             data-th-href="@{${linshareURL}}">
                            <img src="cid:logo.linshare@linshare.org"
                              style="display:inline-block;margin-bottom:20px;margin-top:20px" width="233" alt="Logo"
                              height="57"/>
                          </a>
                        </div>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table border="0" cellpadding="0" cellspacing="0" style="width:95%;max-width:500px" width="95%">
                    <tbody>
                    <tr>
                      <td
                        style="border-collapse:collapse;border-radius:3px;font-weight:300;border:1px solid #e1e1e1;background:white;border-top:none;"
                        valign="top">
                        <table border="0" cellpadding="20" cellspacing="0" width="100%">
                          <tbody>
                          <tr>
                            <td style="border-collapse:collapse;padding:0px" valign="top">
                              <div align="left"
                                   style="color:#505050;font-size:14px;line-height:150%;text-align:left">
                                <th:block data-th-replace="${upperMainContentArea}"/>
                              </div>
                              <table border="0" cellspacing="0" cellpadding="0" width="100%"
                                     style="background-color: #f8f8f8;">
                                <tbody>
                                <tr>
                                  <td width="15" style="border-top:1px solid #c9cacc;">
                                  </td>
                                  <td width="20"><img src="cid:logo.arrow@linshare.org"
                                    width="20" height="9" border="0" style="display:block;" alt="down arrow"/></td>
                                  <td style="border-top:1px solid #c9cacc;"></td>
                                </tr>
                                </tbody>
                              </table>
                              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                <tbody>
                                <tr>
                                  <td>
                                    <div align="left"
                                         style="font-size:14px;padding: 0px 17px;background: #f8f8f8;text-align:left;color:#7f7f7f;line-height:20px;">
                                      <div align="left"
                                           style="font-size:13px;line-height:20px;margin:0;padding: 15px 0 20px;">
                                        <th:block data-th-replace="${bottomSecondaryContentArea}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                                </tbody>
                              </table>
                              <table width="100%"
                                     style="background:#f0f0f0;text-align:left;color:#a9a9a9;line-height:20px;border-top:1px solid #e1e1e1">
                                <tbody>
                                <tr data-th-insert="footer :: email_footer">
                                </tr>
                                </tbody>
                              </table>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table bgcolor="white" border="0" cellpadding="10" cellspacing="0"
                         style="background-color:white;border-top:0" width="400">
                    <tbody>
                    <tr>
                      <td style="border-collapse:collapse" valign="top">
                        <table border="0" cellpadding="10" cellspacing="0" width="100%">
                          <tbody>
                          <tr>
                            <td bgcolor="#ffffff" colspan="2"
                                style="border-collapse:collapse;background-color:#ffffff;border:0;padding: 0 8px;"
                                valign="middle">
                              <div align="center"
                                   style="color:#707070;font-size:12px;line-height:125%;text-align:center">
                                <!--/* Do not remove the copyright  ! */-->
                                <div data-th-insert="copyright :: copyright">
                                  <p
                                    style="line-height:15px;font-weight:300;margin-bottom:0;color:#b2b2b2;font-size:10px;margin-top:0">
                                    You are using the Open Source and free version of
                                    <a href="http://www.linshare.org/"
                                       style="text-decoration:none;color:#b2b2b2;"><strong>LinShare</strong>™</a>,
                                    powered by <a href="http://www.linshare.org/"
                                                  style="text-decoration:none;color:#b2b2b2;"><strong>Linagora</strong></a>
                                    ©&nbsp;2009–2018. Contribute to
                                    Linshare R&amp;D by subscribing to an Enterprise offer.
                                  </p>
                                </div>
                              </div>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              </tbody>
            </table>
          </td>
        </tr>
        </tbody>
      </table>
    </center>
  </div>
</div>
<!--/* End of common base layout template*/-->
 </body>
 </html>
<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoEditedItem(titleInfo,oldValue,newValue)">
     <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
    <br/>
      <th:block th:replace="${oldValue}" />  -> <th:block th:replace="${newValue}" />
</div>
<!--/* Edited  date  display settings  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoEditedDateArea(titleInfo,oldValue,newValue)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
    <br/>
 <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue,df)}"/> -> 
 <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(newValue,df)}"/>
</div>
<!--/* Common header template */-->
<head  data-th-fragment="header">
  <title data-th-text="${mailSubject}">Mail subject</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<!--/* Common greeting  template */-->
<div data-th-fragment="greetings(currentFirstName)">
  <p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px"
 data-th-text="#{welcomeMessage(${currentFirstName})}">
Hello Amy,</p>
</div>
<!--/* Common upper email section  template */-->
<div data-th-fragment="contentUpperSection(sectionContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;">
   <div align="left" style="padding:24px 17px 5px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;
border-top: 1px solid #e1e1e1;">
      <th:block th:replace="${sectionContent}" />
       </div>
</div>
<!--/* Common message section template */-->
<div data-th-fragment="contentMessageSection(messageTitle,messageContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;" >
          <div align="left" style="padding:24px 17px 15px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;">
<p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px">
<th:block th:replace="${messageTitle}" />
</p>
<p style="margin:0;color: #88a3b1;">
<th:block th:replace="${messageContent}" />
</p>
</div>
</div>
<!--/* Common link style */-->
<div data-th-fragment="infoActionLink(titleInfo,urlLink)"  style="margin-bottom:17px;" >
<span style="font-weight:bold;" data-th-text="${titleInfo}" >Download link title  </span>
  <br/>
<a target="_blank" style="color:#1294dc;text-decoration:none;"
                          data-th-text="${urlLink}"  th:href="@{${urlLink}}"   >Link </a>
</div>
<!--/* Common date display  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoDateArea(titleInfo,contentInfo)">
     <div data-th-if="${contentInfo != null}">
      <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
      <br/>
      <span  th:with="df=#{date.format}" data-th-text="${#dates.format(contentInfo,df)}">7th of November, 2018</span>
   </div>
</div>
<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoStandardArea(titleInfo,contentInfo)">
     <div data-th-if="${contentInfo != null}">
	   <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
       <br/>
       <th:block th:replace="${contentInfo}" />
	</div>
</div>
<!--/* Common button action style */-->
<span   data-th-fragment="actionButtonLink(labelBtn,urlLink)">
<a
style="border-radius:3px;font-size:15px;color:white;text-decoration:none;padding: 10px 7px;width:auto;max-width:50%;display:block;background-color: #42abe0;text-align: center;margin-top: 17px;"  target="_blank"
data-th-text="${labelBtn}"  th:href="@{${urlLink}}">Button label</a>
</span>
<!--/* Common recipient listing for external and internal users */-->
<div  style="margin-bottom:17px;" data-th-fragment="infoRecipientListingArea(titleInfo,arrayRecipients)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Recipients</span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="recipientData: ${arrayRecipients}">
<div data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
         <span style="color:#787878;font-size:13px"  data-th-utext="${recipientData.mail}">
        my-file-name.pdf
         </span>
</div>
<div data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">
         <span  style="color:#787878;font-size:13px">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
</div>
      </li>
   </ul>
</div>
<div data-th-if="(${!isAnonymous})">
         <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
      </li>
   </ul>
</div>
<!--/* Lists all file links in a share   */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileLinksListingArea(titleInfo,arrayFileLinks,isAnonymous)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
<div data-th-if="(${!isAnonymous})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}" th:href="@{${shareLink.href}}">
        my-file-name.pdf
         </a>
</div>
<div data-th-if="(${isAnonymous})">
         <span style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
</div>
<!--/* Lists all file links in a share  and checks witch one are the recpient\s */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileListWithMyUploadRefs(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
         <a style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
<th:block  data-th-if="(${shareLink.mine})"> <span  data-th-text="#{common.byYou}">|  By You</span></th:block >
      </li>
   </ul>
</div>
<!--/* Lists all file links in a share along with their download status   */-->
<div  data-th-fragment="infoFileListUploadState(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
<li style="color:#00b800;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${shareLink.downloaded})">
 <th:block data-th-if="(${shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px ;font-weight:bold"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
 <th:block data-th-if="(${!shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
      </li>
<li style="color:#787878;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${!shareLink.downloaded})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
      </li>
   </ul>
</div>
<!--/* Lists all recpients download states per file   */-->
<div   style="margin-bottom:17px;"  data-th-fragment="infoFileListRecipientUpload(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 0px; margin: 0;list-style-type:none;">
<li style="color:#787878;font-size:10px;margin-top:10px;"  th:each="shareLink : ${arrayFileLinks}" >
    <span style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
  <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
    <span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">
test-file.jpg</span></a>
    <span data-th-if="(${!shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #787878;"></span>
    <span data-th-if="(${shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #00b800;"></span>
    </span>
    <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;" >
 <th:block  th:each="recipientData: ${shareLink.shares}">
   <th:block data-th-if="(${!recipientData.downloaded})" >
      <li style="color:#787878;font-size:15px"  >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
      <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"
           data-th-if="(${#strings.isEmpty(recipientData.lastName)})">able.cornell@linshare.com </span>
      </li>
   </th:block>
<th:block data-th-if="(${recipientData.downloaded})">
   <li style="color:#00b800;font-size:15px" >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span  style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
<th:block  data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
  <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"> able.cornell@linshare.com </span>
  </th:block>
      </li>
   </th:block>
</th:block>
    </ul>
</li>
   </ul>
</div>' WHERE id=1;
UPDATE mail_content SET subject='[( #{subject(${document.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head  data-th-replace="layout :: header"></head>
  <body>
    <div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <section id="main-content">
      <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
        <div id="section-content">
          <!--/* Greetings */-->
            <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>
          <!--/* End of Greetings */-->
          <!--/* Main email  message content*/-->
          <p>
     <span  data-th-utext="#{beginningMainMsgInt}"></span>
            <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >
                  filename.ext
              </a>
          </span>
  <span  data-th-utext="#{endingMainMsgInt(${daysLeft})}">  </span>
           <!--/* Single download link for external recipient */-->
            <th:block   data-th-replace="layout :: actionButtonLink(#{common.download},${document.href})"/>
          </p> <!--/* End of Main email  message content*/-->
        </div><!--/* End of section-content*/-->
      </div><!--/* End of main-content container*/-->
    </section> <!--/* End of main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
      <th:block data-th-replace="layout :: infoDateArea(#{uploadedThe},${document.creationDate})"/>
      <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${document.expirationDate})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
  </body>
</html>',messages_french='beginningMainMsgInt =  Votre fichier
endingMainMsgInt = sera automatiquement supprimé dans <b> {0} jours</b> de votre Espace Personnel.
subject = Le fichier {0} va bientôt être supprimé
uploadedThe = Déposé le',messages_english='beginningMainMsgInt = Your file
endingMainMsgInt = will automatically be deleted in <b> {0} days</b> from your Personal Space.
subject = The file {0} is about to be deleted
uploadedThe = Upload date' WHERE id=1;

UPDATE mail_content SET subject='[# th:if="${#strings.isEmpty(customSubject)}"]
[# th:if="${sharesCount} > 1"]
[( #{subjectPlural(${shareOwner.firstName},${ shareOwner.lastName})})]
[/]
[# th:if="${sharesCount} ==  1"]
[( #{subjectSingular(${shareOwner.firstName },${ shareOwner.lastName})})]
[/]
[/]
[# th:if="${!#strings.isEmpty(customSubject)}"]
[(${customSubject})]   [( #{subjectCustomAlt(${shareOwner.firstName },${shareOwner.lastName})})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(customMessage)}">
      <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${shareOwner.firstName} , ${shareOwner.lastName})}">Peter Wilson</b> :
        </span>name = {0} {1}
        <span id="message-content" data-th-text="*{customMessage}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div data-th-if="(${!anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        </div>
        <div data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.mail})"/>
        </div> <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
            <span data-th-if="(${sharesCount} ==  1)"
                  data-th-utext="#{mainMsgSingular(${shareOwner.firstName},${shareOwner.lastName},${sharesCount})}">
            Peter WILSON has shared 4 file with you
            </span>
          <span data-th-if="(${sharesCount} > 1)"
                data-th-utext="#{mainMsgPlural(${shareOwner.firstName},${shareOwner.lastName},${sharesCount})}">
            Peter WILSON has shared 4 files with you
            </span>
          <br/>
          <!--/* Check if the external user has a password protected file share */-->
          <span data-th-if="(${protected})">
       <span data-th-if="(${sharesCount} ==  1)" data-th-text="#{helpPasswordMsgSingular}">Click on the link below in order to download it     </span>
            <span data-th-if="(${sharesCount} >  1)" data-th-text="#{helpPasswordMsgPlural}">Click on the links below in order to download them </span>
            </span>
          <span data-th-if="(${!anonymous})">
            <span data-th-if="(${sharesCount} ==  1)">
              <span  data-th-utext="#{click}"></span>
                <span>
                 <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{link}" th:href="@{${filesSharesLink}}" >
                  link
                 </a>
               </span>
              <span data-th-utext="#{helpMsgSingular}"></span>
            </span>
            <span data-th-if="(${sharesCount} >  1)">
              <span  data-th-utext="#{click}"></span>
              <span>
                <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{link}" th:href="@{${filesSharesLink}}" >
                 link
               </a>
              </span>
             <span data-th-utext="#{helpMsgPlural}"></span>
            </span>
            </span>
        </p>
        <!--/* Single download link for external recipient */-->
        <div data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: actionButtonLink(#{downloadBtn},${anonymousURL})"/>
        </div>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <div data-th-if="(${protected})">
      <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
    </div>
    <div data-th-if="(${anonymous})">
      <th:block data-th-replace="layout :: infoActionLink(#{downloadLink},${anonymousURL})"/>
    </div>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shares[0].creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shares[0].expirationDate})"/>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{common.filesInShare},${shares},${anonymous})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>',messages_french='downloadBtn = Télécharger
downloadLink = Lien de téléchargement
helpMsgSingular =  pour visualiser le document partagé.
helpMsgPlural =pour visualiser tous les documents du partage.
helpPasswordMsgSingular = Cliquez sur le lien pour le télécharger et saisissez le mot de passe fourni ici.
helpPasswordMsgPlural = Cliquez sur le lien pour les télécharger et saisissez le mot de passe fourni.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>a partagé {2} fichiers avec vous.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a partagé {2} fichier  avec vous.
msgFrom = Vous avez un message de
name = {0} {1}
password = Mot de passe
subjectCustomAlt =de {0} {1}
subjectPlural =  {0} {1} vous a partagé des fichiers
subjectSingular =  {0} {1} vous a partagé un fichier
click = Cliquez sur ce
link = lien',messages_english='downloadBtn = Download
downloadLink = Download link
helpMsgPlural = to access to all documents in this share.
helpMsgSingular = to access to the document in this share.
helpPasswordMsgSingular = Click on the link below in order to download it and enter the provided password.
helpPasswordMsgPlural = Click on the link below in order to download them and enter the provided password.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} files</b> with you.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} file</b> with you.
msgFrom = You have a message from
name = {0} {1}
password = Password
subjectCustomAlt =by {0} {1}
subjectPlural = {0} {1} has shared some files with you
subjectSingular = {0} {1} has shared a file with you
click = Follow this
link = link' WHERE id=2;

UPDATE mail_content SET subject='[# th:if="${documentsCount} > 1"] 
[( #{subjectPlural})]
[/]
[# th:if="${documentsCount} ==  1"]
[( #{subjectSingular})]
[/]
[# th:if="${!#strings.isEmpty(customSubject)}"]
[(${ ": " +customSubject})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(customMessage)}">
      <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFor}">You have a message from</span>
        </span>
        <span id="message-content" data-th-text="*{customMessage}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${documentsCount} > 1)" data-th-utext="#{numFilesMsgPlural(${documentsCount})}">
            Peter WILSON has  shared 4 files
            </span>
          <span data-th-if="(${documentsCount} ==  1)" data-th-utext="#{numFilesMsgSingular(${documentsCount})}">
            Peter WILSON has  shared 1 file
            </span>
          <span data-th-if="(${recipientsCount} >  1)" th:with="df=#{date.format}"
                data-th-utext="#{recipientCountMsgPlural(${#dates.format(expirationDate,df)},${recipientsCount})}">
             to 3 recipients set to expire for the 7th December 2018
            </span>
          <span data-th-if="(${recipientsCount} ==  1)" th:with="df=#{date.format}"
                data-th-utext="#{recipientCountMsgSingular(${#dates.format(expirationDate,df)},${recipientsCount})}">
            to 1 recipient set to expire for the 7th December 2018
            </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End upper of main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoRecipientListingArea(#{common.recipients},${recipients})"/>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{common.filesInShare},${documents},false)"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='numFilesMsgPlural = Vous avez partagé <b>{0} fichiers</b>
numFilesMsgSingular = Vous avez partagé <b>{0} fichier</b>
recipientCountMsgPlural = avec <b>{1} destinataires</b>. Ce partage expirera le <b>{0}</b>.
recipientCountMsgSingular = avec <b>{1} destinataire</b>. Ce partage expirera le <b>{0}</b>.
subjectPlural = Vous avez partagé des fichiers
subjectSingular = Vous avez partagé un fichier
msgFor = Votre message de partage',messages_english='numFilesMsgPlural = You have shared <b>{0} files</b>
numFilesMsgSingular = You have shared <b>{0} file</b>
recipientCountMsgPlural =   to <b>{1} recipients</b>. The fileshare will expire on : {0}.
recipientCountMsgSingular =   to <b>{1} recipient</b>. The fileshare will  expire on : {0}.
subjectPlural =  You have shared some files 
subjectSingular = You have shared a file
msgFor = Your message of sharing' WHERE id=3;

UPDATE mail_content SET subject='[# th:if="${!anonymous}"]
[( #{subject(${shareRecipient.firstName},${shareRecipient.lastName},${share.name})})]
[/]
[# th:if="${anonymous}"]
[( #{subjectAnonymous(${shareRecipient.mail},${share.name})})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <th:block th:if="${!anonymous}" >
            <span data-th-utext="#{mainMsgInt(${shareRecipient.firstName},${shareRecipient.lastName})}">
            Peter WILSON has downloaded your file
            </span>
          </th:block>
          <th:block th:if="${anonymous} ">
            <span data-th-utext="#{mainMsgExt(${shareRecipient.mail})}">
              unknown@domain.com has downloaded your file
            </span>
          </th:block>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{downloadDate},${actionDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${expiryDate})"/>
     <th:block th:if="${sharesCount}  > 1 ">
         <th:block data-th-replace="layout :: infoFileListUploadState(#{common.filesInShare},${shares})"/>
   </th:block>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='downloadDate = Téléchargé le
fileNameEndOfLine = {0}.
mainMsgExt = Le destinataire externe <b>{0}</b> a téléchargé votre fichier
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a téléchargé votre fichier 
subject =  {0} {1} a téléchargé {2}
subjectAnonymous = {0} a téléchargé {1}',messages_english='downloadDate = Download date
fileNameEndOfLine = {0}.
mainMsgExt = The external recipient <b>{0}</b> has downloaded your file
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has downloaded your file
subject = {0} {1} has downloaded {2}
subjectAnonymous = {0} has downloaded {1}' WHERE id=4;

UPDATE mail_content SET subject='[( #{subject(${shareOwner.firstName},${shareOwner.lastName},${share.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${shareOwner.firstName},${shareOwner.lastName})}">
             Peter WILSON has downloaded your file
          </span>
          <span style="font-weight:bold" data-th-text="${share.name}" >
             filename.ext
          </span>.
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{deletedDate},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='deletedDate = Supprimé le
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> a supprimé le partage
subject = {0} {1} a supprimé le partage de {2}',messages_english='deletedDate = Deletion date
mainMsg = <b>{0} <span style="text-transform:uppercase">{1}</span></b> has deleted the  fileshare
subject = {0} {1} has deleted the fileshare {2}' WHERE id=5;

UPDATE mail_content SET subject='[( #{subject(${share.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-if="(${!anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        </th:block>
        <th:block data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.mail})"/>
        </th:block>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <th:block data-th-if="(${anonymous})">
            <span
              data-th-utext="#{mainMsgExt(${share.name}, ${shareOwner.firstName},${shareOwner.lastName},${daysLeft})}">
              Your share link for Peter sent by Peter WILSON, will expire in 8 days. a-shared-file.txt.
            </span>
          </th:block>
          <th:block data-th-if="(${!anonymous})">
            <span data-th-utext="#{beginningMainMsgInt}"></span>
            <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="${share.name}"
                th:href="@{${share.href}}">
                  filename.ext
             </a>
          </span>
            <span
              data-th-utext="#{endingMainMsgInt(${shareOwner.firstName},${shareOwner.lastName},${daysLeft})}">  </span>
            <!--/* Single download link for external recipient */-->
            <th:block data-th-replace="layout :: actionButtonLink(#{common.download},${share.href})"/>
          </th:block>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block
      data-th-replace="layout :: infoStandardArea(#{sharedBy},#{name(${shareOwner.firstName},${shareOwner.lastName})})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgInt = Le partage
endingMainMsgInt = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b>, va expirer dans <b>{2} jours</b>.
mainMsgExt = Le partage <b>{0}</b> émis par <b> {1} <span style="text-transform:uppercase">{2}</span></b>, va expirer dans <b>{3} jours</b>.
name = {0} {1}
sharedBy = Partagé par
subject =  Le partage {0} va bientôt expirer',messages_english='beginningMainMsgInt = The fileshare
endingMainMsgInt = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b>,  will expire in <b>{2} days</b>.
mainMsgExt = The fileshare <b>{0}</b> sent by <b> {1} <span style="text-transform:uppercase">{2}</span></b>,  will expire in <b>{3} days</b>.
name = {0} {1}
sharedBy = Shared by
subject = The fileshare for {0} is about to expire' WHERE id=6;

UPDATE mail_content SET subject='[# th:if="${documentsCount} > 1"]
[( #{subjectPlural(${documentsCount})})]
[/]
        [# th:if="${documentsCount} ==  1"]
          [( #{subjectSingular(${documentsCount})})]
       [/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${documentsCount} ==  1)" data-th-utext="#{mainMsgSingular(${documentsCount})}">
            Some recipients have not downloaded 2 files yet. You may find further details of the recipients downloads, below.
          </span>
          <span data-th-if="(${documentsCount} >  1)" data-th-utext="#{mainMsgplural(${documentsCount})}">
            Some recipients have not downloaded 2 files yet. You may find further details of the recipients downloads, below.
          </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoFileListRecipientUpload(#{downloadStatesTile},${documents})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareGroup.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shareGroup.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='downloadStatesTile = Etat de téléchargement
mainMsgplural = Certains destinataires n''''ont pas téléchargés <b>{0} fichiers</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
mainMsgSingular = Certains destinataires n''''ont pas téléchargés <b>{0} fichier</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
subjectPlural = Rappel de non-téléchargement : {0} fichiers n''''ont pas été téléchargés.
subjectSingular = Rappel de non-téléchargement :  {0} fichier n''''a pas été téléchargé.',messages_english='downloadStatesTile = Downloads states
mainMsgplural = Some recipients have not downloaded <b>{0} files</b>. You may find further details of the recipients downloads below.
mainMsgSingular = Some recipients have not downloaded <b>{0} file</b>. You may find further details of the recipients downloads below.
subjectPlural = Undownloaded shared files alert : {0} files have not been downloaded yet.
subjectSingular = Undownloaded shared files alert : {0} file have not been downloaded yet.' WHERE id=7;

UPDATE mail_content SET subject='[( #{subject(${creator.firstName},${creator.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${creator.firstName},${creator.lastName},#{productName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${resetLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{activationLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Activer mon compte
accountExpiryDateTitle = Date d''''''expiration
activationLinkTitle = Lien d''''initialisation
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a créé un compte invité sur <b>LinShare</b> qui vous permet de partager des fichiers de façon sécurisée. <br/> Pour vous connecter, vous devez finaliser votre inscription en créant votre mot de passe à l''''aide du lien  ci-dessous.
subject = {0}  {1} vous invite a activer votre compte
userNameTitle = Identifiant',messages_english='accessToLinshareBTn = Activate account
accountExpiryDateTitle = Account expiry date
activationLinkTitle = Initialization link
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a <b>{2}</b> guest account for you, which enables you to transfer files more securely. <br/>To log into your account, you will need to finalize your subscription by creating your password, using the following link.
subject = {0}  {1} invited you to activate your {2} account
userNameTitle = Username' WHERE id=8;

UPDATE mail_content SET subject='[( #{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p style="font-weight:bold;font-size:15px;"  data-th-utext="#{mainTile}">Did you forget your password ?</p>
        <p>
          <span data-th-utext="#{beginingMainMsg}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block data-th-replace="layout :: actionButtonLink(#{changePasswordBtn},${resetLink})"/>
          <br/>
        </p>
        <p  data-th-utext="#{endingMainMsg}"></p>
        <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{resetLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accountExpiryDateTitle = Date d''''expiration
beginingMainMsg =  Suivez le lien ci-dessous afin de réinitialiser le mot de passe de votre compte LinShare.
changePasswordBtn = Réinitialiser
endingMainMsg = Si vous n''''avez pas sollicité ce changement de mot de passe, merci d''''ignorer cet email. Votre mot de passe ne sera pas mis à jour tant que vous n''''en créé pas un nouveau, via le lien ci-dessus.
mainTile = Vous avez oublié votre mot de Passe ?
resetLinkTitle = Lien de réinitialisation
subject =  LinShare instruction de réinitialisation de mot de passe
userNameTitle = Identifiant',messages_english='accountExpiryDateTitle = Account expiry date
beginingMainMsg =  Follow the link below to reset your LinShare password account.
changePasswordBtn = Change password
endingMainMsg = If you did not request a password reset, please ignore this email. Your password will not change until you create a new one via the link above.
mainTile = Did you forget your password ?
resetLinkTitle = LinShare reset password link
subject =  LinShare reset password instructions
userNameTitle = Username' WHERE id=9;

UPDATE mail_content SET subject='[( #{subject(${requestRecipient.mail},${document.name},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg(${requestRecipient.mail})}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >
                  filename.ext
             </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${requestRecipient.mail})}"></span>
          <th:block   data-th-replace="layout :: actionButtonLink(#{common.download},${requestUrl})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{fileUploadedThe},${document.creationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{fileSize},${document.size})"/>
 <th:block data-th-if="(${request.authorizedFiles})">
      <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
         #{uploadedOverTotal(${request.uploadedFilesCount},${request.authorizedFiles})})"/>
 </th:block>
 <th:block data-th-if="(${!request.authorizedFiles})">
      <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
         #{totalUploaded(${request.uploadedFilesCount})})"/>
 </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='endingMainMsg = dans votre Invitation de Dépôt.
fileSize =  Taille du fichier
fileUploadedThe= Fichier déposé le
invitationClosureDate = Date de clôture
invitationCreationDate = Date d''activation
beginningMainMsg = <b> {0} </b> vous a déposé le fichier
numFilesInDepot = Nombre de fichiers déposés
subject =  {0}  vous a déposé {1}  dans votre Invitation de Dépôt
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} fichiers',messages_english='endingMainMsg = in your Upload Request
fileSize =  File size
fileUploadedThe = Upload date
invitationClosureDate = Closure date
invitationCreationDate = Activation date
beginningMainMsg =  <b> {0} </b> has uploaded the file
endingMainMsg = in your Upload Request.
numFilesInDepot = Total uploaded files
subject =  {0}  has uploaded {1}  in your Upload Request
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id=10;

UPDATE mail_content SET subject='[( #{subject(${requestRecipient.mail},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content */-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p style="font-size: 14px;font-weight: bold;color: #df5656;margin-bottom: 7px;" data-th-utext="#{mainMsgTitle}">
          You have no available space.</p>
        <p>
          <span data-th-utext="#{mainMsg(${requestRecipient.mail})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      <!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div th:assert="${!#strings.isEmpty(body)}"
         th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
      <span id="message-title">
        <span data-th-text="#{msgTitle}">You have a message from</span>
      </span>
      <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
        Hi design,<br>
       Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
        within my LinShare space.  <br>Best regards, Peter.
       </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper  main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${maxDepositSize != null})">
      <th:block data-th-replace="layout :: infoStandardArea(#{maxUploadDepotSize},${maxDepositSize})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='invitationClosureDate = Date de clôture
invitationCreationDate = Date d''''activation
mainMsg =  <b>{0}</b>  n''''a pas pu déposer des fichiers dans le dépôt car il n''''y a plus d''''espace disponible dans votre Espace Personnel. Veuillez s''''il vous plait libérez de l''''espace.
mainMsgTitle = Vous n''''avez plus d''''espace disponible.
maxUploadDepotSize =  Taille total du dépôt
msgTitle = Message lié à l''''invitation de dépôt :
recipientsURequest = Destinataires
subject =  {0}  n''''a pu déposer un fichier car il n''''y a plus d''''espace disponible',messages_english='invitationClosureDate = Closure date
invitationCreationDate = Activation date
mainMsg =  <b>{0}</b> is not able to upload any file, since there is no more space available in your Personal Space. Please free up some space.
mainMsgTitle = No more space available.
maxUploadDepotSize = Maximum size of the depot
msgTitle = Upload Request''s  attached message :
recipientsURequest = Recipients
subject =  {0} could not upload a file since there is no more space available' WHERE id=11;

UPDATE mail_content SET subject='[# th:if="${warnOwner}"] [( #{subjectForOwner})]
[/]
[# th:if="${!warnOwner}"]
[( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName})})]
[/]
[# th:if="${!#strings.isEmpty(mailSubject)}"]
[( #{formatMailSubject(${mailSubject})})]
[/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})">
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="(${isRestricted})"   data-th-utext="#{beginningMainMsgUnGrouped(${remainingDays})}"></span>
            <span  data-th-if="!(${isRestricted})"   data-th-utext="#{beginningMainMsgGrouped(${remainingDays})}"></span>
            <span data-th-if="(${documentsCount} ==  1)"   data-th-utext="#{endingMainMsgSingular}" ></span>
            <span  data-th-if="(${documentsCount} >  1)"   data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for owner of the upload request */-->
        <!--/* upper message content for recipients of the upload request */-->
        <th:block data-th-if="(${!warnOwner})" >
          <!--/* Greetings */-->
          <th:block  data-th-replace="layout :: greetings(${requestRecipient.mail})" />
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"   data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
            <th:block   data-th-replace="layout :: actionButtonLink(#{uploadFileBtn},${requestUrl})"/>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div   th:assert="${!#strings.isEmpty(body)}" th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
      <span id="message-title">
        <span data-th-text="#{msgTitle}">You have a message from</span>
      </span>
      <span id="message-content"  data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
         Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
      </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <!--/*Lower message content for the owner of the upload request */-->
    <th:block  data-th-if="(${warnOwner})">
      <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
      <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    </th:block>
    <!--/*Lower message content for the owner of the upload request */-->
    <!--/*Lower message content for recipients of the upload request */-->
    <th:block  data-th-if="(${!warnOwner})">
      <th:block  data-th-if="!(${isRestricted})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="(${isRestricted})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgForRecipient =   L''''invitation dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> va expirer dans <b>{2} jours</b>
beginningMainMsgGrouped =   Votre invitation groupée sera clôturée dans  <b>{0} jours</b>.
beginningMainMsgUnGrouped =   Votre invitation au dépôt sera clôturée dans  <b>{0} jours</b>.
endingMainMsgPlural = et vous avez actuellement reçu <b>{0} fichiers</b>.
endingMainMsgPlural = Il y a un total de <b> {0} fichiers </b> dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez actuellement envoyé  <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = et vous avez actuellement reçu <b>1 fichier</b>.
endingMainMsgSingular = Il y a au total <b>1 fichier </b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez actuellement envoyé  <b>1 fichier </b> dans le dépôt.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate =  Date d''''activation
invitationCreationDate = Date de clôture
msgTitle = Message lié à l''''invitation :
recipientsURequest = Destinataires
subjectForOwner =  Votre invitation de dépôt sera bientôt clôturée
subjectForRecipient = L''''invitation au dépôt de {0} {1} sera bientôt clôturée
uploadFileBtn = Déposer un fichier',messages_english='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s The Upload Request is about to reach it''''s end date in <b>{2} days</b>
beginningMainMsgGrouped = Your invitation will be closed in  <b>{0} days</b>.
beginningMainMsgUnGrouped =  Your invitation is about to be closed in <b>{0} days</b>.
endingMainMsgPlural =  and you currently have received<b>{0} files</b>.
endingMainMsgPlural = There are a total of <b> {0} files </b> in the depot.
endingMainMsgPluralForRecipient = and so far you have sent <b> {0} files </b> in the depot.
endingMainMsgSingular =   and you currently have received<b>1 file</b>.
endingMainMsgSingular = There is a total of <b>1 file </b> in the repository.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle =  Upload Request''''s  attached message :
recipientsURequest = Recipients
subjectForOwner =  Your invitation is about to be closed.
subjectForRecipient =  {0} {1}''''s  invitation is about to be closed
uploadFileBtn = Upload a file' WHERE id=12;

UPDATE mail_content SET subject='[# th:if="${warnOwner}"] 
           [( #{subjectForOwner(${subject})})]
       [/]
        [# th:if="${!warnOwner}"]
           [( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName},${subject})})]
       [/]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content container*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})" >
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="(${isRestricted})"   data-th-utext="#{beginningMainMsgUnGrouped}"></span>
            <span  data-th-if="!(${isRestricted})"   data-th-utext="#{beginningMainMsgGrouped}"></span>
            <span data-th-if="(${documentsCount} ==  1)"   data-th-utext="#{endingMainMsgSingular}" ></span>
            <span  data-th-if="(${documentsCount} >  1)"   data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for owner of the upload request */-->
        <!--/* upper message content for recipients of the upload request */-->
        <th:block data-th-if="(${!warnOwner})" >
          <!--/* Greetings */-->
          <th:block  data-th-replace="layout :: greetings(${requestRecipient.mail})" />
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"  data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of upper main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div   th:assert="${!#strings.isEmpty(body)}" th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section><!--/* End of uppermain-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <!--/*Lower message content for the owner of the upload request */-->
    <th:block  data-th-if="(${warnOwner})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    </th:block>
    <!--/*Lower message content for the owner of the upload request */-->
    <!--/*Lower message content for recipients of the upload request */-->
    <th:block  data-th-if="(${!warnOwner})">
      <th:block  data-th-if="!(${isRestricted})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="(${isRestricted})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgForRecipient = L''''invitation de Dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a expiré.
beginningMainMsgGrouped = Votre Invitation de Dépôt groupée a expiré.
beginningMainMsgUnGrouped = Votre Invitation de Dépôt a expiré.
endingMainMsgPlural = et vous avez reçu un total  de <b>{0} fichiers</b>.
endingMainMsgPluralForRecipient = et vous avez  envoyé  <b> {0} fichiers </b>.
endingMainMsgSingular = et vous avez  reçu au total <b>1 fichier</b>.
endingMainMsgSingularForRecipient = et vous avez  envoyé <b>1 fichier </b>.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate = Date  de clôture
invitationCreationDate =  Date d''''activation
msgTitle = Message lié à l''''Invitation de Dépôt :
recipientsURequest = Destinataires
subjectForOwner = Votre Invitation de Dépôt {0} est clôturée
subjectForRecipient = L'''' Invitation de Dépôt de {0} {1} intitulée {2} est clôturée',messages_english='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Upload Request has expired
beginningMainMsgGrouped = Your grouped Upload Request has expired
beginningMainMsgUnGrouped = Your Upload Request has expired
endingMainMsgPlural = and you have received a total of <b>{0} files</b>.
endingMainMsgPluralForRecipient = and you currently have sent  <b> {0} files </b>.
endingMainMsgSingular = and you have received a total of <b>1 file</b>.
endingMainMsgSingularForRecipient = and you currently have uploaded <b>1 file </b> to the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle = Upload Request''''s  attached message :
recipientsURequest = Recipients
subjectForOwner = Your invitation {0} is now closed
subjectForRecipient =  {0} {1}''''s  invitation {2} is now closed' WHERE id=13;

UPDATE mail_content SET subject='[( #{subject(${requestRecipient.mail},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <section id="main-content">
    <!--/* Upper main-content*/-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="!(${isRestricted})" data-th-utext="#{groupedBeginningMainMsg(${requestRecipient.mail})}"></span>
          <span data-th-if="(${isRestricted})"
                data-th-utext="#{ungroupedBeginningMainMsg(${requestRecipient.mail})}"></span>
          <span data-th-if="(${documentsCount} == 1)" data-th-utext="#{endingMainMsgSingular}"></span>
          <span data-th-if="(${documentsCount} > 1)" data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div th:assert="${!#strings.isEmpty(body)}"
         th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="!(${isRestricted})">
       <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{fileSize}, ${totalSize})"/>
    <th:block data-th-if="(${request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{uploadedOverTotal(${request.uploadedFilesCount},${request.authorizedFiles})})"/>
    </th:block>
    <th:block data-th-if="(${!request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{totalUploaded(${request.uploadedFilesCount})})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section> <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='endingMainMsgPlural =  Il y a <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = Il y a  <b>1 fichier </b> dans le dépôt.
filesInURDepot =  Fichiers déposés
fileSize =  Taille
groupedBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt.
invitationClosureDate = Date de clôture
invitationCreationDate = Date d''activation
msgTitle = Message lié à l''invitation :
numFilesInDepot = Nombre de fichiers déposés
recipientsURequest = Destinataires
subject = {0} a clôturé votre invitation de dépôt : {1}
ungroupedBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt.
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} files',messages_english='endingMainMsgPlural = There are a total of <b> {0} files </b> in the depot.
endingMainMsgSingular =  There is a total <b>1 file </b> in the depot.
filesInURDepot = Files uploaded
fileSize =  Total filesize
groupedBeginningMainMsg = <b>{0}</b> has closed your grouped Upload Request depot.
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle =  Upload request''s  attached message :
numFilesInDepot = Total uploaded files
recipientsURequest = Recipients
subject =  {0}  has closed  your Upload Request depot : {1}
ungroupedBeginningMainMsg  = <b>{0}</b> has closed your Upload Request depot.
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id=14;

UPDATE mail_content SET subject='[( #{subject(${requestRecipient.mail},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <section id="main-content">
    <!--/* Upper main-content*/-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
  <span  data-th-utext="#{mainMsg(${requestRecipient.mail},${deleted.name})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div th:assert="${!#strings.isEmpty(body)}"
         th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
     <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section> <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='invitationClosureDate = Date d''''expiration
invitationCreationDate = Date d''''activation
mainMsg = <b>{0}</b> a supprimé le fichier <b> {1} </b>de votre Invitation de Dépôt.
msgTitle = Message lié à l''''Invitation de Dépôt :
subject =  {0} a supprimé un fichier de votre invitation de dépôt {1}',messages_english='invitationClosureDate = Closure date
invitationCreationDate = Activation date
mainMsg = <b>{0}</b> has deleted the file <b> {1} </b>from your Upload Request depot.
msgTitle = Upload request''''s  attached message :
subject = {0} has deleted a file from the Upload Request depot {1}' WHERE id=15;

UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="${body}" style="white-space: pre-line;">
                  Hi Amy,<br>
                  As agreed,  could you send me the report. Feel free to contact me if need be. <br/>Best regards, Peter.
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
                   <th:block data-th-if="(${!request.wasPreviouslyCreated})">
                       <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                          Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                       </span>
                   </th:block>
                    <th:block data-th-if="(${request.wasPreviouslyCreated})">
                       <span data-th-text="#{msgAlt(${requestOwner.firstName} , ${requestOwner.lastName})}"> Peter Wilson''s Upload Request depot is now activated..</span>
                     </th:block>
                     <br/>
                     <!--/* Check if the external user has a password protected file share */-->
                     <span data-th-if="(${!protected})">
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                     </span>
                     <span data-th-if="(${protected})">
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                     </span>
                  </p>
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
            <div data-th-if="(${protected})">
               <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
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
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers dans le dépôt : <b>{2}</b>.
msgAlt = L''''invitation de {0} {1} est désormais active.
msgFrom = Le message de
msgProtected = Vous pouvez déverrouiller le dépôt en suivant le lien ci-dessous et en saisissant le mot de passe fourni.
msgUnProtected = Vous pouvez y accéder en suivant le lien ci-dessous.
name = {0} {1}
password = Mot de passe
recipientsOfDepot = Destinataires
subject = {0} {1} vous invite à déposer des fichiers dans le dépôt : {2}',messages_english='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> invited you to its upload request : <b>{2}</b>.
msgFrom = Message from
msgAlt = The repository from {0} {1} is now active.
msgProtected = Unlock it by following the link below and entering the password.
msgUnProtected = Access it by following the link below.
name = {0} {1}
password = Password
recipientsOfDepot = Recipients
subject = {0} {1} invited you to its upload request : {2}' WHERE id=16;

UPDATE mail_content SET subject='[(#{subject(${subject})})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${request.subject})}">
                      Your Upload Request repository labeled $subject is now activated.
                     </span>
                     <span data-th-text="#{msgLink}">In order to access it click the link below.</span>
                  </p>
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
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
             <div data-th-if="(${totalMaxDepotSize})">
                   <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille autorisée
mainMsg = Votre dépôt intitulé <b>{0}</b> est désormais actif.
msgLink = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
recipientsOfDepot = Destinataires
subject = Votre invitation de dépôt {0} est désormais active',messages_english='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = Your Upload Request labeled <b>{0}</b> is now active.
msgLink = Access it by following the link below.
recipientsOfDepot = Recipients
subject = Your Upload Request : {0}, is now active' WHERE id=17;

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
                  <b data-th-text="#{name(${requestOwner.firstName}, ${requestOwner.lastName})}">Peter Wilson</b> :
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter Wilson reminds you that he still has not received the requested files. 
                     </span>
                     <span data-th-utext="#{mainMsgEnd}">
                     You can upload your files in the provided depot made available to you labeled  subject.
                     </span>
                     <!--/* Check if the external user has a password protected file share */-->
                     <br/>
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                  </p>
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
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="(${totalMaxDepotSize})">
                 <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> aimerais vous rappeller de déposer vos fichiers.
mainMsgEnd =
msgFrom =  Le message de
msgUnProtected = Pour accéder au dépôt, suivez le lien ci-dessous.
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} attend toujours des fichiers de votre part',messages_english='buttonMsg = Access
closureDate = Closure date
depotSize = Size
mainMsg = <b>{0} {1}</b> kindly reminds you to upload your files.
mainMsgEnd =
msgFrom = Message from
msgUnProtected = In order to upload your files, please follow the link below.
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} is still awaiting your files' WHERE id=18;

UPDATE mail_content SET subject='[( #{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                     </span>
                     <br/>
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                  </p>
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
            <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='buttonMsg = Accès au dépôt
closureDate = Dépôt disponible jusqu''''au
mainMsg = <b>{0} {1}</b> a modifié le mot de passe d''''accès à l''''Invitation de Dépôt : {2}.
msgProtected = Vous trouverez ci-dessous le nouveau mot de passe ainsi que le lien d''''accès.
password = Mot de passe
subject = {0} {1} vous envoie le nouveau mot de passe du dépôt : {2}',messages_english='buttonMsg = Access to the depot
closureDate = Depot closure date
mainMsg = <b>{0} {1}</b> has changed the password of the Upload Request : {2}
msgProtected = You may find the new password below as well as the access link.
password = Password
subject = {0} {1} sent you the new password for the depot: {2}' WHERE id=19;

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
subject = {0} {1} has sent an invitation to access to his Upload Request.' WHERE id=20;

UPDATE mail_content SET subject='[( #{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has closed prematurely his Upload Request Depot labeled : subject.
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="(${isgrouped})">
               <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='closureDate = Date de clôture
filesInURDepot = Fichiers
mainMsg = <b>{0} {1}</b> a fermé son invitation de dépôt : {2}.
recipientsOfDepot = Destinataires
subject = {0} {1} a fermé l''''invitation de dépôt : {2}',messages_english='closureDate = Closure date
filesInURDepot = Files
mainMsg = <b>{0} {1}</b> has closed the upload request labeled : {2}.
recipientsOfDepot = Recipients
subject = {0} {1} has closed his upload request : {2}' WHERE id=21;

UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has deleted your access to the depot : : subject.
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <th:block data-th-replace="layout :: infoDateArea(#{deletionDate},${deletionDate})"/>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='deletionDate = Accès au dépôt retiré le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a retiré votre accès au dépôt de l''''invitation intitulée : {2}.
subject = {0} {1} a supprimé votre accès au dépôt : {2}',messages_english='deletionDate = Deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has removed your access to the depot : {2}.
subject = {0} {1} has removed your access to the depot : {2}' WHERE id=22;

UPDATE mail_content SET subject='[# th:if="${!subject.modified}"]
[(#{subject(${subject.value})})]
[/]
[# th:if="${subject.modified}"]
[(#{subject(${subject.oldValue})})]
[/]',body='<!DOCTYPE html>
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                     </span>
                     <span data-th-utext="#{secondaryMsg}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
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
               <th:block data-th-replace="layout :: infoEditedDateArea(#{expiryDate},${expiryDate.oldValue},${expiryDate.value})"/>
            </span>
            <span data-th-if="(${activationDate.modified})">
               <th:block data-th-replace="layout :: infoEditedDateArea(#{activationDate},${activationDate.oldValue},${activationDate.value})"/>
            </span>
            <span data-th-if="(${subject.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{nameOfDepot},${subject.oldValue},${subject.value})"/>
            </span>
            <span data-th-if="(${closureRight.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{closureRight},${closureRight.oldValue},${closureRight.value})"/>
            </span>
            <span data-th-if="(${deletionRight.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{deletionRight},${deletionRight.oldValue},${deletionRight.value})"/>
            </span>
            <span data-th-if="(${maxFileSize.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{maxFileSize},${maxFileSize.oldValue},${maxFileSize.value})"/>
            </span>
            <span data-th-if="(${maxFileNum.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{maxFileNum},${maxFileNum.oldValue},${maxFileNum.value})"/>
            </span>
            <span data-th-if="(${totalMaxDepotSize.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{depotSize},${totalMaxDepotSize.oldValue},${totalMaxDepotSize.value})"/>
            </span>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='activationDate = Date d''activation
closureRight = Droits de dépôt
deletionRight = Droits de suppression
depotSize = Taille du dépôt
expiryDate = Date de clôture
local = Langue
enableNotification = Activation des notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a modifié des paramètres liés au dépôt.
maxFileNum = Nombre de Fichiers
maxFileSize = Taille autorisée
msgFrom = Nouveau message de
name = {0} {1}
nameOfDepot: Nom du dépôt
secondaryMsg = Les modifications sont listées ci-dessous.
subject = Modification des paramètres du dépôt : {0}',messages_english='activationDate = Activation date
closureRight = Closure rights
deletionRight = Deletion rights
depotSize = Repository size
expiryDate = Closure date
local = Local
enableNotification = Enable notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  has updated some settings related to the Upload Request.
maxFileNum = File number
maxFileSize = File size
msgFrom =  New message from
name = {0} {1}
nameOfDepot: Name of the depot
secondaryMsg = Updated settings are listed below.
subject = Updated Settings for Upload Request : {0}
' WHERE id=23;

UPDATE mail_content SET subject='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${document.name})})]',body='<!DOCTYPE html>
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
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${document.name},${subject})}">
                 Peter WILSON has deleted the file my-file.txt from the depot : subject
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <th:block data-th-replace="layout :: infoDateArea(#{deletionDate},${deletionDate})"/>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='closureDate = Dépôt disponible jusqu''''au
deletionDate = Fichier supprimé le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>a supprimé le fichier  <b>{2} </b> de  l''''Invitation de Dépôt : {3}
subject = {0} {1} a supprimé {2} du dépôt',messages_english='closureDate = Depot closure date
deletionDate = File deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has deleted the file <b>{2} </b>from the depot  : {3}.
subject = {0} {1} has deleted {2} from the depot' WHERE id=24;

UPDATE mail_content SET subject='[( #{subject(${guest.firstName},${guest.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${guest.firstName},${guest.lastName},${daysLeft})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{accountCreationDateTitle},${guestCreationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{userEmailTitle},${guest.mail})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Le compte de votre invité expire
accountCreationDateTitle = Date de création
accountExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
mainMsg = Le compte invité de : <b> {0} <span style="text-transform:uppercase">{1}</span></b> expirera dans {2} jours. Pensez à prolonger la validité du compte si besoin.
subject = Le compte invité de {0}  {1} expire bientôt
userEmailTitle = Email',messages_english='accessToLinshareBTn = Expiration account
accountCreationDateTitle = Account creation date
accountExpiryDateTitle = Account expiry date
activationLinkTitle = Initialization link
mainMsg = The  <b> {0} <span style="text-transform:uppercase">{1}</span></b> guest account is about to expire in {2} days. If this account is still needed,  postpone its expiration date.
subject = {0}  {1} guest account will expire soon.
userEmailTitle = Email' WHERE id=25;

UPDATE mail_content SET subject='[( #{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${daysLeft},${shareRecipient.firstName},${shareRecipient.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle},#{name(${shareRecipient.firstName}, ${shareRecipient.lastName})})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Votre partage expire bientôt
shareRecipientTitle =  Destinataire
shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg =  expire dans {0} jours sans avoir été téléchargé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Votre partage expire bientôt et n''''a pas encore été téléchargé
name = {0} {1}
fileNameEndOfLine = {0}',messages_english='accessToLinshareBTn = Your share will expire soon
shareRecipientTitle = Recipient
shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg =  will expire in {0} days and has not been downloaded by the recipient <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Your share will expire soon and has not been downloaded
name = {0} {1}
fileNameEndOfLine = {0}' WHERE id=26;

UPDATE mail_content SET subject='[( #{subject(${share.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${shareOwner.firstName},${shareOwner.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b> a expiré et a été supprimé par le <b>système</b>.
subject = Le partage {0} a expiré
fileNameEndOfLine = {0}',messages_english='shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b> has expired and been deleted by the <b>system</b>.
subject = The fileshare {0} has expired
fileNameEndOfLine = {0}' WHERE id=27;

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
            <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
            <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMainMsg}"></span>
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
    <th:block data-th-if="(${threadMember.admin})">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/>
    </th:block>
    <th:block data-th-if="(!${threadMember.admin})">
        <th:block data-th-if="(${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/>
        </th:block>
        <th:block data-th-if="(!${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/>
        </th:block>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${threadMember.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au groupe de travail <br>
simpleMainMsg = Vous avez été ajouté au groupe de travail
subject = Vous avez été ajouté au groupe de travail {0}
workGroupRight = Droit par défaut 
workGroupNameTitle = Nom du groupe de travail',messages_english='workGroupCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the workgroup <br>
simpleMainMsg = You have been added to the workgroup
subject = You have been added to the workgroup {0}
workGroupRight = Default right
workGroupNameTitle = Workgroup Name' WHERE id=28;

UPDATE mail_content SET subject='[(#{subject(${workGroupName})})]',body='<!DOCTYPE html>
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
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
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
    <th:block data-th-if="(${threadMember.admin})">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/>
    </th:block>
    <th:block data-th-if="(!${threadMember.admin})">
        <th:block data-th-if="(${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/>
        </th:block>
        <th:block data-th-if="(!${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/>
        </th:block>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupUpdatedDateTitle},${threadMember.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='workGroupUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le groupe de travail
mainMsgNext = ont été mis à jour 
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Vos droits sur le groupe de travail {0} ont été mis à jour
workGroupRight =  Nouveau droit
workGroupNameTitle = Nom du groupe de travail',messages_english='workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the workgroup 
mainMsgNext= have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the workgroup {0} was updated.
workGroupRight = Current right
workGroupNameTitle = Workgroup Name' WHERE id=29;

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
</html>',messages_french='subject = Les accès au groupe de travail {0} vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du groupe de travail <b>{2}</b>
simpleMsg = Les accès au groupe de travail <b>{0}</b> vous ont été retirés.
workGroupNameTitle = Nom du groupe de travail',messages_english='subject = Your access to the workgroup {0} was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the workgroup  <b>{2}</b>
simpleMsg =  Your access to the workgroup <b>{0}</b> was withdrawn.     
workGroupNameTitle = Workgroup Name' WHERE id=30;

UPDATE mail_content SET subject='[( #{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(#{productName},${guest.mail})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{accountCreationDateTitle},${guestCreationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accountCreationDateTitle = Date de création
accountExpiryDateTitle = Date d''''expiration
mainMsg = Le mot de passe du compte {0} <b>{1}</b> a été modifié.
subject = Votre mot de passe a été modifié',messages_english='accountCreationDateTitle = Account creation date
accountExpiryDateTitle = Account expiry date
mainMsg = The password of the account {0} <b>{1}</b> was modified.
subject = Your password has been modified' WHERE id=31;

UPDATE mail_content SET subject='[(#{subject})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${recipient.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}">
                 Peter WILSON has created a new permanent authentication token for your account
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
           <th:block data-th-replace="layout :: infoStandardArea(#{tokenLabel},${label})"/>
           <th:block data-th-replace="layout :: infoDateArea(#{tokenCreationDate},${creationDate})"/>
           <div data-th-if="${!#strings.isEmpty(description)}">
             <th:block data-th-replace="layout :: infoStandardArea(#{tokenDescription},${description})"/>
           </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='subject = Création d''''un jeton d''''accès permanent
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a créé un jeton d''''accès permanent pour votre compte.
tokenCreationDate = Date de création
tokenLabel = Nom
tokenDescription = Description',messages_english='subject = Creation of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a permanent authentication token for your account.
tokenCreationDate = Creation date
tokenLabel = Name
tokenDescription = Description' WHERE id=32;

UPDATE mail_content SET subject='[(#{subject})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${recipient.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}">
                 Peter WILSON has deleted a permanent authentication token for your account
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
           <th:block data-th-replace="layout :: infoStandardArea(#{tokenLabel},${label})"/>
           <th:block data-th-replace="layout :: infoDateArea(#{tokenCreationDate},${creationDate})"/>
           <div data-th-if="${!#strings.isEmpty(description)}">
             <th:block data-th-replace="layout :: infoStandardArea(#{tokenDescription},${description})"/>
           </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='subject = Suppression d''''un jeton d''''accès permanent
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a supprimé un jeton d''''accès permanent pour votre compte.
tokenCreationDate = Date de création
tokenLabel = Nom
tokenDescription = Description
tokenIdentifier = Identifiant',messages_english='subject = Deletion of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has deleted a permanent authentication token for your account.
tokenCreationDate = Creation date
tokenLabel = Name
tokenDescription = Description' WHERE id=33;

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
            <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
            <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMainMsg}"></span>
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
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${threadMember.creationDate})"/>
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
</html>',messages_french='workGroupCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au drive <br>
simpleMainMsg = Vous avez été ajouté au drive
subject = Vous avez été ajouté au drive {0}
workGroupRight = Droit par défaut 
workGroupNameTitle = Nom du drive
nestedWorkGroupsList=Vous avez automatiquement été ajouté aux groupes de travail suivants :
displayDriveAndRole ={0} avec un rôle <span style="text-transform:uppercase">{1}</span>',messages_english='workGroupCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the drive <br>
simpleMainMsg = You have been added to the drive
subject = You have been added to the drive {0}
workGroupRight = Default right
workGroupNameTitle = Drive Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayDriveAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role' WHERE id=34;

UPDATE mail_content SET subject='[(#{subject(${workGroupName})})]',body='<!DOCTYPE html>
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
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
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
    <th:block th:switch="${threadMember.role.name}">
      <p th:case="''DRIVE_ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{workGroupRightAdminTitle})"/></p>  
      <p th:case="''DRIVE_CREATOR''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{workGroupRightWirteTitle})"/></p>  
      <p th:case="''DRIVE_READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{workGroupRightReadTitle})"/></p>  
    </th:block>
    <th:block th:switch="${threadMember.nestedRole.name}">
      <p th:case="''ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/></p>  
      <p th:case="''CONTRIBUTOR''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>  
      <p th:case="''WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/></p>  
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupUpdatedDateTitle},${threadMember.creationDate})"/>
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
</html>',messages_french='workGroupUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le DRIVE
mainMsgNext = et dans ses WorkGroups contenus ont été mis à jour
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Vos droits sur le DRIVE {0} ont été mis à jour
driveRight = Droit sur le DRIVE
workGroupRight =  Droit sur le groupe de travail
workGroupNameTitle = Nom du DRIVE
nestedWorkGroupsList = Liste des workgoups
nbrWorkgoups = Nombre de groupe de travail mis à jours',messages_english='workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the DRIVE 
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the DRIVE {0} was updated.
driveRight = Drive right
workGroupRight = Workgroup right
workGroupNameTitle = Drive Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups' WHERE id=35;

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
</html>',messages_french='subject = Les accès au drive {0} et à ses workgroups vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du drive <b>{2}</b>
simpleMsg = Les accès au drive <b>{0}</b> vous ont été retirés.
workGroupNameTitle = Nom du Drive',messages_english='subject = Your access to the drive {0}  and its workgroups was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the drive  <b>{2}</b>
simpleMsg =  Your access to the drive <b>{0}</b> was withdrawn.     
workGroupNameTitle = Drive Name' WHERE id=36;
UPDATE mail_footer SET messages_french='learnMoreAbout=En savoir plus sur
productOfficialWebsite=http://www.linshare.org/',messages_english='learnMoreAbout=Learn more about
productOfficialWebsite=http://www.linshare.org/',footer='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <body>
    <div data-th-fragment="email_footer">
                                <td style="border-collapse:collapse;padding: 6px 0 4px 17px;" valign="top">
                                  <p style="margin: 0; font-size: 10px;"><span th:text="#{learnMoreAbout}">En savoir plus sur</span>
<a   th:href="@{#{productOfficialWebsite}}"  target="_blank"style="text-decoration:none; color:#a9a9a9;"><strong th:text="#{productName}">LinShare</strong>™</a>
                                  </p>
                                </td>
                                <td style="border-collapse:collapse; padding:  6px 17px 4px 0;"  valign="top" width="60">
                                  <img alt="libre-and-free" height="9"
                                       src="cid:logo.libre.and.free@linshare.org"
                                       style="line-height:100%;width:60px;height:9px;padding:0" width="60" />
 </td>
   </div>
 </body>
 </html>' WHERE id=1;

UPDATE domain_abstract SET mailconfig_id = 1;
