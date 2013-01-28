-- MySQL migration script : 1.0.0 to 1.1.0


-- Functionality : RESTRICTED_GUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (47, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (48, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (24, true, 'RESTRICTED_GUEST', 47, 48, 1);

-- enable mime type filter functionality : bug fixed
UPDATE policy SET system = false where id=7;

-- enable signature functionality : bug fixed
UPDATE policy SET system = false where id=9;

-- LinShare version
INSERT INTO version (description) VALUES ('1.1.0');
