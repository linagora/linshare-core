package org.linagora.linshare.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
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
		"classpath:springContext-dao.xml",
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
	private EnciphermentService enciphermentService;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;
	
	@Autowired
	private UserService userService;
	
	private InputStream inputStream;
	private String inputStreamUuid;
	private User jane;
	private Document aDocument;
	
	private LoadingServiceTestDatas datas;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		
		jane = datas.getUser2();
		
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		inputStreamUuid = fileRepository.insertFile(jane.getLogin(), inputStream, 10000, "linShare-default.properties", "text/plain");
				
		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(inputStreamUuid);
		
		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp=inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		
		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, jane,false,false,new Long(10000));
		List<Signature> signatures = new ArrayList<Signature>();
		aDocument.setSignatures(signatures);
		
		try {
			documentRepository.create(aDocument);
			jane.addDocument(aDocument);
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
		documentRepository.delete(aDocument);
//		Jane.deleteDocument(aDocument);
		jane.getDocuments().clear();
		userRepository.update(jane);
		fileRepository.removeFileByUUID(aDocument.getUuid());
		datas.deleteUsers();
		
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	@Test
	public void testChangeDocumentExtension() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String documentName = new String("foobar.txt");
		String documentNameEncipher = new String("foobar.txt.aes");
		
		String documentNameChanged = enciphermentService.changeDocumentExtension(documentName);

		Assert.assertTrue(documentNameEncipher.equalsIgnoreCase(documentNameChanged));
		
		String documentNameChanged2 = enciphermentService.changeDocumentExtension(documentNameChanged);
		
		Assert.assertTrue(documentName.equalsIgnoreCase(documentNameChanged2));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testIsDocumentEncrypted() throws UnsupportedEncodingException, GeneralSecurityException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Calendar expirationDate = Calendar.getInstance();
		
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		DocumentVo doc = new DocumentVo(aDocument.getUuid(),"doc","",Calendar.getInstance(),expirationDate,"doc","user1@linpki.org",false,false,(long) 10);

		Assert.assertFalse(enciphermentService.isDocumentEncrypted(doc));
		
		aDocument.setEncrypted(true);
		
		Assert.assertTrue(enciphermentService.isDocumentEncrypted(doc));
		
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	@Test
	public void testEncryptDocument() throws BusinessException, IOException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Calendar expirationDate = Calendar.getInstance();
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		
		logger.debug("inputStreamUuid : " + inputStreamUuid);
		logger.debug("aDocument.getIdentifier : " + aDocument.getUuid());

		DocumentVo docVo = new DocumentVo(aDocument.getUuid(),"doc","",Calendar.getInstance(),expirationDate,"doc",jane.getLogin(),false,false,(long) 10);
		
		UserVo userVo = new UserVo(jane);
		printDocs(jane);
		
		Document encryptedDoc = enciphermentService.encryptDocument(docVo, userVo, "password");
		logger.debug("encryptedDoc.getIdentifier : " + encryptedDoc.getUuid());
		logger.debug("aDocument.getIdentifier : " + aDocument.getUuid());
		logger.debug("inputStreamUuid : " + inputStreamUuid);
		
		printDocs(jane);		
		
		aDocument = encryptedDoc;
		
		Assert.assertTrue(aDocument.getEncrypted());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	private void printDocs(User user) {
		logger.debug("begin : " + user.getLogin());
		for (Document doc : user.getDocuments()) {
			logger.debug("doc : " + doc.getUuid());
			
		}
		logger.debug("end");
	}
	
	@Test
	public void testDecryptDocument() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Calendar expirationDate = Calendar.getInstance();
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);

		DocumentVo doc = new DocumentVo(aDocument.getUuid(),"doc","",Calendar.getInstance(),expirationDate,"doc",jane.getLogin(),false,false,(long) 10);

		UserVo userVo = new UserVo(jane);
		
		Document encryptedDoc = enciphermentService.encryptDocument(doc, userVo, "password");
		Assert.assertTrue(aDocument.getEncrypted());
		
		
		// Instantiate new DocumentVo encrypted
		doc = new DocumentVo(aDocument.getUuid(),"doc","",Calendar.getInstance(),expirationDate,"doc",jane.getLogin(),false,false,(long) 10);
		
		Document decryptedDoc = enciphermentService.decryptDocument(doc, userVo, "password");
		Assert.assertFalse(aDocument.getEncrypted());
		
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
