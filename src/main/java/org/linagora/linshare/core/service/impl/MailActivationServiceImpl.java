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
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MailActivationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class MailActivationServiceImpl extends
		AbstractFunctionalityServiceImpl<MailActivation> implements
		MailActivationService {

	protected final Logger logger = LoggerFactory
			.getLogger(MailActivationServiceImpl.class);

	final private MailActivationBusinessService businessService;

	public MailActivationServiceImpl(
			DomainBusinessService domainBusinessService,
			DomainPermissionBusinessService domainPermissionBusinessService,
			MailActivationBusinessService mailActivationBusinessService) {
		super(domainBusinessService, domainPermissionBusinessService);
		this.businessService = mailActivationBusinessService;
	}

	@Override
	public List<MailActivation> findAll(Account actor, String domainId)
			throws BusinessException {
		Validate.notEmpty(domainId);
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		AbstractDomain domain = getDomain(actor, domainId);
		List<MailActivation> res = Lists.newArrayList();
		res.addAll(businessService.getAllFunctionalities(domain, excludes));
		return res;
	}

	@Override
	public MailActivation find(Account actor, String domainId,
			String mailActivationId) throws BusinessException {
		Validate.notEmpty(domainId);
		Validate.notEmpty(mailActivationId);
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		logger.debug("looking for mailActivation : " + mailActivationId
				+ " in domain " + domainId);
		AbstractDomain domain = getDomain(actor, domainId);
		return businessService.getFunctionality(domain, mailActivationId);
	}

	@Override
	public MailActivation update(Account actor, String domainId,
			MailActivation mailActivation) throws BusinessException {
		Validate.notNull(domainId);
		Validate.notNull(mailActivation.getIdentifier());
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		AbstractDomain domain = getDomain(actor, domainId);
		if (checkUpdateRights(actor, domain, mailActivation)) {
			businessService.update(domainId, mailActivation);
		}
		return businessService.getFunctionality(domain,
				mailActivation.getIdentifier());
	}

	@Override
	public void delete(Account actor, String domainId, String mailActivationId)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.notEmpty(domainId);
		Validate.notEmpty(mailActivationId);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		AbstractDomain domain = getDomain(actor, domainId);
		checkDeleteRights(domain);
		businessService.delete(domainId, mailActivationId);
	}
}
