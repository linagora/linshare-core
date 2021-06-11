SET client_encoding = 'UTF8';
SET client_min_messages = warning;
\set ON_ERROR_STOP

BEGIN;

UPDATE domain_abstract SET mailconfig_id = null where mailconfig_id = 1;
DELETE FROM mail_content_lang WHERE id < 1000;
DELETE FROM mail_footer_lang WHERE id < 1000;
DELETE FROM mail_config WHERE id = 1;
DELETE FROM mail_content WHERE id < 1000;
DELETE FROM mail_footer WHERE id < 1000;
DELETE FROM mail_layout WHERE id < 1000;

