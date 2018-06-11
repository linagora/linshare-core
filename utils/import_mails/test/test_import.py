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
