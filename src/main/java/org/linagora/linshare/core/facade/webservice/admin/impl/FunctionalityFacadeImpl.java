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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.webservice.dto.FunctionalityDto;

import com.google.common.collect.Ordering;

public class FunctionalityFacadeImpl extends AdminGenericFacadeImpl implements
		FunctionalityFacade {

	private FunctionalityService functionalityService;

	public FunctionalityFacadeImpl(final AccountService accountService,
			final FunctionalityService functionalityService) {
		super(accountService);
		this.functionalityService = functionalityService;
	}

	@Override
	public FunctionalityDto find(String domainId, String funcId)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Validate.notEmpty(funcId, "functionality identifier must be set.");
		Functionality f = functionalityService.getFunctionality(actor,
				domainId, funcId);
		boolean parentAllowAPUpdate = functionalityService
				.activationPolicyIsMutable(f, domainId);
		boolean parentAllowCPUpdate = functionalityService
				.configurationPolicyIsMutable(f, domainId);
		boolean parentAllowDPUpdate = functionalityService
				.configurationPolicyIsMutable(f, domainId);
		FunctionalityDto func = new FunctionalityDto(f, parentAllowAPUpdate,
				parentAllowCPUpdate, parentAllowDPUpdate);
		func.setDomain(domainId);
		return func;
	}

	@Override
	public List<FunctionalityDto> findAll(String domainId)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Set<Functionality> entities = functionalityService
				.getAllFunctionalities(actor, domainId);

		Map<String, FunctionalityDto> ret = new HashMap<String, FunctionalityDto>();
		List<FunctionalityDto> subs = new ArrayList<FunctionalityDto>();

		for (Functionality f : entities) {
			boolean parentAllowAPUpdate = functionalityService
					.activationPolicyIsMutable(f, domainId);
			boolean parentAllowCPUpdate = functionalityService
					.configurationPolicyIsMutable(f, domainId);
			boolean parentAllowDPUpdate = functionalityService
					.delegationPolicyIsMutable(f, domainId);
			FunctionalityDto func = new FunctionalityDto(f,
					parentAllowAPUpdate, parentAllowCPUpdate, parentAllowDPUpdate);
			// We force the domain id to be coherent to the argument.
			func.setDomain(domainId);

			// We check if this a sub functionality (a parameter)
			if (f.isParam()) {
				if (ret.containsKey(func.getParentIdentifier())) {
					ret.get(func.getParentIdentifier())
							.addFunctionalities(func);
				} else {
					subs.add(func);
				}
			} else {
				ret.put(f.getIdentifier(), func);
			}
		}
		for (FunctionalityDto func : subs) {
			if (ret.containsKey(func.getParentIdentifier())) {
				ret.get(func.getParentIdentifier()).addFunctionalities(func);
			}
		}
		return Ordering.natural().immutableSortedCopy(ret.values());
	}

	@Override
	public FunctionalityDto update(FunctionalityDto func)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);

		Validate.notEmpty(func.getDomain(), "domain identifier must be set.");
		Validate.notEmpty(func.getIdentifier(),
				"functionality identifier must be set.");
		Functionality f = functionalityService.getFunctionality(actor,
				func.getDomain(), func.getIdentifier());

		// copy of activation policy.
		String ap = func.getActivationPolicy().getPolicy().trim().toUpperCase();
		f.getActivationPolicy().setPolicy(Policies.valueOf(ap));
		f.getActivationPolicy().setStatus(
				func.getActivationPolicy().getStatus());

		// copy of configuration policy.
		String cp = func.getConfigurationPolicy().getPolicy().trim()
				.toUpperCase();
		f.getConfigurationPolicy().setPolicy(Policies.valueOf(cp));
		f.getConfigurationPolicy().setStatus(
				func.getConfigurationPolicy().getStatus());

		if (func.getDelegationPolicy() != null) {
			// copy of configuration policy.
			String dp = func.getDelegationPolicy().getPolicy().trim()
					.toUpperCase();
			f.getDelegationPolicy().setPolicy(Policies.valueOf(dp));
			f.getDelegationPolicy().setStatus(
					func.getDelegationPolicy().getStatus());
		}

		// copy of parameters.
		f.updateFunctionalityValuesOnlyFromDto(func);
		Functionality update = functionalityService.update(actor,
				func.getDomain(), f);
		boolean parentAllowAPUpdate = functionalityService
				.activationPolicyIsMutable(update, update.getDomain().getIdentifier());
		boolean parentAllowCPUpdate = functionalityService
				.configurationPolicyIsMutable(f, update.getDomain().getIdentifier());
		boolean parentAllowDPUpdate = functionalityService
				.delegationPolicyIsMutable(f, update.getDomain().getIdentifier());
		return new FunctionalityDto(update, parentAllowAPUpdate, parentAllowCPUpdate, parentAllowDPUpdate);
	}

	@Override
	public void delete(FunctionalityDto func) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(func.getDomain(), "domain identifier must be set.");
		Validate.notEmpty(func.getIdentifier(),
				"functionality identifier must be set.");
		functionalityService.deleteFunctionality(actor, func.getDomain(),
				func.getIdentifier());
	}
}