import pypika
from pypika import functions as fn
from datetime import datetime
import os

class Table:

	def __init__(self, name="", columns=[], data=[]):
		self.name = name
		self.columns = sorted(columns)
		self.data = data

	def format_exit_string(input):
		return input.replace("\\'", "\'").replace("''", "\'\'")

	def get_sql(query, char_escapes=None):
		return query.get_sql(char_escapes) + ';'

	def export_to_sql(self, tables_name_to_not_insert, columns_to_not_insert=[]):
		return "\n\n".join([self.create_insert_query(row, columns_to_not_insert, tables_name_to_not_insert) for row in self.data])

	def export_to_update_sql(self, columns, updates_location, mail_types):
		return "\n\n".join([self.create_update_query(columns, row, updates_location, mail_types) for row in self.data])
						
	def create_insert_query(self, row, columns_to_not_insert=[], tables_name_to_not_insert=["mail_content"]):
		table = pypika.Table(self.name)
		arg_list = tuple(sorted(row.keys()))
		insert_query = pypika.Query() \
								.into(table) \
								.columns(*arg_list) \
								.insert( \
									tuple( \
										[ \
											fn.Now() if isinstance(row[key], datetime)
											else "" if (key in columns_to_not_insert and self.name in tables_name_to_not_insert)
											else Table.format_exit_string(row[key]) if isinstance(row[key], str)
											else row[key] for key in sorted(row.keys()) \
										] \
									) \
								)
		# WORKAROUND: H2 database does not support quoting for table names or column names.
		# Table.get_sql do not let us disabling quoting, we must use query.get_sql to do it (for now).
		return insert_query.get_sql(quote_char="") + ";"

	def create_update_query(self, columns, row, updates_location, mail_types):
		table_to_update = pypika.Table(self.name)
		update_query = pypika.Query() \
						.update(table_to_update) \
						.where(table_to_update.id == row["id"])
		for key in columns:
			if key in row:
				update_query = update_query.set(key, Table.format_exit_string(row[key]))
		# WORKAROUND: H2 database does not support quoting for table names or column names.
		# Table.get_sql do not let us disabling quoting, we must use query.get_sql to do it (for now).
		result_query = update_query.get_sql(quote_char="") + ";"
		file_name = mail_types[row["id"]] if self.name=="mail_content" else self.name + '_' + str(row["id"])
		update_file = open(updates_location + file_name + ".sql", 'w')
		update_file.write(result_query)
		update_file.close()
		print("File " + file_name + ".sql" + " successfully generated here " + updates_location)
		return result_query



