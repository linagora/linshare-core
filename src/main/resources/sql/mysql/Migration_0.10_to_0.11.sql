-- Mysql migration script : 0.10 to 0.11


-- First Modification :
-- later linshare_document table to modify the file_comment field type (varchar to text)

-- drop old column
ALTER TABLE `linshare_document` DROP `file_comment` ;

-- Add new column
ALTER TABLE `linshare_document`  ADD `file_comment` TEXT AFTER `size`;



-- Second Modification :
-- Add new functionality : now we can force secured shareing option for anonimous shares.

-- Functionality : SECURE_URL
INSERT INTO linshare_policy(status, default_status, policy, system) VALUES (false, false, 1, false);
INSERT INTO linshare_policy(status, default_status, policy, system) VALUES (false, false, 1, true);
INSERT INTO linshare_functionality(system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (true, 'SECURED_ANONYMOUS_URL', LAST_INSERT_ID()-1, LAST_INSERT_ID(), 1);



-- Third Modification :
-- Fix time stamping type field for mysql only
ALTER TABLE `linshare_document` CHANGE `timestamp` `timestamp` BLOB NULL DEFAULT NULL;

-- Forth Modification : 
ALTER TABLE `linshare_domain_abstract`  ADD `auth_show_order` bigint ;
UPDATE linshare_domain_abstract SET auth_show_order=1;

-- Last Modification : 
-- Update schema version
ALTER TABLE `linshare_version` ADD PRIMARY KEY(`id`) ;
ALTER TABLE `linshare_version` CHANGE `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `linshare_version` ADD UNIQUE(`description`) ;
INSERT INTO linshare_version (description) VALUES ('0.11.0');


