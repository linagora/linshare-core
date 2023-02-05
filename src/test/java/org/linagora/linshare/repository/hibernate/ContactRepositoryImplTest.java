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
