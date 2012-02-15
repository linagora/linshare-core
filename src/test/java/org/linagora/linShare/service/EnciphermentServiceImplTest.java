package org.linagora.linShare.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.repository.FunctionalityRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.EnciphermentService;
import org.linagora.linShare.core.service.UserService;
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
	private EnciphermentService enciphermentService;
	
	@Autowired
	private DocumentService documentService;
	
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	private LoadingServiceTestDatas datas;
	
	@Before
	public void setUp() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	
	@After
	public void tearDown() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
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
	public void testisDocumentEncrypted() throws UnsupportedEncodingException, GeneralSecurityException, BusinessException {
		String password=new String("password");
		
		
		String identifier = UUID.randomUUID().toString();
		
		Document aDoc = new Document(identifier,"doc","",Calendar.getInstance(),Calendar.getInstance(),datas.getUser1(),false,false,false,(long) 10);
		datas.getUser1().addDocument(aDoc );
		
		DocumentVo doc = new DocumentVo(identifier,"doc","",Calendar.getInstance(),Calendar.getInstance(),"doc","user1@linpki.org",false,false,false,(long) 10);

		UserVo user = new UserVo(datas.getUser1());
		Assert.assertFalse(enciphermentService.isDocumentEncrypted(doc));
		
//		enciphermentService.encryptDocument(doc,user ,password);
//		Assert.assertTrue(enciphermentService.isDocumentEncrypted(doc));
		
	}
	
}
