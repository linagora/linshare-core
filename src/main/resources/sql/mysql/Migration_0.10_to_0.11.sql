-- Mysql migration script : 0.10 to 0.11


-- First Modification :
-- later linshare_document table to modify the file_comment field type (varchar to text)
 
-- Add new column
ALTER TABLE `linshare_document`  ADD `comment` TEXT AFTER `file_comment`;

-- Copy datas
UPDATE linshare_document set comment=file_comment ;

-- drop old column
ALTER TABLE `linshare_document` DROP `file_comment` ;

-- rename new field with old name
ALTER TABLE `linshare_document` CHANGE `comment` `file_comment` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ;




-- Second Modification :
-- Add new functionality : now we can force secured shareing option for anonimous shares.

-- Functionality : SECURE_URL
INSERT INTO linshare_policy(status, default_status, policy, system) VALUES (false, false, 1, false);
INSERT INTO linshare_policy(status, default_status, policy, system) VALUES (false, false, 1, true);
INSERT INTO linshare_functionality(system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (true, 'SECURED_ANONYMOUS_URL', LAST_INSERT_ID()-1, LAST_INSERT_ID(), 1);



-- Third Modification :
-- Fix time stamping type field for mysql only
ALTER TABLE `linshare_document` CHANGE `timestamp` `timestamp` BLOB NULL DEFAULT NULL;


-- Last Modification : 
-- Update schema version
ALTER TABLE `linshare_version` ADD PRIMARY KEY(`id`) ;
ALTER TABLE `linshare_version` CHANGE `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `linshare_version` ADD UNIQUE(`description`) ;
INSERT INTO linshare_version (description) VALUES ('0.11.0');


