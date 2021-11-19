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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.TwakeConnection;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.RemoteServerFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractServerDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPServerDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.TwakeServerDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.RemoteServerService;
import org.linagora.linshare.core.service.impl.CommonRemoteServerServiceImpl;

import com.google.common.base.Strings;

public class RemoteServerFacadeImpl extends AdminGenericFacadeImpl implements RemoteServerFacade {

	private final CommonRemoteServerServiceImpl commonRemoteServerService;
	private final Map<ServerType, RemoteServerService<?>> remoteServices;

	public RemoteServerFacadeImpl(
			final AccountService accountService,
			final CommonRemoteServerServiceImpl commonRemoteServerService,
			final Map<ServerType, RemoteServerService<?>> remoteServices) {
		super(accountService);
		this.commonRemoteServerService = commonRemoteServerService;
		this.remoteServices = remoteServices;
	}

	private RemoteServerService<?> getService(ServerType type) {
		Validate.notNull(type, "ServerType type must be set");
		RemoteServerService<?> remoteService = remoteServices.get(type);
		Validate.notNull(remoteService, "Can not find a service that handle your serverType: " + type);
		return remoteService;
	}

	@Override
	public List<AbstractServerDto> findAll() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		return Stream.concat(findAllLdapRemoteServers(), findAllTwakeRemoteServers())
			.sorted(Comparator.comparing(AbstractServerDto::getCreationDate))
			.collect(Collectors.toUnmodifiableList());
	}

	private Stream<AbstractServerDto> findAllLdapRemoteServers() {
		return getService(ServerType.LDAP)
			.findAll()
			.stream()
			.map(LdapConnection.class::cast)
			.map(LDAPServerDto::from);
	}

	private Stream<AbstractServerDto> findAllTwakeRemoteServers() {
		return getService(ServerType.TWAKE)
			.findAll()
			.stream()
			.map(TwakeConnection.class::cast)
			.map(TwakeServerDto::from);
	}

	@Override
	public AbstractServerDto find(String uuid) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "Server uuid must be set.");
		Optional<ServerType> serverType = commonRemoteServerService.findServerTypeByUuid(uuid);
		if (!serverType.isPresent()) {
			throw new BusinessException(
				BusinessErrorCode.REMOTE_SERVER_NOT_FOUND,
				"Can not found remote server connection with uuid: " + uuid + ".");
		}
		switch (serverType.get()) {
			case LDAP:
				return findLdapServer(uuid);
			case TWAKE:
				return findTwakeServer(uuid);
		}
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	private AbstractServerDto findLdapServer(String uuid) {
		RemoteServerService<LdapConnection> remoteServer = (RemoteServerService<LdapConnection>) getService(ServerType.LDAP);
		return LDAPServerDto.from(remoteServer.find(uuid));
	}

	private TwakeServerDto findTwakeServer(String uuid) {
		RemoteServerService<TwakeConnection> remoteServer = (RemoteServerService<TwakeConnection>) getService(ServerType.TWAKE);
		return TwakeServerDto.from(remoteServer.find(uuid));
	}

	@Override
	public AbstractServerDto create(AbstractServerDto serverDto) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(serverDto, "Server to create must be set");
		ServerType serverType = serverDto.getServerType();
		switch (serverType) {
			case LDAP:
				return createLdapRemoteServer((LDAPServerDto) serverDto);
			case TWAKE:
				return createTwakeRemoteServer((TwakeServerDto) serverDto);
		}
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	private LDAPServerDto createLdapRemoteServer(LDAPServerDto ldapServerDto) {
		RemoteServerService<LdapConnection> remoteServer = (RemoteServerService<LdapConnection>) getService(ServerType.LDAP);
		return LDAPServerDto.from(remoteServer.create(ldapServerDto.toLdapServerObject(Optional.empty())));
	}

	private TwakeServerDto createTwakeRemoteServer(TwakeServerDto twakeServerDto) {
		RemoteServerService<TwakeConnection> remoteServer = (RemoteServerService<TwakeConnection>) getService(ServerType.TWAKE);
		return TwakeServerDto.from(remoteServer.create(twakeServerDto.toTwakeServerObject(Optional.empty())));
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
				return updateLdapServer((LDAPServerDto) serverDto, finalUuid);
			case TWAKE:
				return updateTwakeServer((TwakeServerDto) serverDto, finalUuid);
		}
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	private LDAPServerDto updateLdapServer(LDAPServerDto ldapServerDto, String uuid) {
		RemoteServerService<LdapConnection> remoteServer = (RemoteServerService<LdapConnection>) getService(ServerType.LDAP);
		LdapConnection ldapConnection = remoteServer.update(ldapServerDto.toLdapServerObject(Optional.of(uuid)));
		return LDAPServerDto.from(ldapConnection);
	}

	private TwakeServerDto updateTwakeServer(TwakeServerDto twakeServerDto, String uuid) {
		RemoteServerService<TwakeConnection> remoteServer = (RemoteServerService<TwakeConnection>) getService(ServerType.TWAKE);
		TwakeConnection twakeConnection = remoteServer.update(twakeServerDto.toTwakeServerObject(Optional.of(uuid)));
		return TwakeServerDto.from(twakeConnection);
	}

	@Override
	public AbstractServerDto delete(String uuid, AbstractServerDto serverDto) {
		checkAuthentication(Role.SUPERADMIN);
		String finalUuid = uuid;
		if (Strings.isNullOrEmpty(finalUuid)) {
			finalUuid = serverDto.getUuid();
		}
		Validate.notEmpty(finalUuid, "Server's uuid must be set");
		Optional<ServerType> serverType = commonRemoteServerService.findServerTypeByUuid(finalUuid);
		if (!serverType.isPresent()) {
			throw new BusinessException(
				BusinessErrorCode.REMOTE_SERVER_NOT_FOUND,
				"Can not found remote server connection with uuid: " + finalUuid + ".");
		}
		switch (serverType.get()) {
			case LDAP:
				return deleteLdapServer(finalUuid);
			case TWAKE:
				return deleteTwakeServer(finalUuid);
		}
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	private LDAPServerDto deleteLdapServer(String uuid) {
		RemoteServerService<LdapConnection> remoteServer = (RemoteServerService<LdapConnection>) getService(ServerType.LDAP);
		return LDAPServerDto.from(remoteServer.delete(uuid));
	}

	private TwakeServerDto deleteTwakeServer(String uuid) {
		RemoteServerService<TwakeConnection> remoteServer = (RemoteServerService<TwakeConnection>) getService(ServerType.TWAKE);
		return TwakeServerDto.from(remoteServer.delete(uuid));
	}

	@Override
	public List<DomainDto> findAllDomainsByRemoteServer(String uuid) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "Server's uuid must be set");
		Optional<ServerType> serverType = commonRemoteServerService.findServerTypeByUuid(uuid);
		if (!serverType.isPresent()) {
			throw new BusinessException(
				BusinessErrorCode.REMOTE_SERVER_NOT_FOUND,
				"Can not found remote server connection with uuid: " + uuid + ".");
		}
		switch (serverType.get()) {
			case LDAP:
				return findAllDomainsByLdapServer(uuid);
			case TWAKE:
				return findAllDomainsByTwakeServer(uuid);
		}
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented");
	}

	private List<DomainDto> findAllDomainsByLdapServer(String uuid) {
		RemoteServerService<LdapConnection> remoteServer = (RemoteServerService<LdapConnection>) getService(ServerType.LDAP);
		return remoteServer.findAllDomainsByRemoteServer(remoteServer.find(uuid))
			.stream()
			.map(DomainDto::getLight)
			.collect(Collectors.toUnmodifiableList());
	}

	private List<DomainDto> findAllDomainsByTwakeServer(String uuid) {
		RemoteServerService<TwakeConnection> remoteServer = (RemoteServerService<TwakeConnection>) getService(ServerType.TWAKE);
		return remoteServer.findAllDomainsByRemoteServer(remoteServer.find(uuid))
			.stream()
			.map(DomainDto::getLight)
			.collect(Collectors.toUnmodifiableList());
	}
}
