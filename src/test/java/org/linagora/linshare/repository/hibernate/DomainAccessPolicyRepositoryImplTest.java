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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
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
public class DomainAccessPolicyRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DomainAccessPolicyRepository domainAccessPolicyRepository;

	@Test
	public void testCreateDomainAccessRule1() throws BusinessException{
		DomainAccessPolicy policy = new DomainAccessPolicy();
		logger.debug("Current policy : " + policy.toString());
		
		domainAccessPolicyRepository.create(policy);
		Assertions.assertNotNull(policy.getPersistenceId());
		
		DomainAccessPolicy entityPolicy = domainAccessPolicyRepository.findById(policy.getPersistenceId());
		
		Assertions.assertTrue(entityPolicy != null );
		domainAccessPolicyRepository.delete(entityPolicy);
	}
	
	@Test
	public void testCreateDomainAccessRule2() throws BusinessException{
		
		DomainAccessPolicy policy = new DomainAccessPolicy();
		logger.debug("Current policy : " + policy.toString());
		
		domainAccessPolicyRepository.create(policy);
		
		DomainAccessPolicy entityPolicy = domainAccessPolicyRepository.findById(policy.getPersistenceId());
		
		Assertions.assertTrue(entityPolicy != null );
		
		domainAccessPolicyRepository.delete(entityPolicy);
	}
}
