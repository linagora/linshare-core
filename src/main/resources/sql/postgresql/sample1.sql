-- Jeu de données de tests

INSERT INTO ldap_connection(ldap_connection_id, identifier, provider_url, security_auth, security_principal, security_credentials) VALUES (1, 'linshare-obm', 'ldap://linshare-obm.linshare.team.services.par.lng:389', 'simple', '', '');

INSERT INTO domain_pattern(domain_pattern_id, identifier, description, auth_command, search_user_command, auto_complete_command, system) VALUES (2, 'linshare-obm', '', 'ldap.list(domain, "(&(objectClass=obmUser)(mail="+login+")(givenName=*)(sn=*))");', 'ldap.list(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+firstName+")(sn="+lastName+"))");', 'Not Yet Implemented', false);

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (5, 'user_mail', 'mail', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (6, 'user_firstname', 'givenName', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (7, 'user_lastname', 'sn', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (8, 'user_uid', 'uid', false, true, true, 2);






INSERT INTO user_provider_ldap(id, differential_key, domain_pattern_id, ldap_connection_id) VALUES (1, 'ou=users,dc=int1.linshare.dev,dc=local', 2, 1);


-- Top domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 1, 1, 2);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 0, 1, 1, 2, 1 , 3);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 2, 1, 4);







-- Users
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, enable, destroyed, domain_id) VALUES (50, 2, '9a9ece25-7a0e-4d75-bb55-d4070e25e1e1', current_date, current_date, 0, 'fr', true, false, 3);
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (50, 'Bart', 'Simpson', 'bart.simpson@int1.linshare.dev', true, '', false, true);


-- Thread
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, enable, destroyed, domain_id) VALUES (51, 5, '9806de10-ed0b-11e1-877a-5404a6202d2c', current_date, current_date, 0, 'fr', true, false, 1);
INSERT INTO thread (account_id, name) VALUES (51, 'cours des comptes');


--Thread members
INSERT INTO thread_member (id, thread_id, admin, can_upload, creation_date, modification_date, user_id) VALUES (1, 51, true, true, current_date, current_date, 50); 


-- Tags
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (1, 51, 'Réponse', false, true, null,0);
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (2, 51, 'Demande', false, true, null,0);

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (3, 51, 'Projets', false, true, true,1);
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (1, 3, 'RATP'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (2, 3, 'Ministère de l''intérieur'); 

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (4, 51, 'Phases', false, true, true,1);
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (3, 4, 'Instruction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (4, 4, 'Contradiction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (5, 4, 'Recommandation'); 


-- thread-entry-1-no-dl
INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (1, 'a09e6bea-edcb-11e1-86e7-5404a6202d2c', current_date, 'image/png', 49105, null, null);
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (1, 51, current_date, current_date, 'thread-entry-1-no-dl', '', current_date, '5a663f86-edcb-11e1-a9fd-5404a6202d2c'); 
INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (1, 1, false); 


-- thread-entry-2-no-dl
INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (2, '026e60fa-edcc-11e1-acb9-5404a6202d2c', current_date, 'image/png', 49105, null, null);
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (2, 51, current_date, current_date, 'thread-entry-2-no-dl', '', current_date, '187f5ef8-edcc-11e1-8ed2-5404a6202d2c'); 
INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (2, 2, false); 

-- thread-entry-3-no-dl
INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (3, '79eb3356-edcc-11e1-b379-5404a6202d2c', current_date, 'image/png', 49105, null, null);
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (3, 51, current_date, current_date, 'thread-entry-3-no-dl', '', current_date, '82169142-edcc-11e1-9686-5404a6202d2c'); 
INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (3, 3, false); 


-- thread-entry-4-no-dl
INSERT INTO document (id, uuid, creation_date, type, size, thmb_uuid, timestamp) VALUES (4, '92169010-edcc-11e1-8494-5404a6202d2c', current_date, 'image/png', 49105, null, null);
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, expiration_date, uuid) VALUES (4, 51, current_date, current_date, 'thread-entry-4-no-dl', '', current_date, '996eb78e-edcc-11e1-a48f-5404a6202d2c'); 
INSERT INTO thread_entry (entry_id, document_id, ciphered) VALUES (4, 4, false); 





-- doc 1: réponse, projet:ratp, phase:Instruction
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (1, 1, 1, null); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (2, 1, 3, 1); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (3, 1, 4, 3); 

-- doc 2: réponse, projet:ratp, phase:Contraction
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (4, 2, 1, null); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (5, 2, 3, 1); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (6, 2, 4, 4); 

-- doc 3: réponse, projet:3mi, phase:Instruction
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (7, 3, 1, null); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (8, 3, 3, 2); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (9, 3, 4, 3); 

-- doc 3: question, projet:3m1, phase:Recommandation
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (10, 4, 2, null); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (11, 4, 3, 2); 
INSERT INTO entry_tag_association (id, entry_id, tag_id, enum_value_id) VALUES (12, 4, 4, 5); 



-- tags filters
INSERT INTO tag_filter (id, account_id, name) VALUES (1, 51, 'Demande'); 
INSERT INTO tag_filter (id, account_id, name) VALUES (2, 51, 'Réponse'); 

-- TagFilterByRecipient 1
-- TagFilterBySender 2
-- TagFilterByDomain 3

INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (1, 1, 'MySubDomain', 3); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (1, 1, 2, null); 


INSERT INTO tag_filter_rule (id, tag_filter_id, regexp, tag_rule_type) VALUES (2, 2, 'GuestDomain', 3); 
INSERT INTO tag_filter_rule_tag_association (id, tag_filter_rule_id, tag_id, enum_value_id) VALUES (2, 2, 1, null); 

