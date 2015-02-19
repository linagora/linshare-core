BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


-- Jeu de données de tests

INSERT INTO ldap_connection(id, uuid, label, provider_url, security_auth, security_principal, security_credentials, creation_date, modification_date)
VALUES (1, 'a9b2058f-811f-44b7-8fe5-7a51961eb098', 'linshare-obm', 'ldap://linshare-obm.linagora.dc1:389', 'simple', '', '', now(), now());


-- system domain pattern
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    50,
    'd793ebef-b8bb-4930-bf16-27add911ec13',
    'USER_LDAP_PATTERN',
    'linshare-obm',
    'This is pattern the default pattern for the ldap obm structure.',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    false,
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
    );

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (51, 'user_mail', 'mail', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (52, 'user_firstname', 'givenName', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (53, 'user_lastname', 'sn', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (54, 'user_uid', 'uid', false, true, true, 50, false);



INSERT INTO user_provider(id, uuid, provider_type, base_dn, creation_date, modification_date, ldap_connection_id, ldap_pattern_id)
VALUES (1, '93fd0e8b-fa4c-495d-978f-132e157c2292', 'LDAP_PROVIDER', 'ou=users,dc=int1.linshare.dev,dc=local', now(), now(), 1, 50);


-- Top domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 1, 1, 2);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 0, 1, 1, 2, 1 , 3);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 2, 1, 4);

UPDATE domain_abstract SET mailconfig_id = 1;
UPDATE domain_abstract SET mime_policy_id=1;


-- Technical account with Delegation Role for delegation API.
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, password, destroyed, domain_id)
	VALUES (5, 4, '59236bed-7e4d-4f98-99d9-5c1dd6b503d4', now(),now(), 4, 'en', 'en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', false, 1);
INSERT INTO users(account_id, first_name, last_name, mail, can_upload, comment, restricted, can_create_guest)
	VALUES (5, null, 'Delegation Account', 'delegation@int1.linshare.dev', false, '', false, false);

-- Technical account permission for delegation account.
INSERT INTO technical_account_permission (id, uuid, creation_date, modification_date) VALUES (40, 'fbba4e41-ca60-4f09-8d59-fbfe052acb82', now(), now());

UPDATE account SET technical_account_permission_id = 40 where id = 5;
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'GUESTS_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'GUESTS_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'GUESTS_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'GUESTS_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'GUESTS_DELETE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'DOCUMENT_ENTRIES_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'DOCUMENT_ENTRIES_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'DOCUMENT_ENTRIES_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'DOCUMENT_ENTRIES_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'DOCUMENT_ENTRIES_DELETE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'DOCUMENT_ENTRIES_DOWNLOAD');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'DOCUMENT_ENTRIES_DOWNLOAD_THUMBNAIL');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'SHARE_ENTRIES_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'SHARE_ENTRIES_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'SHARE_ENTRIES_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'SHARE_ENTRIES_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'SHARE_ENTRIES_DELETE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'SHARE_ENTRIES_DOWNLOAD');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'SHARE_ENTRIES_DOWNLOAD_THUMBNAIL');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'ANONYMOUS_SHARE_ENTRIES_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'ANONYMOUS_SHARE_ENTRIES_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'ANONYMOUS_SHARE_ENTRIES_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'ANONYMOUS_SHARE_ENTRIES_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'ANONYMOUS_SHARE_ENTRIES_DELETE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'ANONYMOUS_SHARE_ENTRIES_DOWNLOAD');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'ANONYMOUS_SHARE_ENTRIES_DOWNLOAD_THUMBNAIL');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREADS_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREADS_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREADS_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREADS_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREADS_DELETE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_MEMBERS_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_MEMBERS_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_MEMBERS_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_MEMBERS_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_MEMBERS_DELETE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_ENTRIES_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_ENTRIES_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_ENTRIES_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_ENTRIES_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_ENTRIES_DELETE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_ENTRIES_DOWNLOAD');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'THREAD_ENTRIES_DOWNLOAD_THUMBNAIL');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'LISTS_LIST');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'LISTS_GET');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'LISTS_CREATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'LISTS_UPDATE');
INSERT INTO account_permission (id, technical_account_permission_id, permission) VALUES ((SELECT nextVal('hibernate_sequence')), 40, 'LISTS_DELETE');


-- Users
-- bart simpson
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (50, 2, '9a9ece25-7a0e-4d75-bb55-d4070e25e1e1', current_timestamp(3), current_timestamp(3), 0, 'fr', 'en', true, false, 3);
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (50, 'Bart', 'Simpson', 'bart.simpson@int1.linshare.dev', true, '', false, true);
-- pierre mongin : 15kn60njvhdjh
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id, owner_id , password ) VALUES (53, 3, 'fa2cab19-2cd7-44f5-96f6-418455899d3e', current_timestamp(3), current_timestamp(3), 0, 'fr', 'fr', true, false, 4, 50 , 'OsFTxoUjd62imwHnaV/4zQfrJ5s=');
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, expiration_date) VALUES (53, 'Pierre', 'Mongin', 'pmongin@ratp.fr', true, '', false, false, current_timestamp(3));

-- Upload Requests.
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (1, 'subject of upload request 1', 'body of upload request 1', 'b344b5ca-d9e7-4857-b959-5e86f34a91f7', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (1, 3, 50, 1, '916a6e65-deb8-4120-b2ab-c64bfcbf4e02', 3, 31457280, 10485760, 'STATUS_ENABLED', now(), now(), now(), '2014-08-10 00:00:00', '2014-09-10 00:00:00', null, true, true, true, 'fr', true, null); 

INSERT INTO contact (id, mail) VALUES (1,'ctjhoa@linagora.com');
-- password : 1qm6xtpyu93qp
INSERT INTO upload_request_url (id, contact_id, upload_request_id, uuid, path, password, creation_date, modification_date)
	VALUES (1, 1, 1, '90b8a0f8-af07-4052-8bb8-bc5179f64b72', 'upload_request', 'kITh6Jk+FiuyGQtdtaeFxvYnzug=', now(), now());



-- Thread : projet : RATP
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (51, 5, '9806de10-ed0b-11e1-877a-5404a6202d2c', current_timestamp(3), current_timestamp(3), 0, 'fr', 'fr', true, false, 1);
INSERT INTO thread (account_id, name) VALUES (51, 'RATP');
-- Thread : projet : 3MI
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (52, 5, '34544580-f0ec-11e1-a62a-080027c0eef0', current_timestamp(3), current_timestamp(3), 0, 'fr', 'fr', true, false, 1);
INSERT INTO thread (account_id, name) VALUES (52, 'Ministère de l''intérieur');
-- Thread : projet : Test Thread
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (54, 5, 'c4570914-d004-4506-8abf-04527f342e88', current_timestamp(3), current_timestamp(3), 0, 'en', 'en', true, false, 1);
INSERT INTO thread (account_id, name) VALUES (54, 'Test Thread');


--Thread members
INSERT INTO thread_member (id, thread_id, admin, can_upload, creation_date, modification_date, user_id) VALUES (1, 51, false, false, current_timestamp(3), current_timestamp(3), 50); 
INSERT INTO thread_member (id, thread_id, admin, can_upload, creation_date, modification_date, user_id) VALUES (2, 51, true, true, current_timestamp(3), current_timestamp(3), 53); 

INSERT INTO thread_member (id, thread_id, admin, can_upload, creation_date, modification_date, user_id) VALUES (3, 52, true, true, current_timestamp(3), current_timestamp(3), 50); 

INSERT INTO thread_member (id, thread_id, admin, can_upload, creation_date, modification_date, user_id) VALUES (4, 54, true, true, current_timestamp(3), current_timestamp(3), 50); 
INSERT INTO thread_member (id, thread_id, admin, can_upload, creation_date, modification_date, user_id) VALUES (5, 54, false, true, current_timestamp(3), current_timestamp(3), 53); 



-- -- thread-entry-1-no-dl
-- INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (1, 'a09e6bea-edcb-11e1-86e7-5404a6202d2c', current_timestamp(3), 'image/png', 49105, null, null);
-- INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (1, 51, current_timestamp(3), current_timestamp(3), 'thread-entry-1-no-dl', '', current_timestamp(3), '5a663f86-edcb-11e1-a9fd-5404a6202d2c'); 
-- INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (1, 1, false); 
-- 
-- 
-- -- thread-entry-2-no-dl
-- INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (2, '026e60fa-edcc-11e1-acb9-5404a6202d2c', current_timestamp(3), 'image/png', 49105, null, null);
-- INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (2, 51, current_timestamp(3), current_timestamp(3), 'thread-entry-2-no-dl', '', current_timestamp(3), '187f5ef8-edcc-11e1-8ed2-5404a6202d2c'); 
-- INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (2, 2, false); 
-- 
-- -- thread-entry-3-no-dl
-- INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (3, '79eb3356-edcc-11e1-b379-5404a6202d2c', current_timestamp(3), 'image/png', 49105, null, null);
-- INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (3, 51, current_timestamp(3), current_timestamp(3), 'thread-entry-3-no-dl', '', current_timestamp(3), '82169142-edcc-11e1-9686-5404a6202d2c'); 
-- INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (3, 3, false); 
-- 
-- 
-- -- thread-entry-4-no-dl
-- INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (4, '92169010-edcc-11e1-8494-5404a6202d2c', current_timestamp(3), 'image/png', 49105, null, null);
-- INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (4, 51, current_timestamp(3), current_timestamp(3), 'thread-entry-4-no-dl', '', current_timestamp(3), '996eb78e-edcc-11e1-a48f-5404a6202d2c'); 
-- INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (4, 4, false); 
-- 
-- 
-- 
-- 
-- 
-- -- doc 1: réponse, projet:ratp, phase:Instruction
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (1, 1, 1, null); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (2, 1, 3, 1); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (3, 1, 4, 3); 
-- 
-- -- doc 2: réponse, projet:ratp, phase:Contraction
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (4, 2, 1, null); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (5, 2, 3, 1); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (6, 2, 4, 4); 
-- 
-- -- doc 3: réponse, projet:3mi, phase:Instruction
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (7, 3, 1, null); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (8, 3, 3, 2); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (9, 3, 4, 3); 
-- 
-- -- doc 3: question, projet:3m1, phase:Recommandation
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (10, 4, 2, null); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (11, 4, 3, 2); 
-- INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (12, 4, 4, 5); 
-- 

-- enable guests
UPDATE policy SET status=true where id=27;

-- enable thread tab
UPDATE policy SET status=true , system=false , default_status=true where id=45;

-- enable upload proposition web service
UPDATE policy SET status=true , policy=1 where id=98; -- fixme

-- enable upload request
UPDATE policy SET status=true , policy=1 where id=63;

COMMIT;
