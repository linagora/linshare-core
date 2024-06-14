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
package org.linagora.linshare.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.MailAttachmentService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.TimeService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@Transactional
@Sql({
	
	"/import-tests-document-entry-setup.sql" })
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class ShareNewShareEmailBuilderTest {

	private static Logger logger = LoggerFactory.getLogger(ShareNewShareEmailBuilderTest.class);

	@Autowired
	@InjectMocks
	@Qualifier("shareService")
	private ShareService shareService;

	@Autowired
	@Qualifier("documentEntryBusinessService")
	private DocumentEntryBusinessService documentEntryBusinessService;

	@Autowired
	@Qualifier("documentEntryService")
	private DocumentEntryService documentEntryService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private DomainBusinessService domainBusinessService;

	@Autowired
	protected MailAttachmentService attachmentService;
	
	@Autowired
	@InjectMocks
	private FunctionalityReadOnlyService functionalityReadOnlyService;
	
	@Autowired
	private FunctionalityService functionalityService;
	
	@Mock
	private TimeService timeService;

	private User owner;

	private Account actor;

	private Account admin;

	public ShareNewShareEmailBuilderTest() {
		super();
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		owner = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		actor = (Account) owner;
		admin = userRepository.findByDomainAndMail(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateNewSharesFiles() throws BusinessException, IOException, ParseException {
		User recipient = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		List<String> documents = new  ArrayList<String>();
		documents.add("bfaf3fea-c64a-4ee0-bae8-b1482f1f6401");
		documents.add("fd87394a-41ab-11e5-b191-080027b8274b");
		ShareContainer shareContainer = new ShareContainer();
		shareContainer.addShareRecipient(recipient);
		shareContainer.addDocumentUuid(documents);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2020-08-13 00:00:00"));
		Assertions.assertNotNull(shareService.create(actor, owner, shareContainer));
	}

	@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
	@Test
	public void testShareDocument() throws BusinessException, IOException, ParseException {
		// create files utils
		User recipient = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		// create a document entry
		DocumentEntry entry = documentEntryService.create(actor, actor, tempFile, tempFile.getName(), "", false, null);
		Assertions.assertNotNull(documentEntryService.find(actor, actor, entry.getUuid()));
		// share the created document
		ShareContainer sc = new ShareContainer("Subject", "message", false, false);
		sc.addDocumentUuid(entry.getUuid());
		sc.addShareRecipient(recipient);
		sc.setEnableUSDA(false);
		sc.setNotificationDateForUSDA(null);
		sc.setForceAnonymousSharing(false);
		sc.setExpiryDate(null);
		// check initial state of functionality
		UnitValueFunctionality entity = (UnitValueFunctionality) functionalityService.find(admin,
				admin.getDomain().getUuid(), FunctionalityNames.SHARE_EXPIRATION.toString());
		TimeUnitValueFunctionality shareFunc = new TimeUnitValueFunctionality(entity); 
		Assertions.assertTrue(shareFunc.getActivationPolicy().getStatus());
		// mock now moment
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2020-08-13 00:00:00"));
		Set<Entry> entries = shareService.create(actor, owner, sc);
		Assertions.assertNotNull(entries);
		Assertions.assertEquals(entries.size(), 1);
		// compute the default date
		Calendar calendar = functionalityReadOnlyService.getCalendarWithoutTime(timeService.dateNow());
		calendar.add(shareFunc.toCalendarValue(), shareFunc.getValue());
		Date defaultDate = calendar.getTime();
		// if expiration is null | expected expirationDate = default date
		Assertions.assertEquals(defaultDate, entries.iterator().next().getExpirationDate().getTime());

		// expiration before now "2020-08-13 00:00:00" | expected throw a business exception
		sc.setExpiryDate(parseDate("2020-08-12 00:00:00"));
		BusinessException e = Assertions.assertThrows(BusinessException.class, () -> {
			shareService.create(actor, owner, sc);
		});
		Assertions.assertEquals(e.getErrorCode(), BusinessErrorCode.SHARE_EXPIRY_DATE_INVALID);
		
		// compute an expiration date after the max date of functionality
		Calendar cal = new GregorianCalendar();
		cal.setTime(timeService.dateNow());
		cal.add(shareFunc.toCalendarMaxValue(), shareFunc.getMaxValue());
		cal = functionalityReadOnlyService.getCalendarWithoutTime(cal.getTime());
		cal.add(Calendar.DATE, 1);
		Date afterMaxExpirationDate = cal.getTime();
		sc.setExpiryDate(afterMaxExpirationDate);
		// set the expiration after max date | expected throw an exception
		BusinessException e2 = Assertions.assertThrows(BusinessException.class, () -> {
			shareService.create(actor, owner, sc);
		});
		Assertions.assertEquals(e2.getErrorCode(), BusinessErrorCode.SHARE_EXPIRY_DATE_INVALID);
		// test with no limit in share functionality
		entity.setUnlimited(true);
		functionalityService.update(admin, shareFunc.getDomain().getUuid(), entity);
		Date expiryDateUnlimited = new Date(Long.MAX_VALUE);
		sc.setExpiryDate(expiryDateUnlimited);
		Set<Entry> entries1 = shareService.create(actor, owner, sc);
		Assertions.assertEquals(expiryDateUnlimited, entries1.iterator().next().getExpirationDate().getTime());
	}

	@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
	@Test
	public void testShareDocumentUSDA() throws BusinessException, IOException, ParseException {
		// create files utils
		User recipient = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		// create a document entry
		DocumentEntry entry = documentEntryService.create(actor, actor, tempFile, tempFile.getName(), "", false, null);
		Assertions.assertNotNull(documentEntryService.find(actor, actor, entry.getUuid()));
		// share the created document
		ShareContainer sc = new ShareContainer("Subject", "message", false, false);
		sc.addDocumentUuid(entry.getUuid());
		sc.addShareRecipient(recipient);
		sc.setForceAnonymousSharing(false);
		// check initial conditions
		BooleanValueFunctionality usdaFuncEntity = (BooleanValueFunctionality) functionalityService.find(admin,
				admin.getDomain().getUuid(), FunctionalityNames.UNDOWNLOADED_SHARED_DOCUMENTS_ALERT.toString());
		Assertions.assertTrue(usdaFuncEntity.getActivationPolicy().getStatus());
		Assertions.assertTrue(usdaFuncEntity.getValue());
		IntegerValueFunctionality usdaDurationFuncEntity = (IntegerValueFunctionality) functionalityService.find(admin,
				admin.getDomain().getUuid(),
				FunctionalityNames.UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION.toString());
		Assertions.assertTrue(usdaDurationFuncEntity.getActivationPolicy().getStatus());
		Assertions.assertTrue(usdaDurationFuncEntity.getDelegationPolicy().getStatus());
		Assertions.assertEquals(3, usdaDurationFuncEntity.getValue());
		UnitValueFunctionality shareExpirFuncEntity = (UnitValueFunctionality) functionalityService.find(admin,
				admin.getDomain().getUuid(), FunctionalityNames.SHARE_EXPIRATION.toString());
		TimeUnitValueFunctionality shareFunc = new TimeUnitValueFunctionality(shareExpirFuncEntity);
		Assertions.assertTrue(shareFunc.getActivationPolicy().getStatus());

		// mock now moment
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2020-08-13 22:13:00"));
		// set expiration in 3 days

		Calendar calendar = functionalityReadOnlyService.getCalendarWithoutTime(timeService.dateNow());
		calendar.add(shareFunc.toCalendarValue(), shareFunc.getValue());
		Date defaultDate = calendar.getTime();
		// share with default values expiration
		Set<Entry> entries = shareService.create(actor, owner, sc);
		Entry entry1 = entries.iterator().next();
		Assertions.assertEquals(defaultDate, entry1.getExpirationDate().getTime());

		Assertions.assertTrue(sc.getEnableUSDA(), "since user didn't set it on creation, by default USDA is enbled");
		// compute expected notification date | number of worked days between now and
		// alert notification

		Calendar c = Calendar.getInstance();
		c.setTime(parseDate("2020-08-13 22:13:00"));
		int day = c.get(Calendar.DAY_OF_WEEK);
		int nbWeek = (day - 2 + usdaDurationFuncEntity.getValue()) / 5;
		int finalamount = usdaDurationFuncEntity.getValue() + nbWeek * 2;
		c.add(Calendar.DATE, finalamount);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		Assertions.assertEquals(c.getTime(), sc.getNotificationDateForUSDA(),
				"since user didn't set it on creation, by default notfication is now + usda duration value");
		// create a share with USDA > expiration date | expected raise an exception
		sc.setExpiryDate(parseDate("2020-08-16 22:13:00"));
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(sc.getExpiryDate());
		cal2.add(Calendar.DAY_OF_MONTH, 1);
		sc.setNotificationDateForUSDA(cal2.getTime());
		BusinessException e = Assertions.assertThrows(BusinessException.class, () -> {
			shareService.create(actor, owner, sc);
		});
		Assertions.assertEquals(e.getErrorCode(), BusinessErrorCode.SHARE_WRONG_USDA_NOTIFICATION_DATE_AFTER);
		// create a share with USDA < mocked now | expected raise an exception
		sc.setNotificationDateForUSDA(parseDate("2020-08-12 22:13:00"));
		BusinessException e1 = Assertions.assertThrows(BusinessException.class, () -> {
			shareService.create(actor, owner, sc);
		});
		Assertions.assertEquals(e1.getErrorCode(), BusinessErrorCode.SHARE_WRONG_USDA_NOTIFICATION_DATE_BEFORE);
	}

	private Date parseDate(String inputDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.parse(inputDate);
	}

	@Test
	public void testCreateNewSharesFiles_MailAttachment() throws BusinessException, IOException, ParseException {
		User recipient = userRepository.findByMail(LinShareTestConstants.FOO_ACCOUNT);
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		// EnableForAll is disabled and the language == emailContext language -> inserted
		attachmentService.create(admin, true, "Logo", false, cfg.getUuid(),
				"Test mail attachment", "logo.mail.attachment2.test", Language.ENGLISH, tempFile, null);
		Assertions.assertFalse(cfg.getMailAttachments().isEmpty());
		List<String> documents = new  ArrayList<String>();
		documents.add("bfaf3fea-c64a-4ee0-bae8-b1482f1f6401");
		documents.add("fd87394a-41ab-11e5-b191-080027b8274b");
		ShareContainer shareContainer = new ShareContainer();
		shareContainer.addShareRecipient(recipient);
		shareContainer.addDocumentUuid(documents);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2020-08-13 00:00:00"));
		Assertions.assertNotNull(shareService.create(actor, owner, shareContainer));
	}
}
