
UPDATE domain_abstract SET mailconfig_id = 1 where mailconfig_id is null;

COMMIT;
