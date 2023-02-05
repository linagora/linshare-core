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
package org.linagora.linshare.repository.hibernate;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DomainPolicyRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessRepository;

	private static String domainePolicyName0 = "TestPolicy0";

	@Test
	public void testCreateDomainPolicy1() throws BusinessException{
		DomainAccessPolicy accessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(accessPolicy);
		DomainPolicy policy = new DomainPolicy(domainePolicyName0, accessPolicy);

		logger.debug("Current accessPolicy : " + accessPolicy.toString());
		logger.debug("Current policy : " + policy.toString());


		domainPolicyRepository.create(policy);
		Assertions.assertNotNull(policy.getPersistenceId());

		DomainPolicy entityPolicy = domainPolicyRepository.findById(policy.getUuid());

		Assertions.assertTrue(entityPolicy != null );
		logger.debug("My name is : " + entityPolicy.getUuid());
		logger.debug(entityPolicy.getDomainAccessPolicy().toString());

		domainPolicyRepository.delete(entityPolicy);
	}
}
