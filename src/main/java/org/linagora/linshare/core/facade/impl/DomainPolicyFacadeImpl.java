/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
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
import org.linagora.linshare.core.service.UserAndDomainMultiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainPolicyFacadeImpl implements DomainPolicyFacade {

	private static final Logger logger = LoggerFactory.getLogger(DomainPolicyFacadeImpl.class);

	private final DomainPolicyService domainPolicyService;
	private final UserAndDomainMultiService userAndDomainMultiService;
	private final AbstractDomainService abstractDomainService;

	public DomainPolicyFacadeImpl(DomainPolicyService domainPolicyService,UserAndDomainMultiService userAndDomainMultiService,
			AbstractDomainService abstractDomainService) {
		super();
		this.domainPolicyService = domainPolicyService;
		this.userAndDomainMultiService = userAndDomainMultiService;
		this.abstractDomainService = abstractDomainService;
	}

	private boolean isAuthorized(UserVo actorVo) throws BusinessException {
		if (actorVo != null) {
			User actor = userAndDomainMultiService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());
			if (actor != null) {
				if (actor.getRole().equals(Role.SUPERADMIN)) {
					return true;
				}
				logger.error("you are not authorised.");
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
			throw new BusinessException(
					"You are not authorized to create a domain policy.");
		}
	}

	@Override
	public DomainPolicyVo retrieveDomainPolicy(String identifier) throws BusinessException {
		DomainPolicy policy = domainPolicyService.retrieveDomainPolicy(identifier);
		return new DomainPolicyVo(policy);
	}

	@Override
	public DomainAccessRuleVo retrieveDomainAccessRule(long persistenceId)	throws BusinessException {
		DomainAccessRule rule = domainPolicyService.retrieveDomainAccessRule(persistenceId);
		if (rule instanceof AllowDomain) {
			AllowDomainVo allowDomain = new AllowDomainVo(((AllowDomain) rule).getDomain().getIdentifier());
			allowDomain.setPersistenceId(persistenceId);
			return allowDomain;

		} else if (rule instanceof AllowAllDomain) {
			AllowAllDomainVo allowAll = new AllowAllDomainVo();
			allowAll.setPersistenceId(persistenceId);
			return allowAll;

		} else if (rule instanceof DenyDomain) {
			DenyDomainVo denyDomain = new DenyDomainVo(((DenyDomain) rule).getDomain().getIdentifier());
			denyDomain.setPersistenceId(persistenceId);
			return denyDomain;

		} else if (rule instanceof DenyAllDomain) {
			DenyAllDomainVo denyAll = new DenyAllDomainVo();
			denyAll.setPersistenceId(persistenceId);
			return denyAll;
		}
		return new DomainAccessRuleVo(rule);
	}

	@Override
	public void updateDomainPolicy(UserVo actorVo, DomainPolicyVo domainPolicyVo) throws BusinessException {
		if (isAuthorized(actorVo)) {
			DomainPolicy policy = domainPolicyService.retrieveDomainPolicy(domainPolicyVo.getIdentifier());

			policy.setDescription(domainPolicyVo.getPolicyDescription());

			policy.getDomainAccessPolicy().getRules().clear();
			for (DomainAccessRuleVo current : domainPolicyVo.getDomainAccessPolicy().getRules()) {
				if (current.getPersistenceId() == 0) {
					if (current instanceof AllowDomainVo) {
						AbstractDomain domain = abstractDomainService.retrieveDomain(((AllowDomainVo) current).getDomainIdentifier());
						policy.getDomainAccessPolicy().addRule(new AllowDomain(domain));
					} else if (current instanceof DenyDomainVo) {
						AbstractDomain domain = abstractDomainService.retrieveDomain(((DenyDomainVo) current).getDomainIdentifier());
						policy.getDomainAccessPolicy().addRule(new DenyDomain(domain));
					} else if (current instanceof AllowAllDomainVo) {
						policy.getDomainAccessPolicy().addRule(new AllowAllDomain());
					} else {
						policy.getDomainAccessPolicy().addRule(new DenyAllDomain());
					}
				} else
					policy.getDomainAccessPolicy().addRule(domainPolicyService.retrieveDomainAccessRule(current.getPersistenceId()));
			}
			domainPolicyService.updateDomainPolicy(policy);
		}
	}

	@Override
	public void deletePolicy(String policyToDelete, UserVo actorVo)	throws BusinessException {
		if (isAuthorized(actorVo)) {
			domainPolicyService.deletePolicy(policyToDelete);
		} else {
			throw new BusinessException(
					"You are not authorized to delete a policy.");
		}
	}

	@Override
	public boolean policyIsDeletable(String policyToDelete, UserVo actorVo)	throws BusinessException {
		if (isAuthorized(actorVo)) {
			return domainPolicyService.policyIsDeletable(policyToDelete);
		} else
			return false;
	}

	public void deleteDomainAccessRule(DomainAccessRuleVo ruleVo, DomainPolicyVo domainPolicyVo) throws BusinessException {
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

	public void insertOnTop(DomainPolicyVo policyVo, DomainAccessRuleVo ruleVo) {
		List<DomainAccessRuleVo> list = new ArrayList<DomainAccessRuleVo>();
		list.add(ruleVo);
		for (DomainAccessRuleVo current : policyVo.getDomainAccessPolicy().getRules()) {
			list.add(current);
		}
		policyVo.getDomainAccessPolicy().getRules().clear();
		policyVo.getDomainAccessPolicy().setRules(list);
	}

	public void sortDomainAccessRules(DomainPolicyVo policyVo,
		List<DomainAccessRuleVo> rulesVo) throws BusinessException {
		List<DomainAccessRuleVo> list = new ArrayList<DomainAccessRuleVo>();
		for (DomainAccessRuleVo ruleVo : rulesVo) {
			DomainAccessRuleVo rule = this.retrieveDomainAccessRule(ruleVo.getPersistenceId());
			list.add(rule);
		}
		policyVo.getDomainAccessPolicy().getRules().clear();
		policyVo.getDomainAccessPolicy().setRules(list);
	}

}
