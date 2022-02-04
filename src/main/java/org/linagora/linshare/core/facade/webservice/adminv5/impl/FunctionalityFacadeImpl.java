/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.PolicyDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

public class FunctionalityFacadeImpl extends AdminGenericFacadeImpl implements FunctionalityFacade {

	FunctionalityService service;

	public FunctionalityFacadeImpl(
			AccountService accountService,
			FunctionalityService functionalityService) {
		super(accountService);
		this.service = functionalityService;
	}

	@Override
	public FunctionalityDto find(String domainUuid, String funcIdentifier) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "domain uuid must be set.");
		Validate.notEmpty(funcIdentifier, "functionality identifier must be set.");
		Functionality func = service.find(authUser, domainUuid, funcIdentifier, false);
		return FunctionalityDto.toDto().apply(func);
	}

	@Override
	public List<FunctionalityDto> findAll(String domainUuid, String parentIdentifier, boolean withSubFunctionalities) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "domain uuid must be set.");
		Iterable<Functionality> entities = service.findAll(authUser, domainUuid, parentIdentifier, false, withSubFunctionalities);
		Iterable<FunctionalityDto> transform = Iterables.transform(entities, FunctionalityDto.toDto());
		return Ordering.natural().immutableSortedCopy(transform);
	}

	@Override
	public FunctionalityDto update(String domainUuid, String funcIdentifier, FunctionalityDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(dto, "functionality object must be set.");
		Validate.notEmpty(domainUuid, "domain uuid must be set.");
		if (funcIdentifier == null) {
			funcIdentifier = dto.getIdentifier();
		}
		Validate.notEmpty(funcIdentifier, "functionality identifier must be set.");

		Functionality entity = service.find(authUser, domainUuid, funcIdentifier, false);

		updatePolicy(entity.getActivationPolicy(), dto.getActivationPolicy(), entity.getIdentifier(), "activation policy");
		updatePolicy(entity.getConfigurationPolicy(), dto.getConfigurationPolicy(), entity.getIdentifier(), "configuration policy");
		if (entity.getDelegationPolicy() != null) {
			updatePolicy(entity.getDelegationPolicy(), dto.getDelegationPolicy(), entity.getIdentifier(), "delegation policy");
		} else {
			logger.debug("No delegation policy for functionality: %s", entity.getIdentifier());
		}

		// copy of parameters.
		entity.updateFunctionalityValuesOnlyFromDto(dto.getParameter());

		Functionality update = service.update(authUser, domainUuid, entity);
		return FunctionalityDto.toDto().apply(update);
	}

	private void updatePolicy(Policy policyEntity, PolicyDto policyDto, String identifier, String policyName) {
		Validate.notNull(policyDto, policyName + " object is missing");
		Validate.notNull(policyDto.getEnable(), "Enable object of " + policyName + " is missing");
		Validate.notNull(policyDto.getAllowOverride(), "AllowOverride object of "+ policyName + " is missing");
		policyEntity.setStatus(policyDto.getEnable().isValue());
		if (policyDto.getAllowOverride().isValue()) {
			policyEntity.setPolicy(Policies.ALLOWED);
		} else {
			if (policyDto.getEnable().isValue()) {
				policyEntity.setPolicy(Policies.MANDATORY);
			} else {
				policyEntity.setPolicy(Policies.FORBIDDEN);
			}
		}
	}

	@Override
	public FunctionalityDto delete(String domainUuid, String funcIdentifier, FunctionalityDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "domain uuid must be set.");
		if (funcIdentifier == null) {
			if (dto != null) {
				funcIdentifier = dto.getIdentifier();
			}
		}
		Validate.notEmpty(funcIdentifier, "functionality identifier must be set.");
		service.delete(authUser, domainUuid, funcIdentifier);
		Functionality entity = service.find(authUser, domainUuid, funcIdentifier, false);
		return FunctionalityDto.toDto().apply(entity);
	}

}
