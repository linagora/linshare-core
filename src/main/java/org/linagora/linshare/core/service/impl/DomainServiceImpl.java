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
import org.linagora.linshare.core.business.service.DomainAccessPolicyBusinessService;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.business.service.MailContentBusinessService;
import org.linagora.linshare.core.business.service.MailFooterBusinessService;
import org.linagora.linshare.core.business.service.MailLayoutBusinessService;
import org.linagora.linshare.core.business.service.MimePolicyBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.WelcomeMessagesBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.core.service.DriveProviderService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.GroupProviderService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.mongo.entities.logs.DomainAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;

import com.google.common.collect.Lists;

public class DomainServiceImpl extends DomainServiceCommonImpl implements DomainService {

	protected final AbstractDomainService abstractDomainService;
	protected final DomainBusinessService businessService;
	protected final DomainPolicyService domainPolicyService;
	protected final AbstractDomainRepository abstractDomainRepository;
	protected final AuditAdminMongoRepository auditMongoRepository;

	protected final WelcomeMessagesBusinessService welcomeMessagesBusinessService;
	protected final MailConfigBusinessService mailConfigBusinessService;
	protected final MimePolicyBusinessService mimePolicyBusinessService;
	private final FunctionalityService functionalityService;
	private final DomainAccessPolicyBusinessService domainAccessPolicyBusinessService;
	private final MailLayoutBusinessService mailLayoutBusinessService;
	private final MailFooterBusinessService mailFooterBusinessService;
	private final MailContentBusinessService mailContentBusinessService;
	private final MimeTypeService mimeTypeService;
	private final UserProviderService userProviderService;
	private final GroupProviderService groupProviderService;
	private final DriveProviderService driveProviderService;

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
			MailConfigBusinessService mailConfigBusinessService,
			MimePolicyBusinessService mimePolicyBusinessService,
			FunctionalityService functionalityService,
			DomainAccessPolicyBusinessService domainAccessPolicyBusinessService,
			MailLayoutBusinessService mailLayoutBusinessService,
			MailFooterBusinessService mailFooterBusinessService,
			MailContentBusinessService mailContentBusinessService,
			MimeTypeService mimeTypeService,
			UserProviderService userProviderService,
			GroupProviderService groupProviderService,
			DriveProviderService driveProviderService) {
		super(sanitizerInputHtmlBusinessService, domainQuotaBusinessService, containerQuotaBusinessService);
		this.abstractDomainService = abstractDomainService;
		this.businessService = businessService;
		this.domainPolicyService = domainPolicyService;
		this.abstractDomainRepository = abstractDomainRepository;
		this.auditMongoRepository = auditMongoRepository;
		this.welcomeMessagesBusinessService = welcomeMessagesBusinessService;
		this.mailConfigBusinessService = mailConfigBusinessService;
		this.mimePolicyBusinessService = mimePolicyBusinessService;
		this.functionalityService = functionalityService;
		this.domainAccessPolicyBusinessService = domainAccessPolicyBusinessService;
		this.mailLayoutBusinessService = mailLayoutBusinessService;
		this.mailFooterBusinessService = mailFooterBusinessService;
		this.mailContentBusinessService = mailContentBusinessService;
		this.mimeTypeService = mimeTypeService;
		this.userProviderService = userProviderService;
		this.groupProviderService = groupProviderService;
		this.driveProviderService = driveProviderService;
	}
	@Override
	public AbstractDomain find(Account actor, String uuid) throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(uuid, "Domain uuid must be set.");
		// TODO: access control check ?
		AbstractDomain domain = businessService.findById(uuid);
		return domain;
	}

	@Override
	public List<AbstractDomain> findAll(Account actor) {
		preChecks(actor);
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
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "Only root is authorized to create domains.");
		}
		Validate.notEmpty(name, "Name must be set.");
		Validate.notNull(type, "Domain type must be set.");
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
		// audit for root only or all admins ? description field is only for root.
		DomainAuditLogEntry log = new DomainAuditLogEntry(actor, LogAction.CREATE, AuditLogEntryType.DOMAIN, domain);
		auditMongoRepository.insert(log);
		return domain;
	}

	private boolean isAdminForThisUser(Account actor, AbstractDomain domain) {
		if (actor.hasSuperAdminRole()) {
			return true;
		}
		// nested administrators
		if (actor.hasAdminRole()) {
			if (actor.getDomain().equals(domain) ) {
				return true;
			} else if (actor.getDomain().isTopDomain()) {
				// TODO: select count(*) from domain as d where d.parent_id = <domain.getId()> and domain.id = <actor.getDomainId()>;
				// if count > 1 => ok
			} else if (actor.getDomain().isSubDomain()) {
				// there is no nested domain in a sub domain , he can only administrate his domain or nothing
				return false;
			} else if (actor.getDomain().isGuestDomain()) {
				// there is no nested domain in a guest domain, he can only administrate his domain or nothing
				return false;
			}
		}
		return false;
	}

	@Override
	public AbstractDomain update(Account actor, String domainUuid, AbstractDomain dto) throws BusinessException {
		preChecks(actor);
		AbstractDomain domain = find(actor, domainUuid);
		if(!isAdminForThisUser(actor, domain)) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You are not authorized to update this domain.");
		}
		Validate.notNull(dto.getDefaultRole(), "Missing default user role");
		Validate.notNull(dto.getExternalMailLocale(), "Missing default email language");
		// update entity
		domain.setLabel(getNameForUpdate(actor, dto, domain));
		domain.setDescription(getDescriptionForUpdate(actor, dto, domain));
		// TODO missing role validation: [UPLOAD_REQUEST, SIMPLE, SUPERADMIN, DELEGATION, ANONYMOUS, SYSTEM, SAFE, ADMIN]
		domain.setDefaultRole(dto.getDefaultRole());
		domain.setExternalMailLocale(dto.getExternalMailLocale());
		domain = businessService.update(domain);
		// TODO DomainAuditLogEntry ? audit for root only or all admins ? description field is only for root.
		return domain;
	}

	private String getNameForUpdate(Account actor, AbstractDomain dto, AbstractDomain domain) {
		if (actor.hasSuperAdminRole()) {
			String name = sanitizerInputHtmlBusinessService.strictClean(dto.getLabel());
			Validate.notEmpty(name, "Name can't be null or empty");
			return name;
		} else {
			if (!domain.getLabel().equals(dto.getLabel())) {
				throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You are not authorized to update attribute 'name' of a domain.");
			}
			return domain.getLabel();
		}
	}

	private String getDescriptionForUpdate(Account actor, AbstractDomain dto, AbstractDomain domain) {
		if (actor.hasSuperAdminRole()) {
			if (dto.getDescription() == null) {
				return domain.getDescription();
			} else {
				return sanitizerInputHtmlBusinessService.clean(dto.getDescription());
			}
		} else {
			if (dto.getDescription() == null) {
				return domain.getDescription();
			} else {
				if (!domain.getDescription().equals(dto.getDescription())) {
					throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You are not authorized to update attribute 'description' of a domain.");
				}
			}
			return domain.getDescription();
		}
	}

	@Override
	public AbstractDomain markToPurge(Account actor, String domainUuid) {
		preChecks(actor);
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "Only root is authorized to delete domains.");
		}
		AbstractDomain domain = find(actor, domainUuid);
		List<AbstractDomain> abstractDomains = businessService.getSubDomainsByDomain(domain.getUuid());
		if (!abstractDomains.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_OPERATION, " Domain contains subDomains");
		}

		try {
			logger.info("starting purging domain");

			deleteFunctionalities(actor, domain);
			deleteDomainAccessRules(domain);
			deleteMailLayouts(domain);
			deleteMailFooters(domain);
			deleteMailContents(domain);
			deleteMailConfigs(domain);
			deleteMimePolicies(actor, domain);
			deleteUserProvider(domain);
			deleteGroupProvider(domain);
			deleteDriveProvider(domain);
			deleteWelcomeMessages(domain);

			DomainAuditLogEntry log = new DomainAuditLogEntry(actor, LogAction.DELETE, AuditLogEntryType.DOMAIN, domain);
			unsetProperties(domain);

			abstractDomainRepository.markToPurge(domain);
			auditMongoRepository.insert(log);

			logger.info("domain purged successfully");
			return domain;
		} catch (BusinessException businessException) {
			logger.error("Error occurred while deleting domain dependencies", businessException);
			throw businessException;
		}
	}

	private void unsetProperties(AbstractDomain domain) {
		if (domain.getPolicy() != null)
			domain.setPolicy(null);
		domain.setCurrentMailConfiguration(null);
		domain.setCurrentWelcomeMessages(null);
		domain.setMimePolicy(null);
	}

	private void deleteWelcomeMessages(AbstractDomain domain) {
		if (domain.getWelcomeMessages() != null) {
			domain.getWelcomeMessages()
				.forEach(welcomeMessagesBusinessService::delete);
			domain.setWelcomeMessages(null);
		}
	}

	private void deleteDriveProvider(AbstractDomain domain) {
		if (domain.getDriveProvider() != null) {
			driveProviderService.delete(domain.getDriveProvider());
			domain.setDriveProvider(null);
		}
	}

	private void deleteGroupProvider(AbstractDomain domain) {
		if (domain.getGroupProvider() != null) {
			groupProviderService.delete(domain.getGroupProvider());
			domain.setGroupProvider(null);
		}
	}

	private void deleteUserProvider(AbstractDomain domain) {
		if (domain.getUserProvider() != null) {
			userProviderService.delete(domain.getUserProvider());
			domain.setUserProvider(null);
		}
	}

	private void deleteMimePolicies(Account actor, AbstractDomain domain) {
		if (domain.getMimePolicies() != null) {
			domain.getMimePolicies()
				.forEach(mimePolicy -> {
					mimePolicy.getMimeTypes()
						.forEach(mimeType -> mimeTypeService.delete(actor, mimeType));
					mimePolicyBusinessService.delete(mimePolicy);
				});
			domain.setMimePolicies(null);
		}
	}

	private void deleteMailConfigs(AbstractDomain domain) {
		if (domain.getMailConfigs() != null) {
			domain.getMailConfigs()
				.forEach(mailConfig -> {
					mailConfig.getMailContentLangs()
						.forEach(mailConfigBusinessService::deleteContentLang);
					mailConfigBusinessService.delete(mailConfig);
				});
			domain.setMailConfigs(null);
		}
	}

	private void deleteMailContents(AbstractDomain domain) {
		if (domain.getMailContents() != null) {
			domain.getMailContents()
				.forEach(mailContent -> {
					mailConfigBusinessService.findMailsContentLangByMailContent(mailContent)
						.forEach(mailConfigBusinessService::deleteContentLang);
					mailContentBusinessService.delete(mailContent);
				});
			domain.setMailContents(null);
		}
	}

	private void deleteMailFooters(AbstractDomain domain) {
		if (domain.getMailFooters() != null) {
			domain.getMailFooters()
				.forEach(mailFooter -> {
					mailFooterBusinessService.findByMailFooter(mailFooter)
						.forEach(mailConfigBusinessService::deleteFooterLang);
					mailFooterBusinessService.delete(mailFooter);
				});
			domain.setMailFooters(null);
		}
	}

	private void deleteMailLayouts(AbstractDomain domain) {
		if (domain.getMailLayouts() != null) {
			domain.getMailLayouts()
				.forEach(mailLayoutBusinessService::delete);
			domain.setMailLayouts(null);
		}
	}

	private void deleteDomainAccessRules(AbstractDomain domain) {
		if (domain.getDomainAccessRules() != null) {
			domain.getDomainAccessRules()
				.forEach(domainAccessPolicyBusinessService::delete);
		}
	}

	private void deleteFunctionalities(Account actor, AbstractDomain domain) {
		if (domain.getFunctionalities() != null) {
			domain.getFunctionalities()
				.forEach(functionality -> functionalityService.delete(actor, domain.getUuid(), functionality.getIdentifier()));
		}
	}
}
