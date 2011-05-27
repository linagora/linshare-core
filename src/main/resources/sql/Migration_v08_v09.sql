create table linshare_messages_configuration (
    messages_configuration_id int8 not null,
    primary key (messages_configuration_id)
);

insert into linshare_messages_configuration (messages_configuration_id) VALUES (1);

alter table linshare_parameter add column closed_domain bool default false;
alter table linshare_parameter add column restricted_domain bool default false;
alter table linshare_parameter add column domain_with_guests bool default true;
alter table linshare_parameter add column guest_can_create_other bool default true;
alter table linshare_parameter add column messages_configuration_id int8 not null default 1;


alter table linshare_mail_templates drop constraint FDD6A0CABCA44B78EB;
alter table linshare_mail_subjects drop constraint FDD6CCCABCA44789EB;
alter table linshare_welcome_texts drop constraint FK36A0C738A44B78EB;

alter table linshare_mail_templates rename column parameter_id to messages_configuration_id;
alter table linshare_welcome_texts rename column parameter_id to messages_configuration_id;
alter table linshare_mail_subjects rename column parameter_id to messages_configuration_id;

alter table linshare_welcome_texts add constraint FK36A0C738A44B78EB 
        foreign key (messages_configuration_id) references linshare_messages_configuration;

alter table linshare_mail_templates add constraint FDD6A0CABCA44B78EB 
        foreign key (messages_configuration_id) references linshare_messages_configuration;

alter table linshare_mail_subjects add constraint FDD6CCCABCA44789EB 
        foreign key (messages_configuration_id) references linshare_messages_configuration;


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

    alter table linshare_parameter 
        add constraint FA56DAC97C6F5FA7F1 
        foreign key (messages_configuration_id) 
        references linshare_messages_configuration;

alter table linshare_user add column domain_id int8 default 1;

--------
--- here you have to insert your domain configuration here
--- and the super admin account
--- generated with update/linshare8_to_linshare9.sh
--------

    alter table linshare_user 
        add constraint FK56DFC97C6F5F97F1 
        foreign key (domain_id) 
        references linshare_domain;

    insert into linshare_version (id, description) values (9, 'LinShare version 0.9');


--- Since 0.9.1

    alter table linshare_log_entry add column actor_domain varchar(255);
    create index index_log_entry_actor_domain on linshare_log_entry (actor_domain);

