/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.hibernate.RecipientFavouriteRepositoryImpl;
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
public class RecipientFavouriteRepositoryImplTest {
	private static Logger logger = LoggerFactory.getLogger(RecipientFavouriteRepositoryImplTest.class);

	@Qualifier("recipientFavouriteRepository")
	@Autowired
	private RecipientFavouriteRepositoryImpl favouriteRepository;	

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	
	private User user1;  /* John Doe */
	private User user2;	 /* Jane Smith */
	private User user3;	 /* Foo Bar */
	
	public User getUser1() {
		return user1;
	}

	public User getUser2() {
		return user2;
	}

	public User getUser3() {
		return user3;
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);

		user1 = new Internal("John","Doe","user1@linshare.org", null);
		user2 = new Internal("Jane","Smith","user2@linshare.org", null);
		user3 = new Internal("Foo","Bar","user3@linshare.org", null); 

		AbstractDomain userGuestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		user1.setCmisLocale(userGuestDomain.getDefaultTapestryLocale().toString());
		user2.setCmisLocale(userGuestDomain.getDefaultTapestryLocale().toString());
		user3.setCmisLocale(userGuestDomain.getDefaultTapestryLocale().toString());

		user1.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlDomain));
		user2.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain));
		user3.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));

		user1 = userRepository.create(user1);		
		user2 = userRepository.create(user2);
		user3 = userRepository.create(user3);
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		userRepository.delete(user1);
		userRepository.delete(user2);
		userRepository.delete(user3);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	
	@Test
	public void testIncrement() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = getUser1();
		
		String user2 = getUser2().getLogin();
		String user3 = getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		favouriteRepository.incAndCreate(owner, recipients, null);
		
		Assertions.assertTrue(favouriteRepository.existFavourite(owner, user2));
		Assertions.assertTrue(favouriteRepository.existFavourite(owner, user3));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testRecipientsOrderedByWeightDesc() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = user1;
		
		String user2 = getUser2().getMail();
		String user3 = getUser3().getMail();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);
			
		List<String> elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeight(owner);
		
		favouriteRepository.incAndCreate(owner, recipients, null);
	
		elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeight(owner);
		favouriteRepository.incAndCreate(owner, user3, null, false);

		elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeight(owner);
		Assertions.assertTrue(elementsOrderByWeightDesc.get(1).equals(user3));
		Assertions.assertTrue(elementsOrderByWeightDesc.get(0).equals(user2));
		List<String> recipientsOrderedByWeightDesc = favouriteRepository.reorderElementsByWeightDesc(elementsOrderByWeightDesc, owner); 
		Assertions.assertTrue(recipientsOrderedByWeightDesc.get(1).equals(user2));
		Assertions.assertTrue(recipientsOrderedByWeightDesc.get(0).equals(user3));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testReorderRecipientsByWeightDesc() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = getUser1();
		
		String user2 = getUser2().getLogin();
		String user3 = getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		favouriteRepository.incAndCreate(owner, recipients, null);
		
		favouriteRepository.incAndCreate(owner, user3, null, false);
		
		List<String> recipientsOrderedByWeightDesc = favouriteRepository.reorderElementsByWeightDesc(recipients, owner);
		Assertions.assertTrue(recipientsOrderedByWeightDesc.get(0).equals(user3));
		Assertions.assertTrue(recipientsOrderedByWeightDesc.get(1).equals(user2));
		
		List<String> elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeightDesc(owner);
		Assertions.assertTrue(elementsOrderByWeightDesc.get(0).equals(user3));
		Assertions.assertTrue(elementsOrderByWeightDesc.get(1).equals(user2));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindRecipientFavorite() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = getUser1();
		
		String user2 = getUser2().getLogin();
		String user3 = getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		favouriteRepository.incAndCreate(owner, recipients, null);
		
		favouriteRepository.incAndCreate(owner, user3, null, false);
		Assertions.assertFalse(favouriteRepository.findMatchElementsOrderByWeight( getUser3().getMail(), owner).isEmpty());
		Assertions.assertTrue(favouriteRepository.findMatchElementsOrderByWeight( "failMail@mail.com", owner).isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDeleteFavoritesOfUser() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User owner = getUser1();
		
		String user3 = getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user3);

		favouriteRepository.incAndCreate(owner, recipients, null);
		
		Assertions.assertFalse(favouriteRepository.findMatchElementsOrderByWeight(getUser3().getMail(), owner).isEmpty());
		favouriteRepository.deleteFavoritesOfUser(owner);
		Assertions.assertTrue(favouriteRepository.findMatchElementsOrderByWeight( getUser3().getMail(), owner).isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
