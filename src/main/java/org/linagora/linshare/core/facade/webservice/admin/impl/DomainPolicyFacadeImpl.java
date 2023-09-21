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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowAllDomain;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyAllDomain;
import org.linagora.linshare.core.domain.entities.DenyDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPolicyFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainAccessPolicyDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainAccessRuleDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainPolicyService;

import com.google.common.collect.Lists;


public class DomainPolicyFacadeImpl extends AdminGenericFacadeImpl implements
		DomainPolicyFacade {

	private final DomainPolicyService domainPolicyService;

	private final DomainBusinessService domainBusinessService;

	public DomainPolicyFacadeImpl(final AccountService accountService,
			final DomainPolicyService domainPolicyService,
			DomainBusinessService domainBusinessService) {
		super(accountService);
		this.domainPolicyService = domainPolicyService;
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public DomainPolicyDto find(String identifier) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(identifier, "domain policy identifier must be set.");
		return new DomainPolicyDto(domainPolicyService.find(identifier));
	}

	@Override
	public Set<DomainPolicyDto> findAll() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Set<DomainPolicyDto> domainPolicies = new HashSet<DomainPolicyDto>();
		for (DomainPolicy domainPolicy : domainPolicyService
				.findAll()) {
			domainPolicies.add(new DomainPolicyDto(domainPolicy));
		}
		return domainPolicies;
	}

	@Override
	public DomainPolicyDto create(DomainPolicyDto dto) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "policy dto must be set.");
		Validate.notEmpty(dto.getLabel(), "policy identifier must be set.");
		DomainPolicy policy = new DomainPolicy(dto.getLabel());
		policy.setDomainAccessPolicy(transformToDomainAccessPolicy(dto.getAccessPolicy()));
		return new DomainPolicyDto(domainPolicyService.create(policy));
	}

	@Override
	public DomainPolicyDto update(DomainPolicyDto dto)
			throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "policy dto must be set.");
		Validate.notEmpty(dto.getIdentifier(), "policy identifier must be set.");
		DomainPolicy policy = new DomainPolicy(dto);
		policy.setDomainAccessPolicy(transformToDomainAccessPolicy(dto.getAccessPolicy()));
		return new DomainPolicyDto(domainPolicyService.update(policy));
	}

	private DomainAccessPolicy transformToDomainAccessPolicy(DomainAccessPolicyDto dapDto) throws BusinessException {
		Validate.notNull(dapDto, "DomainAccessPolicyDto can not be null.");
		DomainAccessPolicy dap = new DomainAccessPolicy();
		List<DomainAccessRule> rules = dap.getRules();
		if(Objects.isNull(rules)) {
			rules = Lists.newArrayList();
		}
		for (DomainAccessRuleDto ruleDto : dapDto.getRules()) {
			Validate.notNull(ruleDto.getType(), "Rule type dto must be set.");


			DomainAccessRule rule;
			AbstractDomain domain;
			switch (ruleDto.getType()) {
			case ALLOW_ALL:
				rule = new AllowAllDomain();
				break;
			case DENY_ALL:
				rule = new DenyAllDomain();
				break;
			case ALLOW:
				Validate.notNull(ruleDto.getDomain(), "Domain dto must be set.");
				Validate.notEmpty(ruleDto.getDomain().getIdentifier(), "Domain identifier must be set.");
				domain = domainBusinessService.findById(ruleDto.getDomain()
						.getIdentifier());
				rule = new AllowDomain(domain);
				break;
			case DENY:
				Validate.notNull(ruleDto.getDomain(), "Domain dto must be set.");
				Validate.notEmpty(ruleDto.getDomain().getIdentifier(), "Domain identifier must be set.");
				domain = domainBusinessService.findById(ruleDto.getDomain()
						.getIdentifier());
				rule = new DenyDomain(domain);
				break;
			default:
				throw new IllegalArgumentException();
			}
			rules.add(rule);
		}
		dap.setRules(rules);
		return dap;
	}

	@Override
	public DomainPolicyDto delete(String identifier)
			throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(identifier, "Domain policy identifier must be set");
		DomainPolicy policy = domainPolicyService.delete(identifier);
		return new DomainPolicyDto(policy);
	}

	public void assign(String domainUuid, String domainPolicyUuid) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid must be set.");
		Validate.notEmpty(domainPolicyUuid, "MimePolicy uuid must be set.");
		domainPolicyService.assign(actor, domainUuid, domainPolicyUuid);
	}
}
