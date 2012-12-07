package org.linagora.linshare.repository.hibernate;

import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class ContactRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests  {

	@Autowired
	private ContactRepository contactRepository;
	
	private String mail = "toto@linagora.com";
	
	@Test
	public void testCreateContact() throws IllegalArgumentException, BusinessException {
	
		Contact contact = new Contact(mail);
		contactRepository.create(contact);
		contactRepository.delete(contact);
	}
	
	@Test
	public void testFindContact() throws IllegalArgumentException, BusinessException {
	
		
		Contact contact = new Contact(mail);
		contactRepository.create(contact);
		
		Contact c = contactRepository.findByMail(mail);
		logger.debug("mail id : " + c.getPersistenceId());
		Assert.assertNotNull(c.getPersistenceId());
	}

	
	@Test
	public void testCreateContactTwice() throws IllegalArgumentException, BusinessException {
	
		Contact contact = new Contact(mail);
		contactRepository.create(contact);
		try {
			contactRepository.create(contact);
			Assert.assertTrue(false);
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}
		
		contactRepository.delete(contact);
	}
}
