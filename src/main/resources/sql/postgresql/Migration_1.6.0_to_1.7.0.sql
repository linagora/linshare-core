-- Postgresql migration script : 1.6.0 to 1.7.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


DROP INDEX mailing_list_index;

DROP INDEX mailing_list_contact_index;

DROP TABLE technical_account_permission_account;

DROP TABLE IF EXISTS functionality_boolean;
CREATE TABLE functionality_boolean (
	functionality_id int8 NOT NULL, 
	boolean_value    bool NOT NULL, 
	PRIMARY KEY (functionality_id));
ALTER TABLE functionality_boolean DROP CONSTRAINT IF EXISTS FKfunctional171577;
ALTER TABLE functionality_boolean ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);

CREATE TABLE account_permission (
	id int8 NOT NULL,
	technical_account_permission_id int8 NOT NULL,
	permission varchar(255) NOT NULL
);

CREATE TABLE upload_proposition (
	id int8 NOT NULL,
	uuid varchar(255) NOT NULL,
	domain_abstract_id int8 NOT NULL,
	status varchar(255) NOT NULL,
	subject varchar(255) NOT NULL,
	body text,
	mail varchar(255) NOT NULL,
	first_name varchar(255) NOT NULL,
	last_name varchar(255) NOT NULL,
	domain_source varchar(255),
	recipient_mail varchar(255) NOT NULL,
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL
);

CREATE TABLE upload_proposition_action (
	id int8 NOT NULL,
	uuid varchar(255) NOT NULL,
	upload_proposition_filter_id int8 NOT NULL,
	action_type varchar(255) NOT NULL,
	"data" text,
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL
);

CREATE TABLE upload_proposition_filter (
	id int8 NOT NULL,
	domain_abstract_id int8 NOT NULL,
	uuid varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	"match" varchar(255) NOT NULL,
	enable bool NOT NULL,
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL,
	sort_order int4 NOT NULL
);

CREATE TABLE upload_proposition_rule (
	id int8 NOT NULL,
	uuid varchar(255) NOT NULL,
	upload_proposition_filter_id int8 NOT NULL,
	operator varchar(255) NOT NULL,
	field varchar(255) NOT NULL,
	"value" varchar(255),
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL
);

CREATE TABLE upload_request (
	id int8 NOT NULL,
	domain_abstract_id int8 NOT NULL,
	account_id int8 NOT NULL,
	upload_request_group_id int8 NOT NULL,
	uuid varchar(255) NOT NULL,
	max_file int4,
	max_deposit_size int8,
	max_file_size int8,
	status varchar(255) NOT NULL,
	activation_date timestamp(6) NOT NULL,
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL,
	notification_date timestamp(6) NOT NULL,
	expiry_date timestamp(6) NOT NULL,
	upload_proposition_request_uuid varchar(255),
	can_delete bool NOT NULL,
	can_close bool NOT NULL,
	can_edit_expiry_date bool NOT NULL,
	locale varchar(255),
	secured bool NOT NULL,
	mail_message_id varchar(255)
);

CREATE TABLE upload_request_entry (
	entry_id int8 NOT NULL,
	document_entry_entry_id int8,
	upload_request_id int8 NOT NULL,
	"size" int8 NOT NULL
);

CREATE TABLE upload_request_group (
	id int8 NOT NULL,
	subject text NOT NULL,
	body text NOT NULL,
	uuid varchar(255) NOT NULL,
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL
);

CREATE TABLE upload_request_history (
	id int8 NOT NULL,
	upload_request_id int8 NOT NULL,
	status varchar(255) NOT NULL,
	status_updated bool NOT NULL,
	event_type varchar(255) NOT NULL,
	uuid varchar(255) NOT NULL,
	activation_date timestamp(6) NOT NULL,
	expiry_date timestamp(6) NOT NULL,
	notification_date timestamp(6) NOT NULL,
	max_deposit_size int8,
	max_file_count int4,
	max_file_size int8,
	upload_proposition_request_uuid varchar(255),
	can_delete bool NOT NULL,
	can_close bool NOT NULL,
	can_edit_expiry_date bool NOT NULL,
	locale varchar(255) NOT NULL,
	secured bool NOT NULL,
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL,
	mail_message_id varchar(255)
);

CREATE TABLE upload_request_template (
	id int8 NOT NULL,
	uuid varchar(255) NOT NULL,
	account_id int8 NOT NULL,
	name varchar(255) NOT NULL,
	description varchar(255),
	duration_before_activation int8,
	unit_before_activation int8,
	duration_before_expiry int8,
	unit_before_expiry int8,
	group_mode bool,
	deposit_mode bool,
	max_file int8,
	max_file_size int8,
	max_deposit_size int8,
	locale varchar(255),
	secured bool,
	day_before_notification int8,
	prolongation_mode bool,
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL
);

CREATE TABLE upload_request_url (
	id int8 NOT NULL,
	contact_id int8 NOT NULL,
	upload_request_id int8 NOT NULL,
	uuid varchar(255) NOT NULL,
	"path" varchar(255) NOT NULL,
	password varchar(255),
	creation_date timestamp(6) NOT NULL,
	modification_date timestamp(6) NOT NULL
);

ALTER TABLE users
	ALTER COLUMN restricted DROP NOT NULL;

ALTER TABLE technical_account_permission
	DROP COLUMN "write",
	DROP COLUMN all_permissions,
	ADD COLUMN uuid varchar(255) NOT NULL,
	ADD COLUMN creation_date date NOT NULL,
	ADD COLUMN modification_date date NOT NULL;

ALTER TABLE account_permission
	ADD CONSTRAINT account_permission_pkey PRIMARY KEY (id);

ALTER TABLE upload_proposition
	ADD CONSTRAINT upload_proposition_pkey PRIMARY KEY (id);

ALTER TABLE upload_proposition_action
	ADD CONSTRAINT upload_proposition_action_pkey PRIMARY KEY (id);

ALTER TABLE upload_proposition_filter
	ADD CONSTRAINT upload_proposition_filter_pkey PRIMARY KEY (id);

ALTER TABLE upload_proposition_rule
	ADD CONSTRAINT upload_proposition_rule_pkey PRIMARY KEY (id);

ALTER TABLE upload_request
	ADD CONSTRAINT upload_request_pkey PRIMARY KEY (id);

ALTER TABLE upload_request_entry
	ADD CONSTRAINT upload_request_entry_pkey PRIMARY KEY (entry_id);

ALTER TABLE upload_request_group
	ADD CONSTRAINT upload_request_group_pkey PRIMARY KEY (id);

ALTER TABLE upload_request_history
	ADD CONSTRAINT upload_request_history_pkey PRIMARY KEY (id);

ALTER TABLE upload_request_template
	ADD CONSTRAINT upload_request_template_pkey PRIMARY KEY (id);

ALTER TABLE upload_request_url
	ADD CONSTRAINT upload_request_url_pkey PRIMARY KEY (id);

ALTER TABLE account_permission
	ADD CONSTRAINT fkaccount_pe759382 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission(id);

ALTER TABLE technical_account_permission
	ADD CONSTRAINT technical_account_permission_uuid_key UNIQUE (uuid);

ALTER TABLE upload_proposition
	ADD CONSTRAINT fkupload_pro226633 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract(id);

ALTER TABLE upload_proposition_action
	ADD CONSTRAINT fkupload_pro841666 FOREIGN KEY (upload_proposition_filter_id) REFERENCES upload_proposition_filter(id);

ALTER TABLE upload_proposition_filter
	ADD CONSTRAINT fkupload_pro316142 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract(id);

ALTER TABLE upload_proposition_rule
	ADD CONSTRAINT fkupload_pro672390 FOREIGN KEY (upload_proposition_filter_id) REFERENCES upload_proposition_filter(id);

ALTER TABLE upload_request
	ADD CONSTRAINT upload_request_uuid_key UNIQUE (uuid);

ALTER TABLE upload_request
	ADD CONSTRAINT fkupload_req220337 FOREIGN KEY (account_id) REFERENCES account(id);

ALTER TABLE upload_request
	ADD CONSTRAINT fkupload_req840249 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract(id);

ALTER TABLE upload_request
	ADD CONSTRAINT fkupload_req916400 FOREIGN KEY (upload_request_group_id) REFERENCES upload_request_group(id);

ALTER TABLE upload_request_entry
	ADD CONSTRAINT fkupload_req11781 FOREIGN KEY (document_entry_entry_id) REFERENCES document_entry(entry_id);

ALTER TABLE upload_request_entry
	ADD CONSTRAINT fkupload_req220981 FOREIGN KEY (upload_request_id) REFERENCES upload_request(id);

ALTER TABLE upload_request_entry
	ADD CONSTRAINT fkupload_req254795 FOREIGN KEY (entry_id) REFERENCES entry(id);

ALTER TABLE upload_request_history
	ADD CONSTRAINT upload_request_history_uuid_key UNIQUE (uuid);

ALTER TABLE upload_request_history
	ADD CONSTRAINT fkupload_req678768 FOREIGN KEY (upload_request_id) REFERENCES upload_request(id);

ALTER TABLE upload_request_template
	ADD CONSTRAINT fkupload_req618325 FOREIGN KEY (account_id) REFERENCES account(id);

ALTER TABLE upload_request_url
	ADD CONSTRAINT upload_request_url_uuid_key UNIQUE (uuid);

ALTER TABLE upload_request_url
	ADD CONSTRAINT fkupload_req601912 FOREIGN KEY (contact_id) REFERENCES contact(id);

ALTER TABLE upload_request_url
	ADD CONSTRAINT fkupload_req833645 FOREIGN KEY (upload_request_id) REFERENCES upload_request(id);

CREATE INDEX mailing_list_uuid ON mailing_list USING btree (uuid);

CREATE INDEX mailing_list_contact_uuid ON mailing_list_contact USING btree (uuid);


-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.7.0');


-- Functionality : UPLOAD_REQUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (63, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (64, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(31, false, 'UPLOAD_REQUEST', 63, 64, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (31, 'http://linshare-upload-request.local');



-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (65, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (66, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (67, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(32, false, 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION', 65, 66, 67, 1, 'UPLOAD_REQUEST', true);
INSERT INTO unit(id, unit_type, unit_value) VALUES (7, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (32, 0, 7);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (68, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (69, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (70, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(33, false, 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION', 68, 69, 70, 1, 'UPLOAD_REQUEST', true);
-- time unit : month
 INSERT INTO unit(id, unit_type, unit_value) VALUES (8, 0, 2);
-- month : 1 month
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (33, 1, 8);

-- Functionality : UPLOAD_REQUEST__GROUPED_MODE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (71, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (72, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (73, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(34, false, 'UPLOAD_REQUEST__GROUPED_MODE', 71, 72, 73, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (34, false);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_COUNT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (74, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (75, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (76, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(35, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_COUNT', 74, 75, 76, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (35, 3);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_SIZE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (77, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (78, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (79, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(36, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE', 77, 78, 79, 1, 'UPLOAD_REQUEST', true);
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value) VALUES (9, 1, 1);
-- size : 10 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (36, 10, 9);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (80, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (81, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (82, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(37, false, 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE', 80, 81, 82, 1, 'UPLOAD_REQUEST', true);
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value) VALUES (10, 1, 1);
-- size : 30 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (37, 30, 10);

-- Functionality : UPLOAD_REQUEST__NOTIFICATION_LANGUAGE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (83, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (84, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (85, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(38, false, 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE', 83, 84, 85, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (38, 'en');

-- Functionality : UPLOAD_REQUEST__SECURED_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (86, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (87, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (88, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(39, false, 'UPLOAD_REQUEST__SECURED_URL', 86, 87, 88, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (39, false);

-- Functionality : UPLOAD_REQUEST__PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (89, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (90, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (91, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(40, false, 'UPLOAD_REQUEST__PROLONGATION', 89, 90, 91, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (40, false);

-- Functionality : UPLOAD_REQUEST__CAN_DELETE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (92, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (93, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (94, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(41, false, 'UPLOAD_REQUEST__CAN_DELETE', 92, 93, 94, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (41, true);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (95, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (96, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (97, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(42, false, 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION', 95, 96, 97, 1, 'UPLOAD_REQUEST', true);
-- time unit : day
INSERT INTO unit(id, unit_type, unit_value) VALUES (11, 0, 0);
-- time : 7 days
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (42, 7, 11);

-- Functionality : UPLOAD_REQUEST__CAN_CLOSE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (98, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (99, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (100, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(43, false, 'UPLOAD_REQUEST__CAN_CLOSE', 98, 99, 100, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (43, true);

 -- Functionality : UPLOAD_PROPOSITION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (101, false, false, 2, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (102, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id)
 VALUES(44, false, 'UPLOAD_PROPOSITION', 101, 102, 1);


TRUNCATE mail_content_lang;

UPDATE mail_content SET id = id + 38 WHERE id >= 13;

-- UPLOAD_PROPOSITION_CREATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (13, 'dd7d6a36-03b6-48e8-bfb5-3c2d8dc227fd', 1, 0, 12, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'New upload proposition', E'A user ${actorRepresentation} has send to you an upload proposition: ${subject}', E'<strong>${firstName} ${lastName}</strong> has just send to you an upload request: ${subject}<br/>${body}<br/>You need to activate or reject this request <br/><br/>');
-- UPLOAD_PROPOSITION_REJECTED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (14, '62af93dd-0b19-4376-bc76-08b7a97fc0f2', 1, 0, 13, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload proposition rejected', E'A user ${actorRepresentation} has rejected your upload proposition: ${subject}', E'<strong>${firstName} ${lastName}</strong> has just rejected your upload proposition: ${subject}<br/>${body}<br/><br/>');
-- UPLOAD_REQUEST_UPDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (15, '40f36a3b-39ea-4723-a292-9c86e2ee8f94', 1, 0, 14, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request updated', E'A user ${actorRepresentation} has updated upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has just updated the upload request: ${subject}<br/>${body}<br/>New settings can be found here: <a href="${url}">${url}</a><br/><br/>');
-- UPLOAD_REQUEST_ACTIVATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (16, '817ae032-9022-4c22-97a3-cfb5ce50817c', 1, 0, 15, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request activated', E'A user ${actorRepresentation} has activated upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has just activate the upload request: ${subject}<br/>${body}<br/>To upload files, simply click on the following link or copy/paste it into your favorite browser: <a href="${url}">${url}</a><br/><p>Upload request may be <b>encrypted</b>, use <em>password</em>: <code>${password}</code><br/><br/>');
-- UPLOAD_PROPOSITION_AUTO_FILTER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (17, 'd692674c-e797-49f1-a415-1df7ea5c8fee', 1, 0, 16, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload proposition filtered', E'An upload proposition has been filtered: ${subject}', E'A new upload proposition has been filtered.<br/>Subject: ${subject}<br/>${body}<br/><br/>');
-- UPLOAD_REQUEST_CREATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (18, '40a74e4e-a663-4ad2-98ef-1e5d70d3536c', 1, 0, 17, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request created', E'A user ${actorRepresentation} has created upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has just made you an upload request: ${subject}.<br/>${body}<br/>It will be activated ${activationDate}<br/><br/>');
-- UPLOAD_REQUEST_ACKNOWLEDGMENT
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (19, '5ea27e5b-9260-4ce1-b1bd-27372c5b653d', 1, 0, 18, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request acknowledgment', E'A user ${actorRepresentation} has upload a file for upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has upload a file.<br/>File name: ${fileName}<br/>Deposit date: ${depositDate}<br/>File size: ${fileSize}<br/><br/>');
-- UPLOAD_REQUEST_REMINDER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (20, '0d87e08d-d102-42b9-8ced-4d49c21ce126', 1, 0, 19, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request reminder', E'A user ${actorRepresentation} reminds you have an upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> reminds you have got an upload request : ${subject}.<br/>${body}<br/>To upload files, simply click on the following link or copy/paste it into your favorite browser: <a href="${url}">${url}</a><br/><p>Upload request may be <b>encrypted</b>, use <em>password</em>: <code>${password}</code><br/><br/><br/><br/>');
-- UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (21, 'd43b22d6-d915-41cc-99e4-9c9db66c5aac', 1, 0, 20, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request will be expired', E'The upload request: ${subject}, will expire', E'Expiry date approaching for upload request: ${subject}<br/>${body}<br/>Be sure that the request is complete<br/>Files already uploaded: ${files}<br/><br/>');
-- UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (22, '0bea7e7c-e2e9-44ff-bbb3-7e28967a4d67', 1, 0, 21, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request will be expired', E'The upload request: ${subject}, will expire', E'Expiry date approaching for upload request: ${subject}<br/>${body}<br/>Files already uploaded: ${files}<br/>To upload files, simply click on the following link or copy/paste it into your favorite browser: <a href="${url}">${url}</a><br/><br/>');
-- UPLOAD_REQUEST_WARN_OWNER_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (23, '0cd705f3-f1f5-450d-bfcd-f2f5a60c57f8', 1, 0, 22, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request is expired', E'The upload request: ${subject}, is expired', E'Expiration of the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/><br/>');
-- UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (24, '7412940b-870b-4f58-877c-9955a423a5f3', 1, 0, 23, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request is expired', E'The upload request: ${subject}, is expired', E'Expiration of the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/>You will not be able to upload file anymore<br/><br/>');
-- UPLOAD_REQUEST_CLOSED_BY_RECIPIENT
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (25, '6c0c1214-0a77-46d0-92c5-c41d225bf9aa', 1, 0, 24, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request closed', E'A user ${actorRepresentation} has just closed upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has just closed the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/><br/>');
-- UPLOAD_REQUEST_CLOSED_BY_OWNER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (26, '1956ca27-5127-4f42-a41d-81a72a325aae', 1, 0, 25, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request closed', E'A user ${actorRepresentation} has just closed upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has just closed the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/>You will not be able to upload file anymore<br/><br/>');
-- UPLOAD_REQUEST_DELETED_BY_OWNER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (27, '690f1bbc-4f99-4e70-a6cd-44388e3e2c86', 1, 0, 26, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request deleted', E'A user ${actorRepresentation} has just deleted an upload request', E'<strong>${firstName} ${lastName}</strong> has just deleted the upload request: ${subject}<br/>${body}<br/>You will not be able to upload file anymore<br/><br/>');
-- UPLOAD_REQUEST_NO_SPACE_LEFT
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (28, '48fee30b-b2d3-4f85-b9ee-22044f9dbb4d', 1, 0, 27, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request error: no space left', E'A user ${actorRepresentation} has just tried to upload a file but server had no space left', E'<strong>${firstName} ${lastName}</strong> has just tried to upload in the upload request: ${subject}<br/>${body}<br>Please free space and notify the recipient to retry is upload<br/><br/>');

-- UPLOAD_PROPOSITION_CREATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (63, '3cbc9145-4fc9-43bc-9417-a157bdda2575', 1, 1, 12, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouvelle demande d’invitation de partage', E'${actorRepresentation} vous a envoyé une demande invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> vous a envoyé une demande d’invitation de dépôt.<br/>Vous devez activer ou rejeter cette demande.<br/><br/>');
-- UPLOAD_PROPOSITION_REJECTED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (64, 'c8ba5fd5-b3b1-463f-b24a-ef113e7df294', 1, 1, 13, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Demande d’invitation de partage rejetée', E'${actorRepresentation} a rejeté votre invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a rejeté votre invitation de dépôt: ${subject}<br/>${body}<br/><br/>');
-- UPLOAD_REQUEST_UPDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (65, 'c8ba5fd5-b3b1-463f-b24a-ef113e7df294', 1, 1, 14, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de partage modifiée', E'${actorRepresentation} a mis à jour l’invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a mis à jour l’invitation de dépôt: ${subject}<br/>${body}<br/>La nouvelle configuration est disponible ici: <a href="${url}">${url}</a><br/><br/>');
-- UPLOAD_REQUEST_ACTIVATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (66, '24c92194-4291-4deb-9fb7-2c6b6fb40e18', 1, 1, 15, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de partage activée', E'${actorRepresentation} a activé l’invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a activé l’invitation de dépôt: ${subject}<br/>${body}<br/>Pour déposer des fichiers, cliquer sur le lien suivant ou copier/coller ce dernier dans votre navigateur favori: <a href="${url}">${url}</a><br/>L’invitation de dépôt peut être <b>protégée</b>, utiliser le <em>mot de passe</em> suivant: <code>${password}</code><br/><br/>');
-- UPLOAD_REQUEST_AUTO_FILTER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (67, 'aac3fd67-043c-46b2-9fe6-7aa89d12c099', 1, 1, 16, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt filtrée', E'Une invitation de dépôt a été filtrée: ${subject}', E'Une nouvelle demande d’invitation de dépôt à été filtrée.<br/>Subject: ${subject}<br/>${body}<br/><br/>');
-- UPLOAD_REQUEST_CREATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (68, '6d821746-e481-4eb1-84f8-0d64a0b8f526', 1, 1, 17, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouvelle invitation de dépôt', E'${actorRepresentation} a créé une invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a créé l’invitation de dépôt: ${subject}<br/>${body}<br/>Elle sera active le ${activationDate}<br/><br/>');
-- UPLOAD_REQUEST_ACKNOWLEDGMENT
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (69, '879ea2d3-68e4-465b-b6ce-4ee58998e441', 1, 1, 18, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Accusé de reception d’invitation de dépôt', E'${actorRepresentation} a déposé un fichier pour l’invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a déposé un fichier.<br/>Nom du fichier: ${fileName}<br/>Date de dépôt: ${depositDate}<br/>Taille du fichier: ${fileSize}<br/><br/>');
-- UPLOAD_REQUEST_REMINDER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (70, '7a1baafb-1db3-4e9b-b39f-2f770d9e848b', 1, 1, 19, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Rappel d’invitation de dépôt', E'${actorRepresentation} vous rappelle l’invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> vous rappelle l’invitation de dépôt: ${subject}<br/>${body}<br/>Pour déposer des fichiers, cliquer sur le lien suivant ou copier/coller ce dernier dans votre navigateur favoris: <a href="${url}">${url}</a><br/>L’invitation de dépôt peu être <b>protégée</b>, utiliser le <em>nouveau mot de passe</em> suivant: <code>${password}</code><br/><br/>');
-- UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (71, '259b20a2-48a9-4282-bac9-07b6673062c4', 1, 1, 20, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt bientôt expirée', E'L’invitation de dépôt: ${subject}, va expirée', E'La date d’expiraton approche pour l’invitation de dépôt: ${subject}<br/>${body}<br/>Vérifier que l’invitation est complêtée<br/>Fichiers déjà déposés: ${files}<br/><br/>');
-- UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (72, '44c650a5-084d-4821-9e87-d0c54ec4db77', 1, 1, 21, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt bientôt expirée', E'L’invitation de dépôt: ${subject}, va expirée', E'La date d’expiration approche pour l’invitation de dépôt: ${subject}<br/>${body}<br/>Fichiers déjà déposés: ${files}<br/>Pour déposer des fichiers, cliquer sur le lien suivant ou copier/coller ce dernier dans votre navigateur favoris: <a href="${url}">${url}</a><br/><br/>');
-- UPLOAD_REQUEST_WARN_OWNER_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (73, '5ae69e7f-cbf7-4958-a069-6e74135810d4', 1, 1, 22, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt expirée', E'L’invitation de dépôt: ${subject}, est expirée', E'Expiration de l’invitation de dépôt: ${subject}<br/>${body}<br/>Fichiers déposés: ${files}<br/><br/>');
-- UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (74, '52d97982-ea1e-43b6-8012-39ba1578f0be', 1, 1, 23, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt expirée', E'L’invitation de dépôt: ${subject}, est expirée', E'Expiration de l’invitation de dépôt: ${subject}<br/>${body}</br>Fichiers déposés: ${files}<br/>Vous ne serez plus en mesure d’y déposer des fichiers<br/><br/>');
-- UPLOAD_REQUEST_CLOSED_BY_RECIPIENT
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (75, '7f0fb9f5-6215-4e1f-946f-d7532e390684', 1, 1, 24, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt clôturée', E'${actorRepresentation} a clôturé l’invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a clôturé l’invitation de dépôt: ${subject}<br/>${body}<br/>Fichiers déposés: ${files}<br/><br/>');
-- UPLOAD_REQUEST_CLOSED_BY_OWNER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (76, 'ce8256c9-bdf5-45fa-ad1d-51d2b546273e', 1, 1, 25, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt clôturée', E'${actorRepresentation} a clôturé l’invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a clôturé l’invitation de dépôt: ${subject}<br/>${body}<br/>Fichiers déposés: ${files}<br/>Vous ne serez plus en mesure d’y déposer des fichiers<br/><br/>');
-- UPLOAD_REQUEST_DELETED_BY_OWNER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (77, '1ebf231f-ab5e-469a-9487-c460db735e96', 1, 1, 26, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Invitation de dépôt supprimée', E'${actorRepresentation} a supprimé l’invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a supprimé une invitation de dépôt: ${subject}<br/>${body}<br/>Vous ne serez plus en mesure d’y déposer des fichiers<br/><br/>');
-- UPLOAD_REQUEST_NO_SPACE_LEFT
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (78, 'a89afcb4-2bef-431c-9967-e2cf4de38933', 1, 1, 27, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Erreur sur une d’invitation de dépôt: espace disque insuffisant', E'${actorRepresentation} a essayé de déposer un fichier mais le serveur n’a pas suffisamment d’espace', E'<strong>${firstName} ${lastName}</strong> a tenté de déposer un fichier dans l’invitation de dépôt: ${subject}<br/>${body}<br/>Veuiller libérer de l’espace puis notifier le destinataire d’exécuter son dépôt à nouveau<br/><br/>');


INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (1, 1, 0, 1, 0, 'd6868568-f5bd-4677-b4e2-9d6924a58871');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (2, 1, 0, 2, 1, '4f3c4723-531e-449b-a1ae-d304fd3d2387');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (3, 1, 0, 3, 2, '81041673-c699-4849-8be4-58eea4507305');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (4, 1, 0, 4, 3, '85538234-1fc1-47a2-850d-7f7b59f1640e');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (5, 1, 0, 5, 4, '796a98eb-0b97-4756-b23e-74b5a939c2e3');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (6, 1, 0, 6, 5, 'ed70cc00-099e-4c44-8937-e8f51835000b');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (7, 1, 0, 7, 6, 'f355793b-17d4-499c-bb2b-e3264bc13dbd');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (8, 1, 0, 8, 7, '5a6764fc-350c-4f10-bdb0-e95ca7607607');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (9, 1, 0, 9, 8, 'befd8182-88a6-4c72-8bae-5fcb7a79b8e7');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (10, 1, 0, 10, 9, 'fa59abad-490b-4cd5-9a31-3c3302fc4a18');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (11, 1, 0, 11, 10, '5bd828fa-d25e-47fa-9c0d-1bb84304e692');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (12, 1, 0, 12, 11, 'a9096a7e-949c-4fae-aedf-2347c40cd999');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (13, 1, 0, 13, 12, '1216ca54-f510-426c-a12b-8158efa21619');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (14, 1, 0, 14, 13, '9f87c53d-80e5-4e10-b571-d0c9f9c35017');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (15, 1, 0, 15, 14, '454e3e88-7129-4e98-a79a-e119cb94bd07');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (16, 1, 0, 16, 15, '0a8251dd-9514-4b7b-bf47-c398c00ba21b');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (17, 1, 0, 17, 16, 'e3b99efb-875c-4c63-bd5c-8f121d75876b');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (18, 1, 0, 18, 17, 'e37cbade-db93-487d-96ee-dc491ce63035');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (19, 1, 0, 19, 18, '8d707581-3920-4d82-a8ba-f7984afc54ca');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (20, 1, 0, 20, 19, '64b5df7b-b197-49a7-b0af-aaac2c2f8d79');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (21, 1, 0, 21, 20, 'fd6011cf-e4cf-478d-835b-75b25e024b81');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (22, 1, 0, 22, 21, 'e4439f5b-380b-4a78-86a7-764f15ff599d');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (23, 1, 0, 23, 22, '7a560359-fa35-4ffd-ac1d-1d9ceef1b1e0');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (24, 1, 0, 24, 23, '2b038721-fe6e-4406-b5de-c4c84a964df8');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (25, 1, 0, 25, 24, '822b3ede-daea-4b60-a8a2-2216c7d36fea');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (26, 1, 0, 26, 25, 'd8316b6b-f6c8-408b-ac7d-1ebea767912e');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (27, 1, 0, 27, 26, '7642b888-3bd8-4f8c-b65c-81b61e512137');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (28, 1, 0, 28, 27, '9bf9d474-fd10-48da-843c-dfadebd2b455');

INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (51, 1, 1, 51, 0, 'd0af96a7-6a9c-4c3f-8b8c-7c8e2d0449e1');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (52, 1, 1, 52, 1, '28e5855a-c0e7-40fc-8401-9cf25eb53f03');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (53, 1, 1, 53, 2, '41d0f03d-57dd-420e-84b0-7908179c8329');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (54, 1, 1, 54, 3, '72c0fff4-4638-4e98-8223-df27f8f8ea8b');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (55, 1, 1, 55, 4, '8b7f57c1-b4a1-4896-8e19-d3ebf3af4831');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (56, 1, 1, 56, 5, '6fbabf1a-58c0-49b9-859e-d24b0af38c87');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (57, 1, 1, 57, 6, 'b85fc62f-d9eb-454b-9289-fec5eab51a76');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (58, 1, 1, 58, 7, '25540d2d-b3b8-46a9-811b-0549ad300fe0');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (59, 1, 1, 59, 8, '72ae03e7-5865-433c-a2be-a95c655a8e17');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (60, 1, 1, 60, 9, 'e2af2ff6-585b-4cdc-a887-1755e42fcde6');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (61, 1, 1, 61, 10, '1ee1c8bc-75e9-4fbe-a34b-893a86704ec9');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (62, 1, 1, 62, 11, '12242aa8-b75e-404d-85df-68e7bb8c04af');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (63, 1, 1, 63, 12, '4f2ad41c-3969-461d-a6dc-8f692a1738e9');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (64, 1, 1, 64, 13, '362cf576-30ab-41a5-85d0-3d9175935b14');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (65, 1, 1, 65, 14, '35b81d85-0ee7-44f9-b478-20c8429c2b6d');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (66, 1, 1, 66, 15, '92e0a55e-e4e8-43c9-94f0-0d4e74d5748f');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (67, 1, 1, 67, 16, 'eb8a1b1e-758d-4261-8616-8ead644f70b0');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (68, 1, 1, 68, 17, '50ae2621-556c-446d-a399-55ed799022c3');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (69, 1, 1, 69, 18, '6580009b-36fd-472d-9937-41d0097ead91');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (70, 1, 1, 70, 19, 'ed471d9b-6f64-4d36-97cb-654b73579fe9');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (71, 1, 1, 71, 20, '86fdc43c-5fd7-4aba-b01a-90fccbfb5489');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (72, 1, 1, 72, 21, 'ea3f9814-6da9-49bf-94e5-7ff2c789e07b');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (73, 1, 1, 73, 22, 'f9455b1d-3582-4998-8675-bc0a8137fc73');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (74, 1, 1, 74, 23, '8f91e46b-1cee-45bc-8712-23ea0298db87');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (75, 1, 1, 75, 24, 'e5a9f689-c005-47c2-958f-b68071b1bf6f');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (76, 1, 1, 76, 25, 'a7994bd1-bd67-4cc6-93f3-be935c1cdb67');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (77, 1, 1, 77, 26, '5e1fb460-1efc-497c-96d8-6adf162cbc4e');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (78, 1, 1, 78, 27, '2daaea2a-1b13-48b4-89a6-032f7e034a2d');

-- system account for upload-request:
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (3, 7, 'system-account-uploadrequest', now(),now(), 3, 'en', 'en', true, false, 1);

-- system account for upload-proposition
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, password, destroyed, domain_id)
	VALUES (4, 4, '89877610-574a-4e79-aeef-5606b96bde35', now(),now(), 5, 'en', 'en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', false, 1);
INSERT INTO users(account_id, first_name, last_name, mail, can_upload, comment, restricted, can_create_guest)
	VALUES (4, null, 'Technical Account for upload proposition', 'linshare-noreply@linagora.com', false, '', false, false);

COMMIT;
