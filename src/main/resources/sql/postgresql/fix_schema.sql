-- Postgresql schema fix script for 1.2.0

BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

DELETE TABLE IF EXISTS thread_member_history;
DELETE TABLE IF EXISTS account_thread_member_history;
DELETE TABLE IF EXISTS thread_thread_member_history;

COMMIT;
