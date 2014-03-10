/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.facade.impl;

import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityOldService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityFacadeImpl implements FunctionalityFacade {

	protected final Logger logger = LoggerFactory
			.getLogger(FunctionalityFacadeImpl.class);

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final AbstractDomainService abstractDomainService;

	public FunctionalityFacadeImpl(
			AbstractDomainService abstractDomainService,
			FunctionalityReadOnlyService functionalityReadOnlyService) {
		super();
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	public Integer completionThreshold(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			IntegerValueFunctionality completionFunctionality = functionalityReadOnlyService
					.getCompletionFunctionality(domain);
			if (completionFunctionality.getActivationPolicy().getStatus()) {
				return completionFunctionality.getValue();
			}
		} else {
			logger.error("Can't find completion functionality for domain : "
					+ domainIdentifier);
		}
		return LinShareConstants.completionThresholdConstantForDeactivation;
	}

	@Override
	public boolean isEnableUserTab(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality userTabFunctionality = functionalityReadOnlyService
					.getUserTabFunctionality(domain);
			return userTabFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find user tab functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableAuditTab(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality auditTabFunctionality = functionalityReadOnlyService
					.getAuditTabFunctionality(domain);
			return auditTabFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find audit tab functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableThreadTab(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality threadTabFunctionality = functionalityReadOnlyService
					.getThreadTabFunctionality(domain);
			return threadTabFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find help tab functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableUpdateFiles(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality updateFilesFunctionality = functionalityReadOnlyService
					.getUpdateFilesFunctionality(domain);
			return updateFilesFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find update files functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableCustomLogoLink(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality customLogoLinkFunctionality = functionalityReadOnlyService
					.getCustomLinkLogoFunctionality(domain);
			return customLogoLinkFunctionality.getActivationPolicy()
					.getStatus();
		} else {
			logger.error("Can't find custom logo link functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableCreateThread(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality createThreadFunctionality = functionalityReadOnlyService
					.getThreadCreationPermissionFunctionality(domain);
			return createThreadFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find thread creation functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableHelpTab(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality helpTabFunctionality = functionalityReadOnlyService
					.getHelpTabFunctionality(domain);
			return helpTabFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find help tab functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableListTab(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality listTabFunctionality = functionalityReadOnlyService
					.getListTabFunctionality(domain);
			return listTabFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find list tab functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean isEnableGuest(String domainIdentifier) {
		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		if (domain != null) {
			Functionality helpTabFunctionality = functionalityReadOnlyService
					.getGuestFunctionality(domain);
			return helpTabFunctionality.getActivationPolicy().getStatus();
		} else {
			logger.error("Can't find help tab functionality for domain : "
					+ domainIdentifier);
		}
		return false;
	}

	@Override
	public boolean getDefaultRestrictedGuestValue(String domainIdentifier) {
		return functionalityReadOnlyService
				.getDefaultRestrictedGuestValue(domainIdentifier);
	}

	@Override
	public boolean isRestrictedGuestEnabled(String domainIdentifier) {
		return functionalityReadOnlyService
				.isRestrictedGuestAllowed(domainIdentifier);
	}
}
