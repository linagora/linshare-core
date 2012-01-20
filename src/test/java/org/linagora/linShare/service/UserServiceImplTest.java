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
package org.linagora.linShare.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.LinShareConstants;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Internal;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.repository.FunctionalityRepository;
import org.linagora.linShare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//@ContextConfiguration(locations = { 
//		"classpath:springContext-test.xml",
//		"classpath:springContext-datasource.xml",
//		"classpath:springContext-repository.xml",
//		"classpath:springContext-service.xml"		
//		})
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class UserServiceImplTest extends AbstractJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);
	
	private LoadingServiceTestDatas datas;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	
	
	@Before
	@Transactional (propagation=Propagation.REQUIRED)
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
//		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository);
//		datas.loadDatas();
		logger.debug("End setUp");
	}

	@After
	@Transactional (propagation=Propagation.REQUIRED)
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
//		datas.deleteDatas();
		logger.debug("End tearDown");
	}
	
	@Test
	public void testSaveOrUpdateUser() {
		
		AbstractDomain domain = abstractDomainRepository.findById(LinShareConstants.rootDomainIdentifier);
		
		Internal user = new Internal("user1@linpki.org","John","Doe","user1@linpki.org");
		
		logger.info("Trying to create a user without domain : should fail.");
		try {
			userService.saveOrUpdateUser(user);
			Assert.fail("It should fail before this message.");
		} catch (TechnicalException e) {
			logger.debug("TechnicalException raise as planned.");
		}
		
		logger.debug("user id : " + user.getId());
		user.setDomain(domain);
		userService.saveOrUpdateUser(user);
		logger.debug("user id : " + user.getId());
		Assert.assertTrue(user.getCanUpload());
		user.setCanUpload(false);
		userService.saveOrUpdateUser(user);
		Assert.assertFalse(user.getCanUpload());
		
	}
	
	@Test
	public void testFindOrCreateUser() {
		logger.debug("Trying to find or create a user");
		long id_user=0;
		
		try {
			logger.debug("Trying to find john doe: should create this user");
			User user = userService.findOrCreateUserWithDomainPolicies("user1@linpki.org", LinShareConstants.rootDomainIdentifier);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			id_user= user.getId();
			logger.debug("the user created was " + user.getFirstName() + " " + user.getLastName() +" (id=" + user.getId() + ")");
		} catch (BusinessException e) {
			logger.error("userService can not create an user ");
			logger.error(e.toString());
		}
		
		try {
			logger.debug("Trying to find john doe: should return this user");
			User user = userService.findOrCreateUser("user1@linpki.org", LinShareConstants.rootDomainIdentifier);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			// Should be the same user, not a new one.
			Assert.assertEquals(id_user, user.getId());
			logger.debug("the user created was " + user.getFirstName() + " " + user.getLastName() +" (id=" + user.getId() + ")");
			
		} catch (BusinessException e) {
			logger.error("userService can not find an user ");
			logger.error(e.toString());
		}
		
		try {
			logger.debug("Trying to find john doe: should return this user");
			User user = userService.findOrCreateUser("user1@linpki.org", LinShareConstants.rootDomainIdentifier);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			// Should be the same user, not a new one.
			Assert.assertEquals(id_user, user.getId());
			logger.debug("the user created was " + user.getFirstName() + " " + user.getLastName() +" (id=" + user.getId() + ")");
			
		} catch (BusinessException e) {
			logger.error("userService can not find an user ");
			logger.error(e.toString());
		}
	}
		
		
	
	
}
