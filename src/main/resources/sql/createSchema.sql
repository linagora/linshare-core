
    create table linshare_messages_configuration (
        messages_configuration_id int8 not null,
        primary key (messages_configuration_id)
    );


    create table linshare_allowed_mimetype (
        id int8 not null,
        extensions varchar(255),
        mimetype varchar(255),
        status int4,
        primary key (id)
    );

    create table linshare_contact (
        contact_id int8 not null,
        mail varchar(255) not null,
        primary key (contact_id)
    );

    create table linshare_document (
        document_id int8 not null,
        identifier varchar(255) not null unique,
        name varchar(255) not null,
        creation_date timestamp not null,
        expiration_date timestamp not null,
        deletion_date timestamp,
        type varchar(255),
        encrypted bool,
        shared bool,
        shared_with_group bool,
        size int8,
        file_comment text,
        owner_id int8,
        thmb_uuid varchar(255),
        timestamp bytea,
        primary key (document_id)
    );
	
    create table linshare_parameter (
        parameter_id int8 not null,
        file_size_max int8,
        user_available_size int8,
        global_quota int8,
        global_used_quota int8,
        active_global_quota bool,
        active_mimetype bool,
        active_signature bool,
        active_encipherment bool,
        active_doc_time_stamp bool,
        user_expiry_time int4,
        user_expiry_time_unit_id int4,
        custom_logo_url varchar(255),
        default_expiry_time int4,
        default_expiry_time_unit_id int4,
        default_file_expiry_time int4,
        default_file_expiry_time_unit_id int4,
        delete_doc_expiry_time bool default false,
	closed_domain bool default false,
	restricted_domain bool default false,
	domain_with_guests bool default true,
	guest_can_create_other bool default true,
	messages_configuration_id int8 not null,
        primary key (parameter_id)
    );

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
        sender_id int8,
        primary key (secured_url_id),
        unique (url_path, alea)
    );

    create table linshare_secured_url_documents (
        secured_url_id int8 not null,
        elt int8 not null,
        document_index int4 not null,
        primary key (secured_url_id, document_index)
    );

    create table linshare_secured_url_recipients (
        contact_id int8 not null,
        elt int8 not null,
        contact_index int4 not null,
        primary key (contact_id, contact_index)
    );

    create table linshare_share (
        share_id int8 not null,
        document_id int8,
        sender_id int8,
        recipient_id int8,
        expiration_date timestamp,
        sharing_date timestamp,
        share_active bool,
        downloaded bool,
        comment text,
        primary key (share_id)
    );

    create table linshare_share_expiry_rules (
        parameter_id int8 not null,
        expiry_time int4,
        time_unit_id int4,
        share_size int4,
        size_unit_id int4,
        rule_sort_order int4 not null,
        primary key (parameter_id, rule_sort_order)
    );

    create table linshare_signature (
        signature_id int8 not null,
        identifier varchar(255) not null unique,
        name varchar(255) not null,
        creation_date timestamp not null,
        type varchar(255),
        size int8,
        cert_subjectdn varchar(255),
        cert_issuerdn varchar(255),
        cert_notafter timestamp,
        cert text,
        signer_id int8,
        document_id_fk int8,
        sort_order int4,
        primary key (signature_id)
    );

    create table linshare_user (
        user_id int8 not null,
        user_type_id varchar(255) not null,
	domain_id int8,
        login varchar(255) not null unique,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        encipherment_key_pass bytea,
        mail varchar(255) not null unique,
        creation_date timestamp,
        role_id int4 not null,
        can_upload bool,
        can_create_guest bool default false,
        restricted bool default false,
        password varchar(255),
        locale varchar(255),
        expiry_date timestamp,
        comment text,
        owner_id int8,
        primary key (user_id)
    );

    create table linshare_welcome_texts (
        messages_configuration_id int8 not null,
        welcome_text text,
        user_type_id int4,
        language_id int4
    );

    create table public.linshare_log_entry (
        id int8 not null,
        entry_type varchar(255) not null,
        action_date timestamp not null,
        actor_mail varchar(255) not null,
        actor_firstname varchar(255) not null,
        actor_lastname varchar(255) not null,
        actor_domain varchar(255),
        log_action varchar(255) not null,
        description varchar(255),
        file_name varchar(255),
        file_type varchar(255),
        file_size int8,
        target_mail varchar(255),
        target_firstname varchar(255),
        target_lastname varchar(255),
        expiration_date timestamp,
        primary key (id)
    );

    create table public.linshare_version (
        id int8 not null,
        description varchar(255) not null
    );
    
    create table linshare_cookie (
        cookie_id int8 not null,
        identifier varchar(64) not null unique,
        user_name varchar(255) not null,
        value varchar(64) not null,
        last_use timestamp not null,
        primary key (cookie_id)
	);
	
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
        owner_id int8,
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
	
	create index index_cookie_identifier on linshare_cookie (identifier);

    create index index_document_name on linshare_document (name);

    create index index_document_owner_id on linshare_document (owner_id);

    create index index_document_identifier on linshare_document (identifier);

    create index index_document_expiration_date on linshare_document (expiration_date);

    alter table linshare_document 
        add constraint FK56846E4C675F9781 
        foreign key (owner_id) 
        references linshare_user;

    create index index_favourite_recipient_id on linshare_recipient_favourite (user_id);

    alter table linshare_recipient_favourite 
        add constraint FK847BEC32FB78E769 
        foreign key (user_id) 
        references linshare_user;

    create index idx_secured_url on linshare_secured_url (url_path, alea);

    create index index_securedurl_sender_id on linshare_secured_url (sender_id);

    alter table linshare_secured_url 
        add constraint FK5391E32C62928BF 
        foreign key (sender_id) 
        references linshare_user;

    alter table linshare_secured_url_documents 
        add constraint FK139F29651FBB6B4E 
        foreign key (secured_url_id) 
        references linshare_secured_url;

    alter table linshare_secured_url_documents 
        add constraint FK139F29659AF607D7 
        foreign key (elt) 
        references linshare_document;

    alter table linshare_secured_url_recipients 
        add constraint FK7C25D06D464C4A4B 
        foreign key (contact_id) 
        references linshare_secured_url;

    alter table linshare_secured_url_recipients 
        add constraint FK7C25D06DE97B80DE 
        foreign key (elt) 
        references linshare_contact;

    create index index_share_document_id on linshare_share (document_id);

    create index index_share_sender_id on linshare_share (sender_id);

    create index index_share_recipient_id on linshare_share (recipient_id);

    create index index_share_expiration_date on linshare_share (expiration_date);

    alter table linshare_share 
        add constraint FK83E1284EB927C5E9 
        foreign key (document_id) 
        references linshare_document;

    alter table linshare_share 
        add constraint FK83E1284E62928BF 
        foreign key (sender_id) 
        references linshare_user;

    alter table linshare_share 
        add constraint FK83E1284E4F9C165B 
        foreign key (recipient_id) 
        references linshare_user;

    alter table linshare_share_expiry_rules 
        add constraint FKFDA1673CA44B78EB 
        foreign key (parameter_id) 
        references linshare_parameter;

    create index index_signature_signer_id on linshare_signature (signer_id);

    alter table linshare_signature 
        add constraint FK81C9A1A74472B3AA 
        foreign key (signer_id) 
        references linshare_user;

    alter table linshare_signature 
        add constraint FK81C9A1A7C0BBD6F 
        foreign key (document_id_fk) 
        references linshare_document;

    create index index_user_last_name on linshare_user (last_name);

    create index index_user_mail on linshare_user (mail);

    create index index_user_login on linshare_user (login);

    create index index_user_first_name on linshare_user (first_name);

    alter table linshare_user 
        add constraint FK56D6C97C675F9781 
        foreign key (owner_id) 
        references linshare_user;

    alter table linshare_welcome_texts 
        add constraint FK36A0C738A44B78EB 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration;
        
    create table linshare_mail_templates (
        messages_configuration_id int8 not null,
        template_id int4 not null,
        language_id int4 not null,
        content_html text,
        content_txt text
    );

    create table linshare_mail_subjects (
        messages_configuration_id int8 not null,
        subject_id int4 not null,
        language_id int4 not null,
        content text
    );

    create table linshare_ldap_connection (
	ldap_connection_id int8 not null, 
	identifier varchar(255) not null unique, 
	provider_url varchar(255), 
	security_auth varchar(255), 
	security_principal varchar(255), 
	security_credentials varchar(255),
        primary key (ldap_connection_id)
    );

    create index index_ldap_connection on linshare_ldap_connection (identifier);

    create table linshare_domain_pattern (
	domain_pattern_id int8 not null, 
	identifier varchar(255) not null unique, 
	description varchar(255), 
	get_user_command varchar(255), 
	get_all_domain_users_command varchar(255), 
	auth_command varchar(255), 
	search_user_command varchar(255), 
	get_user_result varchar(255),
        primary key (domain_pattern_id)
    );

    create index index_domain_pattern on linshare_domain_pattern (identifier);

    create table linshare_domain (
	domain_id int8 not null,
	identifier varchar(255),
	differential_key varchar(255),
	domain_pattern_id int8 not null,
	ldap_connection_id int8 not null,
	parameter_id int8 not null,
        primary key (domain_id)
    );

    create index index_domain on linshare_domain (identifier);

    alter table linshare_domain 
        add constraint FAA6A0CAAAA44B78EB 
        foreign key (domain_pattern_id) 
        references linshare_domain_pattern;

    alter table linshare_domain 
        add constraint FBB6A0CABBB44B78EB 
        foreign key (ldap_connection_id) 
        references linshare_ldap_connection;

    alter table linshare_domain 
        add constraint FCC6A0CABCACCB78EB 
        foreign key (parameter_id) 
        references linshare_parameter;


    alter table linshare_mail_templates 
        add constraint FDD6A0CABCA44B78EB 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration;
        
    alter table linshare_mail_subjects 
        add constraint FDD6CCCABCA44789EB 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration;

    create index index_userlog_entry_target_mail on public.linshare_log_entry (target_mail);

    create index index_sharelog_entry_file_name on public.linshare_log_entry (file_name);

    create index index_log_entry_actor_first_name on public.linshare_log_entry (actor_firstname);

    create index index_log_entry_actor_mail on public.linshare_log_entry (actor_mail);

    create index index_log_entry_actor_domain on public.linshare_log_entry (actor_domain);

    create index index_log_entry_action_date on public.linshare_log_entry (action_date);

    create index index_log_entry_action on public.linshare_log_entry (log_action);

    create index index_sharelog_entry_target_mail on public.linshare_log_entry (target_mail);

    create index index_log_entry_actor_last_name on public.linshare_log_entry (actor_lastname);

    create index index_filelog_entry_file_name on public.linshare_log_entry (file_name);

    

    alter table linshare_user 
        add constraint FK56DFC97C6F5F97F1 
        foreign key (domain_id) 
        references linshare_domain;


    alter table linshare_parameter 
        add constraint FA56DAC97C6F5FA7F1 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration;



    create sequence hibernate_sequence;

    insert into linshare_version (id, description) values (9, 'LinShare version 0.9');
