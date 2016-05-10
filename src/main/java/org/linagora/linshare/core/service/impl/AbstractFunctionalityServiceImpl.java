/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AbstractFunctionality;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractFunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFunctionalityServiceImpl<T extends AbstractFunctionality> implements
		AbstractFunctionalityService<T> {

	private final Logger logger = LoggerFactory.getLogger(AbstractFunctionalityServiceImpl.class);

	final protected DomainBusinessService domainBusinessService;

	final protected DomainPermissionBusinessService domainPermissionBusinessService;

	// Exclude some types : they won't appear outside the service.
	protected List<String> excludes = new ArrayList<String>();

	public AbstractFunctionalityServiceImpl(
			DomainBusinessService domainBusinessService,
			DomainPermissionBusinessService domainPermissionBusinessService) {
		super();
		this.domainBusinessService = domainBusinessService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
	}

	protected boolean checkUpdateRights(Account actor, AbstractDomain domain, T functionality)
			throws BusinessException {
		T entity = this.find(actor, domain.getUuid(), functionality.getIdentifier());

		// consistency checks
		if (entity.getClass() != functionality.getClass()) {
			String message = entity.getClass().toString() + " != " + functionality.getClass().toString();
			throw new BusinessException(BusinessErrorCode.UPDATE_FORBIDDEN, "Same identifier, different entity types : " + message);
		}
		functionality.getActivationPolicy().applyConsistency();
		functionality.getConfigurationPolicy().applyConsistency();
		if (functionality.getDelegationPolicy() != null) {
			functionality.getDelegationPolicy().applyConsistency();
		}

		// we check if the parent functionality allow modifications of the activation policy (AP).
		boolean parentAllowAPUpdate = entity.getActivationPolicy().getParentAllowUpdate();
		if(!parentAllowAPUpdate) {
			// Modifications are not allowed.
			if (!entity.getActivationPolicy().businessEquals(functionality.getActivationPolicy())) {
				// AP entity is different of the input AP functionality  => FORBIDDEN
				logger.error("current actor '" + actor.getAccountRepresentation() + "' does not have the right to update the entity (AP) '" + functionality +"' in domain '" + domain +"'");
				throw new BusinessException(BusinessErrorCode.UPDATE_FORBIDDEN, "You does not have the right to update this entity");
			}
			if(entity.getActivationPolicy().isForbidden()) {
				if (!functionality.businessEquals(entity, true)) {
					logger.error("current actor '" + actor.getAccountRepresentation() + "' does not have the right to update the entity (All) '" + functionality +"' in domain '" + domain +"'");
					throw new BusinessException(BusinessErrorCode.UPDATE_FORBIDDEN, "You does not have the right to update this entity");
				}
			}
		}

		// we check if the parent functionality allow modifications of the configuration policy (CP).
		boolean parentAllowCPUpdate = entity.getConfigurationPolicy().getParentAllowUpdate();
		if(!parentAllowCPUpdate) {
			// Modifications are not allowed.
			if (!entity.getConfigurationPolicy().businessEquals(functionality.getConfigurationPolicy())) {
				// CP entity is different of the input CP functionality  => FORBIDDEN
				logger.error("current actor '" + actor.getAccountRepresentation() + "' does not have the right to update the entity (CP) '" + functionality +"' in domain '" + domain +"'");
				throw new BusinessException(BusinessErrorCode.UPDATE_FORBIDDEN, "You does not have the right to update this entity");
			}
		}

		// we check if the parent functionality allow modifications of the delegation policy (DP).
		if (entity.getDelegationPolicy() != null) {
			boolean parentAllowDPUpdate = entity.getDelegationPolicy().getParentAllowUpdate();
			if(!parentAllowDPUpdate) {
				// Modifications are not allowed.
				if (!entity.getDelegationPolicy().businessEquals(functionality.getDelegationPolicy())) {
					// DP entity is different of the input DP functionality  => FORBIDDEN
					logger.error("current actor '" + actor.getAccountRepresentation() + "' does not have the right to update the entity (DP) '" + functionality +"' in domain '" + domain +"'");
					throw new BusinessException(BusinessErrorCode.UPDATE_FORBIDDEN, "You does not have the right to update this entity");
				}
			}
		}

		boolean parentAllowParamUpdate = entity.getParentAllowParametersUpdate();
		if(!parentAllowParamUpdate) {
			if (!functionality.businessEquals(entity, false)) {
				logger.error("current actor '" + actor.getAccountRepresentation() + "' does not have the right to update the entity (PARAM) '" + functionality +"' in domain '" + domain +"'");
				throw new BusinessException(BusinessErrorCode.UPDATE_FORBIDDEN, "You does not have the right to update this entity");
			}
		}

		// we check if there is any modifications
		if (functionality.businessEquals(entity, true)) {
			logger.debug("functionality " + functionality.toString() + " was not modified.");
			return false;
		}
		return true;
	}

	protected AbstractDomain getDomain(Account actor, String domainId)
			throws BusinessException {
		AbstractDomain domain = domainBusinessService.findById(domainId);
		if (domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST, "The input domain does not exist.");
		}
		if (!domainPermissionBusinessService
				.isAdminforThisDomain(actor, domain)) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"The current domain does not exist : " + domainId);
		}
		return domain;
	}

	protected void checkDeleteRights(AbstractDomain domain) throws BusinessException {
		AbstractDomain rootDomain = domainBusinessService.getUniqueRootDomain();
		if (domain.equals(rootDomain)) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_INVALID_OPERATION,
					"You are not authorized to delete a root functionality");
		}
	}
}
