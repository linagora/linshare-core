/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.QuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml"
		})
public class DocumentEntryServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory
			.getLogger(DocumentEntryServiceImplTest.class);

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Qualifier("jcloudFileDataStore")
	@Autowired
	private FileDataStore fileDataStore;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	@Autowired
	private DocumentEntryService documentEntryService;

	@Autowired
	private QuotaService quotaService;

	private User john;

	private User jane;
	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	private final String fileName = "linshare-default.properties";
	private final String comment = "file description";
	private DocumentEntry aDocumentEntry;

	private LoadingServiceTestDatas datas;

	private static final String EXCEPTION_GET_MESSAGE = "You are not authorized to get this entry.";
	@Before
	public void setUp() throws Exception {
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		jane = datas.getUser2();
		createFunctionalities();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateDocumentEntry()
			throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName, comment, false, null);
		Assert.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) != null);
		Document aDocument = aDocumentEntry.getDocument();
		documentEntryRepository.delete(aDocumentEntry);
		jane.getEntries().clear();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateDocumentEntrySpecialCharacters() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile,
				"EP_TEST_v233<script>alert(document.cookie)</script>", comment, false, null);
		Assert.assertNotNull(documentEntryRepository.findById(aDocumentEntry.getUuid()));
		Assert.assertEquals(aDocumentEntry.getName(), "EP_TEST_v233_script_alert(document.cookie)__script_");
		Document aDocument = aDocumentEntry.getDocument();
		documentEntryRepository.delete(aDocumentEntry);
		jane.getEntries().clear();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private InputStream getStream(String resourceName) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}

	@Test
	public void testUpdateDocumentEntrySpecialCharacters() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream stream1 = getStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream1, tempFile);
		InputStream stream2 = getStream("linshare.properties.sample");
		File tempFile2 = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream2, tempFile2);
		aDocumentEntry = documentEntryService.create(john, john, tempFile, fileName, comment, false, null);
		Assert.assertNotNull(documentEntryRepository.findById(aDocumentEntry.getUuid()));
		Assert.assertEquals(aDocumentEntry.getName(), fileName);
		aDocumentEntry = documentEntryService.update(john, john, aDocumentEntry.getUuid(), tempFile2,
				"EP_TEST_v233<script>alert(document.cookie)</script>");
		Assert.assertEquals(aDocumentEntry.getName(), "EP_TEST_v233_script_alert(document.cookie)__script_");
		Document aDocument = aDocumentEntry.getDocument();
		documentEntryRepository.delete(aDocumentEntry);
		jane.getEntries().clear();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	/**
	 * We need this method because all the functionalities are check when we
	 * create a DocumentEntry
	 * 
	 * @throws IllegalArgumentException
	 * @throws BusinessException
	 */
	private void createFunctionalities()
			throws IllegalArgumentException, BusinessException {
		Integer value = 1;
		ArrayList<Functionality> functionalities = new ArrayList<Functionality>();
		functionalities.add(new UnitValueFunctionality("MIME_TYPE", true,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false), jane.getDomain(), value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new UnitValueFunctionality("ANTIVIRUS", true,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false), jane.getDomain(), value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new UnitValueFunctionality("ENCIPHERMENT", true,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true), jane.getDomain(), value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new StringValueFunctionality("TIME_STAMPING", true,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false), jane.getDomain(), ""));

		functionalities.add(new UnitValueFunctionality("DOCUMENT_EXPIRATION", true,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false), jane.getDomain(), value,
				new TimeUnitClass(TimeUnit.DAY)));
		for (Functionality functionality : functionalities) {
			functionalityRepository.create(functionality);
			jane.getDomain().addFunctionality(functionality);
		}
	}

	@Test
	public void testFindAllMyDocumentEntries()
			throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		User owner = jane;
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName, comment, false, null);
		List<DocumentEntry> documents = documentEntryService.findAll(actor, owner);
		Assert.assertTrue(documents.contains(aDocumentEntry));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteDocumentEntries() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account intruder = john;
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName, comment, false, null);
		aDocumentEntry.getDocument().setSignatures(new HashSet<Signature>());
		try {
			documentEntryService.delete(intruder, intruder, aDocumentEntry.getUuid());
			assertTrue("The intruder shouldn't have access to this resource. An exception must be thrown", false);
		} catch (Exception e) {
			assertTrue("Wrong exception is thrown : " + e.getMessage(), EXCEPTION_GET_MESSAGE.equals(e.getMessage()));
		}
		try {
			documentEntryService.delete(actor, actor, aDocumentEntry.getUuid());
			Assert.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) == null);
		} catch (BusinessException e) {
			assertFalse(
					"The user should have access to this resource. This exception must not be thrown " + e.getMessage(),
					EXCEPTION_GET_MESSAGE.equals(e.getMessage()));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testQuotaDeleteDocumentEntries() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		AccountQuota actorQuota = quotaService.findByRelatedAccount(actor);
		Long quota = quotaService.getRealTimeUsedSpace(actor, actor, actorQuota.getUuid());
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName, comment, false, null);
		Long quotaAfterCreate = quotaService.getRealTimeUsedSpace(actor, actor, actorQuota.getUuid());
		Long expectedQuota = quota + aDocumentEntry.getSize();
		Assert.assertEquals(expectedQuota, quotaAfterCreate);
		documentEntryService.delete(actor, actor, aDocumentEntry.getUuid());
		Long newQuota = quotaService.getRealTimeUsedSpace(actor, actor, actorQuota.getUuid());
		Assert.assertEquals(quota, newQuota);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindDocumentEntries() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account intruder = john;
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName, comment, false, null);
		try {
			aDocumentEntry = documentEntryService.find(intruder, intruder, aDocumentEntry.getUuid());
			assertTrue("The intruder shouldn't have access to this resource. An exception must be thrown", false);
		} catch (Exception e) {
			assertTrue("Wrong exception is thrown", EXCEPTION_GET_MESSAGE.equals(e.getMessage()));
		}
		try {
			aDocumentEntry = documentEntryService.find(actor, actor, aDocumentEntry.getUuid());
			assertTrue("The actor should have access to this resource.", aDocumentEntry != null);
		} catch (BusinessException e) {
			assertFalse(
					"The user should have access to this resource. This exception must not be thrown " + e.getMessage(),
					EXCEPTION_GET_MESSAGE.equals(e.getMessage()));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateDocumentEntries() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account intruder = john;
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName, comment, false, null);
		aDocumentEntry.getDocument().setSignatures(new HashSet<Signature>());
		try {
			documentEntryService.update(intruder, intruder, aDocumentEntry.getUuid(), tempFile, "new-file-name.txt");
			assertTrue("The intruder shouldn't have access to this resource. An exception must be thrown", false);
		} catch (Exception e) {
			assertTrue("Wrong exception is thrown : " + e.getMessage(), EXCEPTION_GET_MESSAGE.equals(e.getMessage()));
		}
		try {
			documentEntryService.update(actor, actor, aDocumentEntry.getUuid(), tempFile, "New file Name");
			Assert.assertTrue(
					"New file Name".equals(documentEntryRepository.findById(aDocumentEntry.getUuid()).getName()));
		} catch (BusinessException e) {
			assertFalse(
					"The user should have access to this resource. This exception must not be thrown " + e.getMessage(),
					EXCEPTION_GET_MESSAGE.equals(e.getMessage()));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
