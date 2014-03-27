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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowAllDomain;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyAllDomain;
import org.linagora.linshare.core.domain.entities.DenyDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.AllowAllDomainVo;
import org.linagora.linshare.core.domain.vo.AllowDomainVo;
import org.linagora.linshare.core.domain.vo.DenyAllDomainVo;
import org.linagora.linshare.core.domain.vo.DenyDomainVo;
import org.linagora.linshare.core.domain.vo.DomainAccessRuleVo;
import org.linagora.linshare.core.domain.vo.DomainPolicyVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainPolicyFacadeImpl implements DomainPolicyFacade {

	private static final Logger logger = LoggerFactory.getLogger(DomainPolicyFacadeImpl.class);

	private final DomainPolicyService domainPolicyService;
	private final UserService userService;
	private final AbstractDomainService abstractDomainService;

	public DomainPolicyFacadeImpl(DomainPolicyService domainPolicyService,UserService userService, AbstractDomainService abstractDomainService) {
		super();
		this.domainPolicyService = domainPolicyService;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
	}

	private boolean isAuthorized(UserVo actorVo) throws BusinessException {
		if (actorVo != null) {
			User actor = userService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());
			if (actor != null) {
				if (actor.getRole().equals(Role.SUPERADMIN)) {
					return true;
				}
				logger.error("you are not authorized.");
			} else {
				logger.error("isAuthorized:actor object is null.");
			}
		} else {
			logger.error("isAuthorized:actorVo object is null.");
		}
		return false;
	}

	@Override
	public List<String> findAllDomainPoliciesIdentifiers() {
		return domainPolicyService.getAllDomainPolicyIdentifiers();
	}

	@Override
	public List<DomainPolicyVo> findAllDomainPolicies() throws BusinessException {
		List<DomainPolicyVo> res = new ArrayList<DomainPolicyVo>();
		
		for (DomainPolicy policy : domainPolicyService.findAllDomainPolicy()) {
			res.add(new DomainPolicyVo(policy));
		}
		return res;
	}

	@Override
	public void createDomainPolicy(UserVo actorVo, DomainPolicyVo domainPolicyVo) throws BusinessException {
		if (isAuthorized(actorVo)) {
			DomainPolicy domainPolicy = new DomainPolicy(domainPolicyVo.getIdentifier(),domainPolicyVo.getPolicyDescription(),new DomainAccessPolicy(domainPolicyVo.getDomainAccessPolicy()));
			domainPolicyService.createDomainPolicy(domainPolicy);
		} else {
			throw new BusinessException("You are not authorized to create a domain policy.");
		}
	}

	@Override
	public DomainPolicyVo retrieveDomainPolicy(String identifier) throws BusinessException {
		DomainPolicy policy = domainPolicyService.retrieveDomainPolicy(identifier);
		return new DomainPolicyVo(policy);
	}

	@Override
	public DomainAccessRuleVo retrieveDomainAccessRule(long persistenceId) throws BusinessException {
		DomainAccessRule rule = domainPolicyService.retrieveDomainAccessRule(persistenceId);
		if (rule instanceof AllowDomain) {
			return new AllowDomainVo(((AllowDomain) rule).getDomain().getIdentifier(),rule.getPersistenceId());

		} else if (rule instanceof AllowAllDomain) {
			return new AllowAllDomainVo(persistenceId);

		} else if (rule instanceof DenyDomain) {
			return new DenyDomainVo(((DenyDomain) rule).getDomain().getIdentifier(), rule.getPersistenceId());

		} else if (rule instanceof DenyAllDomain) {
			return new DenyAllDomainVo(persistenceId);
		}
		return new DomainAccessRuleVo(rule);
	}

	@Override
	public void updateDomainPolicy(UserVo actorVo, DomainPolicyVo domainPolicyVo) throws BusinessException {
		if (isAuthorized(actorVo)) {
			DomainPolicy policy = domainPolicyService.retrieveDomainPolicy(domainPolicyVo.getIdentifier());
			policy.setDescription(domainPolicyVo.getPolicyDescription());
			policy.getDomainAccessPolicy().getRules().clear();

			for (DomainAccessRuleVo domainAccessRuleVo : domainPolicyVo.getDomainAccessPolicy().getRules()) {
				if (domainAccessRuleVo.getPersistenceId() == 0) {
					if (domainAccessRuleVo instanceof AllowDomainVo) {
						AbstractDomain domain = abstractDomainService.retrieveDomain(((AllowDomainVo) domainAccessRuleVo).getDomainIdentifier());
						policy.getDomainAccessPolicy().addRule(new AllowDomain(domain));
					} else if (domainAccessRuleVo instanceof DenyDomainVo) {
						AbstractDomain domain = abstractDomainService.retrieveDomain(((DenyDomainVo) domainAccessRuleVo).getDomainIdentifier());
						policy.getDomainAccessPolicy().addRule(new DenyDomain(domain));
					} else if (domainAccessRuleVo instanceof AllowAllDomainVo) {
						policy.getDomainAccessPolicy().addRule(new AllowAllDomain());
					} else {
						policy.getDomainAccessPolicy().addRule(new DenyAllDomain());
					}
				} else {
					policy.getDomainAccessPolicy().addRule(domainPolicyService.retrieveDomainAccessRule(domainAccessRuleVo.getPersistenceId()));
				}
			}
			domainPolicyService.updateDomainPolicy(policy);
		}
	}

	@Override
	public void deletePolicy(UserVo actorVo, String policyToDelete)
			throws BusinessException {
		if (isAuthorized(actorVo)) {
			domainPolicyService.deletePolicy(policyToDelete);
		} else {
			throw new BusinessException(
					"You are not authorized to delete a policy.");
		}
	}

	@Override
	public boolean policyIsDeletable(UserVo actorVo, String policyToDelete)
			throws BusinessException {
		if (isAuthorized(actorVo)) {
			return domainPolicyService.policyIsDeletable(policyToDelete);
		} else
			return false;
	}

	@Override
	public void deleteDomainAccessRule(DomainPolicyVo domainPolicyVo,DomainAccessRuleVo ruleVo) throws BusinessException {
		DomainPolicy policy = domainPolicyService.retrieveDomainPolicy(domainPolicyVo.getIdentifier());
		Iterator<DomainAccessRule> it = policy.getDomainAccessPolicy().getRules().iterator();
		boolean next = true;
		while (it.hasNext() && next == true) {
			DomainAccessRule rule = it.next();
			if (rule.getPersistenceId() == ruleVo.getPersistenceId()) {
				domainPolicyService.deleteDomainAccessRule(policy,rule.getPersistenceId());
				next = false;
			}
		}

	}

	@Override
	public void insertRuleOnTopOfList(DomainPolicyVo policyVo,DomainAccessRuleVo ruleVo) {
		List<DomainAccessRuleVo> list = new ArrayList<DomainAccessRuleVo>();
		list.add(ruleVo);
		
		for (DomainAccessRuleVo domainAccessRuleVo : policyVo.getDomainAccessPolicy().getRules()) {
			list.add(domainAccessRuleVo);
		}
		policyVo.getDomainAccessPolicy().getRules().clear();
		policyVo.getDomainAccessPolicy().setRules(list);
	}

	@Override
	public String checkDomainPolicyIdentifier(String value) {
		List<String> policiesIdentifiers = domainPolicyService.getAllDomainPolicyIdentifiers();
		int i = 0;
		String copy = value;
		
		for (String policyIdentifier : policiesIdentifiers) {
			while (policyIdentifier.equals(copy)) {
				copy = value + i;
				i++;
			}
		}
		return copy;
	}

	@Override
	public void setAndSortDomainAccessRuleList(DomainPolicyVo policyVo,String tabPos) throws BusinessException {
		String[] domainIdentifiers = tabPos.split(";");
		List<DomainAccessRuleVo> rules = new ArrayList<DomainAccessRuleVo>();

		for (String domainIdentifier : domainIdentifiers) {
			if (!domainIdentifier.isEmpty()) {
				DomainAccessRuleVo ruleVo = this.retrieveDomainAccessRule(Long.parseLong(domainIdentifier));
				rules.add(ruleVo);
			}
		}
		policyVo.getDomainAccessPolicy().getRules().clear();
		policyVo.getDomainAccessPolicy().setRules(rules);
	}

	@Override
	public DomainAccessRuleVo getDomainAccessRuleVoFromSelect( DomainAccessRuleType rule, String domainSelection) {
		switch (rule.toInt()) {
		case 0:
			return new AllowAllDomainVo();
		case 1:
			return new DenyAllDomainVo();
		case 2:
			return new AllowDomainVo(domainSelection);
		case 3:
			return new DenyDomainVo(domainSelection);
		}
		return null;
	}

}
