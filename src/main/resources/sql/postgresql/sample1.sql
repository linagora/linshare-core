BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


-- Jeu de données de tests

INSERT INTO ldap_connection(ldap_connection_id, identifier, provider_url, security_auth, security_principal, security_credentials) VALUES (1, 'linshare-obm', 'ldap://linshare-obm2.linagora.dc1:389', 'simple', '', '');


-- system domain pattern
INSERT INTO domain_pattern(
 domain_pattern_id,
 identifier,
 description,
 auth_command,
 search_user_command,
 system,
 auto_complete_command_on_first_and_last_name,
 auto_complete_command_on_all_attributes,
 search_page_size,
 search_size_limit,
 completion_page_size,
 completion_size_limit)
VALUES (
 2,
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
 10
 );

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (5, 'user_mail', 'mail', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (6, 'user_firstname', 'givenName', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (7, 'user_lastname', 'sn', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (8, 'user_uid', 'uid', false, true, true, 2, false);



INSERT INTO user_provider_ldap(id, differential_key, domain_pattern_id, ldap_connection_id) VALUES (1, 'ou=users,dc=int1.linshare.dev,dc=local', 2, 1);


-- Top domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 1, 1, 2);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 0, 1, 1, 2, 1 , 3);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 2, 1, 4);







-- Users
-- bart simpson
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (50, 2, '9a9ece25-7a0e-4d75-bb55-d4070e25e1e1', current_timestamp(3), current_timestamp(3), 0, 'fr', 'en', true, false, 3);
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (50, 'Bart', 'Simpson', 'bart.simpson@int1.linshare.dev', true, '', false, true);
-- pierre mongin : 15kn60njvhdjh
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id, owner_id , password ) VALUES (53, 3, 'fa2cab19-2cd7-44f5-96f6-418455899d3e', current_timestamp(3), current_timestamp(3), 0, 'fr', 'fr', true, false, 4, 50 , 'OsFTxoUjd62imwHnaV/4zQfrJ5s=');
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, expiration_date) VALUES (53, 'Pierre', 'Mongin', 'pmongin@ratp.fr', true, '', false, false, current_timestamp(3));


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

-- Tags
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (1, 51, 'Réponse', false, true, null,0);
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (2, 51, 'Demande', false, true, null,0);

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (3, 51, 'Phases', false, true, true,1);
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (1, 3, 'Instruction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (2, 3, 'Contradiction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (3, 3, 'Recommandation'); 

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (7, 51, 'RATP', false, true, null,0);


-- Tags
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (4, 52, 'Réponse', false, true, null,0);
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (5, 52, 'Demande', false, true, null,0);

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (6, 52, 'Phases', false, true, true,1);
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (4, 6, 'Instruction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (5, 6, 'Contradiction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (6, 6, 'Recommandation'); 

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (8, 52, 'Ministère de l''intérieur', false, true, null,0);




-- tags filters
INSERT INTO tag_filter (id, account_id, name) VALUES (1, 51, 'Demande tag filter'); 
INSERT INTO tag_filter (id, account_id, name) VALUES (2, 51, 'Réponse tag filter'); 
INSERT INTO tag_filter (id, account_id, name) VALUES (5, 51, 'Thread name tag filter'); 

-- TagFilterByRecipient 1
-- TagFilterBySender 2
-- TagFilterByDomain 3

INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (1, 1, 'MySubDomain', 3); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (1, 1, 2, null); 


INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (2, 2, 'GuestDomain', 3); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (2, 2, 1, null); 


INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (5, 5, null, 4); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (5, 5, 7, null); 



-- tags filters
INSERT INTO tag_filter (id, account_id, name) VALUES (3, 52, 'Demande tag filter'); 
INSERT INTO tag_filter (id, account_id, name) VALUES (4, 52, 'Réponse tag filter'); 
INSERT INTO tag_filter (id, account_id, name) VALUES (6, 52, 'Thread name tag filter'); 

-- TagFilterByRecipient 1
-- TagFilterBySender 2
-- TagFilterByDomain 3

INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (3, 3, 'MySubDomain', 3); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (3, 3, 5, null); 


INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (4, 4, 'GuestDomain', 3); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (4, 4, 4, null); 

INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (6, 6, null, 4); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (6, 6, 8, null); 










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


-- default view and ratp view for thread ratp
INSERT INTO thread_view (id, thread_account_id, name) VALUES (1, 51, 'cc_default'); 
INSERT INTO thread_view (id, thread_account_id, name) VALUES (2, 51, 'cc_ratp'); 
UPDATE thread set thread_view_id=2 where account_id=51;
INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (1, 7, 2, 1); 
INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (2, 1, 2, 2); 
INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (3, 2, 2, 2); 
-- INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (4, 3, 2, 3); 
-- DELETE FROM thread_view_asso WHERE id=4;


-- default view and 3mi view for thread 3mi
INSERT INTO thread_view (id, thread_account_id, name) VALUES (3, 52, 'cc_default'); 
INSERT INTO thread_view (id, thread_account_id, name) VALUES (4, 52, 'cc_3mi'); 
UPDATE thread set thread_view_id=4 where account_id=52;
INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (5, 8, 4, 1); 
INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (6, 4, 4, 2); 
INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (7, 5, 4, 2); 
INSERT INTO thread_view_asso (id, tag_id, thread_view_id, depth) VALUES (8, 6, 4, 3); 

-- default view for thread Test Thread
INSERT INTO thread_view (id, thread_account_id, name) VALUES (5, 54, 'cc_default'); 
UPDATE thread set thread_view_id=5 where account_id=54;



COMMIT;
