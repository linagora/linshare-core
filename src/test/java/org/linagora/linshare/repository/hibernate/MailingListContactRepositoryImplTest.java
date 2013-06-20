package org.linagora.linshare.repository.hibernate;

import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class MailingListContactRepositoryImplTest extends AbstractJUnit4SpringContextTests  {

	@Autowired
	private MailingListContactRepository mailingListContactRepository ;
	private static String mailName0 = "test@mail.fr";
	
	@Test
	public void testCreateMailingListContact1() throws BusinessException{
		
		MailingListContact contact = new MailingListContact(mailName0);
		logger.debug("mail:"+contact.getMails());
		
		mailingListContactRepository.create(contact);
		Assert.assertNotNull(contact.getPersistenceId());
		
		MailingListContact myList = mailingListContactRepository.findById(contact.getPersistenceId());
		Assert.assertTrue(myList != null );
	}
}
