/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.RemoteServerFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractServerDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPServerDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LdapConnectionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RemoteServerFacadeImpl extends AdminGenericFacadeImpl implements RemoteServerFacade {

	private final Map<ServerType, LdapConnectionService> remoteServices;

	public RemoteServerFacadeImpl(
			final AccountService accountService,
			final Map<ServerType, LdapConnectionService> remoteServices) {
		super(accountService);
		this.remoteServices = remoteServices;
	}

	private LdapConnectionService getService(ServerType type) {
		Validate.notNull(type, "ServerType type must be set");
		LdapConnectionService remoteService = remoteServices.get(type);
		Validate.notNull(remoteService, "Can not find a service that handle your serverType: " + type);
		return remoteService;
	}

	@Override
	public List<AbstractServerDto> findAll() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		return getService(ServerType.LDAP)
				.findAll()
				.stream()
				.map(LDAPServerDto::from)
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public AbstractServerDto find(String uuid) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "Server uuid must be set.");
		LdapConnectionService remoteServer = getService(ServerType.LDAP);
		LdapConnection ldapConnection = remoteServer.find(uuid);
		return LDAPServerDto.from(ldapConnection);
	}


	@Override
	public AbstractServerDto create(AbstractServerDto serverDto) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(serverDto, "Server to create must be set");
		ServerType serverType = serverDto.getServerType();
		switch (serverType) {
			case LDAP:
				LDAPServerDto ldapServerDto = (LDAPServerDto) serverDto;
				LdapConnectionService remoteServer = getService(ServerType.LDAP);
				return LDAPServerDto.from(remoteServer.create(ldapServerDto.toLdapServerObject(Optional.empty())));
			case TWAKE:
		}
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	@Override
	public AbstractServerDto update(String uuid, AbstractServerDto serverDto) {
		checkAuthentication(Role.SUPERADMIN);
		String finalUuid = uuid;
		if (Strings.isNullOrEmpty(finalUuid)) {
			finalUuid = serverDto.getUuid();
		}
		Validate.notEmpty(finalUuid, "Server's uuid must be set");
		ServerType serverType = serverDto.getServerType();
		switch (serverType) {
			case LDAP:
				LDAPServerDto ldapServerDto = (LDAPServerDto) serverDto;
				LdapConnectionService remoteServer = getService(ServerType.LDAP);
				LdapConnection ldapConnection = remoteServer.update(ldapServerDto.toLdapServerObject(Optional.of(finalUuid)));
				LDAPServerDto from = LDAPServerDto.from(ldapConnection);
				return from;
			case TWAKE:
		}
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	@Override
	public AbstractServerDto delete(String uuid, AbstractServerDto serverDto) {
		checkAuthentication(Role.SUPERADMIN);
		String finalUuid = uuid;
		if (Strings.isNullOrEmpty(finalUuid)) {
			finalUuid = serverDto.getUuid();
		}
		Validate.notEmpty(finalUuid, "Server's uuid must be set");
		LdapConnectionService remoteServer = getService(ServerType.LDAP);
		LdapConnection conn = remoteServer.delete(finalUuid);
		return LDAPServerDto.from(conn);
	}

	@Override
	public List<DomainDto> findAllDomainsByLdapServer(String uuid) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "Ldap server's uuid must be set");
		LdapConnectionService remoteServer = getService(ServerType.LDAP);
		LdapConnection ldapConnection = remoteServer.find(uuid);
		List<AbstractDomain> domains = remoteServer.findAllDomainsByRemoteServer(ldapConnection);
		return ImmutableList.copyOf(Lists.transform(domains, DomainDto.toDto()));
	}
}
