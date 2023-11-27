
-- Insert test mime policies

INSERT INTO mime_policy(id, domain_id, uuid, name, mode, displayable, creation_date, modification_date, unknown_type_allowed)
	VALUES
	(2, 1, '0d3ff074-d22d-11ed-afa1-0242ac120002', 'Second Mime Policy', 0, 0, now(), now(), false),
	(3, 3, '7bd723c4-d23a-11ed-afa1-0242ac120002', 'Third Mime Policy', 0, 0, now(), now(), false);