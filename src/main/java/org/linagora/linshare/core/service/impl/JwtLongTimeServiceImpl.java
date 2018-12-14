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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.JwtLongTimeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.JwtToken;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.JwtLongTimeCreatedEmailContext;
import org.linagora.linshare.core.notifications.context.JwtLongTimeDeletedEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.JwtLongTimeResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.JwtLongTimeService;
import org.linagora.linshare.core.service.JwtService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.JwtLongTimeAuditLogEntry;

import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;

public class JwtLongTimeServiceImpl extends GenericServiceImpl<Account, PermanentToken> implements JwtLongTimeService {

	protected Clock clock = DefaultClock.INSTANCE;

	protected String issuer;

	protected NotifierService notifierService;

	protected MailBuildingService mailBuildingService;

	protected JwtService jwtService;

	protected AccountRepository<Account> accountRepository;

	protected JwtLongTimeBusinessService jwtLongTimeBusinessService;

	protected DomainPermissionBusinessService permissionService;

	protected AbstractDomainService abstractDomainService;

	protected LogEntryService logEntryService;

	protected AuditLogEntryService auditLogEntryService;

	protected FunctionalityReadOnlyService functionalityReadOnlyService;

	public JwtLongTimeServiceImpl(
			String issuer,
			JwtLongTimeBusinessService jwtLongTimeBusinessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			JwtService jwtService,
			JwtLongTimeResourceAccessControl rac,
			DomainPermissionBusinessService permissionSerivce,
			AbstractDomainService abstractDomainService,
			AccountRepository<Account> accountRepository,
			LogEntryService logEntryService,
			AuditLogEntryService auditLogEntryService,
			FunctionalityReadOnlyService functionalityReadOnlyService) {
		super(rac);
		this.issuer = issuer;
		this.jwtLongTimeBusinessService = jwtLongTimeBusinessService;
		this.jwtService = jwtService;
		this.permissionService = permissionSerivce;
		this.abstractDomainService = abstractDomainService;
		this.accountRepository = accountRepository;
		this.logEntryService = logEntryService;
		this.auditLogEntryService = auditLogEntryService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	public PermanentToken create(Account authUser, Account actor, PermanentToken permanentToken) throws BusinessException {
		Validate.notNull(actor, "actor must be set");
		final Date creationDate = clock.now();
		final String tokenUuid = UUID.randomUUID().toString();
		GenericLightEntity lightActor = new GenericLightEntity(actor.getLsUuid(), actor.getFullName());
		GenericLightEntity lightDomain = new GenericLightEntity(actor.getDomain());
		PermanentToken jwtLongTime = new PermanentToken(tokenUuid, creationDate, issuer, permanentToken.getLabel(),
				permanentToken.getDescription(), lightActor, actor.getMail(), lightDomain);
		String token = jwtService.generateToken(actor, tokenUuid, creationDate);
		checkCreatePermission(authUser, authUser, PermanentToken.class, BusinessErrorCode.JWT_PERMANENT_TOKEN_CAN_NOT_CREATE, jwtLongTime);
		jwtLongTimeBusinessService.create(jwtLongTime);
		AuditLogEntryUser createLog = new JwtLongTimeAuditLogEntry(authUser, actor, LogAction.CREATE,
				AuditLogEntryType.JWT_PERMANENT_TOKEN, jwtLongTime);
		logEntryService.insert(createLog);
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			EmailContext context = new JwtLongTimeCreatedEmailContext(authUser, actor, jwtLongTime);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail);
		}
		jwtLongTime.setJwtToken(new JwtToken(token));
		return jwtLongTime;
	}

	@Override
	public List<PermanentToken> findAll(Account authUser, Account actor) throws BusinessException {
		Validate.notNull(authUser);
		checkListPermission(authUser, actor, null, BusinessErrorCode.JWT_PERMANENT_TOKEN_FORBIDDEN, null);
		List<PermanentToken> tokens = jwtLongTimeBusinessService.findAll(actor);
		return tokens;
	}

	@Override
	public PermanentToken delete(Account authUser, Account actor, PermanentToken jwtLongTime) throws BusinessException {
		Validate.notNull(authUser, "AuthUser must be set.");
		Validate.notNull(actor, "Actor must be set");
		Validate.notNull(jwtLongTime, "Token must be set");
		PermanentToken permanentToken = find(authUser, authUser, jwtLongTime.getUuid());
		checkDeletePermission(authUser, actor, PermanentToken.class, BusinessErrorCode.JWT_PERMANENT_TOKEN_CAN_NOT_DELETE,
				permanentToken);
		AuditLogEntryUser createLog = new JwtLongTimeAuditLogEntry(authUser, actor, LogAction.DELETE,
				AuditLogEntryType.JWT_PERMANENT_TOKEN, permanentToken);
		jwtLongTimeBusinessService.delete(jwtLongTime);
		EmailContext context = new JwtLongTimeDeletedEmailContext(authUser, actor, permanentToken);
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail);
		}
		logEntryService.insert(createLog);
		return permanentToken;
	}

	@Override
	public PermanentToken find(Account authUser, Account actor, String uuid) throws BusinessException {
		Validate.notNull(authUser, "AuthUser must be set.");
		Validate.notNull(actor, "Actor must be set");
		PermanentToken found = jwtLongTimeBusinessService.find(uuid);
		if (found == null) {
			String message = "The requested token has not been found.";
			throw new BusinessException(BusinessErrorCode.JWT_PERMANENT_TOKEN_NOT_FOUND, message);
		}
		checkReadPermission(authUser, actor, PermanentToken.class, BusinessErrorCode.JWT_PERMANENT_TOKEN_CAN_NOT_READ, found);
		return found;
	}

	@Override
	public List<PermanentToken> findAllByDomain(Account authUser, AbstractDomain domain) throws BusinessException {
		Validate.notNull(domain, "domain must be set");
		Functionality functionality = functionalityReadOnlyService.getJwtLongTimeFunctionality(authUser.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.THREAD_FORBIDDEN, "Functionality forbidden.");
		}
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.JWT_PERMANENT_TOKEN_FORBIDDEN,
					"You are not allowed to use this domain");
		}
		return jwtLongTimeBusinessService.findAllByDomain(domain.getUuid());
	}

	@Override
	public PermanentToken update(User authUser, User actor, String uuid, PermanentToken permanentToken) {
		Validate.notEmpty(uuid, "uuid must be set");
		Validate.notNull(permanentToken, "permanentToken must be set");
		Validate.notNull(permanentToken.getLabel(), "Label must be set");
		PermanentToken found = jwtLongTimeBusinessService.find(uuid);
		checkUpdatePermission(authUser, actor, PermanentToken.class, BusinessErrorCode.JWT_PERMANENT_TOKEN_CAN_NOT_READ, found);
		found.setLabel(permanentToken.getLabel());
		found.setDescription(permanentToken.getDescription());
		return jwtLongTimeBusinessService.update(found);
	}

}
