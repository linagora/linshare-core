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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;

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
	public FunctionalityAdminDto find(String domainId, String funcId, boolean tree)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Validate.notEmpty(funcId, "functionality identifier must be set.");
		Functionality func = service.find(actor, domainId, funcId, tree);
		return FunctionalityAdminDto.toDto().apply(func);
	}

	@Override
	public List<FunctionalityAdminDto> findAll(String domainId, String parentId, boolean tree, boolean withSubFunctionalities)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Iterable<Functionality> entities = service.findAll(actor, domainId, parentId, tree, withSubFunctionalities);
		Iterable<FunctionalityAdminDto> transform = Iterables.transform(entities, FunctionalityAdminDto.toDto());
		// Copy is made because the transaction is closed at the end of every method in facade classes.
		return Ordering.natural().immutableSortedCopy(transform);
	}

	@Override
	public FunctionalityAdminDto update(FunctionalityAdminDto func)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);

		Validate.notEmpty(func.getDomain(), "domain identifier must be set.");
		Validate.notEmpty(func.getIdentifier(),
				"functionality identifier must be set.");
		Functionality entity = service.find(actor, func.getDomain(),
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
		entity.updateFunctionalityValuesOnlyFromDto(func);
		Functionality update = service.update(actor,
				func.getDomain(), entity);
		return new FunctionalityAdminDto(update);
	}

	@Override
	public void delete(FunctionalityAdminDto func) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(func.getDomain(), "domain identifier must be set.");
		Validate.notEmpty(func.getIdentifier(),
				"functionality identifier must be set.");
		service.delete(actor, func.getDomain(),
				func.getIdentifier());
	}
}