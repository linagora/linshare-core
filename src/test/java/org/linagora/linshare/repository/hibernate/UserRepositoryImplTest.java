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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class UserRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String LOGIN = "login";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String MAIL = "mail";
    private static final String PASSWORD = "password";

    private static final String LOGIN2 = "login2";
    private static final String FIRST_NAME2 = "jean";
    private static final String LAST_NAME2 = "laporte";
    private static final String MAIL2 = "jean@jean.com";
    
    private static final String LOGIN3 = "login3";
    private static final String FIRST_NAME3 = "robert";
    private static final String LAST_NAME3 = "lepoint";
    private static final String MAIL3 = "robert@lepoint.com";

 // default import.sql
 	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;
    
    private boolean flag=false;
    
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	private AbstractDomain domain;
	
	@Before
	public void setUp() throws Exception {
		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		
		String encpassword = HashUtils.hashSha1withBase64(PASSWORD.getBytes());		
		if(!flag){
			User u1=new Guest(FIRST_NAME2, LAST_NAME2, MAIL2,encpassword, true, "comment");
			u1.setLocale(domain.getDefaultLocale());
			u1.setDomain(domain);
			userRepository.create(u1);
			
		
			User u2=new Guest(FIRST_NAME3, LAST_NAME3, MAIL3,encpassword, true, "comment");
			u2.setLocale(domain.getDefaultLocale());
			u2.setDomain(domain);
			userRepository.create(u2);
			flag=true;
		}
		
	}

	@Test
	public void testExistUser() throws BusinessException{
		
		String encpassword = HashUtils.hashSha1withBase64(PASSWORD.getBytes());
		
		User u = new Guest( FIRST_NAME, LAST_NAME, MAIL, encpassword, true, "comment");
		u.setLocale(domain.getDefaultLocale());
		u.setDomain(domain);
	
		u = userRepository.create(u);

		Assert.assertTrue(userRepository.exist(u.getLsUuid()));
		Assert.assertFalse(userRepository.exist("toto"));
	}
	
	@Test
	public void testfindUser() throws BusinessException{
		User u = new Internal( FIRST_NAME, LAST_NAME, MAIL, null);
		u.setLocale(domain.getDefaultLocale());
		u.setDomain(domain);
		
		userRepository.create(u);
		
		User userFound = userRepository.findByMailAndDomain(DOMAIN_IDENTIFIER,MAIL);
		Assert.assertNotNull(userFound);
		Assert.assertEquals(FIRST_NAME,userFound.getFirstName());
	}
	
//	@Test
//	public void testShares() throws IllegalArgumentException, BusinessException{
//		
//			
//		User sender=userRepository.findByLogin(LOGIN2);
//		
//		User receiver=userRepository.findByLogin(LOGIN3);
//		
//		
//		
//		
//		
//		/**
//		 * Creation of a document.
//		 */
//		Document document=new Document("document1","hop.txt", "txt", GregorianCalendar.getInstance(),
//				GregorianCalendar.getInstance(), sender, false,
//				true, new Long(100000));		
//		
//		documentRepository.create(document);
//		
//		/**
//		 * Creation of share
//		 */
//		Share share=new Share(sender,receiver,document,"plop",GregorianCalendar.getInstance(),true,false);
//		shareRepository.create(share);
//		
//		sender.addShare(share);
//		receiver.addReceivedShare(share);
//		userRepository.update(sender);
//		userRepository.update(receiver);
//		sender=userRepository.findByLogin(sender.getMail());
//		receiver=userRepository.findByLogin(receiver.getMail());
//		
//		for(Share currentShare:sender.getShares()){
//			System.out.println("Sender: "+currentShare.getSender().getMail());
//			System.out.println("Receiver: "+currentShare.getReceiver().getMail());
//		}
//		
//		
//		
//		
//		if(sender.getShares().contains(share) && receiver.getReceivedShares().contains(share)){
//			Assert.assertTrue(true);
//		}else{
//			Assert.assertFalse(true);
//		}
//	}
//	
	@After
	public void destroyUsers(){
		
	}

	
}
