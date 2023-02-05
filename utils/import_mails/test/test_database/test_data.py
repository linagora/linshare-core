#
# Copyright (C) 2007-2023 - LINAGORA
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#


mail_layout_row = {'id': 1, 'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 475858), 'uuid': '15044750-89d1-11e3-8d50-5404a683a462', 'visible': True, 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 475858), 'domain_abstract_id': 1, 'description': 'Default HTML layout', 'messages_french': 'common.availableUntil = Expire le\ncommon.byYou= | Par vous\ncommon.download= Télécharger\ncommon.filesInShare=Fichiers joints\ncommon.recipients = Destinataires\ncommon.titleSharedThe= Partagé le\ndate.format=d MMMM, yyyy\nproductCompagny=Linagora\nproductName=LinShare\nworkGroupRightAdminTitle = Administrateur\nworkGroupRightWirteTitle = Écriture\nworkGroupRightReadTitle = Lecture\nwelcomeMessage = Bonjour {0},', 'layout': '<!DOCTYPE html>\n<html xmlns:th="http://www.thymeleaf.org">\n<body>\n<!--/* Beginning of common base layout template*/-->\n<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">\n  <div style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:\'Open Sans\',arial,Helvetica,sans-serif;">\n    <center>\n      <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"\n             style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px"\n             width="90%">\n        <tbody>\n        <tr>\n          <td align="center" style="border-collapse:collapse" valign="top">\n            <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px" width="90%">\n              <tbody>\n              <tr>\n                <td align="center" style="border-collapse:collapse" valign="top">\n                  <table bgcolor="transparent" border="0" cellpadding="0" cellspacing="0"\n                         style="background-color:transparent;border-bottom:0;padding:0px">\n                    <tbody>\n                    <tr>\n                      <td align="center" bgcolor="#ffffff"\n                          style="border-collapse:collapse;color:#202020;background-color:#ffffff;font-size:34px;font-weight:bold;line-height:100%;padding:0;text-align:center;vertical-align:middle">\n                        <div align="center" style="text-align:center">\n                          <a target="_blank"\n                             style="border:0;line-height:100%;outline:none;text-decoration:none;width:233px;height:57px;padding:20px 0 20px 0"\n                             data-th-href="@{${linshareURL}}">\n                            <img src="cid:logo.linshare@linshare.org"\n                              style="display:inline-block;margin-bottom:20px;margin-top:20px" width="233" alt="Logo"\n                              height="57"/>\n                          </a>\n                        </div>\n                      </td>\n                    </tr>\n                    </tbody>\n                  </table>\n                </td>\n              </tr>\n              <tr>\n                <td align="center" style="border-collapse:collapse" valign="top">\n                  <table border="0" cellpadding="0" cellspacing="0" style="width:95%;max-width:500px" width="95%">\n                    <tbody>\n                    <tr>\n                      <td\n                        style="border-collapse:collapse;border-radius:3px;font-weight:300;border:1px solid #e1e1e1;background:white;border-top:none;"\n                        valign="top">\n                        <table border="0" cellpadding="20" cellspacing="0" width="100%">\n                          <tbody>\n                          <tr>\n                            <td style="border-collapse:collapse;padding:0px" valign="top">\n                              <div align="left"\n                                   style="color:#505050;font-size:14px;line-height:150%;text-align:left">\n                                <th:block data-th-replace="${upperMainContentArea}"/>\n                              </div>\n                              <table border="0" cellspacing="0" cellpadding="0" width="100%"\n                                     style="background-color: #f8f8f8;">\n                                <tbody>\n                                <tr>\n                                  <td width="15" style="border-top:1px solid #c9cacc;">\n                                  </td>\n                                  <td width="20"><img src="cid:logo.arrow@linshare.org"\n                                    width="20" height="9" border="0" style="display:block;" alt="down arrow"/></td>\n                                  <td style="border-top:1px solid #c9cacc;"></td>\n                                </tr>\n                                </tbody>\n                              </table>\n                              <table border="0" cellspacing="0" cellpadding="0" width="100%">\n                                <tbody>\n                                <tr>\n                                  <td>\n                                    <div align="left"\n                                         style="font-size:14px;padding: 0px 17px;background: #f8f8f8;text-align:left;color:#7f7f7f;line-height:20px;">\n                                      <div align="left"\n                                           style="font-size:13px;line-height:20px;margin:0;padding: 15px 0 20px;">\n                                        <th:block data-th-replace="${bottomSecondaryContentArea}"/>\n                                      </div>\n                                    </div>\n                                  </td>\n                                </tr>\n                                </tbody>\n                              </table>\n                              <table width="100%"\n                                     style="background:#f0f0f0;text-align:left;color:#a9a9a9;line-height:20px;border-top:1px solid #e1e1e1">\n                                <tbody>\n                                <tr data-th-insert="footer :: email_footer">\n                                </tr>\n                                </tbody>\n                              </table>\n                            </td>\n                          </tr>\n                          </tbody>\n                        </table>\n                      </td>\n                    </tr>\n                    </tbody>\n                  </table>\n                </td>\n              </tr>\n              <tr>\n                <td align="center" style="border-collapse:collapse" valign="top">\n                  <table bgcolor="white" border="0" cellpadding="10" cellspacing="0"\n                         style="background-color:white;border-top:0" width="400">\n                    <tbody>\n                    <tr>\n                      <td style="border-collapse:collapse" valign="top">\n                        <table border="0" cellpadding="10" cellspacing="0" width="100%">\n                          <tbody>\n                          <tr>\n                            <td bgcolor="#ffffff" colspan="2"\n                                style="border-collapse:collapse;background-color:#ffffff;border:0;padding: 0 8px;"\n                                valign="middle">\n                              <div align="center"\n                                   style="color:#707070;font-size:12px;line-height:125%;text-align:center">\n                                <!--/* Do not remove the copyright  ! */-->\n                                <div data-th-insert="copyright :: copyright">\n                                  <p\n                                    style="line-height:15px;font-weight:300;margin-bottom:0;color:#b2b2b2;font-size:10px;margin-top:0">\n                                    You are using the Open Source and free version of\n                                    <a href="http://www.linshare.org/"\n                                       style="text-decoration:none;color:#b2b2b2;"><strong>LinShare</strong>™</a>,\n                                    powered by <a href="http://www.linshare.org/"\n                                                  style="text-decoration:none;color:#b2b2b2;"><strong>Linagora</strong></a>\n                                    ©&nbsp;2009–2018. Contribute to\n                                    Linshare R&amp;D by subscribing to an Enterprise offer.\n                                  </p>\n                                </div>\n                              </div>\n                            </td>\n                          </tr>\n                          </tbody>\n                        </table>\n                      </td>\n                    </tr>\n                    </tbody>\n                  </table>\n                </td>\n              </tr>\n              </tbody>\n            </table>\n          </td>\n        </tr>\n        </tbody>\n      </table>\n    </center>\n  </div>\n</div>\n<!--/* End of common base layout template*/-->\n </body>\n </html>\n<!--/* Common lower info title style */-->\n<div style="margin-bottom:17px;" data-th-fragment="infoEditedItem(titleInfo,oldValue,newValue)">\n     <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>\n    <br/>\n      <th:block th:replace="${oldValue}" />  -> <th:block th:replace="${newValue}" />\n</div>\n<!--/* Edited  date  display settings  style */-->\n<div style="margin-bottom:17px;" data-th-fragment="infoEditedDateArea(titleInfo,oldValue,newValue)">\n     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>\n    <br/>\n <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue,df)}"/> -> \n <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(newValue,df)}"/>\n</div>\n<!--/* Common header template */-->\n<head  data-th-fragment="header">\n  <title data-th-text="${mailSubject}">Mail subject</title>\n        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />\n</head>\n<!--/* Common greeting  template */-->\n<div data-th-fragment="greetings(currentFirstName)">\n  <p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px"\n data-th-text="#{welcomeMessage(${currentFirstName})}">\nHello Amy,</p>\n</div>\n<!--/* Common upper email section  template */-->\n<div data-th-fragment="contentUpperSection(sectionContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;">\n   <div align="left" style="padding:24px 17px 5px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;\nborder-top: 1px solid #e1e1e1;">\n      <th:block th:replace="${sectionContent}" />\n       </div>\n</div>\n<!--/* Common message section template */-->\n<div data-th-fragment="contentMessageSection(messageTitle,messageContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;" >\n          <div align="left" style="padding:24px 17px 15px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;">\n<p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px">\n<th:block th:replace="${messageTitle}" />\n</p>\n<p style="margin:0;color: #88a3b1;">\n<th:block th:replace="${messageContent}" />\n</p>\n</div>\n</div>\n<!--/* Common link style */-->\n<div data-th-fragment="infoActionLink(titleInfo,urlLink)"  style="margin-bottom:17px;" >\n<span style="font-weight:bold;" data-th-text="${titleInfo}" >Download link title  </span>\n  <br/>\n<a target="_blank" style="color:#1294dc;text-decoration:none;"\n                          data-th-text="${urlLink}"  th:href="@{${urlLink}}"   >Link </a>\n</div>\n<!--/* Common date display  style */-->\n<div style="margin-bottom:17px;" data-th-fragment="infoDateArea(titleInfo,contentInfo)">\n     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>\n    <br/>\n <span  th:with="df=#{date.format}" data-th-text="${#dates.format(contentInfo,df)}">7th of November, 2018</span>\n</div>\n<!--/* Common lower info title style */-->\n<div style="margin-bottom:17px;" data-th-fragment="infoStandardArea(titleInfo,contentInfo)">\n     <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>\n    <br/>\n      <th:block th:replace="${contentInfo}" />\n</div>\n<!--/* Common button action style */-->\n<span   data-th-fragment="actionButtonLink(labelBtn,urlLink)">\n<a\nstyle="border-radius:3px;font-size:15px;color:white;text-decoration:none;padding: 10px 7px;width:auto;max-width:50%;display:block;background-color: #42abe0;text-align: center;margin-top: 17px;"  target="_blank"\ndata-th-text="${labelBtn}"  th:href="@{${urlLink}}">Button label</a>\n</span>\n<!--/* Common recipient listing for external and internal users */-->\n<div  style="margin-bottom:17px;" data-th-fragment="infoRecipientListingArea(titleInfo,arrayRecipients)">\n     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Recipients</span>\n   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">\n      <li style="color:#787878;font-size:10px" th:each="recipientData: ${arrayRecipients}">\n<div data-th-if="(${#strings.isEmpty(recipientData.lastName)})">\n         <span style="color:#787878;font-size:13px"  data-th-utext="${recipientData.mail}">\n        my-file-name.pdf\n         </span>\n</div>\n<div data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">\n         <span  style="color:#787878;font-size:13px">\n          <th:block  data-th-utext="${recipientData.firstName}"/>\n          <th:block data-th-utext="${recipientData.lastName}"/>\n       </span>\n</div>\n      </li>\n   </ul>\n</div>\n<div data-th-if="(${!isAnonymous})">\n         <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}"  data-th-utext="${shareLink.name}">\n        my-file-name.pdf\n         </a>\n</div>\n      </li>\n   </ul>\n</div>\n<!--/* Lists all file links in a share   */-->\n<div   style="margin-bottom:17px;" data-th-fragment="infoFileLinksListingArea(titleInfo,arrayFileLinks,isAnonymous)">\n     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>\n   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">\n      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">\n<div data-th-if="(${!isAnonymous})">\n         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}" th:href="@{${shareLink.href}}">\n        my-file-name.pdf\n         </a>\n</div>\n<div data-th-if="(${isAnonymous})">\n         <span style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">\n        my-file-name.pdf\n         </a>\n</div>\n</div>\n<!--/* Lists all file links in a share  and checks witch one are the recpient\\s */-->\n<div   style="margin-bottom:17px;" data-th-fragment="infoFileListWithMyUploadRefs(titleInfo,arrayFileLinks)">\n     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>\n   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">\n      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">\n         <a style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">\n        my-file-name.pdf\n         </a>\n<th:block  data-th-if="(${shareLink.mine})"> <span  data-th-text="#{common.byYou}">|  By You</span></th:block >\n      </li>\n   </ul>\n</div>\n<!--/* Lists all file links in a share along with their download status   */-->\n<div  data-th-fragment="infoFileListUploadState(titleInfo,arrayFileLinks)">\n     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>\n   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">\n<li style="color:#00b800;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${shareLink.downloaded})">\n <th:block data-th-if="(${shareLink.isDownloading})">\n         <a style="color:#1294dc;text-decoration:none;font-size:13px ;font-weight:bold"  data-th-utext="${shareLink.name}">\n        my-file-name.pdf\n         </a>\n  </th:block>\n <th:block data-th-if="(${!shareLink.isDownloading})">\n         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">\n        my-file-name.pdf\n         </a>\n  </th:block>\n      </li>\n<li style="color:#787878;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${!shareLink.downloaded})">\n         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">\n        my-file-name.pdf\n         </a>\n      </li>\n   </ul>\n</div>\n<!--/* Lists all recpients download states per file   */-->\n<div   style="margin-bottom:17px;"  data-th-fragment="infoFileListRecipientUpload(titleInfo,arrayFileLinks)">\n     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>\n   <ul style="padding: 5px 0px; margin: 0;list-style-type:none;">\n<li style="color:#787878;font-size:10px;margin-top:10px;"  th:each="shareLink : ${arrayFileLinks}" >\n    <span style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">\n  <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">\n    <span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">\ntest-file.jpg</span></a>\n    <span data-th-if="(${!shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #787878;"></span>\n    <span data-th-if="(${shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #00b800;"></span>\n    </span>\n    <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;" >\n <th:block  th:each="recipientData: ${shareLink.shares}">\n   <th:block data-th-if="(${!recipientData.downloaded})" >\n      <li style="color:#787878;font-size:15px"  >\n      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >\n        <span style="color:#7f7f7f;font-size:13px;">\n          <th:block  data-th-utext="${recipientData.firstName}"/>\n          <th:block data-th-utext="${recipientData.lastName}"/>\n       </span>\n     </th:block>\n      <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"\n           data-th-if="(${#strings.isEmpty(recipientData.lastName)})">able.cornell@linshare.com </span>\n      </li>\n   </th:block>\n<th:block data-th-if="(${recipientData.downloaded})">\n   <li style="color:#00b800;font-size:15px" >\n      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >\n        <span  style="color:#7f7f7f;font-size:13px;">\n          <th:block  data-th-utext="${recipientData.firstName}"/>\n          <th:block data-th-utext="${recipientData.lastName}"/>\n       </span>\n     </th:block>\n<th:block  data-th-if="(${#strings.isEmpty(recipientData.lastName)})">\n  <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"> able.cornell@linshare.com </span>\n  </th:block>\n      </li>\n   </th:block>\n</th:block>\n    </ul>\n</li>\n   </ul>\n</div>', 'messages_english': 'common.availableUntil = Expiry date\ncommon.byYou= | By you\ncommon.download= Download\ncommon.filesInShare = Attached files\ncommon.recipients = Recipients\ncommon.titleSharedThe= Creation date\ndate.format= MMMM d, yyyy\nproductCompagny=Linagora\nproductName=LinShare\nworkGroupRightAdminTitle = Administrator\nworkGroupRightWirteTitle = Write\nworkGroupRightReadTitle = Read\nwelcomeMessage = Hello {0},', 'readonly': True}

mail_config_row = {'id': 1, 'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 480481), 'mail_layout_id': 1, 'visible': True, 'readonly': True, 'domain_abstract_id': 1, 'name': 'Default mail config', 'uuid': '946b190d-4c95-485f-bfe6-d288a2de1edd', 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 480481)}

mail_content_row = {'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 521149), 'description': None, 'domain_abstract_id': 1, 'messages_english': 'beginningMainMsgInt = Your file\nendingMainMsgInt = will automatically be deleted in <b> {0} days</b> from your Personal Space.\nsubject = The file {0} is about to be deleted\nuploadedThe = Upload date', 'subject': '[( #{subject(${document.name})})]', 'mail_content_type': 1, 'id': 1, 'uuid': '1507e9c0-c1e1-4e0f-9efb-506f63cbba97', 'visible': True, 'readonly': True, 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 521149), 'body': '<!DOCTYPE html>\n<html xmlns:th="http://www.thymeleaf.org">\n  <head  data-th-replace="layout :: header"></head>\n  <body>\n    <div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">\n    <section id="main-content">\n      <div th:replace="layout :: contentUpperSection( ~{::#section-content})">\n        <div id="section-content">\n          <!--/* Greetings */-->\n            <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>\n          <!--/* End of Greetings */-->\n          <!--/* Main email  message content*/-->\n          <p>\n     <span  data-th-utext="#{beginningMainMsgInt}"></span>\n            <span>\n             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >\n                  filename.ext\n              </a>\n          </span>\n  <span  data-th-utext="#{endingMainMsgInt(${daysLeft})}">  </span>\n           <!--/* Single download link for external recipient */-->\n            <th:block   data-th-replace="layout :: actionButtonLink(#{common.download},${document.href})"/>\n          </p> <!--/* End of Main email  message content*/-->\n        </div><!--/* End of section-content*/-->\n      </div><!--/* End of main-content container*/-->\n    </section> <!--/* End of main-content*/-->\n    <!--/* Secondary content for  bottom email section */-->\n    <section id="secondary-content">\n      <th:block data-th-replace="layout :: infoDateArea(#{uploadedThe},${document.creationDate})"/>\n      <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${document.expirationDate})"/>\n    </section>  <!--/* End of Secondary content for bottom email section */-->\n  </body>\n</html>', 'messages_french': 'beginningMainMsgInt =  Votre fichier\nendingMainMsgInt = sera automatiquement supprimé dans <b> {0} jours</b> de votre Espace Personnel.\nsubject = Le fichier {0} va bientôt être supprimé\nuploadedThe = Déposé le'}

mail_content_lang_row = {'id': 1, 'uuid': '4f3c4723-531e-449b-a1ae-d304fd3d2387', 'mail_content_id': 1, 'readonly': True, 'language': 0, 'mail_content_type': 1, 'mail_config_id': 1}

mail_footer_row = {'id': 1, 'footer': '<!DOCTYPE html>\n<html xmlns:th="http://www.thymeleaf.org">\n  <body>\n    <div data-th-fragment="email_footer">\n                                <td style="border-collapse:collapse;padding: 6px 0 4px 17px;" valign="top">\n                                  <p style="margin: 0; font-size: 10px;"><span th:text="#{learnMoreAbout}">En savoir plus sur</span>\n<a   th:href="@{#{productOfficialWebsite}}"  target="_blank"style="text-decoration:none; color:#a9a9a9;"><strong th:text="#{productName}">LinShare</strong>™</a>\n                                  </p>\n                                </td>\n                                <td style="border-collapse:collapse; padding:  6px 17px 4px 0;"  valign="top" width="60">\n                                  <img alt="libre-and-free" height="9"\n                                       src="cid:logo.libre.and.free@linshare.org"\n                                       style="line-height:100%;width:60px;height:9px;padding:0" width="60" />\n </td>\n   </div>\n </body>\n </html>', 'uuid': 'e85f4a22-8cf2-11e3-8a7a-5404a683a462', 'visible': True, 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 595546), 'domain_abstract_id': 1, 'description': 'footer html', 'messages_french': 'learnMoreAbout=En savoir plus sur\nproductOfficialWebsite=http://www.linshare.org/', 'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 595546), 'messages_english': 'learnMoreAbout=Learn more about\nproductOfficialWebsite=http://www.linshare.org/', 'readonly': True}

mail_footer_lang_row = {'id': 1, 'uuid': 'bf87e580-fb25-49bb-8d63-579a31a8f81e', 'readonly': True, 'language': 0, 'mail_footer_id': 1, 'mail_config_id': 1}

mail_activation_row = {'id': 1, 'policy_configuration_id': 138, 'policy_delegation_id': 139, 'domain_id': 1, 'system': False, 'enable': True, 'identifier': 'FILE_WARN_OWNER_BEFORE_FILE_EXPIRY', 'policy_activation_id': 137}

