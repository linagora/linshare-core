/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.FunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityServiceImpl implements FunctionalityService {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityServiceImpl.class);

	final private FunctionalityBusinessService functionalityBusinessService;

	final private DomainBusinessService domainBusinessService;

	final private DomainPermissionBusinessService domainPermissionBusinessService;

	public FunctionalityServiceImpl(
			FunctionalityBusinessService functionalityBusinessService,
			DomainBusinessService domainBusinessService,
			DomainPermissionBusinessService domainPermissionBusinessService
			) {
		super();
		this.functionalityBusinessService = functionalityBusinessService;
		this.domainBusinessService = domainBusinessService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
	}

	@Override
	public Set<Functionality> getAllFunctionalities(Account actor, AbstractDomain domain) throws BusinessException {
		Validate.notNull(domain);
		Validate.notEmpty(domain.getIdentifier());
		checkDomainRights(actor, domain.getIdentifier());
		return functionalityBusinessService.getAllFunctionalities(domain);
	}

	@Override
	public Set<Functionality> getAllFunctionalities(Account actor, String domain) throws BusinessException {
		Validate.notEmpty(domain);
		checkDomainRights(actor, domain);
		return functionalityBusinessService.getAllFunctionalities(domain);
	}

	@Override
	public boolean activationPolicyIsMutable(Functionality f, String domain) {
		Validate.notNull(f);
		Validate.notNull(domain);
		return functionalityBusinessService.activationPolicyIsMutable(f, domain);
	}

	@Override
	public boolean configurationPolicyIsMutable(Functionality f, String domain) {
		Validate.notNull(f);
		Validate.notNull(domain);
		return functionalityBusinessService.configurationPolicyIsMutable(f, domain);
	}

	@Override
	public Functionality getFunctionality(Account actor, String domainId, String functionalityId) throws BusinessException {
		Validate.notNull(domainId);
		Validate.notNull(functionalityId);
		logger.debug("looking for functionality : " + functionalityId + " in domain "+ domainId);
		checkDomainRights(actor, domainId);
		return functionalityBusinessService.getFunctionality(domainId, functionalityId);
	}

	@Override
	public void deleteFunctionality(Account actor, String domainId, String functionalityId) throws IllegalArgumentException, BusinessException {
		checkDomainRights(actor, domainId);
		functionalityBusinessService.delete(domainId, functionalityId);
	}

	@Override
	public void update(Account actor, String domain, Functionality functionality) throws BusinessException {
		Validate.notNull(domain);
		Validate.notNull(functionality.getIdentifier());
		checkDomainRights(actor, domain);

		if (checkUpdateRights(actor, domain, functionality)) {
			functionalityBusinessService.update(domain, functionality);
		}
	}

	@Override
	public void update(Account actor, AbstractDomain domain, Functionality functionality) throws BusinessException {
		this.update(actor, domain.getIdentifier(), functionality);
	}

	/**
	 * Return true if you need to update the input functionality.
	 * @param actor
	 * @param domain
	 * @param functionality : new functionality.
	 * @param entity : original functionality. 
	 * @return
	 * @throws BusinessException
	 */
	private boolean checkUpdateRights(Account actor, String domain, Functionality functionality)
			throws BusinessException {
		Functionality entity = this.getFunctionality(actor, domain, functionality.getIdentifier());

		// consistency checks
		if (!entity.getType().equals(functionality.getType())) {
			String message = entity.getType().toString() + " != " + entity.getType().toString();
			throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT, "Same identifier, different functionality types : " + message);
		}

		functionality.getActivationPolicy().applyConsistency();

		functionality.getConfigurationPolicy().applyConsistency();

		// we check if the parent functionality allow modifications of the activation policy (AP).
		boolean parentAllowAPUpdate = activationPolicyIsMutable(entity, domain);
		if(!parentAllowAPUpdate) {
			// Modifications are not allowed.
			if (!entity.getActivationPolicy().businessEquals(functionality.getActivationPolicy())) {
				// AP entity is different of the input AP functionality  => FORBIDDEN
				logger.error("current actor '" + actor.getAccountReprentation() + "' does not have the right to update the functionnality (AP) '" + functionality +"' in domain '" + domain +"'");
				throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT, "You does not have the right to update this functionality");
			}
			if(entity.getActivationPolicy().isForbidden()) {
				if (!functionality.businessEquals(entity, true)) {
					logger.error("current actor '" + actor.getAccountReprentation() + "' does not have the right to update the functionnality (All) '" + functionality +"' in domain '" + domain +"'");
					throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT, "You does not have the right to update this functionality");
				}
			}
		}

		// we check if the parent functionality allow modifications of the configuration policy (CP).
		boolean parentAllowCPUpdate = configurationPolicyIsMutable(entity, domain);
		if(!parentAllowCPUpdate) {
			// Modifications are not allowed.
			if (!entity.getConfigurationPolicy().businessEquals(functionality.getConfigurationPolicy())) {
				// AP entity is different of the input CP functionality  => FORBIDDEN
				logger.error("current actor '" + actor.getAccountReprentation() + "' does not have the right to update the functionnality (CP) '" + functionality +"' in domain '" + domain +"'");
				throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT, "You does not have the right to update this functionality");
			}
		}

		// we check if there is any modifications
		if (functionality.businessEquals(entity, true)) {
			logger.debug("functionality " + functionality.toString() + " was not modified.");
			return false;
		}

		// TODO :if ap is forbidden ? check status ?
		// TODO: func.isSystem ??
		return true;
	}

	private void checkDomainRights(Account actor, String domainId) throws BusinessException {
		if(!domainPermissionBusinessService.isAdminforThisDomain(actor, domainId)) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXISTS,"The current domain does not exist : domainId");
		}
	}
}
