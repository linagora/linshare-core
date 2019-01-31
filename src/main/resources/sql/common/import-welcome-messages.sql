--Welcome messages
INSERT INTO welcome_messages(id, uuid, name, description, creation_date, modification_date, domain_id)
	VALUES (1, '4bc57114-c8c9-11e4-a859-37b5db95d856', 'WelcomeName', 'a Welcome description', now(), now(), 1);
--Melcome messages Entry
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id)
	VALUES (1, 'en', '<h2>Welcome to LinShare</h2><p>Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.</p>', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id)
	VALUES (2, 'fr', '<h2>Bienvenue dans LinShare</h2><p>Bienvenue dans LinShare, le logiciel libre de partage de fichiers sécurisé.</p>', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id)
	VALUES (3, 'vi', '<h2>Chào mừng bạn đến với LinShare</h2><p>Chào mừng bạn đến với LinShare, phần mềm nguồn mở chia sẻ file bảo mật.</p>', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id)
	VALUES (4, 'ru', '<h2>Добро пожаловать в LinShare</h2><p>Добро пожаловать в LinShare - открытое приложение для надежного обмена файлами.</p>', 1);
-- Default setting welcome messages for all domains
UPDATE domain_abstract SET welcome_messages_id = 1;