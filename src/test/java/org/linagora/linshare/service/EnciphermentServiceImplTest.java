package org.linagora.linshare.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.EnciphermentService;
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
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class EnciphermentServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(EnciphermentServiceImplTest.class);
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	@Autowired
	private DocumentEntryRepository documentEntryRepository;
	
	@Autowired
	private EnciphermentService enciphermentService;
	
	@Autowired
	private UserService userService;
	
	private InputStream inputStream;
	private String uuid;
	private User jane;
	private DocumentEntry aDocumentEntry;
	private Document aDocument;
	
	private LoadingServiceTestDatas datas;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		jane = datas.getUser2();
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
		
		for (Functionality functionality : functionalities) {
			functionalityRepository.create(functionality);
			jane.getDomain().addFunctionality(functionality);
		}
		
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		uuid = fileRepository.insertFile(jane.getLogin(), inputStream, 10000, "linshare-default.properties", "text/plain");
		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(uuid);
		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp = inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		aDocument = new Document(uuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, jane,false,false,new Long(10000));
		aDocumentEntry = new DocumentEntry(jane, "new document", aDocument);
		HashSet<Signature> signatures = new HashSet<Signature>();
		aDocument.setSignatures(signatures);
		
		try {
			documentRepository.create(aDocument);
			documentEntryRepository.create(aDocumentEntry);
			jane.getEntries().add(aDocumentEntry);
			userRepository.update(jane);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug("aDocument.getIdentifier : " + aDocument.getUuid());
		printDocs(jane);
		documentEntryRepository.delete(aDocumentEntry);
		jane.getEntries().clear();
		userRepository.update(jane);
		fileRepository.removeFileByUUID(aDocument.getUuid());
		documentRepository.delete(aDocument);
		datas.deleteUsers();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	@Test
	public void testEncipherDocument() throws BusinessException, IOException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar expirationDate = Calendar.getInstance();
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		logger.debug("inputStreamUuid : " + uuid);
		logger.debug("aDocument.getIdentifier : " + aDocument.getUuid());
		printDocs(jane);
		DocumentEntry encryptedDocumentEntry = enciphermentService.encryptDocument(jane, aDocumentEntry, jane, "password");
		logger.debug("encryptedDoc.getIdentifier : " + encryptedDocumentEntry.getUuid());
		logger.debug("aDocument.getIdentifier : " + aDocument.getUuid());
		logger.debug("inputStreamUuid : " + uuid);
		printDocs(jane);
		aDocumentEntry = encryptedDocumentEntry;
		aDocument = encryptedDocumentEntry.getDocument();
		Assert.assertTrue(encryptedDocumentEntry.getCiphered());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	private void printDocs(User user) {
		logger.debug("begin : " + user.getLogin());
		for (Entry doc : user.getEntries()) {
			if (doc.getEntryType() == EntryType.DOCUMENT) {
				logger.debug("doc : " + ((DocumentEntry)doc).getDocument().getUuid());
			}
		}
		logger.debug("end");
	}
	
	@Test
	public void testDecryptDocument() throws BusinessException, IOException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar expirationDate = Calendar.getInstance();
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		testEncipherDocument();
		aDocument.setSignatures(new HashSet<Signature>());
		DocumentEntry decryptedDocumentEntry = enciphermentService.decryptDocument(jane, aDocumentEntry, jane, "password");
		Assert.assertFalse(decryptedDocumentEntry.getCiphered());
		aDocumentEntry = decryptedDocumentEntry;
		aDocument = decryptedDocumentEntry.getDocument();
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}


