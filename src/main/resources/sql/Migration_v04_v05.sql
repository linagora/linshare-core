alter table linshare_allowed_mimetype
add column status int4;

update linshare_allowed_mimetype
set status = (select mimetypesdenied::int from linshare_parameter);

alter table linshare_parameter
drop column mimetypesdenied;

create table linshare_recipient_favourite (
        id int8 not null,
        user_id int8,
        recipient varchar(255),
        weight int8,
        primary key (id)
    );

create table linshare_secured_url (
        secured_url_id int8 not null,
        url_path varchar(255) not null,
        alea varchar(255) not null,
        expiration_date timestamp not null,
        password varchar(255),
        primary key (secured_url_id),
		sender_id bigint,
        unique (url_path, alea)
    );

    create table linshare_secured_url_documents (
        secured_url_id int8 not null,
        elt int8 not null,
        document_index int4 not null,
        primary key (secured_url_id, document_index)
    );

    create index index_favourite_recipient_id on linshare_recipient_favourite (user_id);


   alter table linshare_recipient_favourite 
        add constraint FK847BEC32FB78E769 
        foreign key (user_id) 
        references linshare_user;

    create index idx_secured_url on linshare_secured_url (url_path, alea);

    alter table linshare_secured_url_documents 
        add constraint FK139F29651FBB6B4E 
        foreign key (secured_url_id) 
        references linshare_secured_url;

    alter table linshare_secured_url
        add constraint fk5391e32c62928bf 
        foreign key (sender_id) 
        references linshare_user;

    alter table linshare_secured_url_documents 
        add constraint FK139F29659AF607D7 
        foreign key (elt) 
        references linshare_document;



    insert into linshare_version (id, description) values (2, 'LinShare version 0.5');
