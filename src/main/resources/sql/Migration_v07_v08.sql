insert into linshare_version (id, description) values (8, 'LinShare version 0.8');

create table linshare_group (
        group_id int8 not null,
		group_user_id int8 not null,
		name varchar(255) not null,
        functional_email varchar(255),
        description text,
        primary key (group_id)
);

create table linshare_group_members (
        group_id int8 not null,
        user_id int8 not null,
        member_type_id int4 not null,
        membership_date timestamp not null,
		primary key (group_id,user_id)
);

create index index_group_name on linshare_group (name);
create index index_group_user_id on linshare_group_members (user_id);

alter table linshare_group_members 
        add constraint FK3684AE4C675E97A1 
        foreign key (user_id) 
        references linshare_user;

alter table linshare_group_members 
        add constraint FK4284AE4C675E9722 
        foreign key (group_id) 
        references linshare_group;

alter table linshare_group
        add constraint FK3684CCCCCCAE97A1 
        foreign key (group_user_id) 
        references linshare_user;

ALTER TABLE linshare_document add column shared_with_group bool;
UPDATE linshare_document SET shared_with_group='false';



create table linshare_allowed_contact (
        id int8 not null,
        user_id int8 not null,
        contact_id int8 not null,
        primary key (id)
);

create index index_allowed_contact_user_id on linshare_allowed_contact (user_id);
create index index_allowed_contact_contact_id on linshare_allowed_contact (contact_id);

alter table linshare_allowed_contact
        add constraint FK3684FF4C67FF97FF 
        foreign key (user_id) 
        references linshare_user;

alter table linshare_allowed_contact
        add constraint FK4284AA4C675AA721 
        foreign key (contact_id) 
        references linshare_user;

alter table linshare_user add column restricted bool default false;


alter table linshare_parameter add COLUMN active_doc_time_stamp bool default false;
alter table linshare_document add COLUMN timestamp bytea;
