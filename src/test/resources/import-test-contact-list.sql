insert into contact_list (id, domain_abstract_id, user_id, identifier, is_public, description, uuid, creation_date, modification_date)
values  (2881, 2, 11, 'Test list', true, null, '3146761c-e09d-4f41-9ba4-8187138e68e9', now(), now()),
        (2885, 2, 11, 'External internal', true, null, '1c8a8877-3c14-4664-979e-77047a536005', now(), now());

insert into contact_list_contact (id, contact_list_id, mail, first_name, last_name, uuid, creation_date, modification_date)
values  (2882, 2881, 'dwho@linshare.org', 'Doctor', 'WHO', '538988ff-4e7c-432c-bf07-22d8e8cac537', now(), now()),
        (2883, 2881, 'grant.big@linshare.org', 'Grant', 'BIG', '86525094-95bf-4bb4-884d-e8269ab5e6ed', now(), now()),
        (2884, 2881, 'felton.gumper@linshare.org', 'Felton', 'GUMPER', 'fffbdf24-9f01-4315-ac93-8d3e10467d24', now(), now()),
        (2886, 2885, 'jp@test.fr', 'jp', '', '15f5336c-73bf-4517-8e44-a0d5e6366bc9', now(), now()),
        (2887, 2885, 'external1@linshare.org', 'A', 'A', '1b01d7de-0074-46aa-88c1-0ae6ff733689', now(), now()),
        (2888, 2885, 'felton.gumper@linshare.org', 'Felton', 'GUMPER', 'bb028126-3795-4960-be18-1882c5fd948e', now(), now());

