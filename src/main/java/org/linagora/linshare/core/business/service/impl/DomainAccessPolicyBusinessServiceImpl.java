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
package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.DomainAccessPolicyBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainAccessRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainAccessPolicyBusinessServiceImpl implements
		DomainAccessPolicyBusinessService {

	private static final Logger logger = LoggerFactory
			.getLogger(DomainAccessPolicyBusinessServiceImpl.class);

	private final DomainAccessRuleRepository domainAccessRuleRepository;

	public DomainAccessPolicyBusinessServiceImpl(
			DomainAccessRuleRepository domainAccessRuleRepository) {
		super();
		this.domainAccessRuleRepository = domainAccessRuleRepository;

	}

	@Override
	public DomainAccessRule retrieveDomainAccessRule(long id) {
		return domainAccessRuleRepository.findById(id);
	}

	@Override
	public void deleteDomainAccessRule(long persistenceID)
			throws BusinessException {

		DomainAccessRule rule = retrieveDomainAccessRule(persistenceID);
		if (rule == null) {
			logger.error("rule not found: " + persistenceID);
		} else {
			logger.debug("delete rule: " + persistenceID);
			domainAccessRuleRepository.delete(rule);
		}
	}

	@Override
	public DomainAccessRule find(long id) throws BusinessException {
		DomainAccessRule domainAccessRule = domainAccessRuleRepository
				.findById(id);
		if (domainAccessRule == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Can not find domainAccessRule");
		}
		return domainAccessRule;
	}

	@Override
	public DomainAccessRule create(DomainAccessRule domainAccessRule)
			throws BusinessException {
		return domainAccessRuleRepository.create(domainAccessRule);
	}

	@Override
	public DomainAccessRule update(DomainAccessRule domainAccessRule)
			throws BusinessException {
		return domainAccessRuleRepository.update(domainAccessRule);
	}

	@Override
	public void delete(DomainAccessRule domainAccessRule)
			throws BusinessException {
		domainAccessRuleRepository.delete(find(domainAccessRule
				.getPersistenceId()));
	}

	@Override
	public List<DomainAccessRule> findByDomain(AbstractDomain domain) {
		return domainAccessRuleRepository.findByDomain(domain);
	}

	@Override
	public boolean domainHasPolicyRules(AbstractDomain domain)
			throws BusinessException {
		long numberOfAccessRules = domainAccessRuleRepository
				.countNumberAccessRulesByDomain(domain);
		return numberOfAccessRules > 0;
	}
}
