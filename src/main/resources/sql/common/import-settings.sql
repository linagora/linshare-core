-- LinShare version
INSERT INTO version (id, version, creation_date) VALUES (1, '6.3.0', now());

-- Sequence for hibernate
SELECT setval('hibernate_sequence', 1000);

COMMIT;
