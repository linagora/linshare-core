
ALTER TABLE mail_content_lang ENABLE TRIGGER ALL;
ALTER TABLE mail_content ENABLE TRIGGER ALL;
ALTER TABLE mail_footer_lang ENABLE TRIGGER ALL;
ALTER TABLE mail_footer ENABLE TRIGGER ALL;

UPDATE domain_abstract SET mailconfig_id = 1 where mailconfig_id is null;

COMMIT;
