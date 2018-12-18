--Functionality : FILESIZE_MAX
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (1, true, true, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (2, true, true, 1, false);
-- if a functionality is system, you will not be able see/modify its parameters
-- INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (1, false, 'FILESIZE_MAX', 1, 2, 1);
-- INSERT INTO unit(id, unit_type, unit_value) VALUES (1, 1, 1);
-- INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (1, 10, 1);


-- Functionality : QUOTA_GLOBAL
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (3, false, false, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (4, true, true, 1, false);
-- INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (2, false, 'QUOTA_GLOBAL', 3, 4, 1);
-- INSERT INTO unit(id, unit_type, unit_value) VALUES (2, 1, 1);
-- INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (2, 1, 2);


-- Functionality : QUOTA_USER
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (5, true, true, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (6, true, true, 1, false);
-- INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (3, false, 'QUOTA_USER', 5, 6, 1);
-- INSERT INTO unit(id, unit_type, unit_value) VALUES (3, 1, 1);
-- INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (3, 100, 3);


-- Functionality : MIME_TYPE
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (7, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (8, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date) 
	VALUES (4, true, 'MIME_TYPE', 7, 8, 1, now(), now());


--This functionality is not yet available in LinShare 2.0.0
---- Functionality : SIGNATURE
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (9, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (10, false, false, 2, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (5, true, 'SIGNATURE', 9, 10, 1);

--This functionality is not yet available in LinShare 2.0.0
---- Functionality : ENCIPHERMENT
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (11, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (12, false, false, 2, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (6, true, 'ENCIPHERMENT', 11, 12, 1);

-- Functionality : TIME_STAMPING
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (13, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (14, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date) 
	VALUES (7, false, 'TIME_STAMPING', 13, 14, 1, now(), now());
INSERT INTO functionality_string(functionality_id, string_value) 
	VALUES (7, 'http://localhost:8080/signserver/tsa?signerId=1');


-- Functionality : ANTIVIRUS
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (15, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (16, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date) 
	VALUES (8, true, 'ANTIVIRUS', 15, 16, 1, now(), now());

--useless - deleted
---- Functionality : CUSTOM_LOGO
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (17, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (18, true, true, 1, false);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (9, false, 'CUSTOM_LOGO', 17, 18, 1);
--INSERT INTO functionality_string(functionality_id, string_value) VALUES (9, 'http://linshare-ui-user.local/custom/images/logo.png');

--useless - deleted
---- Functionality : CUSTOM_LOGO__LINK
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (59, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (60, false, false, 1, false);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (29, false, 'CUSTOM_LOGO__LINK', 59, 60, 1, 'CUSTOM_LOGO', true);
--INSERT INTO functionality_string(functionality_id, string_value) VALUES (29, 'http://linshare-ui-user.local');

-- Functionality : GUESTS
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (27, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (28, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date) 
	VALUES (14, true, 'GUESTS', 27, 28, 1, now(), now());

-- Functionality : GUESTS__EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (19, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (20, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (111, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date) 
	VALUES (10, false, 'GUESTS__EXPIRATION', 19, 20, 111, 1, 'GUESTS', true, now(), now());
INSERT INTO unit(id, unit_type, unit_value) 
	VALUES (4, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) 
	VALUES (10, 3, 4);

-- Functionality : GUESTS__RESTRICTED
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (47, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (48, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (112, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date) 
	VALUES (24, false, 'GUESTS__RESTRICTED', 47, 48, 112, 1, 'GUESTS', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value) 
	VALUES (24, true);

-- Functionality : GUESTS__CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (113, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (114, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (115, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date) 
	VALUES (48, false, 'GUESTS__CAN_UPLOAD', 113, 114, 115, 1, 'GUESTS', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (48, true);

-- Functionality : DOCUMENT_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (21, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (22, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date) 
	VALUES (11, false, 'DOCUMENT_EXPIRATION', 21, 22, 1, now(), now());
INSERT INTO unit(id, unit_type, unit_value) 
	VALUES (5, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) 
	VALUES (11, 3, 5);


-- Functionality : SHARE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (23, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (24, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (122, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, creation_date, modification_date) 
	VALUES (12, false, 'SHARE_EXPIRATION', 23, 24, 122, 1, now(), now());
INSERT INTO unit(id, unit_type, unit_value) 
	VALUES (6, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) 
	VALUES (12, 3, 6);

-- Functionality : SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (120, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (121, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date) 
	VALUES (50, false, 'SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION', 120, 121, 1, 'SHARE_EXPIRATION', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value) 
	VALUES (50, false);

-- Functionality : ANONYMOUS_URL
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (25, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (26, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (116, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, creation_date, modification_date) 
	VALUES (13, false, 'ANONYMOUS_URL', 25, 26, 116, 1, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (13, true);


-- Functionality : INTERNAL_CAN_UPLOAD formerly known as USER_CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (29, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (30, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (15, true, 'INTERNAL_CAN_UPLOAD', 29, 30, 1, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)	
VALUES (15, true);

-- Functionality : COMPLETION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (31, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (32, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (16, false, 'COMPLETION', 31, 32, 1, now(), now());
INSERT INTO functionality_integer(functionality_id, integer_value)
	VALUES (16, 3);

--useless - deleted
---- Functionality : TAB_HELP
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (33, true, true, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (34, false, false, 1, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (17, true, 'TAB_HELP', 33, 34, 1);

--useless - deleted
---- Functionality : TAB_AUDIT
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (35, true, true, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (36, false, false, 1, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (18, true, 'TAB_AUDIT', 35, 36, 1);

--useless - deleted
---- Functionality : TAB_USER
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (37, true, true, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (38, false, false, 1, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (19, true, 'TAB_USER', 37, 38, 1);

-- Functionality : SHARE_NOTIFICATION_BEFORE_EXPIRATION
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (43, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (44, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (22, false, 'SHARE_NOTIFICATION_BEFORE_EXPIRATION', 43, 44, 1, now(), now());
INSERT INTO functionality_string(functionality_id, string_value)
	VALUES (22, '2,7');

-- Functionality : WORK_GROUP
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (45, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (46, false, false, 1, true);
-- if a functionality is system, you will not be able see/modify its parameters
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (23, true, 'WORK_GROUP', 45, 46, 1, now(), now());

-- Functionality : WORK_GROUP__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (57, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (58, false, false, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (117, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (28, false, 'WORK_GROUP__CREATION_RIGHT', 57, 58, 1, 'WORK_GROUP', true, now(), now());
-- INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (28, true);

	-- Functionality : WORK_GROUP__FILE_VERSIONING
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (297, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (298, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (299, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (63, false, 'WORK_GROUP__FILE_VERSIONING', 297, 298, 299, 1, 'WORK_GROUP', true, now(), now());

	-- Functionality : WORK_GROUP__FILE_VERSIONING_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (300, false, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (301, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (302, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (64, false, 'WORK_GROUP__FILE_VERSIONING_EXPIRATION', 300, 301, 302, 1, 'WORK_GROUP', true, now(), now());
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (12, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) 
	VALUES (64, 3, 12);

	-- Functionality : WORKGROUP__FILE_EDITION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (303, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (304, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (305, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (65, false, 'WORK_GROUP__FILE_EDITION', 303, 304, 305, 1, 'WORK_GROUP', true, now(), now());

-- Functionality : CONTACTS_LIST
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (53, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (54, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (26, true, 'CONTACTS_LIST', 53, 54, 1, now(), now());

--Functionality : CONTACTS_LIST__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (55, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (56, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(27, false, 'CONTACTS_LIST__CREATION_RIGHT', 55, 56, null, 1, 'CONTACTS_LIST', true, now(), now());


-- Functionality : DOMAIN
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (118, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (119, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES(49, false, 'DOMAIN', 118, 119, 1, now(), now());

-- Functionality : DOMAIN__NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (61, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (62, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(30, false, 'DOMAIN__NOTIFICATION_URL', 61, 62, 1, 'DOMAIN', true, now(), now());
INSERT INTO functionality_string(functionality_id, string_value)
	VALUES (30, 'http://linshare-ui-user.local/');

-- Functionality : DOMAIN__MAIL
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (49, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)	
	VALUES (50, false, false, 2, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (25, false, 'DOMAIN__MAIL', 49, 50, 1, 'DOMAIN', true,now(), now());
INSERT INTO functionality_string(functionality_id, string_value)
	VALUES (25, 'linshare-noreply@linagora.com');


-- Functionality : UPLOAD_REQUEST
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (63, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (64, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES(31, false, 'UPLOAD_REQUEST', 63, 64, 1, now(), now());
INSERT INTO functionality_string(functionality_id, string_value)
	VALUES (31, 'http://linshare-upload-request.local');

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (65, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (66, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (67, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(32, false, 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION', 65, 66, 67, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (7, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id)
	VALUES (32, 0, 7);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (68, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (69, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (70, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(33, false, 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION', 68, 69, 70, 1, 'UPLOAD_REQUEST', true, now(), now());
-- time unit : month
 INSERT INTO unit(id, unit_type, unit_value)
 	VALUES (8, 0, 2);
-- month : 1 month
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id)
	VALUES (33, 1, 8);

-- Functionality : UPLOAD_REQUEST__GROUPED_MODE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (71, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (72, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (73, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(34, false, 'UPLOAD_REQUEST__GROUPED_MODE', 71, 72, 73, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (34, false);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_COUNT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (74, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (75, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (76, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(35, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_COUNT', 74, 75, 76, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_integer(functionality_id, integer_value)
	VALUES (35, 3);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_SIZE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (77, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (78, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (79, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(36, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE', 77, 78, 79, 1, 'UPLOAD_REQUEST', true, now(), now());
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (9, 1, 1);
-- size : 10 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id)
	VALUES (36, 10, 9);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (80, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (81, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (82, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(37, false, 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE', 80, 81, 82, 1, 'UPLOAD_REQUEST', true, now(), now());
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (10, 1, 1);
-- size : 30 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id)
	VALUES (37, 30, 10);

-- Functionality : UPLOAD_REQUEST__NOTIFICATION_LANGUAGE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (83, true, true, 1, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (84, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (85, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(38, false, 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE', 83, 84, 85, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_enum_lang(functionality_id, lang_value)
	VALUES (38, 'en');

-- Functionality : UPLOAD_REQUEST__SECURED_URL
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (86, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (87, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (88, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(39, false, 'UPLOAD_REQUEST__SECURED_URL', 86, 87, 88, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (39, false);

-- Functionality : UPLOAD_REQUEST__PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (89, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (90, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (91, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(40, false, 'UPLOAD_REQUEST__PROLONGATION', 89, 90, 91, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (40, false);

-- Functionality : UPLOAD_REQUEST__CAN_DELETE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (92, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (93, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (94, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(41, false, 'UPLOAD_REQUEST__CAN_DELETE', 92, 93, 94, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (41, true);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (95, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (96, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (97, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(42, false, 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION', 95, 96, 97, 1, 'UPLOAD_REQUEST', true, now(), now());
-- time unit : day
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (11, 0, 0);
-- time : 7 days
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id)
	VALUES (42, 7, 11);

-- Functionality : UPLOAD_REQUEST__CAN_CLOSE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (98, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (99, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (100, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(43, false, 'UPLOAD_REQUEST__CAN_CLOSE', 98, 99, 100, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (43, true);

 -- Functionality : UPLOAD_PROPOSITION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (101, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (102, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES(44, false, 'UPLOAD_PROPOSITION', 101, 102, 1, now(), now());

-- Functionality : GUEST__EXPIRATION_ALLOW_PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (123, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (124, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(51, false, 'GUESTS__EXPIRATION_ALLOW_PROLONGATION', 123, 124, null, 1, 'GUESTS', true, now(), now());

-- Functionality : UPLOAD_REQUEST_ENABLE_TEMPLATE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (129, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (130, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param, creation_date, modification_date)
	VALUES(53, false, 'UPLOAD_REQUEST_ENABLE_TEMPLATE', 129, 130, 1, false, now(), now());

-- Functionality : SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (126, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (127, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (128, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, creation_date, modification_date)
	VALUES(52, false, 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER', 126, 127, 128, 1, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (52, true);

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (131, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (132, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (133, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, creation_date, modification_date)
 	VALUES(54, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', 131, 132, 133, 1, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (54, true);

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (134, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (135, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (136, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	 VALUES(55, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION', 134, 135, 136, 1, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', true, now(), now());
INSERT INTO functionality_integer(functionality_id, integer_value)
	VALUES (55, 3);

-- Functionality : ANONYMOUS_URL__NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (224, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (225, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (226, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(56, false, 'ANONYMOUS_URL__NOTIFICATION', 224, 225, 226, 1, 'ANONYMOUS_URL', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (56, true);

-- Functionality : ANONYMOUS_URL__NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (228, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (229, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (230, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
 	VALUES(57, false, 'ANONYMOUS_URL__NOTIFICATION_URL', 228, 229, 230, 1, 'ANONYMOUS_URL', true, now(), now());
INSERT INTO functionality_string(functionality_id, string_value)
	VALUES (57, 'http://linshare-ui-user.local/');

-- Functionality : ANONYMOUS_URL__FORCE_ANONYMOUS_SHARING
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (279, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (280, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (281, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(58, false, 'ANONYMOUS_URL__FORCE_ANONYMOUS_SHARING', 279, 280, 281, 1, 'ANONYMOUS_URL', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (58, false);

-- Functionality : ANONYMOUS_URL__HIDE_RECEIVED_SHARE_MENU
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (282, false, false, 2, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (283, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES(59, false, 'ANONYMOUS_URL__HIDE_RECEIVED_SHARE_MENU', 282, 283, 1, 'ANONYMOUS_URL', true, now(), now());

-- Functionality : JWT_PERMANENT_TOKEN
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (290, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (291, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (60, true, 'JWT_PERMANENT_TOKEN', 290, 291, 1, now(), now());

-- Functionality : JWT_PERMANENT_TOKEN__USER_MANAGEMENT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (292, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (293, false, false, 1, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (294, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (61, false, 'JWT_PERMANENT_TOKEN__USER_MANAGEMENT', 292, 293, 294, 1, 'JWT_PERMANENT_TOKEN', true, now(), now());
