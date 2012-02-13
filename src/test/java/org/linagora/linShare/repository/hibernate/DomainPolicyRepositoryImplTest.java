/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.repository.hibernate;

import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linShare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linShare.core.domain.entities.DomainPolicy;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DomainPolicyRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	private static String domainePolicyName0 = "TestPolicy0";
	
	@Test
	public void testCreateDomainPolicy1() throws BusinessException{
		DomainAccessPolicy accessPolicy = new DomainAccessPolicy();
		DomainPolicy policy = new DomainPolicy(domainePolicyName0, accessPolicy);
				
		logger.debug("Current accessPolicy : " + accessPolicy.toString());
		logger.debug("Current policy : " + policy.toString());
		
		domainPolicyRepository.create(policy);
		Assert.assertNotNull(policy.getPersistenceId());
		
		DomainPolicy entityPolicy = domainPolicyRepository.findById(policy.getIdentifier());
		
		Assert.assertTrue(entityPolicy != null );
		logger.debug("My name is : " + entityPolicy.getIdentifier());
		logger.debug(entityPolicy.getDomainAccessPolicy().toString());
		
		domainPolicyRepository.delete(entityPolicy);
	}
}
