insert into linshare_version (id, description) values (7, 'LinShare version 0.7');

ALTER TABLE linshare_document ADD COLUMN thmb_uuid VARCHAR(255);

create table linshare_cookie (
        cookie_id int8 not null,
        identifier varchar(64) not null unique,
        user_name varchar(255) not null,
        value varchar(64) not null,
        last_use timestamp not null,
        primary key (cookie_id)
);
create index index_cookie_identifier on linshare_cookie (identifier);



