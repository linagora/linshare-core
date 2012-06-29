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
package org.linagora.linshare.service;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.common.service.MailTestRetriever;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Reason;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.ShareRepository;
import org.linagora.linshare.core.service.DocumentService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;
import org.subethamail.wiser.Wiser;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class ShareServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory.getLogger(ShareServiceImplTest.class);

	
	private User sender;

	private User recipient;

	private InputStream inputStream;

	private Document document;

	private TransactionTemplate transactionTemplate;

	@Autowired
	private ShareService shareService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ShareRepository shareRepository;
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private FunctionalityService functionalityService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private MailTestRetriever mailTestRetriever;

	@Resource(name = "transactionManager")
	private HibernateTransactionManager tx;
	
	private Wiser wiser;
	private User owner;
	private Document myDocForTest;
	private boolean guestFuncStatus;
	
	
	public ShareServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();

		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LinShareConstants.rootDomainIdentifier);
		Functionality guestFunctionality = functionalityService.getGuestFunctionality(rootDomain);
		guestFuncStatus = guestFunctionality.getActivationPolicy().getStatus();
		guestFunctionality.getActivationPolicy().setStatus(true);
		functionalityService.update(rootDomain, guestFunctionality);

		try {
			owner = userService
					.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
			
			Assert.assertNotNull(owner);
			
			inputStream = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("linShare-default.properties");

														
			MailContainer mailContainer = new MailContainer("", Language.DEFAULT);
			sender = userService.createGuest(
						mailTestRetriever.getSenderMail(),
						"sender", "senderName",
						mailTestRetriever.getSenderMail(),
						true, true, "comment", mailContainer,
						owner.getLogin(), owner.getDomainId());
				
			recipient = userService.createGuest(
					mailTestRetriever.getRecipientMail(),
					"receiver", "receiverName",
					mailTestRetriever.getRecipientMail(),
					true, true, "comment", mailContainer,
					owner.getLogin(), owner.getDomainId());

		} catch (BusinessException e1) {
			e1.printStackTrace();
			Assert.assertTrue(false);
		}

		/**
		 * Creating documents for testing.
		 */
		try {
			Assert.assertNotNull(owner);
			if (documentRepository.findAll().size() > 1) {
				Assert.fail("Too much doc in the repository");
			}
			if (documentRepository.findAll().size() == 0) {
				logger.info("inserting a new file for tests");
				myDocForTest = documentService.insertFile(sender.getLogin(),
						inputStream, 20000,
						"linShare.properties", documentService
								.getMimeType(inputStream,
										"linShare.properties"),
						owner);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

		
		logger.debug(LinShareTestConstants.END_SETUP);

	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);

//		this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//			public void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					documentService.deleteFile(mailTestRetriever.getSenderMail(), myDocForTest.getUuid(),Reason.NONE);
					User root = userService.findUserInDB(LinShareConstants.rootDomainIdentifier, "root@localhost.localdomain");
					userService.deleteUser(sender.getLogin(), root);
					userService.deleteUser(recipient.getLogin(), root);
					userService.deleteUser(owner.getLogin(), root);
				} catch (BusinessException e) {
					logger.error("can'delete users during tearDown method !");
					e.printStackTrace();
				}
//			} // end of method doInTransactionWithoutResult
//		} // end of class 
//		); // end of method execute
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LinShareConstants.rootDomainIdentifier);
		Functionality guestFunctionality = functionalityService.getGuestFunctionality(rootDomain);
		guestFunctionality.getActivationPolicy().setStatus(guestFuncStatus);
		functionalityService.update(rootDomain, guestFunctionality);
		
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);

	}
	
	@Test
	public void testRemoveReceivedShareForUser() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, null);
		
		sender.deleteShare(shares.getSuccessesItem().get(0));
		shareService.removeReceivedShareForUser(shares.getSuccessesItem().get(0), recipient, recipient);
		
		Assert.assertTrue(recipient.getReceivedShares().isEmpty());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testRemoveSentShareForUser() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, null);
		
		
		recipient.deleteReceivedShare(shares.getSuccessesItem().get(0));
		shareService.removeSentShareForUser(shares.getSuccessesItem().get(0), sender, sender);
		
		Assert.assertTrue(sender.getShares().isEmpty());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDeleteShare() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, null);
		
		recipient.deleteReceivedShare(shares.getSuccessesItem().get(0));
		sender.deleteShare(shares.getSuccessesItem().get(0));
		//no need to delete share, it will be delete automatically by hibernate
		Assert.assertTrue(recipient.getReceivedShares().isEmpty());
		Assert.assertTrue(sender.getShares().isEmpty());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testShareDocumentsToUser() throws BusinessException, ParseException {

		logger.info(LinShareTestConstants.BEGIN_TEST);
	
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, null);

		Share finalShare = null;
		/**
		 * Check if the success share contains sender and
		 * recipient.
		 */
		for (Share successShare : shares.getSuccessesItem()) {
			if (successShare.getSender().equals(sender) && 
					successShare.getReceiver().equals(recipient) && 
						successShare.getDocument().equals(document)) {
				Assert.assertTrue(true);
				finalShare = successShare;
			} else {
				Assert.assertFalse(true);
			}
		}
		/**
		 * Check if the sender contains the share as sent. And
		 * check if the recipient has the share as received.
		 */

		Assert.assertTrue(recipient.getReceivedShares().contains(finalShare));
		Assert.assertTrue(sender.getShares().contains(finalShare));
		documents.clear();
		recipients.clear();
		
		
		recipient.deleteReceivedShare(shares.getSuccessesItem().get(0));
		sender.deleteShare(shares.getSuccessesItem().get(0));
		//no need to delete share, it will be delete automatically by hibernate
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteAllSharesWithDocument() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, null);
			
		Assert.assertTrue(recipient.getReceivedShares().contains(shares.getSuccessesItem().get(0)));
		Assert.assertTrue(sender.getShares().contains(shares.getSuccessesItem().get(0)));
		
		try{
		shareService.deleteAllSharesWithDocument(document, sender,  new MailContainer("", Language.DEFAULT));
		}catch(TechnicalException e){
			logger.debug("Test succeed, but throw exception on notification");
		}
		
		Assert.assertFalse(recipient.getReceivedShares().contains(shares.getSuccessesItem().get(0)));
		Assert.assertFalse(sender.getShares().contains(shares.getSuccessesItem().get(0)));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRefreshShareAttributeOfDoc() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, null);
		
		
        // remove the share :
		sender.deleteShare(shares.getSuccessesItem().get(0));
        shareService.removeReceivedShareForUser(shares.getSuccessesItem().get(0), recipient, recipient);
		
		Assert.assertTrue(document.getShared());
        shareService.refreshShareAttributeOfDoc(document);
        
        Assert.assertFalse(document.getShared());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testCleanOutdatedShares()throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		
		Functionality functionality = functionalityRepository.findById(domain, FunctionalityNames.SHARE_EXPIRATION);
		functionality .getActivationPolicy().setStatus(true);
		functionalityRepository.update(functionality);
		
		sender.setDomain(domain);
		recipient.setDomain(domain);
		
		userService.saveOrUpdateUser(recipient);
		userService.saveOrUpdateUser(sender);

		
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
	
		
		Calendar cldr = Calendar.getInstance();
		
		//Subtract 2 years from the actual date
		cldr.add(Calendar.YEAR, -2);
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, cldr );		
		
		//recipient.getReceivedShares().remove(shares.getSuccessesItem().get(0));
		//sender.getShares().remove(shares.getSuccessesItem().get(0));
		
		shareService.cleanOutdatedShares();
		
		Assert.assertTrue(shareService.getSharesLinkedToDocument(document).isEmpty());
		
		functionality .getActivationPolicy().setStatus(false);
		functionalityRepository.update(functionality);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testShareDocumentsWithSecuredUrlToUser() throws IllegalArgumentException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		List<Contact> contactList = new ArrayList<Contact>();
		contactList.add(new Contact(recipient.getMail()));
		
		SecuredUrl shares = shareService.shareDocumentsWithSecuredAnonymousUrlToUser(sender, documents, true, contactList, null);
		
		Assert.assertTrue(shares.getSender().equals(sender));
		
		Assert.assertTrue(shares.getDocuments().contains(document));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testGetSecureUrlLinkedToDocument() throws IllegalArgumentException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		List<Contact> contactList = new ArrayList<Contact>();
		contactList.add(new Contact(recipient.getMail()));
		
		SecuredUrl shares = shareService.shareDocumentsWithSecuredAnonymousUrlToUser(sender, documents, true, contactList, null);
		
		Assert.assertTrue(shareService.getSecureUrlLinkedToDocument(document).contains(shares));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testGetSharesLinkedToDocument() throws IllegalArgumentException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> recipients = new ArrayList<User>();
		recipients.add(recipient);
		
		List<Contact> contactList = new ArrayList<Contact>();
		contactList.add(new Contact(recipient.getMail()));
		
		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, sender, recipients, null );		
		
		Assert.assertTrue(shareService.getSharesLinkedToDocument(document).contains(shares.getSuccessesItem().get(0)));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	/**
	 * Create and drop a share directly.
	 */
	@Ignore
	@Test
	public void testCreateAndDropAShareByRemoveUser() {

		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = null;
		User recipient = null;
		try {
			owner = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlDomain);
			recipient = userService.findOrCreateUser(mailTestRetriever.getRecipientMail(), LoadingServiceTestDatas.sqlSubDomain);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail();
		}
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> users = new ArrayList<User>();
		users.add(recipient);

		SuccessesAndFailsItems<Share> shares = shareService.shareDocumentsToUser(documents, owner, users,null);

		Share finalShare = null;
		/**
		 * Check if the success share contains sender and
		 * recipient.
		 */
		for (Share successShare : shares.getSuccessesItem()) {
				
			Assert.assertEquals(successShare.getSender(), owner);
			Assert.assertEquals(successShare.getReceiver(), recipient);
			Assert.assertEquals(successShare.getDocument(), document);
		
			finalShare = successShare;
		}

		/**
		 * Check if the sender contains the share as sent. And
		 * check if the recipient has the share as received.
		 */

		Assert.assertTrue(recipient.getReceivedShares().contains(finalShare));
		Assert.assertTrue(owner.getShares().contains(finalShare));


//		/**
//		 * Now we have our Shares for each user. We will test to
//		 * remove theses shares.
//		 */
//
//		try {
//			owner = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);;
//			userService.deleteUser(mailTestRetriever.getRecipientMail(), owner);
//			
//			sender = userService.findOrCreateUser(mailTestRetriever.getSenderMail(), LoadingServiceTestDatas.sqlSubDomain);
//		} catch (BusinessException e) {
//			e.printStackTrace();
//			Assert.assertFalse(true);
//		}
//		Assert.assertTrue(sender.getShares().size() == 0);
//		Assert.assertTrue(shareRepository.findAll().size() == 0);


		logger.debug(LinShareTestConstants.END_TEST);
	}

	/**
	 * Create and drop a share directly.
	 */
	@Ignore
	@Test
	public void testCreateAndDropAShareByRemoveDocument() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		User owner = null;
		User recipient = null;
		try {
			owner = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
			recipient = userService.findOrCreateUser(mailTestRetriever.getRecipientMail(), LoadingServiceTestDatas.sqlSubDomain);;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// User sender=userService.findUser(MAILSENDER);
		Document document = documentRepository.findAll().get(0);
		ArrayList<Document> documents = new ArrayList<Document>();
		documents.add(document);

		ArrayList<User> users = new ArrayList<User>();
		users.add(recipient);

		SuccessesAndFailsItems<Share> shares = shareService
				.shareDocumentsToUser(documents, owner, users,
						null);

		Share finalShare = null;
		/**
		 * Check if the success share contains sender and
		 * recipient.
		 */
		for (Share successShare : shares.getSuccessesItem()) {
			if (successShare.getSender().equals(owner)
					&& successShare.getReceiver().equals(
							recipient)
					&& successShare.getDocument().equals(
							document)) {
				Assert.assertTrue(true);
				finalShare = successShare;
			} else {
				Assert.assertFalse(true);
			}
		}
		/**
		 * Check if the sender contains the share as sent. And
		 * check if the recipient has the share as received.
		 */

		Assert.assertTrue(recipient.getReceivedShares()
				.contains(finalShare)
				&& owner.getShares().contains(finalShare));

		/**
		 * Now we have our Shares for each user. We will test to
		 * remove theses shares.
		 */

		try {

			recipient = userService.findOrCreateUser(mailTestRetriever.getRecipientMail(), LoadingServiceTestDatas.sqlSubDomain);
			User sender = userService.findOrCreateUser(mailTestRetriever.getRecipientMail(), LoadingServiceTestDatas.sqlSubDomain);
			
			documentService.deleteFile(mailTestRetriever
					.getSenderMail(), documentRepository
					.findAll().get(0).getUuid(),
					Reason.NONE);
			Assert
					.assertTrue(shareRepository.findAll()
							.size() == 0);
			Assert.assertTrue(recipient.getReceivedShares()
					.size() == 0);
			Assert.assertTrue(sender.getShares().size() == 0);

		} catch (BusinessException e) {
			Assert.assertFalse(true);
			e.printStackTrace();
		}

		logger.debug(LinShareTestConstants.END_TEST);
	}

}
