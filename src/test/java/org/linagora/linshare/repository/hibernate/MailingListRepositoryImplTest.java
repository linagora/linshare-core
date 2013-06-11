/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class MailingListRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private MailingListRepository mailingListRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;
	
	private static String mailingListName0 = "TestMailingList0";
	private static List<String> mails = Arrays.asList("toto@mail.fr", "tata@mail.fr");
	
	@Test
	public void testCreateMailingList1() throws BusinessException{
		
		MailingList current=new MailingList(mailingListName0,mails);
		logger.debug("Current listId : " + current.getIdentifier());
		logger.debug("Mails:");
		for(String actual : current.getMails())
		{
			logger.debug(actual);
		}
		
		User owner=userRepository.findByMail("bart.simpson@int1.linshare.dev");
		current.setOwner(owner);
		logger.debug("Owner: "+current.getOwner().getFirstName()+" "+current.getOwner().getLastName());
		
		current.setPublic(true);
		logger.debug("Visibility: "+current.isPublic());
		
		AbstractDomain entityDomain = abstractDomainRepository.findById("MySubDomain");
		current.setDomain(entityDomain);
		logger.debug("Domain: "+current.getDomain().getIdentifier());
		
		mailingListRepository.create(current);
		Assert.assertNotNull(current.getPersistenceId());
		
		
		
		MailingList myList = mailingListRepository.findById(current.getPersistenceId());
		Assert.assertTrue(myList != null );
		logger.debug("My name is : " + myList.getIdentifier());
		logger.debug("Mails:" );
		for(String actual : myList.getMails())
		{
			logger.debug(actual);
		}
		logger.debug("My owner: "+myList.getOwner().getFirstName()+" "+myList.getOwner().getLastName());
		logger.debug("My visibility: "+myList.visibility(myList.isPublic()));
		logger.debug("My Domain: "+myList.getDomain().getIdentifier());
		
		mailingListRepository.delete(myList);
	}
}
