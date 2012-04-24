package org.linagora.linShare.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.GroupMemberType;
import org.linagora.linShare.core.domain.constants.GroupMembershipStatus;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.repository.FunctionalityRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.MailContentBuildingService;
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
		
		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, john,false,false,false,new Long(10000));
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
		docsVo.add(new DocumentVo(aDocument.getIdentifier(), aDocument.getName(), "", aDocument.getCreationDate(), aDocument.getExpirationDate(), aDocument.getType(), john.getLogin(), false, false, false, aDocument.getSize()));
		
		
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
	
	@Test
	public void testBuildMailGroupSharingDeleted() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		
		Group group = new Group();
		group.setName("group test");
		
		// buildMailGroupSharingDeleted
		MailContainer mailContainerBuild = mailContentBuildingService.buildMailGroupSharingDeleted(actor, mailContainer, john, group, aDocument);
		testMailGenerate(mailContainerBuild);

		// buildMailGroupSharingDeletedWithRecipient
		MailContainerWithRecipient mailContainerWithRecipientBuild = mailContentBuildingService.buildMailGroupSharingDeletedWithRecipient(actor, mailContainerBuild, john, group, aDocument);
		testMailGenerate(mailContainerWithRecipientBuild);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailGroupMembershipStatus() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		
		User recipient = jane;
		
		Group group = new Group();
		group.setName("group test");
		
		GroupMember newMember = new GroupMember();
		newMember.setUser(recipient);
		newMember.setMembershipDate(Calendar.getInstance());
		newMember.setType(GroupMemberType.MEMBER);
		newMember.setOwner(john);
		
		// buildMailGroupMembershipStatus
		MailContainer mailContainerBuild = mailContentBuildingService.buildMailGroupMembershipStatus(actor, mailContainer, newMember, group, GroupMembershipStatus.ACCEPTED);
		testMailGenerate(mailContainerBuild);

		// buildMailGroupMembershipStatusWithRecipient
		MailContainerWithRecipient mailContainerWithRecipientBuild = mailContentBuildingService.buildMailGroupMembershipStatusWithRecipient(actor, mailContainerBuild, newMember, group, GroupMembershipStatus.ACCEPTED);
		testMailGenerate(mailContainerWithRecipientBuild);
		Assert.assertTrue(mailContainerWithRecipientBuild.getRecipient().equals(actor.getMail()));

		//buildMailGroupMembershipStatusWithOneRecipient
		List<MailContainerWithRecipient> mailContainerBuildList = mailContentBuildingService.buildMailGroupMembershipStatusWithOneRecipient(actor, mailContainerBuild, newMember, group, GroupMembershipStatus.ACCEPTED);
		for (MailContainerWithRecipient mailContainerWithRecipient : mailContainerBuildList) {
			Assert.assertTrue(mailContainerWithRecipient.getRecipient().equals(actor.getMail()));
			testMailGenerate(mailContainerWithRecipient);
		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testBuildMailUpcomingOutdatedSecuredUrl() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User actor = john;
		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
		mailContainer.setLanguage(Language.FRENCH);
		
		User recipient = jane;
		
		Group group = new Group();
		group.setName("group test");
		
		GroupMember newMember = new GroupMember();
		newMember.setUser(recipient);
		newMember.setMembershipDate(Calendar.getInstance());
		newMember.setType(GroupMemberType.MEMBER);
		newMember.setOwner(john);
		
		List<Contact> recipients = new ArrayList<Contact>();
		recipients.add(new Contact(jane.getMail()));
		
		// buildMailUpcomingOutdatedSecuredUrl
		MailContainer mailContainerBuild = mailContentBuildingService.buildMailUpcomingOutdatedSecuredUrl(actor, mailContainer, new SecuredUrl("urlPath", "alea", aDocument.getExpirationDate(), john, recipients), new Contact(recipient.getMail()), 5, "");
		testMailGenerate(mailContainerBuild);

		// buildMailUpcomingOutdatedSecuredUrl
		MailContainerWithRecipient mailContainerWithRecipientBuild = mailContentBuildingService.buildMailUpcomingOutdatedSecuredUrlWithRecipient(actor, mailContainer, new SecuredUrl("urlPath", "alea", aDocument.getExpirationDate(), john, recipients), new Contact(recipient.getMail()), 5, "");
		testMailGenerate(mailContainerWithRecipientBuild);
		Assert.assertTrue(mailContainerWithRecipientBuild.getRecipient().equals(recipient.getMail()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
