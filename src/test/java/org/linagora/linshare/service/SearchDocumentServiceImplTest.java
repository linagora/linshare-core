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
package org.linagora.linshare.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SearchDocumentService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.wiser.Wiser;

/*
 * This all class was disable because of a huge spring context problem
 * */
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
"classpath:springContext-test.xml" })
public class SearchDocumentServiceImplTest extends
AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(SearchDocumentServiceImplTest.class);

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	@Autowired
	private FileSystemDao fileRepository;

	@Autowired
	private SearchDocumentService searchDocumentService;

	@Autowired
	private ShareEntryBusinessService shareEntryBusinessService;

	private InputStream inputStream;
	private String inputStreamUuid;
	private User John;
	private User Jane;
	private UserVo johnVo;
	private UserVo janeVo;
	private Document aDocument;
	private DocumentEntry aDocumentEntry;

	private LoadingServiceTestDatas datas;

	private Wiser wiser;

	public SearchDocumentServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);

		datas = new LoadingServiceTestDatas(functionalityRepository,
				abstractDomainRepository, domainPolicyRepository,
				userRepository, userService);
		datas.loadUsers();

		John = datas.getUser1();
		Jane = datas.getUser2();

		janeVo = new UserVo(Jane);
		johnVo = new UserVo(John);

		Integer value = 1;
		ArrayList<Functionality> functionalities = new ArrayList<Functionality>();
		functionalities.add(new UnitValueFunctionality("QUOTA_GLOBAL", true,
				new Policy(Policies.ALLOWED, false), new Policy(
						Policies.ALLOWED, false), Jane.getDomain(), value,
						new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new UnitValueFunctionality("QUOTA_USER", true,
				new Policy(Policies.ALLOWED, false), new Policy(
						Policies.ALLOWED, false), Jane.getDomain(), value,
						new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new UnitValueFunctionality("MIME_TYPE", true,
				new Policy(Policies.ALLOWED, false), new Policy(
						Policies.ALLOWED, false), Jane.getDomain(), value,
						new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new UnitValueFunctionality("ANTIVIRUS", true,
				new Policy(Policies.ALLOWED, false), new Policy(
						Policies.ALLOWED, false), Jane.getDomain(), value,
						new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new UnitValueFunctionality("ENCIPHERMENT", true,
				new Policy(Policies.ALLOWED, true), new Policy(
						Policies.ALLOWED, true), Jane.getDomain(), value,
						new FileSizeUnitClass(FileSizeUnit.GIGA)));

		functionalities.add(new StringValueFunctionality("TIME_STAMPING", true,
				new Policy(Policies.ALLOWED, false), new Policy(
						Policies.ALLOWED, false), Jane.getDomain(), ""));

		functionalities.add(new UnitValueFunctionality("FILE_EXPIRATION", true,
				new Policy(Policies.ALLOWED, false), new Policy(
						Policies.ALLOWED, false), Jane.getDomain(), value,
						new TimeUnitClass(TimeUnit.DAY)));

		for (Functionality functionality : functionalities) {
			functionalityRepository.create(functionality);
			Jane.getDomain().addFunctionality(functionality);
		}

		inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		inputStreamUuid = fileRepository
				.insertFile(Jane.getLogin(), inputStream, 10000,
						"linshare-default.properties", "text/plain");

		FileInfo inputStreamInfo = fileRepository
				.getFileInfoByUUID(inputStreamUuid);

		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp = inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);

		aDocument = new Document(inputStreamUuid, inputStreamInfo.getName(),
				inputStreamInfo.getMimeType(), lastModifiedLin, exp, Jane,
				false, false, new Long(10000));
		HashSet<Signature> signatures = new HashSet<Signature>();
		aDocumentEntry = new DocumentEntry(Jane, inputStreamInfo.getName(), aDocument);
		aDocument.setSignatures(signatures);

		try {
			documentRepository.create(aDocument);
			documentEntryRepository.create(aDocumentEntry);
			Jane.getEntries().add(aDocumentEntry);
			userRepository.update(Jane);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail();
		}
		wiser.start();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		
		documentEntryRepository.delete(aDocumentEntry);
		Jane.getEntries().clear();
		userRepository.update(Jane);
		fileRepository.removeFileByUUID(aDocument.getUuid());
		documentRepository.delete(aDocument);
		datas.deleteUsers();
		
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testRetrieveDocument() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<DocumentEntry> janeDocuments = searchDocumentService
				.retrieveDocumentContainsCriterion(Jane,
						new SearchDocumentCriterion(janeVo, null, null, null,
								null, null, null, null, null, null, null));

		Assert.assertTrue(janeDocuments.contains(aDocumentEntry));

		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testRetrieveDocumentContainsCriterion()
			throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		// User criterion
		SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(
				janeVo, null, null, null, null, null, null, null, null, null,
				null);

		List<DocumentEntry> documents = searchDocumentService
				.retrieveDocumentContainsCriterion(Jane,
						searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());
		Assert.assertTrue(documents.size() == 1);

		// User criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(johnVo, null,
				null, null, null, null, null, null, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		// Name criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo,
				aDocumentEntry.getName(), null, null, null, null, null, null,
				null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());

		// Name criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, "toto",
				null, null, null, null, null, null, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		// Min size criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				new Long(0), null, null, null, null, null, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());

		// Min size criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				new Long(Long.MAX_VALUE), null, null, null, null, null, null,
				null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		// Max size criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, new Long(Long.MAX_VALUE), null, null, null, null, null,
				null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());

		// Max size criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, new Long(0), null, null, null, null, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		// Type criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, aDocument.getType(), null, null, null, null, null,
				null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());

		// Type criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, "toto", null, null, null, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		// Creation date criterion
		Calendar creationDate = Calendar.getInstance();
		creationDate.add(Calendar.YEAR, -2);

		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, null, null, creationDate, null, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());

		// Creation date criterion fail
		creationDate.add(Calendar.YEAR, 4);
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, null, null, creationDate, null, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		// Expiration date criterion
		Calendar expirationDate = Calendar.getInstance();
		expirationDate.add(Calendar.YEAR, 2);

		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, null, null, null, expirationDate, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());

		// Expiration date criterion fail
		expirationDate.add(Calendar.YEAR, -4);
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, null, null, null, expirationDate, null, null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		// Extension criterion
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, null, null, null, null, "properties", null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertFalse(documents.isEmpty());

		// Extension criterion fail
		searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
				null, null, null, null, null, null, "toto", null, null);

		documents = searchDocumentService.retrieveDocumentContainsCriterion(
				Jane, searchDocumentCriterion);

		Assert.assertTrue(documents.isEmpty());

		Calendar cldr = Calendar.getInstance();
		cldr.add(Calendar.YEAR, 1);

		List<DocumentEntry> shareDocuments = new ArrayList<DocumentEntry>();
		shareDocuments.add(aDocumentEntry);

		ArrayList<ShareEntry> shares = new ArrayList<ShareEntry>();

		for (DocumentEntry documentEntry : shareDocuments) {
			shares.add(shareEntryBusinessService.createShare(documentEntry,
					John, Jane, cldr));
		}
		
		try {
			// Shared criterion
			Boolean shared = false;
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, shared, null, null, null, null, null);

			documents = searchDocumentService
					.retrieveDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Shared criterion fail
			shared = true;
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, shared, null, null, null, null, null);

			documents = searchDocumentService
					.retrieveDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

		} finally {
			for (ShareEntry share : shares) {
				shareEntryBusinessService.deleteShare(share);
			}
			logger.debug(LinShareTestConstants.END_TEST);
		}
	}

	@Test
	public void testRetrieveShareDocumentContainsCriterion()
			throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Calendar cldr = Calendar.getInstance();
		cldr.add(Calendar.YEAR, 1);

		List<DocumentEntry> shareDocuments = new ArrayList<DocumentEntry>();
		shareDocuments.add(aDocumentEntry);

		ArrayList<ShareEntry> shares = new ArrayList<ShareEntry>();

		for (DocumentEntry documentEntry : shareDocuments) {
			shares.add(shareEntryBusinessService.createShare(documentEntry,
					John, Jane, cldr));
		}

		try {

			// User criterion
			SearchDocumentCriterion searchDocumentCriterion = new SearchDocumentCriterion(
					janeVo, null, null, null, null, null, null, null, null, null,
					null);

			List<ShareEntry> documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// User criterion fail
			searchDocumentCriterion = new SearchDocumentCriterion(johnVo, null,
					null, null, null, null, null, null, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Name criterion
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo,
					aDocumentEntry.getName(), null, null, null, null, null, null,
					null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Name criterion fail
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, "toto",
					null, null, null, null, null, null, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Min size criterion
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					new Long(0), null, null, null, null, null, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Min size criterion fail
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					new Long(Long.MAX_VALUE), null, null, null, null, null, null,
					null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Max size criterion
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, new Long(Long.MAX_VALUE), null, null, null, null, null,
					null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Max size criterion fail
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, new Long(0), null, null, null, null, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Type criterion
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, aDocument.getType(), null, null, null, null, null,
					null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Type criterion fail
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, "toto", null, null, null, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Creation date criterion
			Calendar creationDate = Calendar.getInstance();
			creationDate.add(Calendar.YEAR, -2);

			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, creationDate, null, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Creation date criterion fail
			creationDate.add(Calendar.YEAR, 4);
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, creationDate, null, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Expiration date criterion
			Calendar expirationDate = Calendar.getInstance();
			expirationDate.add(Calendar.YEAR, 2);

			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, null, expirationDate, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Expiration date criterion fail
			expirationDate.add(Calendar.YEAR, -4);
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, null, expirationDate, null, null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Extension criterion
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, null, null, "properties", null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Extension criterion fail
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, null, null, "toto", null, null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

			// Shared From criterion
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, null, null, null, johnVo.getMail(),
					null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertFalse(documents.isEmpty());

			// Shared From criterion fail
			searchDocumentCriterion = new SearchDocumentCriterion(janeVo, null,
					null, null, null, null, null, null, null, "toto", null);

			documents = searchDocumentService
					.retrieveShareDocumentContainsCriterion(Jane,
							searchDocumentCriterion);

			Assert.assertTrue(documents.isEmpty());

		} finally {
			for (ShareEntry share : shares) {
				shareEntryBusinessService.deleteShare(share);
			}
			logger.debug(LinShareTestConstants.END_TEST);
		}
	}

}
