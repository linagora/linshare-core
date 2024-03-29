/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.io.IOUtils;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.PublicKeyFormat;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.PublicKeyFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.PublicKeyService;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;

import com.google.common.base.Strings;

public class PublicKeyFacadeImpl extends AdminGenericFacadeImpl implements PublicKeyFacade {

	protected PublicKeyService publicKeyService;

	protected final AbstractDomainService abstractDomainService;

	public PublicKeyFacadeImpl(final AccountService accountService,
			PublicKeyService publicKeyService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.publicKeyService = publicKeyService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public PublicKeyLs find(String uuid) throws BusinessException{
		Validate.notEmpty(uuid, "Public key uuid must be set");
		User authUser = checkAuthentication(Role.SUPERADMIN);
		PublicKeyLs publicKeyLs = publicKeyService.find(authUser, uuid);
		return publicKeyLs;
	}

	@Override
	public PublicKeyLs create(PublicKeyLs publicKey) throws BusinessException{
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(publicKey, "publicKey must be set");
		Validate.notEmpty(publicKey.getDomainUuid(), "domain uuid must be set");
		Validate.notEmpty(publicKey.getIssuer(), "Issuer name must be set");
		Validate.notEmpty(publicKey.getPublicKey(), "Public key must be set");
		Validate.notNull(publicKey.getFormat(), "Format must be set");
		AbstractDomain domain = abstractDomainService.findById(publicKey.getDomainUuid());
		return publicKeyService.create(authUser, publicKey, domain);
	}

	@Override
	public PublicKeyLs create(InputStream publicKeyInputS, String domainUuid, String issuer, PublicKeyFormat format)
			throws BusinessException {
		Validate.notNull(publicKeyInputS, "File must be set");
		PublicKeyLs publicKey = null;
		try {
			String pubKey = IOUtils.toString(publicKeyInputS);
			publicKey = new PublicKeyLs(pubKey, domainUuid, issuer, format);
		} catch (IOException e) {
			throw new BusinessException(BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE, "Invalid file content", e);
		}
		return create(publicKey);
	}

	@Override
	public List<PublicKeyLs> findAll(String domainUuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = authUser.getDomain();
		if (domainUuid != null) {
			domain = abstractDomainService.findById(domainUuid);
		}
		return publicKeyService.findAll(authUser, domain);
	}

	@Override
	public PublicKeyLs delete(String uuid, PublicKeyLs publicKeyLs) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		if (!Strings.isNullOrEmpty(uuid)) {
			publicKeyLs = publicKeyService.find(authUser, uuid);
		} else {
			Validate.notNull(publicKeyLs, "publicKey object must be set");
			Validate.notEmpty(publicKeyLs.getUuid(), "publicKey uuid must be set");
			publicKeyLs = publicKeyService.find(authUser, publicKeyLs.getUuid());
		}
		return publicKeyService.delete(authUser, publicKeyLs);
	}

	@Override
	public Set<AuditLogEntryAdmin> findAll(String domainUuid, List<LogAction> actions) {
		Validate.notEmpty(domainUuid, "Domain uuid must be set");
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		return publicKeyService.findAllAudit(authUser, domain, actions);
	}
}
