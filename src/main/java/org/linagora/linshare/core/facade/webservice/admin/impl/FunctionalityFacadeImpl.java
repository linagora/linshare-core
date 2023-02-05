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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

public class FunctionalityFacadeImpl extends AdminGenericFacadeImpl implements
		FunctionalityFacade {

	private FunctionalityService service;

	public FunctionalityFacadeImpl(final AccountService accountService,
			final FunctionalityService functionalityService) {
		super(accountService);
		this.service = functionalityService;
	}

	@Override
	public FunctionalityAdminDto find(Version version, String domainId, String funcId, boolean tree)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Validate.notEmpty(funcId, "functionality identifier must be set.");
		Functionality func = service.find(authUser, domainId, funcId, tree);
		return FunctionalityAdminDto.toDto(version).apply(func);
	}

	@Override
	public List<FunctionalityAdminDto> findAll(Version version, String domainId, String parentId, boolean tree, boolean withSubFunctionalities)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Iterable<Functionality> entities = service.findAll(authUser, domainId, parentId, tree, withSubFunctionalities);
		Iterable<FunctionalityAdminDto> transform = Iterables.transform(entities, FunctionalityAdminDto.toDto(version));
		// Copy is made because the transaction is closed at the end of every method in facade classes.
		return Ordering.natural().immutableSortedCopy(transform);
	}

	@Override
	public FunctionalityAdminDto update(Version version, FunctionalityAdminDto func)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);

		Validate.notEmpty(func.getDomain(), "domain identifier must be set.");
		Validate.notEmpty(func.getIdentifier(),
				"functionality identifier must be set.");
		Functionality entity = service.find(authUser, func.getDomain(),
				func.getIdentifier());

		// copy of activation policy.
		String ap = func.getActivationPolicy().getPolicy().trim().toUpperCase();
		entity.getActivationPolicy().setPolicy(Policies.valueOf(ap));
		entity.getActivationPolicy().setStatus(
				func.getActivationPolicy().getStatus());

		// copy of configuration policy.
		String cp = func.getConfigurationPolicy().getPolicy().trim()
				.toUpperCase();
		entity.getConfigurationPolicy().setPolicy(Policies.valueOf(cp));
		entity.getConfigurationPolicy().setStatus(
				func.getConfigurationPolicy().getStatus());

		if (func.getDelegationPolicy() != null) {
			// copy of configuration policy.
			String dp = func.getDelegationPolicy().getPolicy().trim()
					.toUpperCase();
			entity.getDelegationPolicy().setPolicy(Policies.valueOf(dp));
			entity.getDelegationPolicy().setStatus(
					func.getDelegationPolicy().getStatus());
		}

		// copy of parameters.
		entity.updateFunctionalityValuesOnlyFromDto(version, func);
		Functionality update = service.update(authUser,
				func.getDomain(), entity);
		return new FunctionalityAdminDto(version, update);
	}

	@Override
	public void delete(Version version, FunctionalityAdminDto func) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(func.getDomain(), "domain identifier must be set.");
		Validate.notEmpty(func.getIdentifier(),
				"functionality identifier must be set.");
		service.delete(authUser, func.getDomain(),
				func.getIdentifier());
	}
}
