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
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.business.service.MimePolicyBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.WelcomeMessagesBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.mongo.entities.logs.DomainAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;

import com.google.common.collect.Lists;

public class DomainServiceImpl extends GenericAdminServiceImpl implements DomainService {

	protected final AbstractDomainService abstractDomainService;
	protected final DomainBusinessService businessService;
	protected final DomainPolicyService domainPolicyService;
	protected final AbstractDomainRepository abstractDomainRepository;
	protected final AuditAdminMongoRepository auditMongoRepository;
	protected final DomainQuotaBusinessService domainQuotaBusinessService;
	protected final ContainerQuotaBusinessService containerQuotaBusinessService;

	protected final WelcomeMessagesBusinessService welcomeMessagesBusinessService;
	protected final MailConfigBusinessService mailConfigBusinessService;
	protected final MimePolicyBusinessService mimePolicyBusinessService;

	public DomainServiceImpl(
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainService abstractDomainService,
			DomainBusinessService businessService,
			DomainPolicyService domainPolicyService,
			AbstractDomainRepository abstractDomainRepository,
			AuditAdminMongoRepository auditMongoRepository,
			DomainQuotaBusinessService domainQuotaBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService,
			WelcomeMessagesBusinessService welcomeMessagesBusinessService,
			MailConfigBusinessService mailConfigBusinessService, MimePolicyBusinessService mimePolicyBusinessService) {
		super(sanitizerInputHtmlBusinessService);
		this.abstractDomainService = abstractDomainService;
		this.businessService = businessService;
		this.domainPolicyService = domainPolicyService;
		this.abstractDomainRepository = abstractDomainRepository;
		this.auditMongoRepository = auditMongoRepository;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.welcomeMessagesBusinessService = welcomeMessagesBusinessService;
		this.mailConfigBusinessService = mailConfigBusinessService;
		this.mimePolicyBusinessService = mimePolicyBusinessService;
	}
	@Override
	public AbstractDomain find(Account actor, String uuid) throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(uuid, "Domain uuid must be set.");
		// access control check ?
		AbstractDomain domain = businessService.findById(uuid);
		return domain;
	}

	@Override
	public List<AbstractDomain> findAll(Account actor) {
		if (actor.hasSuperAdminRole()) {
			return abstractDomainRepository.findAll();
		} else {
			List<AbstractDomain> domainList = Lists.newArrayList();
			domainList.add(actor.getDomain());
			domainList.addAll(
					abstractDomainRepository.getSubDomainsByDomain(
							actor.getDomain().getUuid()));
			return domainList;
		}
	}

	@Override
	public List<AbstractDomain> getSubDomainsByDomain(Account actor, String uuid) throws BusinessException {
		return businessService.getSubDomainsByDomain(uuid);
	}

	@Override
	public AbstractDomain create(Account actor, String name, String description, DomainType type, AbstractDomain parent)
			throws BusinessException {
		preChecks(actor);
		name = sanitizerInputHtmlBusinessService.strictClean(name);
		AbstractDomain domain = type.createDomain(name, parent);
		if (description == null) {
			domain.setDescription("");
		} else {
			domain.setDescription(sanitizerInputHtmlBusinessService.strictClean(description));
		}

		// Default domain policy 
		DomainPolicy policy = domainPolicyService.find(LinShareConstants.defaultDomainPolicyIdentifier);
		domain.setPolicy(policy);

		// Default welcome message
		WelcomeMessages wlcm = welcomeMessagesBusinessService.find(LinShareConstants.defaultWelcomeMessagesUuid);
		domain.setCurrentWelcomeMessages(wlcm);

		// Default mail configuration
		MailConfig mailConfig = mailConfigBusinessService.findByUuid(LinShareConstants.defaultMailConfigIdentifier);
		domain.setCurrentMailConfiguration(mailConfig);

		// Default mime policy
		MimePolicy mimePolicy = mimePolicyBusinessService.find(LinShareConstants.defaultMimePolicyIdentifier);
		domain.setMimePolicy(mimePolicy);

		domain = businessService.create(domain);
		createDomainQuotaAndContainerQuota(domain);
		DomainAuditLogEntry log = new DomainAuditLogEntry(actor, LogAction.CREATE, AuditLogEntryType.DOMAIN, domain);
		auditMongoRepository.insert(log);
		return domain;
	}

	private void createDomainQuotaAndContainerQuota(AbstractDomain domain) throws BusinessException {
		AbstractDomain parentDomain = domain.getParentDomain();
		boolean isSubdomain = false;
		if (domain.isSubDomain() || domain.isGuestDomain()) {
			isSubdomain = true;
		}
		// Quota for the new domain
		DomainQuota parentDomainQuota = domainQuotaBusinessService.find(parentDomain);
		DomainQuota domainQuota = new DomainQuota(parentDomainQuota, domain);
		if (isSubdomain) {
			domainQuota.setDefaultQuota(null);
			domainQuota.setDefaultQuotaOverride(null);
			domainQuota.setDefaultDomainShared(null);
			domainQuota.setDefaultDomainSharedOverride(null);
		}
		domainQuotaBusinessService.create(domainQuota);
		// Quota containers for the new domain.
		for (ContainerQuota parentContainerQuota : containerQuotaBusinessService.findAll(parentDomain)) {
			ContainerQuota cq = new ContainerQuota(domain, parentDomain, domainQuota, parentContainerQuota);
			if (isSubdomain) {
				cq.setDefaultQuota(null);
				cq.setDefaultQuotaOverride(null);
			}
			containerQuotaBusinessService.create(cq);
		}
	}
}
