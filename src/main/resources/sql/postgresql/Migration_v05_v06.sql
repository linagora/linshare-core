alter TABLE linshare_welcome_texts ALTER COLUMN welcome_text TYPE text;

alter TABLE linshare_user ADD COLUMN can_create_guest bool default true;

alter TABLE linshare_document ADD COLUMN file_comment text;

alter TABLE linshare_parameter ADD COLUMN delete_doc_expiry_time bool default false;

create TABLE linshare_contact (
	contact_id int8 not null,
	mail varchar(255) not null,
	primary key (contact_id));

create TABLE linshare_secured_url_recipients (
    contact_id int8 not null,
    elt int8 not null,
    contact_index int4 not null,
    primary key (contact_id, contact_index)
);

alter TABLE linshare_secured_url_recipients
    add constraint FK7C25D06D464C4A4B
    foreign key (contact_id)
    references linshare_secured_url;

alter TABLE linshare_secured_url_recipients
    add constraint FK7C25D06DE97B80DE
    foreign key (elt)
    references linshare_contact;

insert into linshare_version (id, description) values (6, 'LinShare version 0.6');
