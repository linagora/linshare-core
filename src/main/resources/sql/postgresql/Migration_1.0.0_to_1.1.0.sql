-- Postgresql migration script : 1.0.0 to 1.1.0

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


-- Functionality : RESTRICTED_GUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (47, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (48, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (24, true, 'RESTRICTED_GUEST', 47, 48, 1);

-- Functionality : DOMAIN_MAIL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (49, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (50, false, false, 2, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (25, false, 'DOMAIN_MAIL', 49, 50, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (25, 'linshare-noreply@linagora.com');

-- enable mime type filter functionality : bug fixed
UPDATE policy SET system = false where id=7;

-- enable signature functionality : bug fixed
UPDATE policy SET system = false where id=9;

-- LinShare version
INSERT INTO version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'1.1.0');
