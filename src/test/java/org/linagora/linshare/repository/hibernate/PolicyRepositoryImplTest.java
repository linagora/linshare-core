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
package org.linagora.linshare.repository.hibernate;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class PolicyRepositoryImplTest  extends AbstractTransactionalJUnit4SpringContextTests{

	
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
		
		Assert.assertEquals(origSize + 1, policyRepository.findAll().size());
	}
	
	@Test
	public void testDeletePolicy() throws BusinessException{
		Policy p = new Policy(Policies.FORBIDDEN);
		policyRepository.create(p);
		Assert.assertNotNull(p.getId());
		Integer origSize = policyRepository.findAll().size();
		
		showContent();
		
		policyRepository.delete(policyRepository.findById(p.getId()));
		Assert.assertEquals(origSize - 1, policyRepository.findAll().size());
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
		
		Assert.assertTrue( policyRepository.findAll().size() >= 3);
		Assert.assertEquals(false, policyRepository.findById(p.getId()).getStatus());
		Assert.assertEquals(p2.getDefaultStatus(), policyRepository.findById(p2.getId()).getStatus());
		Assert.assertEquals(true, policyRepository.findById(p3.getId()).getStatus());
		
	}
	
	@Test
	public void testEqualPolicy1() throws BusinessException{
		Policy p1 = new Policy(Policies.FORBIDDEN);
		Policy p2 = new Policy(Policies.FORBIDDEN);
		p2.setStatus(true);
		p2.applyConsistency();
		Assert.assertTrue(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy2() throws BusinessException{
		Policy p1 = new Policy(Policies.MANDATORY);
		Policy p2 = new Policy(Policies.MANDATORY);
		p2.setStatus(false);
		Assert.assertTrue(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy3() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setStatus(true);
		// Policies.ALLOWED default status is false
		Assert.assertFalse(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy4() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setStatus(false);
		// Policies.ALLOWED default status is false
		Assert.assertTrue(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy5() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setPolicy(Policies.MANDATORY);
		Assert.assertFalse(p1.businessEquals(p2));
	}
	@Test
	public void testEqualPolicy6() throws BusinessException{
		Policy p1 = new Policy(Policies.ALLOWED);
		Policy p2 = new Policy(Policies.ALLOWED);
		p2.setPolicy(Policies.FORBIDDEN);
		Assert.assertFalse(p1.businessEquals(p2));
	}
}
