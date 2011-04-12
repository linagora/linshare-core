    create table linshare_allowed_contact (
        id bigint not null auto_increment,
        user_id bigint,
        contact_id bigint,
        primary key (id)
    );

    create table linshare_allowed_mimetype (
        id bigint not null,
        extensions varchar(255),
        mimetype varchar(255),
        status integer,
        primary key (id)
    );

    create table linshare_contact (
        contact_id bigint not null auto_increment,
        mail varchar(255) not null,
        primary key (contact_id)
    );

    create table linshare_cookie (
        cookie_id bigint not null auto_increment,
        identifier varchar(255) not null unique,
        user_name varchar(255) not null,
        value varchar(255) not null,
        last_use datetime not null,
        primary key (cookie_id)
    );

    create table linshare_document (
        document_id bigint not null auto_increment,
        identifier varchar(255) not null unique,
        name varchar(255) not null,
        creation_date datetime not null,
        expiration_date datetime not null,
        deletion_date timestamp,
        type varchar(255),
        encrypted bit,
        shared bit,
        shared_with_group bit,
        size bigint,
        file_comment varchar(255),
        thmb_uuid varchar(255),
        timestamp tinyblob,
        owner_id bigint,
        primary key (document_id)
    );

    create table linshare_group (
        group_id bigint not null auto_increment,
        name varchar(255) not null,
        group_user_id bigint not null unique,
        functional_email varchar(255),
        description text,
        primary key (group_id)
    );

    create table linshare_group_members (
        group_id bigint not null,
        member_type_id integer,
        membership_date datetime not null,
        user_id bigint,
        owner_id bigint,
        primary key (group_id, membership_date)
    );

    create table linshare_log_entry (
        id bigint not null auto_increment,
        entry_type varchar(255) not null,
        action_date datetime not null,
        actor_mail varchar(255) not null,
        actor_firstname varchar(255) not null,
        actor_lastname varchar(255) not null,
        log_action varchar(255) not null,
        description varchar(255),
        file_name varchar(255),
        file_type varchar(255),
        file_size bigint,
        target_mail varchar(255),
        target_firstname varchar(255),
        target_lastname varchar(255),
        expiration_date datetime,
        primary key (id)
    );

    create table linshare_mail_subjects (
        parameter_id bigint not null,
        subject_id integer not null,
        language_id integer not null,
        content text,
        primary key (parameter_id, subject_id, language_id)
    );

    create table linshare_mail_templates (
        parameter_id bigint not null,
        template_id integer not null,
        language_id integer not null,
        content_html text,
        content_txt text,
        primary key (parameter_id, template_id, language_id)
    );

    create table linshare_parameter (
        parameter_id bigint not null,
        file_size_max bigint,
        user_available_size bigint,
        active_mimetype bit,
        active_signature bit,
        active_encipherment bit,
        active_doc_time_stamp bit,
        user_expiry_time integer,
        user_expiry_time_unit_id integer,
        custom_logo_url varchar(255),
        default_expiry_time integer,
        delete_doc_expiry_time bit,
        default_expiry_time_unit_id integer,
        default_file_expiry_time integer,
        default_file_expiry_time_unit_id integer,
        primary key (parameter_id)
    );

    create table linshare_recipient_favourite (
        id bigint not null auto_increment,
        user_id bigint,
        recipient varchar(255),
        weight bigint,
        primary key (id)
    );

    create table linshare_secured_url (
        secured_url_id bigint not null auto_increment,
        url_path varchar(255) not null,
        alea varchar(255) not null,
        expiration_date datetime not null,
        password varchar(255),
        sender_id bigint,
        primary key (secured_url_id),
        unique( url_path, alea )
    ) CHARACTER SET ascii COLLATE ascii_general_ci;

    create table linshare_secured_url_documents (
        secured_url_id bigint not null,
        elt bigint not null,
        document_index integer not null,
        primary key (secured_url_id, document_index)
    );

    create table linshare_secured_url_recipients (
        contact_id bigint not null,
        elt bigint not null,
        contact_index integer not null,
        primary key (contact_id, contact_index)
    );

    create table linshare_share (
        share_id bigint not null auto_increment,
        document_id bigint,
        sender_id bigint,
        recipient_id bigint,
        expiration_date datetime,
        sharing_date datetime,
        share_active bit,
        downloaded bit,
        comment text,
        primary key (share_id)
    );

    create table linshare_share_expiry_rules (
        parameter_id bigint not null,
        expiry_time integer,
        time_unit_id integer,
        share_size integer,
        size_unit_id integer,
        rule_sort_order integer not null,
        primary key (parameter_id, rule_sort_order)
    );

    create table linshare_signature (
        signature_id bigint not null auto_increment,
        identifier varchar(255) not null unique,
        name varchar(255) not null,
        creation_date datetime not null,
        type varchar(255),
        size bigint,
        cert_subjectdn varchar(255),
        cert_issuerdn varchar(255),
        cert_notafter datetime,
        cert text,
        signer_id bigint,
        document_id_fk bigint,
        sort_order integer,
        primary key (signature_id)
    );

    create table linshare_user (
        user_id bigint not null auto_increment,
        user_type_id varchar(255) not null,
        login varchar(255) not null unique,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        encipherment_key_pass tinyblob,
        mail varchar(255) not null unique,
        creation_date datetime,
        role_id integer not null,
        can_upload bit,
        can_create_guest bit,
        password varchar(255),
        locale varchar(255),
        expiry_date datetime,
        comment text,
        restricted bit,
        owner_id bigint,
        primary key (user_id)
    );

    create table linshare_welcome_texts (
        parameter_id bigint not null,
        welcome_text text,
        user_type_id integer,
        language_id integer
    );

    create index index_allowed_contact_user_id on linshare_allowed_contact (user_id);

    create index index_allowed_contact_contact_id on linshare_allowed_contact (contact_id);

    alter table linshare_allowed_contact 
        add index FKDFE3FE38C9452F4 (contact_id), 
        add constraint FKDFE3FE38C9452F4 
        foreign key (contact_id) 
        references linshare_user (user_id);

    alter table linshare_allowed_contact 
        add index FKDFE3FE38FB78E769 (user_id), 
        add constraint FKDFE3FE38FB78E769 
        foreign key (user_id) 
        references linshare_user (user_id);

    create index index_cookie_identifier on linshare_cookie (identifier);

    create index index_document_name on linshare_document (name);

    create index index_document_owner_id on linshare_document (owner_id);

    create index index_document_identifier on linshare_document (identifier);

    create index index_document_expiration_date on linshare_document (expiration_date);

    alter table linshare_document 
        add index FK56846E4C675F9781 (owner_id), 
        add constraint FK56846E4C675F9781 
        foreign key (owner_id) 
        references linshare_user (user_id);

    create index index_group_name on linshare_group (name);

    alter table linshare_group 
        add index FK833CCEEEFE8695A9 (group_user_id), 
        add constraint FK833CCEEEFE8695A9 
        foreign key (group_user_id) 
        references linshare_user (user_id);

    create index index_group_user_id on linshare_group_members (user_id);

    alter table linshare_group_members 
        add index FK354C70C8FB78E769 (user_id), 
        add constraint FK354C70C8FB78E769 
        foreign key (user_id) 
        references linshare_user (user_id);

    alter table linshare_group_members 
        add index FK354C70C8A0EA11AB (group_id), 
        add constraint FK354C70C8A0EA11AB 
        foreign key (group_id) 
        references linshare_group (group_id);

    alter table linshare_group_members 
        add index FK354C70C8675F9781 (owner_id), 
        add constraint FK354C70C8675F9781 
        foreign key (owner_id) 
        references linshare_user (user_id);

    create index index_userlog_entry_target_mail on linshare_log_entry (target_mail);

    create index index_sharelog_entry_file_name on linshare_log_entry (file_name);

    create index index_log_entry_actor_first_name on linshare_log_entry (actor_firstname);

    create index index_log_entry_actor_mail on linshare_log_entry (actor_mail);

    create index index_log_entry_action_date on linshare_log_entry (action_date);

    create index index_log_entry_action on linshare_log_entry (log_action);

    create index index_sharelog_entry_target_mail on linshare_log_entry (target_mail);

    create index index_log_entry_actor_last_name on linshare_log_entry (actor_lastname);

    create index index_filelog_entry_file_name on linshare_log_entry (file_name);

    alter table linshare_mail_subjects 
        add index FK1C97F3BEA44B78EB (parameter_id), 
        add constraint FK1C97F3BEA44B78EB 
        foreign key (parameter_id) 
        references linshare_parameter (parameter_id);

    alter table linshare_mail_templates 
        add index FKDD1B7F22A44B78EB (parameter_id), 
        add constraint FKDD1B7F22A44B78EB 
        foreign key (parameter_id) 
        references linshare_parameter (parameter_id);

    create index index_favourite_recipient_id on linshare_recipient_favourite (user_id);

    alter table linshare_recipient_favourite 
        add index FK847BEC32FB78E769 (user_id), 
        add constraint FK847BEC32FB78E769 
        foreign key (user_id) 
        references linshare_user (user_id);

    create index idx_secured_url on linshare_secured_url (url_path, alea);

    create index index_securedurl_sender_id on linshare_secured_url (sender_id);

    alter table linshare_secured_url 
        add index FK5391E32C62928BF (sender_id), 
        add constraint FK5391E32C62928BF 
        foreign key (sender_id) 
        references linshare_user (user_id);

    alter table linshare_secured_url_documents 
        add index FK139F29651FBB6B4E (secured_url_id), 
        add constraint FK139F29651FBB6B4E 
        foreign key (secured_url_id) 
        references linshare_secured_url (secured_url_id);

    alter table linshare_secured_url_documents 
        add index FK139F29659AF607D7 (elt), 
        add constraint FK139F29659AF607D7 
        foreign key (elt) 
        references linshare_document (document_id);

    alter table linshare_secured_url_recipients 
        add index FK7C25D06D464C4A4B (contact_id), 
        add constraint FK7C25D06D464C4A4B 
        foreign key (contact_id) 
        references linshare_secured_url (secured_url_id);

    alter table linshare_secured_url_recipients 
        add index FK7C25D06DE97B80DE (elt), 
        add constraint FK7C25D06DE97B80DE 
        foreign key (elt) 
        references linshare_contact (contact_id);

    create index index_share_document_id on linshare_share (document_id);

    create index index_share_sender_id on linshare_share (sender_id);

    create index index_share_recipient_id on linshare_share (recipient_id);

    create index index_share_sharing_date on linshare_share (sharing_date);

    create index index_share_expiration_date on linshare_share (expiration_date);

    alter table linshare_share 
        add index FK83E1284EB927C5E9 (document_id), 
        add constraint FK83E1284EB927C5E9 
        foreign key (document_id) 
        references linshare_document (document_id);

    alter table linshare_share 
        add index FK83E1284E62928BF (sender_id), 
        add constraint FK83E1284E62928BF 
        foreign key (sender_id) 
        references linshare_user (user_id);

    alter table linshare_share 
        add index FK83E1284E4F9C165B (recipient_id), 
        add constraint FK83E1284E4F9C165B 
        foreign key (recipient_id) 
        references linshare_user (user_id);

    alter table linshare_share_expiry_rules 
        add index FKFDA1673CA44B78EB (parameter_id), 
        add constraint FKFDA1673CA44B78EB 
        foreign key (parameter_id) 
        references linshare_parameter (parameter_id);

    create index index_signature_signer_id on linshare_signature (signer_id);

    alter table linshare_signature 
        add index FK81C9A1A74472B3AA (signer_id), 
        add constraint FK81C9A1A74472B3AA 
        foreign key (signer_id) 
        references linshare_user (user_id);

    alter table linshare_signature 
        add index FK81C9A1A7C0BBD6F (document_id_fk), 
        add constraint FK81C9A1A7C0BBD6F 
        foreign key (document_id_fk) 
        references linshare_document (document_id);

    create index index_user_last_name on linshare_user (last_name);

    create index index_user_mail on linshare_user (mail);

    create index index_user_login on linshare_user (login);

    create index index_user_first_name on linshare_user (first_name);

    alter table linshare_user 
        add index FK56D6C97C675F9781 (owner_id), 
        add constraint FK56D6C97C675F9781 
        foreign key (owner_id) 
        references linshare_user (user_id);

    alter table linshare_welcome_texts 
        add index FK36A0C738A44B78EB (parameter_id), 
        add constraint FK36A0C738A44B78EB 
        foreign key (parameter_id) 
        references linshare_parameter (parameter_id);
