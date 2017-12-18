-- LinShare version
INSERT INTO version (id, version) VALUES (1, '2.0.0');

-- Sequence for hibernate
SELECT setval('hibernate_sequence', 1000);

COMMIT;