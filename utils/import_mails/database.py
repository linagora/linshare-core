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

# Script to access PostgreSQL linShare database with Pyscopg

import psycopg2

class Database:

	def __init__(self, config):
		try:
			self.connection = psycopg2.connect("dbname='{dbname}' user='{user}' host='{host}' port={port} password='{password}'".format(dbname=config.get('dbname'), user=config.get('user'), host=config.get('host'), port=config.get('port'), password=config.get('password')))
		except:
			print("No established connection to linShare database")

	def check_connection(config):
		database_config = "dbname='{dbname}' user='{user}' host='{host}' port={port} password='{password}'".format(dbname=config.get('dbname'), user=config.get('user'), host=config.get('host'), port=config.get('port'), password=config.get('password'))
		try:
			psycopg2.connect(database_config)
		except Exception as err:
			print("ERROR --> No Connection established to linshare database with this config : {} \nCheck that your linshare database is running".format(database_config))
			print(err)
			return False
		return True

	def get_connection(self):
		return self.connection

	def select_table_content(self, table_name):
		cursor = self.connection.cursor()
		try:
			cursor.execute("""SELECT * from %s ORDER BY id""" % table_name)
			return cursor.fetchall()
		except:
			print("Cannot select from %s" % table_name)

	def get_table_columns(self, table_name):
		cursor = self.connection.cursor()
		try:
			cursor.execute("""SELECT * from %s LIMIT 0""" % table_name)
			return [desc[0] for desc in cursor.description]
		except:
			print("Cannot select from %s" % table_name)
	
