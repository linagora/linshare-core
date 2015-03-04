package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.LdapConnectionRepository;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapConnectionServiceImpl implements LdapConnectionService {

	private static final Logger logger = LoggerFactory
			.getLogger(LdapConnectionServiceImpl.class);

	private final LdapConnectionRepository ldapConnectionRepository;

	public LdapConnectionServiceImpl(
			LdapConnectionRepository ldapConnectionRepository) {
		super();
		this.ldapConnectionRepository = ldapConnectionRepository;
	}

	@Override
	public LdapConnection create(LdapConnection ldapConnection)
			throws BusinessException {
		Validate.notEmpty(ldapConnection.getLabel(),
				"ldap connection label must be set.");
		return ldapConnectionRepository.create(ldapConnection);
	}

	@Override
	public List<LdapConnection> findAll() throws BusinessException {
		return ldapConnectionRepository.findAll();
	}

	@Override
	public LdapConnection find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Ldap connection uuid must be set.");
		LdapConnection connection = ldapConnectionRepository.findByUuid(uuid);
		if (connection == null)
			throw new BusinessException(
					BusinessErrorCode.LDAP_CONNECTION_NOT_FOUND,
					"Can not found ldap connection with uuid: " + uuid + ".");
		return connection;
	}

	@Override
	public LdapConnection update(LdapConnection ldapConnection)
			throws BusinessException {
		Validate.notNull(ldapConnection, "Ldap connection must be set.");
		Validate.notEmpty(ldapConnection.getUuid(),
				"Ldap connection uuid must be set.");
		LdapConnection ldapConn = find(ldapConnection.getUuid());
		ldapConn.setProviderUrl(ldapConnection.getProviderUrl());
		ldapConn.setSecurityAuth(ldapConnection.getSecurityAuth());
		ldapConn.setSecurityCredentials(ldapConnection.getSecurityCredentials());
		ldapConn.setSecurityPrincipal(ldapConnection.getSecurityPrincipal());
		return ldapConnectionRepository.update(ldapConn);
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Ldap connection uuid must be set.");
		LdapConnection ldapConnection = find(uuid);
		if (ldapConnectionRepository.isUsed(ldapConnection)) {
			throw new BusinessException(
					BusinessErrorCode.LDAP_CONNECTION_STILL_IN_USE,
					"Cannot delete connection because still used by domains");
		}
		LdapConnection conn = find(uuid);
		if (conn == null) {
			logger.error("Ldap connexion not found: " + uuid);
		} else {
			logger.debug("delete ldap connexion : " + uuid);
			ldapConnectionRepository.delete(conn);
		}
	}

	@Override
	public boolean isUsed(String uuid) {
		Validate.notEmpty(uuid, "Ldap connection uuid must be set.");
		LdapConnection ldapConnection = find(uuid);
		return ldapConnectionRepository.isUsed(ldapConnection);
	}
}
