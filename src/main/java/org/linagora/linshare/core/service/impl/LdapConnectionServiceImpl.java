/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.LdapConnectionRepository;
import org.linagora.linshare.core.service.RemoteServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapConnectionServiceImpl implements RemoteServerService<LdapConnection> {

	private static final Logger logger = LoggerFactory
			.getLogger(LdapConnectionServiceImpl.class);

	private final LdapConnectionRepository ldapConnectionRepository;

	private final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	private final AbstractDomainRepository abstractDomainRepository;

	public LdapConnectionServiceImpl(
			LdapConnectionRepository ldapConnectionRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository) {
		super();
		this.ldapConnectionRepository = ldapConnectionRepository;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
		this.abstractDomainRepository = abstractDomainRepository;
	}

	@Override
	public LdapConnection create(LdapConnection ldapConnection)
			throws BusinessException {
		Validate.notEmpty(ldapConnection.getLabel(),
				"ldap connection label must be set.");
		ldapConnection.setLabel(sanitize(ldapConnection.getLabel()));
		return ldapConnectionRepository.create(ldapConnection);
	}

	private String sanitize (String input) {
		return sanitizerInputHtmlBusinessService.strictClean(input);
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
		ldapConn.setLabel(sanitize(ldapConnection.getLabel()));
		ldapConn.setProviderUrl(ldapConnection.getProviderUrl());
		ldapConn.setSecurityAuth(ldapConnection.getSecurityAuth());
		ldapConn.setSecurityCredentials(ldapConnection.getSecurityCredentials());
		ldapConn.setSecurityPrincipal(ldapConnection.getSecurityPrincipal());
		return ldapConnectionRepository.update(ldapConn);
	}

	@Override
	public LdapConnection delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Ldap connection uuid must be set.");
		LdapConnection ldapConnection = find(uuid);
		if (ldapConnectionRepository.isUsed(ldapConnection)) {
			throw new BusinessException(
					BusinessErrorCode.LDAP_CONNECTION_STILL_IN_USE,
					"Cannot delete connection because still used by domains");
		}
		logger.debug("delete ldap connexion : " + uuid);
		ldapConnectionRepository.delete(ldapConnection);
		return ldapConnection;
	}

	@Override
	public boolean isUsed(String uuid) {
		Validate.notEmpty(uuid, "Ldap connection uuid must be set.");
		LdapConnection ldapConnection = find(uuid);
		return ldapConnectionRepository.isUsed(ldapConnection);
	}

	@Override
	public List<AbstractDomain> findAllDomainsByRemoteServer(LdapConnection ldapConnection) {
		Validate.notNull(ldapConnection, "ldapConnection must be set.");
		List<AbstractDomain> domains = abstractDomainRepository.findAllDomainsByRemoteServer(ldapConnection);
		return domains;
	}
}
