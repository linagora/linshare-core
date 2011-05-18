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
package org.linagora.linShare.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.common.service.LinShareMessageHandler;
import org.linagora.linShare.common.service.MailContentRetriever;
import org.linagora.linShare.common.service.MailTestRetriever;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.constants.Reason;
import org.linagora.linShare.core.domain.constants.TimeUnit;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.WelcomeText;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.DomainRepository;
import org.linagora.linShare.core.repository.ShareRepository;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.ParameterService;
import org.linagora.linShare.core.service.ShareService;
import org.linagora.linShare.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.subethamail.smtp.server.SMTPServer;

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
public class ShareServiceTest extends AbstractJUnit4SpringContextTests {

	private User sender;

	@SuppressWarnings("unused")
	private User recipient;

	private InputStream inputStream;

	@SuppressWarnings("unused")
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
	private MailTestRetriever mailTestRetriever;

	@Resource(name = "transactionManager")
	private HibernateTransactionManager tx;
	
	@Autowired
	private DomainService domainService;
	
	@Autowired
	private DomainRepository domainRepository;
	
	@Autowired
	private ParameterService parameterService;
	
	private static final String DOMAIN_IDENTIFIER = "testDomain";

	private SMTPServer wiser;

	public ShareServiceTest() {
		super();
		wiser = new SMTPServer(new LinShareMessageHandler());
		wiser.setPort(2525);
		
		
	}

	@Before
	public void setUp() throws Exception {
		
			wiser.start();
		
		
		this.transactionTemplate = new TransactionTemplate(tx);
		Parameter param = new Parameter("testParam", new Long(1000000000L), new Long(1000000000L), new Long(1000000000L), new Long(0L), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, 
				Boolean.FALSE, new Integer(100), TimeUnit.DAY, "", TimeUnit.DAY, new Integer(100), TimeUnit.DAY, new Integer(100), 
				new ArrayList<ShareExpiryRule>(), Boolean.FALSE, new HashSet<WelcomeText>(), 
				MailContentRetriever.getMailTemplates(), MailContentRetriever.getMailSubjects(),
				Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		LDAPConnectionVo ldapConnVo = new LDAPConnectionVo("testldap", "ldap://localhost:33389", "anonymous");
		DomainPatternVo patternVo = new DomainPatternVo("testPattern", "testPattern", 
				"ldap.entry(\"uid=\" + userId + \",ou=People,\" + domain, \"objectClass=*\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*))\");",
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|(mail=\"+login+\")(uid=\"+login+\")))\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");", 
				"mail givenName sn");
		LDAPConnection ldapConn = domainService.createLDAPConnection(ldapConnVo);
		DomainPattern pattern = domainService.createDomainPattern(patternVo);
		param = parameterService.saveOrUpdate(param);
		
		Domain domain = new Domain(DOMAIN_IDENTIFIER, "dc=linpki,dc=org", pattern, ldapConn, param);
		
		domainRepository.create(domain);

		/**
		 * Creating the stream for testing.
		 */
		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						User owner = null;
						try {
							owner = userService
									.findAndCreateUser("user1@linpki.org", DOMAIN_IDENTIFIER);
							inputStream = Thread.currentThread()
									.getContextClassLoader()
									.getResourceAsStream("linShare-default.properties");

							if (userService.searchUser(
									mailTestRetriever.getSenderMail(),
									"sender", "senderName", null, owner).size() == 0) {
								MailContainer mailContainer = new MailContainer("", Language.DEFAULT);
								sender = userService.createGuest(
										mailTestRetriever.getSenderMail(),
										"sender", "senderName",
										mailTestRetriever.getSenderMail(),
										true, true, "comment", mailContainer,
										owner.getLogin(), DOMAIN_IDENTIFIER);
							} else {
								sender = userService.findUser(mailTestRetriever
										.getSenderMail(), DOMAIN_IDENTIFIER);
							}
							if (userService.searchUser(
									mailTestRetriever.getRecipientMail(),
									"receiver", "receiverName", null, owner)
									.size() == 0) {
								MailContainer mailContainer = new MailContainer("", Language.DEFAULT);
								recipient = userService.createGuest(
										mailTestRetriever.getRecipientMail(),
										"receiver", "receiverName",
										mailTestRetriever.getRecipientMail(),
										true, true, "comment", mailContainer,
										owner.getLogin(), DOMAIN_IDENTIFIER);
							} else {
								recipient = userService
										.findUser(mailTestRetriever
												.getRecipientMail(), DOMAIN_IDENTIFIER);
							}

						} catch (BusinessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						/**
						 * Creating documents for testing.
						 */
						try {
							Assert.assertTrue(null != owner);
							if (documentRepository.findAll().size() > 1) {
								Assert.fail("Too much doc in the repository");
							}
							if (documentRepository.findAll().size() == 0) {
								System.out.println("inserting a new file for tests");
								documentService.insertFile(sender.getLogin(),
										inputStream, 20000,
										"linShare.properties", documentService
												.getMimeType(inputStream,
														"linShare.properties"),
										owner);
							}
						} catch (BusinessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
		wiser.stop();
	}

	/**
	 * Create and drop a share directly.
	 */
	@Test
	@DirtiesContext
	public void createAndDropAShare() {

		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						User sender = null;
						User recipient = null;
						try {
							sender = userService.findUser(mailTestRetriever
								.getSenderMail(), DOMAIN_IDENTIFIER);
							Domain domain = domainService.retrieveDomain(DOMAIN_IDENTIFIER);
							sender.setDomain(domain);
							
							recipient = userService.findUser(mailTestRetriever
									.getRecipientMail(), DOMAIN_IDENTIFIER);
						} catch (BusinessException e) {
							Assert.assertFalse(true);
						}
						Document document = documentRepository.findAll().get(0);
						ArrayList<Document> documents = new ArrayList<Document>();
						documents.add(document);

						ArrayList<User> users = new ArrayList<User>();
						users.add(recipient);

						SuccessesAndFailsItems<Share> shares = shareService
								.shareDocumentsToUser(documents, sender, users,
										"plop", null);

						Share finalShare = null;
						/**
						 * Check if the success share contains sender and
						 * recipient.
						 */
						for (Share successShare : shares.getSuccessesItem()) {
							if (successShare.getSender().equals(sender)
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
								&& sender.getShares().contains(finalShare));

					}
				});

		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						/**
						 * Now we have our Shares for each user. We will test to
						 * remove theses shares.
						 */
						User retriever = null;
						try {
							shareService.deleteShare(shareRepository.findAll()
									.get(0), sender);


							retriever = userService.findUser(mailTestRetriever
									.getSenderMail(), DOMAIN_IDENTIFIER);
						} catch (BusinessException e) {
							Assert.assertFalse(true);
							e.printStackTrace();
						}
						Assert
								.assertTrue(retriever.getReceivedShares()
										.size() == 0);
						Assert
								.assertTrue(shareRepository.findAll().size() == 0);

						// Assert.assertFalse(sender.getShares().contains(share)
						// || recipient.getReceivedShares().contains(share));
					}
				});

	}

	/**
	 * Create and drop a share directly.
	 */
	@Test
	@DirtiesContext
	public void createAndDropAShareByRemoveUser() {

		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						User owner = null;
						User recipient = null;
						try {
							owner = userService.findUser("user1@linpki.org", DOMAIN_IDENTIFIER);
							// User sender=userService.findUser(MAILSENDER);
							recipient = userService.findUser(mailTestRetriever
									.getRecipientMail(), DOMAIN_IDENTIFIER);
						} catch (BusinessException e) {
							e.printStackTrace();
							Assert.assertTrue(false);
						}
						Document document = documentRepository.findAll().get(0);
						ArrayList<Document> documents = new ArrayList<Document>();
						documents.add(document);

						ArrayList<User> users = new ArrayList<User>();
						users.add(recipient);

						SuccessesAndFailsItems<Share> shares = shareService
								.shareDocumentsToUser(documents, owner, users,
										"plop", null);

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
					}
				});

		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {

						/**
						 * Now we have our Shares for each user. We will test to
						 * remove theses shares.
						 */

						try {
							User recipient = userService
									.findUser(mailTestRetriever
											.getRecipientMail(), DOMAIN_IDENTIFIER);
							userService.deleteUser(mailTestRetriever
									.getRecipientMail(), recipient, false);

							sender = userService.findUser(mailTestRetriever
									.getSenderMail(), DOMAIN_IDENTIFIER);
						} catch (BusinessException e) {
							Assert.assertFalse(true);
							e.printStackTrace();
						}
						Assert.assertTrue(sender.getShares().size() == 0);
						Assert
								.assertTrue(shareRepository.findAll().size() == 0);

					}
				});

	}

	/**
	 * Create and drop a share directly.
	 */
	@Test
	@DirtiesContext
	public void createAndDropAShareByRemoveDocument() {

		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						User owner = null;
						User recipient = null;
						try {
							owner = userService.findUser("user1@linpki.org", DOMAIN_IDENTIFIER);
							recipient = userService.findUser(mailTestRetriever
									.getRecipientMail(), DOMAIN_IDENTIFIER);
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
										"plop", null);

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
					}
				});

		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {

						/**
						 * Now we have our Shares for each user. We will test to
						 * remove theses shares.
						 */

						try {
							// User
							// recipient=userService.findUser(mailTestRetriever.getRecipientMail());
							// userService.deleteUser(mailTestRetriever.getRecipientMail(),
							// recipient, false);

							User recipient = userService
									.findUser(mailTestRetriever
											.getRecipientMail(), DOMAIN_IDENTIFIER);
							User sender = userService
									.findUser(mailTestRetriever
											.getRecipientMail(), DOMAIN_IDENTIFIER);
							documentService.deleteFile(mailTestRetriever
									.getSenderMail(), documentRepository
									.findAll().get(0).getIdentifier(),
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

					}
				});

	}

}
