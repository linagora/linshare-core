package org.linagora.linshare.repository.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.ShareRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class GuestRepositoryImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(GuestRepositoryImplTest.class);

	private static final String LOGIN = "login";
	private static final String FIRST_NAME = "first name";
	private static final String LAST_NAME = "last name";
	private static final String MAIL = "mail";
	private static final String PASSWORD = "password";

	private static final String LOGIN2 = "login2";
	private static final String FIRST_NAME2 = "jean";
	private static final String LAST_NAME2 = "laporte";
	private static final String MAIL2 = "foo@yopmail.com";

	private static final String LOGIN3 = "login3";
	private static final String FIRST_NAME3 = "robert";
	private static final String LAST_NAME3 = "lepoint";
	private static final String MAIL3 = "foo@lepoint.com";

	private static final String O_LOGIN = "user1@linpki.org";
	private static final String O_FIRST_NAME = "John";
	private static final String O_LAST_NAME = "Doe";
	private static final String O_MAIL = "user1@linpki.org";

	private boolean flag = false;

	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;
	private static final String topDomainName = "TEST_Domain-0-1";

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	@Qualifier("guestRepository")
	private GuestRepository guestRepository;

	@Autowired
	private ShareRepository shareRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		String encpass = HashUtils.hashSha1withBase64(PASSWORD.getBytes());
		if (!flag) {
			Guest u1 = new Guest(FIRST_NAME2, LAST_NAME2, MAIL2,
					encpass, true, "comment");
			guestRepository.create(u1);
			Guest u2 = new Guest(FIRST_NAME3, LAST_NAME3, MAIL3,
					encpass, true, "comment");
			guestRepository.create(u2);
			flag = true;
		}
		logger.debug(LinShareTestConstants.END_SETUP);
	}

//	@Test
//	public void testExistGuest() throws BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//		String encpassword = HashUtils.hashSha1withBase64(PASSWORD.getBytes());
//		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL, encpassword,
//				true,  "comment");
//		guestRepository.create(u);
//		Assert.assertTrue(guestRepository.exist(LOGIN, encpassword));
//		Assert.assertFalse(guestRepository.exist(LOGIN, "pass"));
//		Assert.assertFalse(guestRepository.exist("login90", encpassword));
//		logger.debug(LinShareTestConstants.END_TEST);
//	}

//	@Test
//	public void testfindGuest() throws BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//
//		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL);
//		AbstractDomain domain = abstractDomainRepository
//				.findById(DOMAIN_IDENTIFIER);
//
//		u.setDomain(domain);
//		guestRepository.create(u);
//
//		Guest userFound = null;
//
//		userFound = guestRepository.findByLogin(LOGIN);
//		Assert.assertNotNull(userFound);
//		Assert.assertEquals(FIRST_NAME, userFound.getFirstName());
//		userFound = null;
//
//		userFound = guestRepository
//				.findByMailAndDomain(DOMAIN_IDENTIFIER, MAIL);
//		Assert.assertNotNull(userFound);
//		Assert.assertEquals(FIRST_NAME, userFound.getFirstName());
//		userFound = null;
//		logger.info(LinShareTestConstants.END_TEST);
//
//	}

//	@Test
//	public void testShares() throws IllegalArgumentException, BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//
//		Guest sender = guestRepository.findByLogin(LOGIN2);
//
//		Guest receiver = guestRepository.findByLogin(LOGIN3);
//
//		/**
//		 * Creation of a document.
//		 */
//		Document document = new Document("document1", "hop.txt", "txt",
//				GregorianCalendar.getInstance(),
//				GregorianCalendar.getInstance(), sender, false, true,
//				new Long(100000));
//
//		documentRepository.create(document);
//
//		/**
//		 * Creation of share
//		 */
//		Share share = new Share(sender, receiver, document, "plop",
//				GregorianCalendar.getInstance(), true, false);
//		shareRepository.create(share);
//
//		sender.addShare(share);
//		receiver.addReceivedShare(share);
//		guestRepository.update(sender);
//		guestRepository.update(receiver);
//		sender = guestRepository.findByLogin(sender.getMail());
//		receiver = guestRepository.findByLogin(receiver.getMail());
//
//		for (Share currentShare : sender.getShares()) {
//			System.out.println("Sender: " + currentShare.getSender().getMail());
//			System.out.println("Receiver: "
//					+ currentShare.getReceiver().getMail());
//		}
//		if (!(sender.getShares().contains(share) && receiver
//				.getReceivedShares().contains(share))) {
//			fail();
//		}
//		logger.info(LinShareTestConstants.END_TEST);
//
//	}

	@Test
	public void testSearchGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<Guest> results = null;

		User owner = new Internal(O_FIRST_NAME, O_LAST_NAME, O_MAIL, null);
		owner.setDomain(abstractDomainRepository.findById(topDomainName));
		userRepository.create(owner);

		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL);

		u.setOwner(owner);

		guestRepository.create(u);

		results = guestRepository.searchGuest(null, null, null, owner);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		results = null;

		results = guestRepository.searchGuest(null, null, LAST_NAME, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		results = null;

		results = guestRepository.searchGuest(null, FIRST_NAME, null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		results = null;

		results = guestRepository.searchGuest(MAIL, null, null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		results = null;

		results = guestRepository.searchGuest("foo@", null, null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 2);
		results = null;

		results = guestRepository.searchGuestAnyWhere("foo@", null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 2);

		logger.info(LinShareTestConstants.END_TEST);

	}

	@Test
	public void testFindOutdatedGuests() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<Guest> results = null;

		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL);
		u.setExpirationDate(new Date(0));

		guestRepository.create(u);

		results = guestRepository.findOutdatedGuests();
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);

		logger.info(LinShareTestConstants.END_TEST);
	}

}
