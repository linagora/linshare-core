# Script to create update and insert 
import os
from json import load
from json import loads
from database import Database
from table import Table
from time import time
import requests
import shutil
import re
from asyncore import read

with open('config.default.json') as f:
		config_default = load(f)
try:
	with open('config.json') as f:
		config = {**config_default, **load(f)}
		print("Loading file config.json... OK")
except FileNotFoundError as err:
	config = config_default
	print("Loading file config.default.json... OK")

separator = "/"

#Sql common destination folder
common_path = "../../src/main/resources/sql/common"

path_folder = common_path + separator
update_file_location = path_folder + "mail_updates"
update_file_folder = update_file_location + separator

structure_file_name = path_folder + "import_mail_structure.sql"
#import_mails_file_name = path_folder + "import-mail.sql"
update_file_name = path_folder + "import_mail_update.sql"
columns_to_update = ["subject", "body", "messages_french", "messages_english", "layout", "footer"]
tables_to_export = ['mail_layout', 'mail_config', 'mail_content', 'mail_content_lang', 'mail_footer', 'mail_footer_lang']
tables_to_extract_content = ['mail_layout', 'mail_content', 'mail_footer']


#Files to copy in common
#files_to_copy = [import_mails_file_name, structure_file_name, update_file_name]

#Folders to copy in common
folders_to_copy = [update_file_location]

#Print the full update file
mode_print_full_update_file=False


if not os.path.exists(update_file_location):
	os.makedirs(update_file_location)


# Get the mail content types from LinShare
def get_content_types(config):
	content_types ={}
	with open(config.get('path_mail_content_types'), 'r') as f:
		read_data = f.read()
		read_data = re.split('public enum MailContentType {', read_data)
		read_data = re.split(';', read_data[1])
		read_data = re.split(',', read_data[0])
		read_data = [enum.strip() for enum in read_data]
		for enum in read_data:
			index = int(re.search(r'\d+', enum).group())
			content_types[index] = re.search(r'\w+', enum).group()
	return content_types


def check_config(config):
	print('{:-^30}'.format('Check configuration'))
	error_fields = [field for field in config if not config[field] ]
	empty_fields = False
	error_database = False
	error_linshare_app = False
	if error_fields:
		raise ValueError('ERROR --> These fields are empty : {}'.format(' | '.join(error_fields)))
		empty_fields = True
	try:
		content_types = get_content_types(config)
		if content_types == {}:
			raise ValueError('Les mail content types sont invalides  : {}'.format(content_types))
	except Exception as err:
		print("ERROR --> Bad MailContentTypes : {}".format(content_types))
		print(err)
		error_linshare_app = True
	error_database = not Database.check_connection(config)
	print('\n')
	print('{:^20}'.format('Name') + '{:^15}'.format('State'))
	print('{:-^30}'.format(''))
	print("{left:25}{right:}".format(left='Config Fields', right='ERROR' if empty_fields else 'OK'))
	print("{left:25}{right:}".format(left='Get Mail Content Types', right='ERROR' if error_linshare_app else 'OK'))
	print("{left:25}{right:}".format(left='Linshare Database', right='ERROR' if error_database else 'OK'))
	print('{:-^30}'.format(''))
	print('\n')
	return not(empty_fields or error_database or error_linshare_app)


def create_table(database, table_name):
	column_names = database.get_table_columns(table_name)
	return Table(table_name, column_names, [dict(zip(column_names, list(row))) for row in database.select_table_content(table_name)])


def create_tables(database, *args):
	imported_database = {}
	print('Extracting Tables')
	for table_name in args:
		imported_database[table_name] = create_table(database, table_name)
		print("{left:25}----> {right}".format(left=table_name, right='DONE'))
	return imported_database


def write_insert_scripts(structure_file_name, tables_to_export, database_to_export, column_names):
	structure_file = open(structure_file_name, 'w')
	for table_name in tables_to_export:
		table_content = database_to_export[table_name]
		content_to_write = table_content.export_to_sql(tables_to_extract_content, column_names)
		structure_file.write(content_to_write)
		structure_file.write("\n\n")
	structure_file.write("UPDATE domain_abstract SET mailconfig_id = 1;")
	structure_file.write("\n")
	structure_file.close()
	print("File " + structure_file_name + " successfully generated")


def write_update_scripts(update_file_name, full_imported_database, tables_content, column_names, updates_location, mail_types, print_full_update_file=mode_print_full_update_file):
	
	content_to_write = ""
	for table_to_update_content in tables_content:
		#Create the folder where the update scripts will be generated
		tables_update_folder = updates_location + table_to_update_content
		if not os.path.exists(tables_update_folder):
			os.makedirs(tables_update_folder)
		table_content = full_imported_database[table_to_update_content]
		content_table = table_content.export_to_update_sql(column_names, tables_update_folder + separator, mail_types)
		content_to_write = content_to_write + content_table + "\n\n" 
	if print_full_update_file:
		update_file = open(update_file_name, 'w')
		update_file.write(content_to_write)
		update_file.write("\n")
		update_file.close()
		print("File " + update_file_name + " successfully generated")
	
if __name__ == '__main__':
	start_time = time()
	if check_config(config):
		database = Database(config)
		content_types = get_content_types(config)
		print("Connection established")
		full_imported_database = create_tables(database, *tables_to_export)
		database.get_connection().close()
		print("Connection closed\n")
		# # Write the mail with the general structure
		write_insert_scripts(structure_file_name, tables_to_export, full_imported_database, columns_to_update)
		# # Write the mail with the update_scripts
		write_update_scripts(update_file_name, full_imported_database, tables_to_extract_content, columns_to_update, update_file_folder, content_types)
		
	print("--- %s ms ---" % ((time() - start_time) * 1000))
	
