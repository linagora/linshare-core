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

import sys, os
app_path = '..'
sys.path.append(os.path.abspath(os.path.join(app_path)))

import os
from json import load
from table import Table
import datetime
import unittest
import app

mail_layout_row = {'id': 1, 'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 475858), 'uuid': '15044750-89d1-11e3-8d50-5404a683a462', 'visible': True, 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 475858), 'domain_abstract_id': 1, 'description': 'Default HTML layout', 'messages_french': 'date.format=d MMMM, yyyy\nproductCompagny=Linagora\nproductName=LinShare\nworkGroupRightAdminTitle = Administrateur\nworkGroupRightWirteTitle = Écriture\nworkGroupRightReadTitle = Lecture\nwelcomeMessage = Bonjour {0},', 'layout': '<!DOCTYPE html>\n<html xmlns:th="http://www.thymeleaf.org">\n<body>\n<!--/* Beginning of common base layout template*/-->\n<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">\n  <div style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:\'Open Sans\'', 'messages_english': 'common.availableUntil = workGroupRightAdminTitle = Administrator\nworkGroupRightWirteTitle = Write\nworkGroupRightReadTitle = Read\nwelcomeMessage = Hello {0},', 'readonly': True}

mail_config_row = {'id': 1, 'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 480481), 'mail_layout_id': 1, 'visible': True, 'readonly': True, 'domain_abstract_id': 1, 'name': 'Default mail config', 'uuid': '946b190d-4c95-485f-bfe6-d288a2de1edd', 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 480481)}

mail_content_row = {'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 521149), 'description': None, 'domain_abstract_id': 1, 'messages_english': 'beginningMainMsgInt = Your file\nendingMainMsgInt = will automatically be deleted in <b> {0} days</b> from your Personal Space.\nsubject = The file {0} is about to be deleted\nuploadedThe = Upload date', 'subject': '[( #{subject(${document.name})})]', 'mail_content_type': 1, 'id': 1, 'uuid': '1507e9c0-c1e1-4e0f-9efb-506f63cbba97', 'visible': True, 'readonly': True, 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 521149), 'body': '<!DOCTYPE html>\n<html xmlns:th="http://www.thymeleaf.org">\n  <head  data-th-replace="layout :: header"></head>\n  <body>\n    <div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">\n    <section id="main-content">\n      <div th:replace="layout :: contentUpperSection( ~{::#section-content})">\n        <div id="section-content">\n          <!--/* Greetings */-->\n            <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>\n          <!--/* End of Greetings */-->\n          <!--/* Main email  message content*/-->\n          <p>\n     <span  data-th-utext="#{beginningMainMsgInt}"></span>\n            <span>\n             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >\n                  filename.ext\n              </a>\n          </span>\n  <span  data-th-utext="#{endingMainMsgInt(${daysLeft})}">  </span>\n           <!--/* Single download link for external recipient */-->\n            <th:block   data-th-replace="layout :: actionButtonLink(#{common.download},${document.href})"/>\n          </p> <!--/* End of Main email  message content*/-->\n        </div><!--/* End of section-content*/-->\n      </div><!--/* End of main-content container*/-->\n    </section> <!--/* End of main-content*/-->\n    <!--/* Secondary content for  bottom email section */-->\n    <section id="secondary-content">\n      <th:block data-th-replace="layout :: infoDateArea(#{uploadedThe},${document.creationDate})"/>\n      <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${document.expirationDate})"/>\n    </section>  <!--/* End of Secondary content for bottom email section */-->\n  </body>\n</html>', 'messages_french': 'beginningMainMsgInt =  Votre fichier\nendingMainMsgInt = sera automatiquement supprimé dans <b> {0} jours</b> de votre Espace Personnel.\nsubject = Le fichier {0} va bientôt être supprimé\nuploadedThe = Déposé le'}

mail_content_lang_row = {'id': 1, 'uuid': '4f3c4723-531e-449b-a1ae-d304fd3d2387', 'mail_content_id': 1, 'readonly': True, 'language': 0, 'mail_content_type': 1, 'mail_config_id': 1}

mail_footer_row = {'id': 1, 'footer': '<!DOCTYPE html>\n<html xmlns:th="http://www.thymeleaf.org">\n  <body>\n    <div data-th-fragment="email_footer">\n                                <td style="border-collapse:collapse;padding: 6px 0 4px 17px;" valign="top">\n                                  <p style="margin: 0; font-size: 10px;"><span th:text="#{learnMoreAbout}">En savoir plus sur</span>\n<a   th:href="@{#{productOfficialWebsite}}"  target="_blank"style="text-decoration:none; color:#a9a9a9;"><strong th:text="#{productName}">LinShare</strong>™</a>\n                                  </p>\n                                </td>\n                                <td style="border-collapse:collapse; padding:  6px 17px 4px 0;"  valign="top" width="60">\n                                  <img alt="libre-and-free" height="9"\n                                       src="cid:logo.libre.and.free@linshare.org"\n                                       style="line-height:100%;width:60px;height:9px;padding:0" width="60" />\n </td>\n   </div>\n </body>\n </html>', 'uuid': 'e85f4a22-8cf2-11e3-8a7a-5404a683a462', 'visible': True, 'modification_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 595546), 'domain_abstract_id': 1, 'description': 'footer html', 'messages_french': 'learnMoreAbout=En savoir plus sur\nproductOfficialWebsite=http://www.linshare.org/', 'creation_date': datetime.datetime(2018, 5, 24, 9, 59, 45, 595546), 'messages_english': 'learnMoreAbout=Learn more about\nproductOfficialWebsite=http://www.linshare.org/', 'readonly': True}

mail_footer_lang_row = {'id': 1, 'uuid': 'bf87e580-fb25-49bb-8d63-579a31a8f81e', 'readonly': True, 'language': 0, 'mail_footer_id': 1, 'mail_config_id': 1}

mail_activation_row = {'id': 1, 'policy_configuration_id': 138, 'policy_delegation_id': 139, 'domain_id': 1, 'system': False, 'enable': True, 'identifier': 'FILE_WARN_OWNER_BEFORE_FILE_EXPIRY', 'policy_activation_id': 137}


class TestFormat(unittest.TestCase):

    def setUp(self):
        self.imported_database = {}
        self.imported_database["mail_layout"] = Table("mail_layout", mail_layout_row.keys(), [mail_layout_row])
        self.imported_database["mail_content"] = Table("mail_content", mail_content_row.keys(), [mail_content_row])
        self.imported_database["mail_content_lang"] = Table("mail_content_lang", mail_content_lang_row.keys(), [mail_content_lang_row])

    def test_pypika_format(self):
        mail_layout_table = self.imported_database["mail_layout"]
        expected_layout = """INSERT INTO "mail_layout" ("creation_date","description","domain_abstract_id","id","layout","messages_english","messages_french","modification_date","readonly","uuid","visible") VALUES (NOW(),'Default HTML layout',1,1,'<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/* Beginning of common base layout template*/-->
<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">
  <div style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:''Open Sans''','common.availableUntil = workGroupRightAdminTitle = Administrator
workGroupRightWirteTitle = Write
workGroupRightReadTitle = Read
welcomeMessage = Hello {0},','date.format=d MMMM, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administrateur
workGroupRightWirteTitle = Écriture
workGroupRightReadTitle = Lecture
welcomeMessage = Bonjour {0},',NOW(),true,'15044750-89d1-11e3-8d50-5404a683a462',true);"""
        print(mail_layout_table.export_to_sql([]))
        print("\n")
        print("\n")
        print(expected_layout)
        self.assertEquals(expected_layout, mail_layout_table.export_to_sql([]), "Not the expected result for mail_layout")


if __name__ == '__main__':
    unittest.main()
