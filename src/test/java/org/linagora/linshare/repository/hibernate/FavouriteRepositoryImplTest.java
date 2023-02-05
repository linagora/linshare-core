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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Abstract class that test CRUD methods.
 *
 * @param T entity type.
 */
@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class FavouriteRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	// default import.sql
 	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

	@Autowired
	private FavouriteRepository<String, User, RecipientFavourite> favouriteRepository;
	
	@Autowired
	private GuestRepository userRepo;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	private AbstractDomain domain;
	
	@BeforeEach
	public void setUp() {
		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		
		Guest robert = new Guest("robert", "lechat", "robert.lechat@linagora.com","secret", true, "comment");
		robert.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		robert.setDomain(domain);
		
		Guest jean = new Guest("jean", "lechat", "jean.lechat@linagora.com","secret", true, "comment");
		jean.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		jean.setDomain(domain);
		
		Guest pierre = new Guest("pierre", "lechat", "pierre.lechat@linagora.com","secret", true,"comment");
		pierre.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		pierre.setDomain(domain);
		
		try {
			userRepo.create(robert);
			userRepo.create(jean);
			userRepo.create(pierre);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	@DirtiesContext
	public void testIncAndCreate(){
		
		ArrayList<String> array=new ArrayList<String>();
		array.add("jean.lechat@linagora.com");
		array.add("pierre.lechat@linagora.com");
		try {
			favouriteRepository.incAndCreate(userRepo.findByMail("robert.lechat@linagora.com"), array, null);
		} catch (LinShareNotSuchElementException e) {
			Assertions.fail();
		} catch (BusinessException e) {
			Assertions.fail();
		}
		
		try {
			//Check the weight
			Assertions.assertTrue(favouriteRepository.getWeight("jean.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(Long.valueOf(1)));
			Assertions.assertTrue(favouriteRepository.getWeight("pierre.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(Long.valueOf(1)));
			
			//Check if it's incremented correctly.
			favouriteRepository.incAndCreate(userRepo.findByMail("robert.lechat@linagora.com"), array, null);
			Assertions.assertTrue(favouriteRepository.getWeight("jean.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(Long.valueOf(2)));
			Assertions.assertTrue(favouriteRepository.getWeight("pierre.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(Long.valueOf(2)));
			
			//Check if it's incremented.
			Calendar calendar = new GregorianCalendar();
			calendar.add(Calendar.DAY_OF_WEEK, 2);
			favouriteRepository.inc(array, userRepo.findByMail("robert.lechat@linagora.com"), calendar.getTime());
			Assertions.assertTrue(favouriteRepository.getWeight("jean.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(Long.valueOf(3)));
			Assertions.assertTrue(favouriteRepository.getWeight("pierre.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(Long.valueOf(3)));
			
			//Check the order after retrieving by getElementsOrderByWeightDesc and getElementsOrderByWeight.
			array.remove(1);
			
			favouriteRepository.inc(array, userRepo.findByMail("robert.lechat@linagora.com"), calendar.getTime());
			
			
			Assertions.assertTrue(favouriteRepository.getElementsOrderByWeightDesc(userRepo.findByMail("robert.lechat@linagora.com")).get(0).equals("jean.lechat@linagora.com"));
			Assertions.assertTrue(favouriteRepository.getElementsOrderByWeightDesc(userRepo.findByMail("robert.lechat@linagora.com")).get(1).equals("pierre.lechat@linagora.com"));
			
			Assertions.assertTrue(favouriteRepository.getElementsOrderByWeight(userRepo.findByMail("robert.lechat@linagora.com")).get(1).equals("jean.lechat@linagora.com"));
			Assertions.assertTrue(favouriteRepository.getElementsOrderByWeight(userRepo.findByMail("robert.lechat@linagora.com")).get(0).equals("pierre.lechat@linagora.com"));
			
			
			//Check the element with max weight
			Assertions.assertTrue(favouriteRepository.getElementWithMaxWeight(userRepo.findByMail("robert.lechat@linagora.com")).equals("jean.lechat@linagora.com"));
			
			//Crazy test just for fun
			Assertions.assertTrue(
					favouriteRepository.reorderElementsByWeightDesc(
							favouriteRepository.getElementsOrderByWeight(userRepo.findByMail("robert.lechat@linagora.com"))
							,userRepo.findByMail("robert.lechat@linagora.com")).equals(
									favouriteRepository.getElementsOrderByWeightDesc(userRepo.findByMail("robert.lechat@linagora.com"))));
			
			//Check the existing of a favourite.
			Assertions.assertFalse(favouriteRepository.existFavourite(userRepo.findByMail("robert.lechat@linagora.com"), "pierre.lechien@linagora.com"));
			Assertions.assertTrue(favouriteRepository.existFavourite(userRepo.findByMail("robert.lechat@linagora.com"), "pierre.lechat@linagora.com"));
			
			
		
		} catch (LinShareNotSuchElementException e) {
			Assertions.fail();
		} catch (BusinessException e) {
			Assertions.fail();
		}
		
	}
	
	
	
	@AfterEach
	public void after(){
		for(RecipientFavourite recipientFavourite:favouriteRepository.findAll()){
			try {
				favouriteRepository.delete(recipientFavourite);
			} catch (IllegalArgumentException e) {
				Assertions.fail();
			} catch (BusinessException e) {
				Assertions.fail();
			}
		}
		
	}

}

