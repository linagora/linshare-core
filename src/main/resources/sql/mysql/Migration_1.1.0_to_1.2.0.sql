-- MySQL migration script : 1.1.0 to 1.2.0

ALTER TABLE allowed_mimetype ADD constraint allowed_mimetype_unique_mimetype UNIQUE(mimetype);
DELETE FROM allowed_mimetype ;

ALTER TABLE document ADD COLUMN check_mime_type bool  DEFAULT false NOT NULL;

-- LinShare version
INSERT INTO version (description) VALUES ('1.2.0');
