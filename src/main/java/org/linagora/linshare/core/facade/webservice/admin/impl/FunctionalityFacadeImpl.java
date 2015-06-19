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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FunctionalityPermissions;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class FunctionalityFacadeImpl extends AdminGenericFacadeImpl implements
		FunctionalityFacade {

	private static final Logger logger = LoggerFactory.getLogger(FunctionalityFacadeImpl.class);

	private FunctionalityService service;

	// FIXME : HACK : Very dirty.
	private List<String> excludes = new ArrayList<String>();

	public FunctionalityFacadeImpl(final AccountService accountService,
			final FunctionalityService functionalityService) {
		super(accountService);
		this.service = functionalityService;
		excludes.add(FunctionalityNames.UPLOAD_REQUEST_ENTRY_URL.toString());
		excludes.add(FunctionalityNames.UPLOAD_REQUEST_ENTRY_URL__EXPIRATION.toString());
		excludes.add(FunctionalityNames.UPLOAD_REQUEST_ENTRY_URL__PASSWORD.toString());
	}

	@Override
	public FunctionalityAdminDto find(String domainId, String funcId, boolean tree)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Validate.notEmpty(funcId, "functionality identifier must be set.");
		Functionality func = service.find(actor,
				domainId, funcId);
		FunctionalityAdminDto res = transform(actor, func);
		if (tree) {
			List<FunctionalityAdminDto> all = findAll(domainId, funcId, false, false);
			for (FunctionalityAdminDto f: all) {
				res.addFunctionalities(f);
			}
		}
		return res;
	}

	@Override
	public List<FunctionalityAdminDto> findAll(String domainId, String parentId, boolean tree, boolean withSubFunctionalities)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		Set<Functionality> entities = service.findAll(actor, domainId);
		Map<String, FunctionalityAdminDto> parents = new HashMap<String, FunctionalityAdminDto>();
		List<FunctionalityAdminDto> subs = new ArrayList<FunctionalityAdminDto>();
		for (Functionality f : entities) {
			// FIXME : HACK : Very dirty.
			if (excludes.contains(f.getIdentifier())) {
				continue;
			}
			FunctionalityAdminDto func = transform(actor, f);
			// We check if this a sub functionality (a parameter)
			if (f.isParam()) {
				subs.add(func);
			} else {
				parents.put(f.getIdentifier(), func);
			}
		}

		Map<String, List<FunctionalityAdminDto>> children = new HashMap<String, List<FunctionalityAdminDto>>();
		for (FunctionalityAdminDto func : subs) {
			if (parents.containsKey(func.getParentIdentifier())) {
				// if parent contains at least one sub functionality to display, the parent must be display.
				if (func.isDisplayable()) {
					FunctionalityAdminDto parent = parents.get(func.getParentIdentifier());
					// We need to display the parent functionality to display children
					// only if the parent is not forbidden.
					if (!parent.getActivationPolicy().getPolicy().equals(Policies.FORBIDDEN.toString())) {
						parent.setDisplayable(true);
					}
				}
				if (tree) {
					FunctionalityAdminDto parent = parents.get(func.getParentIdentifier());
					parent.addFunctionalities(func);
				}
				// storing children in maps
				List<FunctionalityAdminDto> list = children.get(func.getParentIdentifier());
				if (list == null) {
					list = Lists.newArrayList();
					children.put(func.getParentIdentifier(), list);
				}
				list.add(func);
			} else {
				logger.error("Sub functionality {} without a parent : {}", func.getIdentifier(), func.getParentIdentifier());
			}
		}
		List<FunctionalityAdminDto> result = Lists.newArrayList();
		if (parentId == null) {
			result.addAll(parents.values());
		} else {
			FunctionalityNames parentIdentifier = FunctionalityNames.valueOf(parentId);
			List<FunctionalityAdminDto> list = children.get(parentIdentifier.name());
			if (list == null) {
				logger.debug("Functionality {} has no children.", parentIdentifier.name());
				return Lists.newArrayList();
			}
			result.addAll(list);
		}
		if (withSubFunctionalities) {
			result.addAll(subs);
		}
		return Ordering.natural().immutableSortedCopy(
				Iterables.filter(result, isDisplayable()));
	}

	@Override
	public FunctionalityAdminDto update(FunctionalityAdminDto func)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);

		Validate.notEmpty(func.getDomain(), "domain identifier must be set.");
		Validate.notEmpty(func.getIdentifier(),
				"functionality identifier must be set.");
		Functionality entity = service.find(actor,
				func.getDomain(), func.getIdentifier());

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
		return transform(actor, update);
	}

	private FunctionalityAdminDto transform(Account actor, Functionality update)
			throws BusinessException {
		FunctionalityPermissions mutable = service.isMutable(
				actor, update, update.getDomain());
		return new FunctionalityAdminDto(update,
				mutable.isParentAllowAPUpdate(),
				mutable.isParentAllowCPUpdate(),
				mutable.isParentAllowDPUpdate(),
				mutable.isParentAllowParametersUpdate());
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

	private Predicate<FunctionalityAdminDto> isDisplayable() {
		return new Predicate<FunctionalityAdminDto>() {
			@Override
			public boolean apply(FunctionalityAdminDto input) {
				return input.isDisplayable();
			}
		};
	}
}