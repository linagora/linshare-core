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
package org.linagora.linshare.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.hibernate.RecipientFavouriteRepositoryImpl;
import org.linagora.linshare.core.service.RecipientFavouriteService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class RecipientFavouriteServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	private static Logger logger = LoggerFactory.getLogger(RecipientFavouriteServiceImplTest.class);

	@Autowired
	private RecipientFavouriteService recipientFavouriteService;
	
	@Qualifier("recipientFavouriteRepository")
	@Autowired
	private RecipientFavouriteRepositoryImpl favouriteRepository;	
	
	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;
	
	@Autowired
	private UserService userService;
	
	private LoadingServiceTestDatas datas;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		datas.deleteUsers();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	
	@Test
	public void testIncrement() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		recipientFavouriteService.increment(owner, recipients);
		
		Assert.assertTrue(favouriteRepository.existFavourite(owner, user2));
		Assert.assertTrue(favouriteRepository.existFavourite(owner, user3));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testRecipientsOrderedByWeightDesc() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);
			
		favouriteRepository.incAndCreate(recipients,owner);
	
		favouriteRepository.inc(user3, owner);

		List<String> elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeight(owner);
		Assert.assertTrue(elementsOrderByWeightDesc.get(1).equals(user3));
		Assert.assertTrue(elementsOrderByWeightDesc.get(0).equals(user2));
		
		List<String> recipientsOrderedByWeightDesc = recipientFavouriteService.recipientsOrderedByWeightDesc(owner);
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(1).equals(user2));
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(0).equals(user3));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testReorderRecipientsByWeightDesc() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		favouriteRepository.incAndCreate(recipients,owner);
		
		favouriteRepository.inc(user3, owner);
		
		List<String> recipientsOrderedByWeightDesc = recipientFavouriteService.reorderRecipientsByWeightDesc(recipients,owner);
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(0).equals(user3));
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(1).equals(user2));
		
		List<String> elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeightDesc(owner);
		Assert.assertTrue(elementsOrderByWeightDesc.get(0).equals(user3));
		Assert.assertTrue(elementsOrderByWeightDesc.get(1).equals(user2));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindRecipientFavorite() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		favouriteRepository.incAndCreate(recipients,owner);
		
		favouriteRepository.inc(user3, owner);
		Assert.assertFalse(recipientFavouriteService.findRecipientFavorite( datas.getUser3().getMail(), owner).isEmpty());
		Assert.assertTrue(recipientFavouriteService.findRecipientFavorite( "failMail@mail.com", owner).isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDeleteFavoritesOfUser() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User owner = datas.getUser1();
		
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user3);

		favouriteRepository.incAndCreate(recipients,owner);
		
		Assert.assertFalse(recipientFavouriteService.findRecipientFavorite(datas.getUser3().getMail(), owner).isEmpty());
		recipientFavouriteService.deleteFavoritesOfUser(owner);
		Assert.assertTrue(recipientFavouriteService.findRecipientFavorite( datas.getUser3().getMail(), owner).isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
