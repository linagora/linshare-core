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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.PublicKeyFormat;
import org.linagora.linshare.core.domain.constants.PublicKeyUsage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.PublicKeyService;
import org.linagora.linshare.core.utils.PemRsaKeyHelper;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.PublicKeyAuditLogEntry;
import org.linagora.linshare.mongo.repository.PublicKeyMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

public class PublicKeyServiceImpl implements PublicKeyService {

	private static final Logger logger = LoggerFactory.getLogger(PublicKeyServiceImpl.class);

	private static final String CREATION_DATE = "creationDate";

	protected final PublicKeyMongoRepository publicKeyMongoRepository;

	protected final LogEntryService logEntryService;

	protected final DomainPermissionBusinessService permissionService;

	protected final AbstractDomainService abstractDomainService;

	protected final AuditLogEntryService auditLogEntryService;

	public PublicKeyServiceImpl (PublicKeyMongoRepository publicKeyMongoRepository,
			LogEntryService logEntryService,
			DomainPermissionBusinessService permissionService,
			AbstractDomainService abstractDomainService,
			AuditLogEntryService auditLogEntryService) {
		super ();
		this.publicKeyMongoRepository = publicKeyMongoRepository;
		this.logEntryService = logEntryService;
		this.permissionService = permissionService;
		this.abstractDomainService = abstractDomainService;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public PublicKeyLs find(Account authUser, String uuid) {
		Validate.notNull(authUser);
		PublicKeyLs publicKey = publicKeyMongoRepository.findByUuid(uuid);
		if (publicKey == null) {
			logger.debug("Public key not found ", uuid);
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_NOT_FOUND, "Public key not found ");
		}
		AbstractDomain domain = abstractDomainService.findById(publicKey.getDomainUuid());
		if (!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_FORBIDDEN,
					"You are not allowed to use this domain");
		}
		return publicKey;
	}

	@Override
	public PublicKeyLs create(Account authUser, PublicKeyLs publicKey, AbstractDomain domain) throws BusinessException {
		Validate.notNull(publicKey, "publicKey must be set");
		Validate.notEmpty(publicKey.getDomainUuid(), "domain uuid must be set");
		Validate.notEmpty(publicKey.getIssuer(), "Issuer name must be set");
		Validate.notEmpty(publicKey.getPublicKey(), "Public key must be set");
		Validate.notNull(publicKey.getFormat(), "Format must be set");
		Validate.notNull(domain, "domain must be set");
		if (!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_CAN_NOT_CREATE,
					"You are not allowed to use this domain");
		}
		try {
			if (PublicKeyFormat.SSH.equals(publicKey.getFormat())) {
				PemRsaKeyHelper.loadSSHPublicKey(publicKey.getPublicKey());
			} else if (PublicKeyFormat.PEM.equals(publicKey.getFormat())) {
				PemRsaKeyHelper.loadPEMpublicKey(publicKey.getPublicKey());
			} else {
				throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_INVALID_FORMAT, "Unsupported format " + publicKey.getFormat());
			}
		} catch (IllegalArgumentException | InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			logger.debug(e.getMessage(), e);
			logger.error("Invalid public key ", publicKey.getIssuer());
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_INVALID_FORMAT, "Invalid public key.");
		}
		PublicKeyLs publicKeyDupli = findByIssuer(publicKey.getIssuer());
		if (publicKeyDupli != null) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_ALREADY_EXIST,
					"The public key you tried to insert already exist");
		}
		PublicKeyLs pubKey = new PublicKeyLs();
		pubKey.setCreationDate(new Date());
		pubKey.setDestroyed(false);
		pubKey.initUuid();
		pubKey.setFormat(publicKey.getFormat());
		pubKey.setIssuer(publicKey.getIssuer());
		pubKey.setPublicKey(publicKey.getPublicKey());
		pubKey.setUsage(PublicKeyUsage.JWT);
		pubKey.setDomainUuid(publicKey.getDomainUuid());
		pubKey = publicKeyMongoRepository.insert(pubKey);
		AuditLogEntryAdmin createLog = new PublicKeyAuditLogEntry(authUser, LogAction.CREATE,
				AuditLogEntryType.PUBLIC_KEY, pubKey.getUuid(), pubKey);
		logEntryService.insert(createLog);
		return pubKey;
	}

	@Override
	public List<PublicKeyLs> findAll(Account authUser, AbstractDomain domain) {
		Validate.notNull(domain);
		Validate.notNull(authUser);
		if (!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_FORBIDDEN,
					"You are not allowed to use this domain");
		}
		List<PublicKeyLs> publickeys = publicKeyMongoRepository.findByDomainUuid(domain.getUuid(),
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
		return publickeys;
	}

	@Override
	public PublicKeyLs delete(Account authUser, PublicKeyLs publicKeyLs) throws BusinessException {
		Validate.notNull(authUser);
		AbstractDomain domain = abstractDomainService.findById(publicKeyLs.getDomainUuid());
		if (!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_CAN_NOT_DELETE,
					"You are not allowed to use this domain");
		}
		AuditLogEntryAdmin deleteLog = new PublicKeyAuditLogEntry(authUser, LogAction.DELETE,
				AuditLogEntryType.PUBLIC_KEY, publicKeyLs.getUuid(), publicKeyLs);
		publicKeyMongoRepository.delete(publicKeyLs);
		logEntryService.insert(deleteLog);
		return publicKeyLs;
	}

	@Override
	public Set<AuditLogEntryAdmin> findAllAudit(User authUser, AbstractDomain domain, List<LogAction> actions) {
		Validate.notNull(domain);
		if(!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_FORBIDDEN,
					"You are not allowed to use this domain");
		}
		return auditLogEntryService.findAll(authUser, domain.getUuid(), actions);
	}

	@Override
	public PublicKeyLs findByIssuer(String issuer) throws BusinessException {
		Validate.notEmpty(issuer, "Issuer must be set");
		return publicKeyMongoRepository.findByIssuer(issuer);
	}
}
