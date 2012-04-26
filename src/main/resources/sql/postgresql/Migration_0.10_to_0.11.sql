-- Postgresql migration script : 0.10 to 0.11

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


-- First Modification :
-- later linshare_document table to modify the file_comment field type (varchar to text)
 
-- drop old column
ALTER TABLE linshare_document DROP file_comment;

-- Add new column
ALTER TABLE linshare_document  ADD file_comment TEXT;



-- Second Modification :
-- Add new functionality : now we can force secured shareing option for anonimous shares.

-- Functionality : SECURE_URL

INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES ((SELECT nextVal('hibernate_sequence')), false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES ((SELECT nextVal('hibernate_sequence')), false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id)  
VALUES ((SELECT nextVal('hibernate_sequence')), true, 'SECURED_ANONYMOUS_URL',
(SELECT currVal('hibernate_sequence')) -2 , (SELECT currVal('hibernate_sequence'))-1, 1);


-- Third Modification : 
ALTER TABLE linshare_domain_abstract  ADD auth_show_order bigint;
UPDATE linshare_domain_abstract SET auth_show_order=1;

-- Forth Modification : 
-- Update schema version
ALTER TABLE linshare_version ADD UNIQUE(description);
INSERT INTO linshare_version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'0.11.0');






