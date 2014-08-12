/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class DocumentEntryServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(DocumentEntryServiceImplTest.class);

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
	private DocumentRepository documentRepository;

	@Autowired
	private FileSystemDao fileRepository;	

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private DocumentEntryService documentEntryService;

	private User jane;
	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
	private final Long size = (long) 1000;
	private final String fileName = "linshare-default.properties";
	private DocumentEntry aDocumentEntry;

	private LoadingServiceTestDatas datas;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		jane = datas.getUser2();		

		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		datas.deleteUsers();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateDocumentEntry() throws BusinessException, IOException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		createFunctionalities();

		Account actor = jane;
		aDocumentEntry = documentEntryService.create(actor, actor, stream, fileName);
		Assert.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) != null);

		Document aDocument = aDocumentEntry.getDocument();
		documentEntryRepository.delete(aDocumentEntry);
		jane.getEntries().clear();
		userRepository.update(jane);
		fileRepository.removeFileByUUID(aDocument.getUuid());
		documentRepository.delete(aDocument);

		logger.debug(LinShareTestConstants.END_TEST);
	}

	/**
	 * We need this method because all the functionalities are check when we create a DocumentEntry
	 * 
	 * @throws IllegalArgumentException
	 * @throws BusinessException
	 */
	private void createFunctionalities() throws IllegalArgumentException, BusinessException {
		Integer value = 1;
		ArrayList<Functionality> functionalities = new ArrayList<Functionality>();
		functionalities.add(
			new UnitValueFunctionality("QUOTA_GLOBAL",
				true,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				jane.getDomain(),
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
			)
		);

		functionalities.add(
			new UnitValueFunctionality("QUOTA_USER",
				true,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				jane.getDomain(),
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
			)
		);

		functionalities.add(
				new UnitValueFunctionality("MIME_TYPE",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);

		functionalities.add(
				new UnitValueFunctionality("ANTIVIRUS",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);

		functionalities.add(
				new UnitValueFunctionality("ENCIPHERMENT",
					true,
					new Policy(Policies.ALLOWED, true),
					new Policy(Policies.ALLOWED, true),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);

		functionalities.add(
				new StringValueFunctionality("TIME_STAMPING",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					""
				)
		);

		functionalities.add(
				new UnitValueFunctionality("FILE_EXPIRATION",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new TimeUnitClass(TimeUnit.DAY)
				)
		);

		functionalities.add(
				new UnitValueFunctionality("FILESIZE_MAX",
					true,
					new Policy(Policies.ALLOWED, true),
					new Policy(Policies.ALLOWED, true),
					jane.getDomain(),
					5,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);

		for (Functionality functionality : functionalities) {
			functionalityRepository.create(functionality);
			jane.getDomain().addFunctionality(functionality);
		}
	}

	@Test
	public void testFindAllMyDocumentEntries() throws BusinessException, IOException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		User owner = jane;
		createFunctionalities();
		aDocumentEntry = documentEntryService.create(actor, actor, stream, fileName);
		List<DocumentEntry> documents = documentEntryService.findAll(actor, owner);
		Assert.assertTrue(documents.contains(aDocumentEntry));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteDocumentEntries() throws BusinessException, IOException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		createFunctionalities();
		aDocumentEntry = documentEntryService.create(actor, actor, stream, fileName);
		aDocumentEntry.getDocument().setSignatures(new HashSet<Signature>());
		documentEntryService.deleteDocumentEntry(actor, aDocumentEntry);
		Assert.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) == null);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}


