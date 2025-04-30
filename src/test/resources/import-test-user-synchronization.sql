-- Third user provider used on OidcDomain
INSERT INTO user_provider(id, uuid, provider_type, ldapBaseDn, creation_date, modification_date)
VALUES
    (52, '6668197e-301e-11ec-8d3d-0242ac130003', 'OIDC_PROVIDER', 'ou=OidcDomain,dc=linshare,dc=org', now(), now()),
    (53, '6668197e-301e-11ec-8d3d-0242ac130053', 'OIDC_PROVIDER', null, now(), now());

INSERT INTO domain_abstract(
    id, type, uuid, label,
    enable, template, description, default_role,
    default_locale, purge_step, user_provider_id,
    domain_policy_id, parent_id, auth_show_order, mailconfig_id,
    welcome_messages_id, creation_date, modification_date)
VALUES
-- Domaine Oidc with baseDn (ID 6)
(6, 2, 'OidcDomain', 'OidcDomain',
 true, false, 'Domain with baseDn', 0,
 'en', 'IN_USE', 52,
 1, 2, 5, null,
 1, now(), now());

-- Domaine Oidc without baseDn (ID 7)
INSERT INTO domain_abstract(
    id, type, uuid, label,
    enable, template, description, default_role,
    default_locale, purge_step, user_provider_id,
    domain_policy_id, parent_id, auth_show_order, mailconfig_id,
    welcome_messages_id, creation_date, modification_date)
VALUES
    (7, 2, 'OidcDomain-NoBaseDn', 'OidcDomain-NoBaseDn',
     true, false, 'Domain without baseDn', 0,
     'en', 'IN_USE', 53,
     1, 2, 6, null,
     1, now(), now());

SET @oidc_domain_id = 6;

-- LinShare Users
INSERT INTO account(
	id, mail, account_type, ls_uuid,
	creation_date, modification_date, role_id,
	mail_locale, external_mail_locale,
	cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name,
	last_name, can_upload, comment, restricted,
	CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
VALUES
    -- Amy Wolsh (UUID unique)
    (56, 'amy.wolsh@linshare.org', 2, 'aebe1b64-0000-11e5-9fa8-080027b8254x',
     now(), now(), 0,
     'en', 'en', 'en', true, null,
     0, 5 , 'IN_USE', 'Amy',
     'Wolsh', true, '', false,
     true, false, 0),

    -- oidc.without_baseDn (UUID unique)
    (57, 'oidc.without_baseDn@linshare.org', 2, 'aebe1b64-0000-11e5-9fa8-080027b82zzz',
     now(), now(), 0,
     'en', 'en', 'en', true, null,
     0, 7 , 'IN_USE', 'NoBaseDn',
     'without_baseDn', true, '', false,
     true, false, 0),

	-- standard dude topDomain2
	(53, 'standard.dude@linshare.org', 2, 'aebe1b64-0000-11e5-9fa8-080027b8254j',
	now(), now(), 0,
	'en', 'en', 'en', true, null,
	0, 5 , 'IN_USE', 'Standard',
	'Dude', true, '', false,
	true, false, 0),
	-- oidc dude OidcDomain
	(51, 'oidc.dude@linshare.org', 2, 'aaaa1b64-39c0-11e5-9fa8-080027b8254j',
	now(), now(), 0,
	'en', 'en', 'en', true, null,
	0, 6, 'IN_USE', 'Oidc',
	'Dude', true, '', false,
	true, false, 0),
	-- other dude OidcDomain
	(52, 'other.dude@linshare.org', 2, 'bbbb1b64-39c0-11e5-9fa8-080027b8254j',
	now(), now(), 0,
	'en', 'en', 'en', true, null,
	0, @oidc_domain_id, 'IN_USE', 'Other',
	'Dude', true, '', false,
	true, false, 0);

