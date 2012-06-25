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
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.MailContentBuildingService;
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
public class MailContentBuildingServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(MailContentBuildingServiceImplTest.class);
	
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
	private MailContentBuildingService mailContentBuildingService;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	private LoadingServiceTestDatas datas;
	
	private InputStream inputStream;
	private String inputStreamUuid;
	private User john;
	private User jane;
	private Document aDocument;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		
//		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		//datas.loadUsers();
		
		john = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		
		try  {
			jane = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);			
		} catch (BusinessException e) {
			jane = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		}
		
		
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
		/*
		MessagesConfiguration messagesConfiguration = new MessagesConfiguration();
		
		MailTemplate mailTemplate = new MailTemplate();
		mailTemplate.setLanguage(Language.FRENCH);
				
		MailSubject mailSubject = new MailSubject();
		mailSubject.setMailSubject(MailSubjectEnum.ANONYMOUS_DOWNLOAD);
		mailSubject.setLanguage(Language.FRENCH);
		
		// Add all mailTemplates
		for (MailTemplateEnum mailTemplateEnum : MailTemplateEnum.values()) {
			mailTemplate.setMailTemplate(mailTemplateEnum);
			messagesConfiguration.addMailTemplate(mailTemplate);
			messagesConfiguration.addMailSubject(mailSubject);
		}
	
		
//		datas.getRootDomain().setMessagesConfiguration(messagesConfiguration);
		john.getDomain().setMessagesConfiguration(messagesConfiguration);
		
		abstractDomainRepository.update(john.getDomain());
		
		userRepository.update(john);
*/
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		documentRepository.delete(aDocument);
		john.getDocuments().clear();
		userRepository.update(john);
		fileRepository.removeFileByUUID(aDocument.getIdentifier());
		
//		datas.deleteUsers();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	private void testMailGenerate(MailContainer mailContainer){
		Assert.assertNotNull(mailContainer);

	}
	
	@Test
	public void testBuildMailAnonymousDownload() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		List<Document> docs = new ArrayList<Document>();
		docs.add(aDocument);
		
		String email = john.getMail();
		User recipient = jane;
		// buildMailAnonymousDownload
		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailAnonymousDownload(actor, mailContainer, docs, email, recipient);
		testMailGenerate(mailContainerBuild);

		// buildMailAnonymousDownloadWithOneRecipient
		List<MailContainerWithRecipient> mailContainerBuildList =  mailContentBuildingService.buildMailAnonymousDownloadWithOneRecipient(actor, mailContainer, docs, email, recipient);
		
		for (MailContainerWithRecipient mailContainerWithRecipient : mailContainerBuildList) {
			Assert.assertTrue(mailContainerWithRecipient.getRecipient().equals(recipient.getMail()));
			testMailGenerate(mailContainerWithRecipient);
		}
		
		// buildMailAnonymousDownloadWithRecipient
		mailContainerBuild =  mailContentBuildingService.buildMailAnonymousDownloadWithRecipient(actor, mailContainer, docs, email, recipient);
		testMailGenerate(mailContainerBuild);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildMailRegisteredDownload() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		List<Document> docs = new ArrayList<Document>();
		docs.add(aDocument);
		
		String email = john.getMail();
		User recipient = jane;
		
		// buildMailRegisteredDownload
		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailRegisteredDownload(actor, mailContainer, docs, john, recipient);
		testMailGenerate(mailContainerBuild);
		
		// buildMailRegisteredDownloadWithRecipient
		mailContainerBuild =  mailContentBuildingService.buildMailRegisteredDownloadWithRecipient(actor, mailContainer, docs, john, recipient);
		testMailGenerate(mailContainerBuild);
		
		// buildMailRegisteredDownloadWithOneRecipient
		List<MailContainerWithRecipient> mailContainerBuildList = mailContentBuildingService.buildMailRegisteredDownloadWithOneRecipient(actor, mailContainer, docs, john, recipient);
		for (MailContainerWithRecipient mailContainerWithRecipient : mailContainerBuildList) {
			Assert.assertTrue(mailContainerWithRecipient.getRecipient().equals(recipient.getMail()));
			testMailGenerate(mailContainerWithRecipient);
		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailNewGuest() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		List<Document> docs = new ArrayList<Document>();
		docs.add(aDocument);
		
		String email = john.getMail();
		User recipient = jane;
		
		// buildMailNewGuest
		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailNewGuest(actor, mailContainer, john, recipient, "password");
		testMailGenerate(mailContainerBuild);

		// buildMailNewGuestWithRecipient
		mailContainerBuild =  mailContentBuildingService.buildMailNewGuestWithRecipient(actor, mailContainer, john, recipient, "password");
		testMailGenerate(mailContainerBuild);
		
		// buildMailNewGuestWithOneRecipient
		List<MailContainerWithRecipient> mailContainerBuildList = mailContentBuildingService.buildMailNewGuestWithOneRecipient(actor, mailContainer, john, recipient, "password");
		for (MailContainerWithRecipient mailContainerWithRecipient : mailContainerBuildList) {
			Assert.assertTrue(mailContainerWithRecipient.getRecipient().equals(recipient.getMail()));
			testMailGenerate(mailContainerWithRecipient);
		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailResetPassword() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		List<Document> docs = new ArrayList<Document>();
		docs.add(aDocument);
		
		String email = john.getMail();
		User recipient = jane;
		
		// buildMailResetPassword
		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailResetPassword(actor, mailContainer, recipient, "password");
		testMailGenerate(mailContainerBuild);

		// buildMailResetPasswordWithRecipient
		mailContainerBuild =  mailContentBuildingService.buildMailResetPasswordWithRecipient(actor, mailContainer, recipient, "password");
		testMailGenerate(mailContainerBuild);
		
		// buildMailResetPasswordWithOneRecipient
		List<MailContainerWithRecipient> mailContainerBuildList = mailContentBuildingService.buildMailResetPasswordWithOneRecipient(actor, mailContainer, recipient, "password");
		for (MailContainerWithRecipient mailContainerWithRecipient : mailContainerBuildList) {
			Assert.assertTrue(mailContainerWithRecipient.getRecipient().equals(recipient.getMail()));
			testMailGenerate(mailContainerWithRecipient);
		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailNewSharing() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		List<DocumentVo> docsVo = new ArrayList<DocumentVo>();
		docsVo.add(new DocumentVo(aDocument.getIdentifier(), aDocument.getName(), "", aDocument.getCreationDate(), aDocument.getExpirationDate(), aDocument.getType(), john.getLogin(), false, false, aDocument.getSize()));
		
		
		User recipient = jane;
		
		// buildMailNewSharing
		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailNewSharing(actor, mailContainer, john, jane, docsVo, "linShareUrl", "linShareUrlParam", "password", true, "jwsEncryptUrl");
		testMailGenerate(mailContainerBuild);

		// buildMailNewSharingWithRecipient
		MailContainerWithRecipient mailContainerWithRecipientBuild =  mailContentBuildingService.buildMailNewSharingWithRecipient(actor, mailContainer, john, jane, docsVo, "linShareUrl", "linShareUrlParam", "password", true, "jwsEncryptUrl");
		testMailGenerate(mailContainerWithRecipientBuild);
		Assert.assertTrue(mailContainerWithRecipientBuild.getRecipient().equals(recipient.getMail()));

		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailSharedDocUpdated() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		
		User recipient = jane;
		
		// buildMailSharedDocUpdated
		MailContainer mailContainerBuild = mailContentBuildingService.buildMailSharedDocUpdated(actor, mailContainer, john, recipient.getMail(), aDocument, "oldDocName", " "+ aDocument.getSize(), "linShareUrl", "linShareUrlParam");
		testMailGenerate(mailContainerBuild);
		

		// buildMailSharedDocUpdatedWithRecipient
		MailContainerWithRecipient mailContainerWithRecipientBuild = mailContentBuildingService.buildMailSharedDocUpdatedWithRecipient(actor, mailContainer, john, recipient.getMail(), aDocument, "oldDocName", " "+ aDocument.getSize(), "linShareUrl", "linShareUrlParam");
		testMailGenerate(mailContainerWithRecipientBuild);
		Assert.assertTrue(mailContainerWithRecipientBuild.getRecipient().equals(recipient.getMail()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
