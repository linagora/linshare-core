package org.linagora.linshare.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.SecuredUrlRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SecuredUrlService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.utils.HashUtils;
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
public class SecuredUrlServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(SecuredUrlServiceImplTest.class);

	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Autowired
	private SecuredUrlRepository securedUrlRepository;
	
	@Autowired
	private SecuredUrlService securedUrlService;
	
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
	private User john;
	private Document aDocument;
	
	private LoadingServiceTestDatas datas;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		
		john = datas.getUser1();
		jane = datas.getUser2();
		
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		inputStreamUuid = fileRepository.insertFile(john.getLogin(), inputStream, 10000, "linShare-default.properties", "text/plain");
				
		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(inputStreamUuid);
		
		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp=inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		
		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, john,false,false,new Long(10000));
		List<Signature> signatures = new ArrayList<Signature>();
		aDocument.setSignatures(signatures);
		
		try {
			documentRepository.create(aDocument);
			john.addDocument(aDocument);
			userRepository.update(john);
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
		documentRepository.delete(aDocument);
		john.getDocuments().clear();
		userRepository.update(john);
		fileRepository.removeFileByUUID(aDocument.getUuid());
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	@Test
	public void testCreate(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		//Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password, recipients, expirationDate);
		
		Assert.assertTrue(securedUrl.getDocuments().get(0).equals(aDocument));
		Assert.assertTrue(securedUrl.getPassword().equals(HashUtils.hashSha1withBase64(password.getBytes())));
		Assert.assertTrue(securedUrl.getExpirationTime().equals(expirationDate));
		Assert.assertTrue(securedUrl.getSender().equals(john));
		Assert.assertTrue(securedUrl.getRecipients().get(0).equals(janeContact));

		
		// Test with a specific url
		String testUrl = "my/test/url";
		securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		Assert.assertTrue(securedUrl.getDocuments().get(0).equals(aDocument));
		Assert.assertTrue(securedUrl.getPassword().equals(HashUtils.hashSha1withBase64(password.getBytes())));
		Assert.assertTrue(securedUrl.getExpirationTime().equals(expirationDate));
		Assert.assertTrue(securedUrl.getSender().equals(john));
		Assert.assertTrue(securedUrl.getRecipients().get(0).equals(janeContact));
		Assert.assertTrue(securedUrl.getUrlPath().equals(testUrl));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDelete(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		//Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		securedUrlService.delete(securedUrl.getAlea(), securedUrl.getUrlPath());
		
		try {
			Assert.assertNull(securedUrlRepository.find(securedUrl.getAlea(), securedUrl.getUrlPath()));
		} catch (LinShareNotSuchElementException e) {
		  logger.debug("test succeed no secured url find");
		}
			
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testGetDocument() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		//Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		Document doc = securedUrlService.getDocument(securedUrl.getAlea(), securedUrl.getUrlPath(), 0);
		
		Assert.assertTrue(doc.equals(aDocument));
		
		doc = securedUrlService.getDocument(securedUrl.getAlea(), securedUrl.getUrlPath(), password, 0);
		
		Assert.assertTrue(doc.equals(aDocument));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testGetDocuments() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		//Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		List<Document> documents2 = securedUrlService.getDocuments(securedUrl.getAlea(), securedUrl.getUrlPath());
		
		Assert.assertTrue(documents2.contains(aDocument));


		documents2 = securedUrlService.getDocuments(securedUrl.getAlea(), securedUrl.getUrlPath(), password);
		Assert.assertTrue(documents2.contains(aDocument));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testIsValid() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		//Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		// This secured url exist
		Assert.assertTrue(securedUrlService.isValid(securedUrl.getAlea(), securedUrl.getUrlPath()));

		// This secured url doesn't exist		
		Assert.assertFalse(securedUrlService.isValid("foo", "bar/test"));
		
		// This secured url exist
		Assert.assertTrue(securedUrlService.isValid(securedUrl.getAlea(), securedUrl.getUrlPath(), password));
		
		// This secured url doesn't exist		
		Assert.assertFalse(securedUrlService.isValid(securedUrl.getAlea(), securedUrl.getUrlPath(), "fakepassword"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testIsProtectedByPassword() throws LinShareNotSuchElementException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		//Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		Assert.assertTrue(securedUrlService.isProtectedByPassword(securedUrl.getAlea(), securedUrl.getUrlPath()));
		
		SecuredUrl securedUrl2 = securedUrlService.create(documents, jane,null,"new/test/url", recipients, expirationDate);
		
		Assert.assertFalse(securedUrlService.isProtectedByPassword(securedUrl2.getAlea(), securedUrl2.getUrlPath()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRemoveOutdatedSecuredUrl() throws LinShareNotSuchElementException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		// Substract 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		securedUrlService.removeOutdatedSecuredUrl();
		
		try {
			Assert.assertNull(securedUrlRepository.find(securedUrl.getAlea(), securedUrl.getUrlPath()));
		} catch (LinShareNotSuchElementException e) {
		  logger.debug("test succeed no secured url find");
		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testExists() throws LinShareNotSuchElementException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		// Substract 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		// This secured url exist
		Assert.assertTrue(securedUrlService.exists(securedUrl.getAlea(), securedUrl.getUrlPath()));

		// This secured url doesn't exist		
		Assert.assertFalse(securedUrlService.exists("foo", "bar/test"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testGetSecuredUrlOwner() throws LinShareNotSuchElementException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		// Substract 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		Assert.assertTrue(securedUrlService.getSecuredUrlOwner(securedUrl.getAlea(), securedUrl.getUrlPath()).equals(john));

		Assert.assertFalse(securedUrlService.getSecuredUrlOwner(securedUrl.getAlea(), securedUrl.getUrlPath()).equals(jane));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testGetUrlsByMailAndFile() throws LinShareNotSuchElementException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<Document> documents = new ArrayList<Document>();
		documents.add(aDocument);

		String password = "password";

		Contact janeContact = new Contact(jane.getMail());
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(janeContact);
		
		Calendar expirationDate = Calendar.getInstance();
		
		// Substract 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		
		String testUrl = "my/test/url";
		SecuredUrl securedUrl = securedUrlService.create(documents, john, password,testUrl, recipients, expirationDate);
		
		
		DocumentVo documentVo = new DocumentVo(aDocument.getUuid(), aDocument.getName(), "", aDocument.getCreationDate(), aDocument.getExpirationDate(), aDocument.getType(), aDocument.getOwner().getLogin(), aDocument.getEncrypted(), aDocument.getShared(), aDocument.getSize());
		
		List<SecuredUrl> urlsByMailAndFile = securedUrlService.getUrlsByMailAndFile(john, documentVo);
		
		Assert.assertTrue(urlsByMailAndFile.contains(securedUrl));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
