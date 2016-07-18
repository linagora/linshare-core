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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainPolicyBusinessService;
import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainPolicyServiceImpl implements DomainPolicyService {

	private static final Logger logger = LoggerFactory
			.getLogger(DomainPolicyServiceImpl.class);
	private final DomainPolicyBusinessService domainPolicyBusinessService;

	public DomainPolicyServiceImpl(
			DomainPolicyBusinessService domainPolicyBusinessService) {
		super();
		this.domainPolicyBusinessService = domainPolicyBusinessService;
	}

	@Override
	public DomainPolicy delete(String policyToDelete) throws BusinessException {
		return domainPolicyBusinessService.delete(policyToDelete);
	}

	@Override
	public boolean policyIsDeletable(String policyToDelete) {
		return domainPolicyBusinessService.policyIsDeletable(policyToDelete);
	}

	@Override
	public DomainPolicy create(DomainPolicy domainPolicy)
			throws BusinessException {
		if (domainPolicy == null
				|| domainPolicy.getDomainAccessPolicy() == null)
			throw new BusinessException(
					"Creating a domain policy without an associated access policy is impossible");
		DomainPolicy policy = domainPolicyBusinessService.find(domainPolicy.getUuid());
		if (policy != null)
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_POLICY_ALREADY_EXISTS,
					"Domain policy with identifier : "
							+ domainPolicy.getUuid() + " already exists.");
		return domainPolicyBusinessService.create(domainPolicy);
	}

	@Override
	public DomainPolicy update(DomainPolicy domainPolicy) throws BusinessException {
		return domainPolicyBusinessService.update(domainPolicy);
	}

	@Override
	public DomainPolicy find(String identifier) {
		Validate.notEmpty(identifier);
		DomainPolicy domainPolicy = domainPolicyBusinessService
				.find(identifier);
		if (domainPolicy == null)
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,
					"Domain policy with identifer: " + identifier
							+ " not found");
		return domainPolicy;
	}

	@Override
	public List<DomainPolicy> findAll() throws BusinessException {
		return domainPolicyBusinessService.findAll();
	}

	private List<AbstractDomain> getAuthorizedDomain(AbstractDomain domain,
			List<DomainAccessRule> rules) {
		Set<AbstractDomain> set = new HashSet<AbstractDomain>();
		set.add(domain);
		return getAuthorizedDomain(set, rules);
	}

	private List<AbstractDomain> getAuthorizedDomain(
			Set<AbstractDomain> domains, List<DomainAccessRule> rules) {

		List<AbstractDomain> includes = new ArrayList<AbstractDomain>();
		List<AbstractDomain> excludes = new ArrayList<AbstractDomain>();

		for (AbstractDomain domain : domains) {
			logger.debug("check:domain : " + domain.toString());
			for (DomainAccessRule domainAccessRule : rules) {
				logger.debug("check:domainAccessRule : "
						+ domainAccessRule.getDomainAccessRuleType().toString());
				if (domainAccessRule.getDomainAccessRuleType().equals(
						DomainAccessRuleType.ALLOW_ALL)) {
					// logger.debug("check:domainAccessRule : ALLOW_ALL");
					// Allow domain without any check
					if (!includes.contains(domain)
							&& !excludes.contains(domain)) {
						includes.add(domain);
					}
					// This rule should me the last one
					break;
				} else if (domainAccessRule.getDomainAccessRuleType().equals(
						DomainAccessRuleType.DENY_ALL)) {
					// logger.debug("check:domainAccessRule : DENY_ALL");
					// Deny domain without any check
					if (!excludes.contains(domain)) {
						excludes.add(domain);
					}
					// This rule should me the last one
					break;

				} else if (domainAccessRule.getDomainAccessRuleType().equals(
						DomainAccessRuleType.ALLOW)) {
					// logger.debug("check:domainAccessRule : ALLOW");
					// Allow domain
					AllowDomain allowDomain = (AllowDomain) domainAccessRule;

					if (allowDomain.getDomain().equals(domain)
							&& !includes.contains(domain)) {
						logger.debug(" ALLOW : " + domain.getUuid());
						includes.add(domain);
					}

				} else if (domainAccessRule.getDomainAccessRuleType().equals(
						DomainAccessRuleType.DENY)) {
					// Deny domain
					// logger.debug("check:domainAccessRule : DENY");
					DenyDomain denyDomain = (DenyDomain) domainAccessRule;

					if (denyDomain.getDomain().equals(domain)
							&& !excludes.contains(domain)) {
						logger.debug(" DENY : " + domain.getUuid());
						excludes.add(domain);
					} else {
						includes.add(domain);
					}
				}
			}
		}

		return includes;
	}

	@Override
	public boolean isAuthorizedToCommunicateWithItSelf(AbstractDomain domain) {
		logger.debug("Begin isAuthorizedToCommunicateWithItSelf : " + domain);
		List<AbstractDomain> result = getAuthorizedDomain(domain, domain
				.getPolicy().getDomainAccessPolicy().getRules());
		if (result != null && result.size() == 1) {
			logger.debug("Domain '" + domain.getUuid()
					+ "' is authorized to communicate with itself.");
			return true;
		}
		logger.debug("Domain '" + domain.getUuid()
				+ "' is not authorized to communicate with itself.");
		logger.debug("End isAuthorizedToCommunicateWithItSelf : " + domain);
		return false;
	}

	@Override
	public boolean isAuthorizedToCommunicateWithItsParent(AbstractDomain domain) {
		logger.debug("Begin isAuthorizedToCommunicateWithItsParent : " + domain);
		if (domain.getParentDomain() != null) {
			List<AbstractDomain> result = getAuthorizedDomain(
					domain.getParentDomain(), domain.getPolicy()
							.getDomainAccessPolicy().getRules());
			if (result != null && result.size() == 1) {
				logger.debug("Domain '" + domain.getUuid()
						+ "' is authorized to communicate with its parent.");
				return true;
			}
		}
		logger.debug("Domain '" + domain.getUuid()
				+ "' is not authorized to communicate with its parent.");
		logger.debug("End isAuthorizedToCommunicateWithItsParent : " + domain);
		return false;
	}

	@Override
	public List<AbstractDomain> getAuthorizedSubDomain(AbstractDomain domain) {

		logger.debug("Begin public getAuthorizedSubDomain : " + domain);
		List<AbstractDomain> result = new ArrayList<AbstractDomain>();
		List<DomainAccessRule> rules = domain.getPolicy()
				.getDomainAccessPolicy().getRules();

		// Check for communication with subdomains.
		if (domain.getSubdomain() != null) {
			result.addAll(getAuthorizedDomain(domain.getSubdomain(), rules));
		}

		logger.debug("domain result list size : " + result.size());
		for (AbstractDomain abstractDomain : result) {
			logger.debug("result : " + abstractDomain.getUuid());
		}
		logger.debug("End public getAuthorizedSubDomain : " + domain);
		return result;
	}

	@Override
	public List<AbstractDomain> getAuthorizedSibblingDomain(
			AbstractDomain domain) {
		logger.debug("Begin getAuthorizedSibblingDomain : " + domain);
		List<AbstractDomain> result = new ArrayList<AbstractDomain>();
		List<DomainAccessRule> rules = domain.getPolicy()
				.getDomainAccessPolicy().getRules();

		// Check for communication with siblings.
		if (domain.getParentDomain() != null) {
			result.addAll(getAuthorizedDomain(domain.getParentDomain()
					.getSubdomain(), rules));
		}

		logger.debug("domain result list size : " + result.size());
		for (AbstractDomain abstractDomain : result) {
			logger.debug("result : " + abstractDomain.getUuid());
		}
		logger.debug("End getAuthorizedSibblingDomain : " + domain);
		return result;
	}

	private List<AbstractDomain> getAllAuthorizedDomain(AbstractDomain domain,
			List<DomainAccessRule> rules) {
		List<AbstractDomain> result = new ArrayList<AbstractDomain>();

		// Step 1 : check for self communication
		result.addAll(getAuthorizedDomain(domain, rules));

		// Step 2 : check for communication with sub domains.
		if (domain.getSubdomain() != null) {

			for (AbstractDomain sub : domain.getSubdomain()) {
				for (AbstractDomain d : getAllAuthorizedDomain(sub, rules)) {
					if (!result.contains(d)) {
						result.add(d);
					}
				}
			}

		}
		return result;
	}

	private List<AbstractDomain> getAllAuthorizedDomainReverse(
			AbstractDomain domain, List<DomainAccessRule> rules) {
		List<AbstractDomain> result = new ArrayList<AbstractDomain>();

		if (domain.getParentDomain() != null) {
			result.addAll(getAllAuthorizedDomainReverse(
					domain.getParentDomain(), rules));
		}

		// Step 1 : check for self communication
		result.addAll(getAuthorizedDomain(domain, rules));

		// Step 2 : check for communication with sub domains.
		if (domain.getSubdomain() != null) {

			for (AbstractDomain sub : domain.getSubdomain()) {
				for (AbstractDomain d : getAllAuthorizedDomain(sub, rules)) {
					if (!result.contains(d)) {
						result.add(d);
					}
				}
			}

		}
		return result;
	}

	@Override
	public List<AbstractDomain> getAllAuthorizedDomain(AbstractDomain domain) {
		logger.debug("Begin getAllAuthorizedDomain : " + domain);
		List<AbstractDomain> result = new ArrayList<AbstractDomain>();
		List<DomainAccessRule> rules = domain.getPolicy()
				.getDomainAccessPolicy().getRules();

		for (AbstractDomain d : getAllAuthorizedDomain(domain, rules)) {
			if (!result.contains(d)) {
				result.add(d);
			}
		}

		for (AbstractDomain d : getAllAuthorizedDomainReverse(domain, rules)) {
			if (!result.contains(d)) {
				result.add(d);
			}
		}

		logger.debug("domain result list size : " + result.size());
		for (AbstractDomain abstractDomain : result) {
			logger.debug("result : " + abstractDomain.getUuid());
		}
		logger.debug("End getAllAuthorizedDomain : " + domain);
		return result;
	}

}
