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
package org.linagora.linshare.repository.hibernate;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/** Abstract class that test CRUD methods.
 *
 * @param T entity type.
 */
@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class FavouriteRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private FavouriteRepository<String, User, RecipientFavourite> favouriteRepository;
	
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<Guest> userRepo;
	
	
	
	@Before
	public void setUp(){
		Guest robert=new Guest( "robert", "lechat", "robert.lechat@linagora.com","secret", true, "comment");
		Guest jean=new Guest("jean", "lechat", "jean.lechat@linagora.com","secret", true, "comment");
		Guest pierre=new Guest("pierre", "lechat", "pierre.lechat@linagora.com","secret", true,"comment");
		
		try {
			userRepo.create(robert);
			userRepo.create(jean);
			userRepo.create(pierre);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
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
			favouriteRepository.incAndCreate(array, userRepo.findByMail("robert.lechat@linagora.com"));
		} catch (LinShareNotSuchElementException e) {
			Assert.fail();
		} catch (BusinessException e) {
			Assert.fail();
		}
		
		try {
			//Check the weight
			Assert.assertTrue(favouriteRepository.getWeight("jean.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(new Long(1)));
			Assert.assertTrue(favouriteRepository.getWeight("pierre.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(new Long(1)));
			
			//Check if it's incremented correctly.
			favouriteRepository.incAndCreate(array, userRepo.findByMail("robert.lechat@linagora.com"));
			Assert.assertTrue(favouriteRepository.getWeight("jean.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(new Long(2)));
			Assert.assertTrue(favouriteRepository.getWeight("pierre.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(new Long(2)));
			
			//Check if it's incremented.
			favouriteRepository.inc(array, userRepo.findByMail("robert.lechat@linagora.com"));
			Assert.assertTrue(favouriteRepository.getWeight("jean.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(new Long(3)));
			Assert.assertTrue(favouriteRepository.getWeight("pierre.lechat@linagora.com", userRepo.findByMail("robert.lechat@linagora.com")).equals(new Long(3)));
			
			//Check the order after retrieving by getElementsOrderByWeightDesc and getElementsOrderByWeight.
			array.remove(1);
			
			favouriteRepository.inc(array, userRepo.findByMail("robert.lechat@linagora.com"));
			
			
			Assert.assertTrue(favouriteRepository.getElementsOrderByWeightDesc(userRepo.findByMail("robert.lechat@linagora.com")).get(0).equals("jean.lechat@linagora.com"));
			Assert.assertTrue(favouriteRepository.getElementsOrderByWeightDesc(userRepo.findByMail("robert.lechat@linagora.com")).get(1).equals("pierre.lechat@linagora.com"));
			
			Assert.assertTrue(favouriteRepository.getElementsOrderByWeight(userRepo.findByMail("robert.lechat@linagora.com")).get(1).equals("jean.lechat@linagora.com"));
			Assert.assertTrue(favouriteRepository.getElementsOrderByWeight(userRepo.findByMail("robert.lechat@linagora.com")).get(0).equals("pierre.lechat@linagora.com"));
			
			
			//Check the element with max weight
			Assert.assertTrue(favouriteRepository.getElementWithMaxWeight(userRepo.findByMail("robert.lechat@linagora.com")).equals("jean.lechat@linagora.com"));
			
			//Crazy test just for fun
			Assert.assertTrue(
					favouriteRepository.reorderElementsByWeightDesc(
							favouriteRepository.getElementsOrderByWeight(userRepo.findByMail("robert.lechat@linagora.com"))
							,userRepo.findByMail("robert.lechat@linagora.com")).equals(
									favouriteRepository.getElementsOrderByWeightDesc(userRepo.findByMail("robert.lechat@linagora.com"))));
			
			//Check the existing of a favourite.
			Assert.assertFalse(favouriteRepository.existFavourite(userRepo.findByMail("robert.lechat@linagora.com"), "pierre.lechien@linagora.com"));
			Assert.assertTrue(favouriteRepository.existFavourite(userRepo.findByMail("robert.lechat@linagora.com"), "pierre.lechat@linagora.com"));
			
			
		
		} catch (LinShareNotSuchElementException e) {
			Assert.fail();
		} catch (BusinessException e) {
			Assert.fail();
		}
		
	}
	
	
	
	@After
	public void after(){
		for(RecipientFavourite recipientFavourite:favouriteRepository.findAll()){
			try {
				favouriteRepository.delete(recipientFavourite);
			} catch (IllegalArgumentException e) {
				Assert.fail();
			} catch (BusinessException e) {
				Assert.fail();
			}
		}
		
	}

}

