SET storage_engine=INNODB;

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

    create table linshare_version (
        id bigint not null auto_increment,
        description varchar(255) not null unique,
        primary key (id)
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
        file_comment text,
        thmb_uuid varchar(255),
        timestamp blob,
        owner_id bigint,
        primary key (document_id)
    );

    create table linshare_domain_abstract (
        id bigint not null auto_increment,
        type integer not null,
        identifier varchar(255) not null unique,
        label varchar(255) not null,
        enable bit not null,
        template bit not null,
        description varchar(255),
        default_role integer,
        default_locale varchar(255),
        used_space bigint not null,
        user_provider_id bigint unique,
        domain_policy_id bigint not null,
        parent_id bigint,
        auth_show_order bigint,
        messages_configuration_id bigint,
        primary key (id)
    );

    create table linshare_domain_access_policy (
        id bigint not null auto_increment,
        primary key (id)
    );

    create table linshare_domain_access_rule (
        id bigint not null auto_increment,
        domain_access_rule_type integer not null,
        `regexp` varchar(255),
        domain_id bigint,
        domain_access_policy_id bigint not null,
        rule_index integer,
        primary key (id)
    );

    create table linshare_domain_pattern (
        domain_pattern_id bigint not null auto_increment,
        identifier varchar(255) not null unique,
        description varchar(255),
        get_user_command varchar(255),
        get_all_domain_users_command varchar(255),
        auth_command varchar(255),
        search_user_command varchar(255),
        user_mail varchar(255),
        user_firstname varchar(255),
        user_lastname varchar(255),
        primary key (domain_pattern_id)
    );

    create table linshare_domain_policy (
        id bigint not null auto_increment,
        identifier varchar(255) not null unique,
        description varchar(255),
        domain_access_policy_id bigint unique,
        primary key (id)
    );

    create table linshare_functionality (
        id bigint not null auto_increment,
        system bit not null,
        identifier varchar(255) not null,
        policy_activation_id bigint unique,
        policy_configuration_id bigint unique,
        domain_id bigint not null,
        primary key (id),
        unique (identifier, domain_id)
    );

    create table linshare_functionality_integer (
        functionality_id bigint not null,
        integer_value integer,
        primary key (functionality_id)
    );

    create table linshare_functionality_range_unit (
        functionality_id bigint not null,
        min integer,
        max integer,
        unit_min_id bigint unique,
        unit_max_id bigint unique,
        primary key (functionality_id)
    );

    create table linshare_functionality_string (
        functionality_id bigint not null,
        string_value varchar(255),
        primary key (functionality_id)
    );

    create table linshare_functionality_unit (
        functionality_id bigint not null,
        integer_value integer,
        unit_id bigint unique,
        primary key (functionality_id)
    );

    create table linshare_functionality_unit_boolean (
        functionality_id bigint not null,
        integer_value integer,
        boolean_value bit,
        unit_id bigint unique,
        primary key (functionality_id)
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

    create table linshare_ldap_connection (
        ldap_connection_id bigint not null auto_increment,
        identifier varchar(255) not null unique,
        provider_url varchar(255),
        security_auth varchar(255),
        security_principal varchar(255),
        security_credentials varchar(255),
        primary key (ldap_connection_id)
    );

    create table linshare_log_entry (
        id bigint not null auto_increment,
        entry_type varchar(255) not null,
        action_date datetime not null,
        actor_mail varchar(255) not null,
        actor_firstname varchar(255) not null,
        actor_lastname varchar(255) not null,
        actor_domain varchar(255),
        log_action varchar(255) not null,
        description varchar(255),
        file_name varchar(255),
        file_type varchar(255),
        file_size bigint,
        target_mail varchar(255),
        target_firstname varchar(255),
        target_lastname varchar(255),
        target_domain varchar(255),
        expiration_date datetime,
        primary key (id)
    );

    create table linshare_mail_subjects (
        messages_configuration_id bigint not null,
        subject_id integer not null,
        language_id integer not null,
        content text,
        primary key (messages_configuration_id, subject_id, language_id)
    );

    create table linshare_mail_templates (
        messages_configuration_id bigint not null,
        template_id integer not null,
        language_id integer not null,
        content_html text,
        content_txt text,
        primary key (messages_configuration_id, template_id, language_id)
    );

    create table linshare_messages_configuration (
        messages_configuration_id bigint not null auto_increment,
        primary key (messages_configuration_id)
    );

    create table linshare_policy (
        id bigint not null auto_increment,
        status bit not null,
        default_status bit not null,
        policy integer not null,
        system bit not null,
        primary key (id)
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
        `password` varchar(255),
        sender_id bigint,
        primary key (secured_url_id),
        unique (url_path, alea)
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
        domain_id bigint not null,
        expiry_time integer,
        time_unit_id integer,
        share_size integer,
        size_unit_id integer,
        rule_sort_order integer not null,
        primary key (domain_id, rule_sort_order)
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

    create table linshare_unit (
        id bigint not null auto_increment,
        unit_type integer not null,
        unit_value integer,
        primary key (id)
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
        domain_id bigint,
        expiry_date datetime,
        comment text,
        restricted bit,
        owner_id bigint,
        primary key (user_id)
    );

    create table linshare_user_provider_ldap (
        id bigint not null auto_increment,
        differential_key varchar(255),
        domain_pattern_id bigint not null,
        ldap_connection_id bigint not null,
        primary key (id)
    );

    create table linshare_welcome_texts (
        messages_configuration_id bigint not null,
        welcome_text text,
        language_id integer
    );

    alter table linshare_allowed_contact 
        add index index_allowed_contact_contact_id (contact_id), 
        add constraint index_allowed_contact_contact_id 
        foreign key (contact_id) 
        references linshare_user (user_id);

    alter table linshare_allowed_contact 
        add index index_allowed_contact_user_id (user_id), 
        add constraint index_allowed_contact_user_id 
        foreign key (user_id) 
        references linshare_user (user_id);


    create index index_document_name on linshare_document (name);

    create index index_document_expiration_date on linshare_document (expiration_date);

    alter table linshare_document 
        add index index_document_owner_id (owner_id), 
        add constraint index_document_owner_id 
        foreign key (owner_id) 
        references linshare_user (user_id);

    alter table linshare_domain_abstract 
        add index FK449BC2EC59E1E332 (domain_policy_id), 
        add constraint FK449BC2EC59E1E332 
        foreign key (domain_policy_id) 
        references linshare_domain_policy (id);

    alter table linshare_domain_abstract 
        add constraint user_provider_id
        foreign key (user_provider_id) 
        references linshare_user_provider_ldap (id);

    alter table linshare_domain_abstract 
        add index FK449BC2EC9083E725 (parent_id), 
        add constraint FK449BC2EC9083E725 
        foreign key (parent_id) 
        references linshare_domain_abstract (id);

    alter table linshare_domain_abstract 
        add index FK449BC2EC126FF4F2 (messages_configuration_id), 
        add constraint FK449BC2EC126FF4F2 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration (messages_configuration_id);

    alter table linshare_domain_access_rule 
        add index index_domain_access_rule_domain_id (domain_id), 
        add constraint index_domain_access_rule_domain_id 
        foreign key (domain_id) 
        references linshare_domain_abstract (id);

    alter table linshare_domain_access_rule 
        add constraint index_domain_access_rule_id 
        foreign key (domain_access_policy_id) 
        references linshare_domain_access_policy (id);


    alter table linshare_domain_policy 
        add constraint index_domain_access_policy_id
        foreign key (domain_access_policy_id) 
        references linshare_domain_access_policy (id);

    alter table linshare_functionality 
        add constraint FK7430C53A71796372 
        foreign key (policy_configuration_id) 
        references linshare_policy (id);

    alter table linshare_functionality 
        add index FK7430C53A3C036CCB (domain_id), 
        add constraint FK7430C53A3C036CCB 
        foreign key (domain_id) 
        references linshare_domain_abstract (id);

    alter table linshare_functionality 
        add constraint FK7430C53A58FE5398 
        foreign key (policy_activation_id) 
        references linshare_policy (id);

    alter table linshare_functionality_integer 
        add constraint FK8662133910439D2B 
        foreign key (functionality_id) 
        references linshare_functionality (id);

    alter table linshare_functionality_range_unit 
        add constraint FK55007F6B4B6B3004 
        foreign key (unit_max_id) 
        references linshare_unit (id);

    alter table linshare_functionality_range_unit 
        add constraint FK55007F6B10439D2B 
        foreign key (functionality_id) 
        references linshare_functionality (id);

    alter table linshare_functionality_range_unit 
        add constraint FK55007F6B4BD76056 
        foreign key (unit_min_id) 
        references linshare_unit (id);

    alter table linshare_functionality_string 
        add constraint FKB2A122B610439D2B 
        foreign key (functionality_id) 
        references linshare_functionality (id);

    alter table linshare_functionality_unit 
        add constraint FK3CED016910439D2B 
        foreign key (functionality_id) 
        references linshare_functionality (id);

    alter table linshare_functionality_unit 
        add constraint FK3CED0169F329E0C9 
        foreign key (unit_id) 
        references linshare_unit (id);

    alter table linshare_functionality_unit_boolean 
        add constraint FKA4D89B5210439D2B 
        foreign key (functionality_id) 
        references linshare_functionality (id);

    alter table linshare_functionality_unit_boolean 
        add constraint FKA4D89B52F329E0C9 
        foreign key (unit_id) 
        references linshare_unit (id);

    create index index_group_name on linshare_group (name);

    alter table linshare_group 
        add constraint FK833CCEEEFE8695A9 
        foreign key (group_user_id) 
        references linshare_user (user_id);

    create index index_group_members_user_id on linshare_group_members (user_id);

    alter table linshare_group_members 
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

    create index index_log_entry_actor_domain on linshare_log_entry (actor_domain);

    create index index_log_entry_actor_first_name on linshare_log_entry (actor_firstname);

    create index index_log_entry_actor_mail on linshare_log_entry (actor_mail);

    create index index_log_entry_action_date on linshare_log_entry (action_date);

    create index index_log_entry_action on linshare_log_entry (log_action);

    create index index_sharelog_entry_target_mail on linshare_log_entry (target_mail);

    create index index_log_entry_actor_last_name on linshare_log_entry (actor_lastname);

    create index index_filelog_entry_file_name on linshare_log_entry (file_name);

    alter table linshare_mail_subjects 
        add index FK1C97F3BE126FF4F2 (messages_configuration_id), 
        add constraint FK1C97F3BE126FF4F2 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration (messages_configuration_id);

    alter table linshare_mail_templates 
        add index index_messages_configuration_id (messages_configuration_id), 
        add constraint index_messages_configuration_id 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration (messages_configuration_id);

    alter table linshare_recipient_favourite 
        add index index_favourite_recipient_id (user_id), 
        add constraint index_favourite_recipient_id 
        foreign key (user_id) 
        references linshare_user (user_id);

    alter table linshare_secured_url 
        add index index_securedurl_sender_id (sender_id), 
        add constraint index_securedurl_sender_id
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

    create index index_secured_url_contact_id on linshare_secured_url_recipients (contact_id);

    alter table linshare_secured_url_recipients 
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
        add constraint FK83E1284EB927C5E9 
        foreign key (document_id) 
        references linshare_document (document_id);

    alter table linshare_share 
        add constraint FK83E1284E62928BF 
        foreign key (sender_id) 
        references linshare_user (user_id);

    alter table linshare_share 
        add constraint FK83E1284E4F9C165B 
        foreign key (recipient_id) 
        references linshare_user (user_id);

    create index index_share_expiry_rule_id on linshare_share_expiry_rules (domain_id);

    alter table linshare_share_expiry_rules 
        add constraint FKFDA1673C3C036CCB 
        foreign key (domain_id) 
        references linshare_domain_abstract (id);

    create index index_signature_signer_id on linshare_signature (signer_id);

    alter table linshare_signature 
        add constraint FK81C9A1A74472B3AA 
        foreign key (signer_id) 
        references linshare_user (user_id);

    alter table linshare_signature 
        add index FK81C9A1A7C0BBD6F (document_id_fk), 
        add constraint FK81C9A1A7C0BBD6F 
        foreign key (document_id_fk) 
        references linshare_document (document_id);

    create index index_user_last_name on linshare_user (last_name);

    create index index_user_first_name on linshare_user (first_name);

    alter table linshare_user 
        add index FK56D6C97C3C036CCB (domain_id), 
        add constraint FK56D6C97C3C036CCB 
        foreign key (domain_id) 
        references linshare_domain_abstract (id);

    alter table linshare_user 
        add index FK56D6C97C675F9781 (owner_id), 
        add constraint FK56D6C97C675F9781 
        foreign key (owner_id) 
        references linshare_user (user_id);

    alter table linshare_user_provider_ldap 
        add index index_linshare_user_provider_ldap_connection_id (ldap_connection_id), 
        add constraint index_linshare_user_provider_ldap_connection_id 
        foreign key (ldap_connection_id) 
        references linshare_ldap_connection (ldap_connection_id);

    alter table linshare_user_provider_ldap 
        add index FK409CAFB2372A0802 (domain_pattern_id), 
        add constraint FK409CAFB2372A0802 
        foreign key (domain_pattern_id) 
        references linshare_domain_pattern (domain_pattern_id);

    alter table linshare_welcome_texts 
        add index index_messages_configuration_welcome_id (messages_configuration_id), 
        add constraint index_messages_configuration_welcome_id
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration (messages_configuration_id);
