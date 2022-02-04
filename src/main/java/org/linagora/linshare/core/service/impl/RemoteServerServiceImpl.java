/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import org.linagora.linshare.core.domain.entities.RemoteServer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.RemoteServerRepository;
import org.linagora.linshare.core.service.RemoteServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RemoteServerServiceImpl<T extends RemoteServer> implements RemoteServerService<T> {

	private static final Logger logger = LoggerFactory
			.getLogger(RemoteServerServiceImpl.class);

	private final RemoteServerRepository<T> remoteServerRepository;

	private final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	protected final AbstractDomainRepository abstractDomainRepository;

	public RemoteServerServiceImpl(
			RemoteServerRepository<T> remoteServerRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository) {
		super();
		this.remoteServerRepository = remoteServerRepository;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
		this.abstractDomainRepository = abstractDomainRepository;
	}

	@Override
	public T create(T remoteServer)
			throws BusinessException {
		Validate.notEmpty(remoteServer.getLabel(),
				"Remote server label must be set.");
		remoteServer.setLabel(sanitize(remoteServer.getLabel()));
		return remoteServerRepository.create(remoteServer);
	}

	private String sanitize (String input) {
		return sanitizerInputHtmlBusinessService.strictClean(input);
	}

	@Override
	public List<T> findAll() throws BusinessException {
		return remoteServerRepository.findAll();
	}

	@Override
	public T find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Remote server uuid must be set.");
		T remoteServer = remoteServerRepository.findByUuid(uuid);
		if (remoteServer == null)
			throw new BusinessException(
					BusinessErrorCode.REMOTE_SERVER_NOT_FOUND,
					"Can not found remote server connection with uuid: " + uuid + ".");
		return remoteServer;
	}

	@Override
	public T update(T remoteServer)
			throws BusinessException {
		Validate.notNull(remoteServer, "Ldap connection must be set.");
		Validate.notEmpty(remoteServer.getUuid(),
				"Ldap connection uuid must be set.");
		T updateRemoteServer = find(remoteServer.getUuid());
		updateRemoteServer.setLabel(sanitize(remoteServer.getLabel()));
		updateRemoteServer.setProviderUrl(remoteServer.getProviderUrl());
		updateFields(remoteServer, updateRemoteServer);
		return remoteServerRepository.update(updateRemoteServer);
	}

	protected abstract void updateFields(T remoteServer, T updateRemoteServer);

	@Override
	public T delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Ldap connection uuid must be set.");
		T remoteServer = find(uuid);
		if (remoteServerRepository.isUsed(remoteServer)) {
			throw new BusinessException(
					BusinessErrorCode.REMOTE_SERVER_STILL_IN_USE,
					"Cannot delete connection because still used by domains");
		}
		logger.debug("delete remote server : " + uuid);
		remoteServerRepository.delete(remoteServer);
		return remoteServer;
	}

	@Override
	public boolean isUsed(String uuid) {
		Validate.notEmpty(uuid, "Remote server uuid must be set.");
		T remoteServer = find(uuid);
		return remoteServerRepository.isUsed(remoteServer);
	}
}
