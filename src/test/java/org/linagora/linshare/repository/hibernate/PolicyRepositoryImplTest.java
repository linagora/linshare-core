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


import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class PolicyRepositoryImplTest  {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	@Qualifier("policyRepository")
	private PolicyRepository policyRepository;
	
	
	private void showContent(){
		if(logger.isDebugEnabled()) {
			List<Policy> list = policyRepository.findAll();
			logger.debug("Size=" + list.size());
			for (Policy policy : list) {
				logger.debug(policy.toString());
			}
		}
	}
	
	@Test
	public void testCreatePolicy() throws BusinessException{
		
		Integer origSize = policyRepository.findAll().size();
		Policy p = new Policy(Policies.FORBIDDEN);
		policyRepository.create(p);
		
		showContent();
		
		Assertions.assertEquals(origSize + 1, policyRepository.findAll().size());
	}
	
	@Test
	public void testDeletePolicy() throws BusinessException{
		Policy p = new Policy(Policies.FORBIDDEN);
		policyRepository.create(p);
		Assertions.assertNotNull(p.getId());
		Integer origSize = policyRepository.findAll().size();
		
		showContent();
		
		policyRepository.delete(policyRepository.findById(p.getId()));
		Assertions.assertEquals(origSize - 1, policyRepository.findAll().size());
	}
	
	@Test
	public void testPolicyBehave() throws BusinessException{
		Policy p = new Policy(Policies.FORBIDDEN);
		p.applyConsistency();
		policyRepository.create(p);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.applyConsistency();
		policyRepository.create(p2);
		Policy p3 = new Policy(Policies.MANDATORY);
		p3.applyConsistency();
		policyRepository.create(p3);
		
		showContent();
		
		Assertions.assertTrue( policyRepository.findAll().size() >= 3);
		Assertions.assertEquals(false, policyRepository.findById(p.getId()).getStatus());
		Assertions.assertEquals(p2.getDefaultStatus(), policyRepository.findById(p2.getId()).getStatus());
		Assertions.assertEquals(true, policyRepository.findById(p3.getId()).getStatus());
		
	}
	
	@Test
	public void testEqualPolicy1() throws BusinessException{
		Policy p1 = new Policy(Policies.FORBIDDEN);
		Policy p2 = new Policy(Policies.FORBIDDEN);
		p2.setStatus(true);
		p2.applyConsistency();
		Assertions.assertTrue(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy2() throws BusinessException{
		Policy p1 = new Policy(Policies.MANDATORY);
		Policy p2 = new Policy(Policies.MANDATORY);
		p2.setStatus(false);
		Assertions.assertTrue(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy3() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setStatus(true);
		// Policies.ALLOWED default status is false
		Assertions.assertFalse(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy4() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setStatus(false);
		// Policies.ALLOWED default status is false
		Assertions.assertTrue(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy5() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setPolicy(Policies.MANDATORY);
		Assertions.assertFalse(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy6() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setPolicy(Policies.FORBIDDEN);
		Assertions.assertFalse(p1.businessEquals(p2));
	}
}
