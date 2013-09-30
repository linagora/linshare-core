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

import junit.framework.Assert;
import org.junit.Test;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class MailingListRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private MailingListRepository mailingListRepository;
	
	private static String mailingListName0 = "TestMailingList0";
	
	@Test
	public void testCreateMailingList1() throws BusinessException{
		
		MailingList current=new MailingList(mailingListName0);
		logger.debug("Current listId : " + current.getIdentifier());
		current.setPublic(true);
		logger.debug("Visibility: "+current.isPublic());
		current.setDescription("yoyo");

		mailingListRepository.create(current);
		Assert.assertNotNull(current.getPersistenceId());
		
		MailingList myList = mailingListRepository.findByIdentifier(null, "mailingListName0");
		Assert.assertTrue(myList != null );
		logger.debug("My name is : " + myList.getIdentifier());
		logger.debug("My visibility: "+myList.visibility(myList.isPublic()));

		mailingListRepository.delete(myList);
	}
}
