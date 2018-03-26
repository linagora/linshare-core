/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.PublicKeyService;
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
		if (publicKey != null) {
			AbstractDomain domain = abstractDomainService.findById(publicKey.getDomainUuid());
			if (!permissionService.isAdminforThisDomain(authUser, domain)) {
				throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_FORBIDDEN,
						"You are not allowed to use this domain");
			}
		} else {
			logger.debug("Public key not found ", uuid);
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_NOT_FOUND, "Public key not found ");
		}
		return publicKey;
	}

	@Override
	public PublicKeyLs create(Account authUser, PublicKeyLs publicKey, AbstractDomain domain) throws BusinessException {
		Validate.notNull(domain, "domain must be set");
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_CAN_NOT_CREATE, "You are not allowed to use this domain");
		}
		PublicKeyLs pubKey = new PublicKeyLs(publicKey);
		pubKey = publicKeyMongoRepository.insert(pubKey);
		AuditLogEntryAdmin createLog = new PublicKeyAuditLogEntry(authUser, LogAction.CREATE, AuditLogEntryType.PUBLIC_KEY,
				pubKey.getUuid(), pubKey);
		logEntryService.insert(createLog);
		return pubKey;
	}

	@Override
	public List<PublicKeyLs> findAll(Account authUser, AbstractDomain domain) {
		Validate.notNull(domain);
		Validate.notNull(authUser);
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_FORBIDDEN,
					"You are not allowed to use this domain");
		}
		List<PublicKeyLs> publickeys = publicKeyMongoRepository.findByDomainUuid(domain.getUuid(),
				new Sort(Sort.Direction.DESC, CREATION_DATE));
		return publickeys;
	}

	@Override
	public PublicKeyLs delete(Account authUser, PublicKeyLs publicKeyLs) throws BusinessException {
		Validate.notNull(authUser);
		AbstractDomain domain = abstractDomainService.findById(publicKeyLs.getDomainUuid());
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
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
		if(!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.PUBLIC_KEY_FORBIDDEN,
					"You are not allowed to use this domain");
		}
		return auditLogEntryService.findAll(authUser, domain.getUuid(), actions);
	}
}
