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
import unittest
import app

from database import Database

with open("../config.json") as f:
	config = load(f)


class TestCases(unittest.TestCase):

	database = None

	def setUp(self):
		self.database = Database(config)
		print("Connection established")

	def test_import_data(self):
		mail_layout = app.create_table(self.database, 'mail_layout')
		self.assertEquals(['creation_date', 'description', 'domain_abstract_id', 'id', 'layout', 'messages_english', 'messages_french', 'modification_date', 'readonly', 'uuid', 'visible'], mail_layout.columns)
		first_layout_row = mail_layout.data[0]
		mail_content = app.create_table(self.database, 'mail_content')
		print(mail_content.columns)
		self.assertEquals(['body', 'creation_date', 'description', 'domain_abstract_id', 'id', 'mail_content_type', 'messages_english', 'messages_french', 'modification_date', 'readonly', 'subject', 'uuid', 'visible'], mail_content.columns)


if __name__ == '__main__':
    unittest.main()
