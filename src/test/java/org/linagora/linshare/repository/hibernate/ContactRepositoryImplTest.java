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
package org.linagora.linshare.repository.hibernate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class ContactRepositoryImplTest {

	private static Logger logger = LoggerFactory.getLogger(ContactRepositoryImplTest.class);

	@Autowired
	private ContactRepository contactRepository;
	
	private String mail = "toto@linagora.com";
	
	@Test
	public void testCreateContact() throws IllegalArgumentException, BusinessException {
	
		Contact contact = new Contact(mail);
		contactRepository.create(contact);
	}
	
	@Test
	public void testFindContact() throws IllegalArgumentException, BusinessException {
	
		
		Contact contact = new Contact(mail);
		contactRepository.create(contact);
		
		Contact c = contactRepository.findByMail(mail);
		logger.debug("mail id : " + c.getPersistenceId());
		Assertions.assertNotNull(c.getPersistenceId());
	}

	
	@Test
	public void testCreateContactTwice() throws IllegalArgumentException, BusinessException {
	
		Contact contact = new Contact(mail);
		contactRepository.create(contact);
		try {
			contactRepository.create(contact);
			Assertions.assertTrue(false);
		} catch (BusinessException e) {
			Assertions.assertTrue(true);
		}
		
		contactRepository.delete(contact);
	}
}
