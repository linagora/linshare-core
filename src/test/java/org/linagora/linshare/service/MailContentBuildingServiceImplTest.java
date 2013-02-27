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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
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
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
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
	private AnonymousShareEntryService anonymousShareEntryService;
	
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	private LoadingServiceTestDatas datas;
	
	private InputStream inputStream;
	
	private String inputStreamUuid;
	
	private User john;
	
	private User jane;
	
	private Document aDocument;
	
	private DocumentEntry aDocumentEntry;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
//		john = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
//		try  {
//			jane = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);			
//		} catch (BusinessException e) {
//			jane = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
//		}
//		
//		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
//		inputStreamUuid = fileRepository.insertFile(john.getLogin(), inputStream, 10000, "linshare-default.properties", "text/plain");
//				
//		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(inputStreamUuid);
//		
//		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
//		Calendar exp=inputStreamInfo.getLastModified();
//		exp.add(Calendar.HOUR, 4);
//		
//		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, john,false,false,new Long(10000));
//		Set<Signature> signatures = new HashSet<Signature>();
//		aDocument.setSignatures(signatures);
//		aDocumentEntry = new DocumentEntry(john, "new document", aDocument);
//
//		try {
//			documentRepository.create(aDocument);
//			documentEntryRepository.create(aDocumentEntry);
//			john.getEntries().add(aDocumentEntry);
//			userRepository.update(john);
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//			Assert.fail();
//		} catch (BusinessException e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
//		/*
//		MessagesConfiguration messagesConfiguration = new MessagesConfiguration();
//		
//		MailTemplate mailTemplate = new MailTemplate();
//		mailTemplate.setLanguage(Language.FRENCH);
//				
//		MailSubject mailSubject = new MailSubject();
//		mailSubject.setMailSubject(MailSubjectEnum.ANONYMOUS_DOWNLOAD);
//		mailSubject.setLanguage(Language.FRENCH);
//		
//		// Add all mailTemplates
//		for (MailTemplateEnum mailTemplateEnum : MailTemplateEnum.values()) {
//			mailTemplate.setMailTemplate(mailTemplateEnum);
//			messagesConfiguration.addMailTemplate(mailTemplate);
//			messagesConfiguration.addMailSubject(mailSubject);
//		}
//	
//		
////		datas.getRootDomain().setMessagesConfiguration(messagesConfiguration);
//		john.getDomain().setMessagesConfiguration(messagesConfiguration);
//		
//		abstractDomainRepository.update(john.getDomain());
//		
//		userRepository.update(john);
//*/
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		
//		documentEntryRepository.delete(aDocumentEntry);
//		documentRepository.delete(aDocument);
//		john.getEntries().clear();
//		userRepository.update(john);
//		fileRepository.removeFileByUUID(aDocument.getUuid());
		
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	private void testMailGenerate(MailContainer mailContainer){
		Assert.assertNotNull(mailContainer);
	}
	
	@Ignore
	@Test
	public void testBuildMailAnonymousDownload() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
	
//		User actor = john;
//		Contact recipient = new Contact(jane.getMail());
//		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
//		mailContainer.setLanguage(Language.FRENCH);
//		List<DocumentEntry> docs = new ArrayList<DocumentEntry>();
//		docs.add(aDocumentEntry);
//
//		// buildMailAnonymousDownload
//		AnonymousShareEntry anonymousShareEntry = anonymousShareEntryService.createAnonymousShare(docs, actor, recipient, Calendar.getInstance(), false, mailContainer).get(0);
//		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailAnonymousDownload(anonymousShareEntry);
//		testMailGenerate(mailContainerBuild);
//
//		// buildMailAnonymousDownloadWithOneRecipient
//		mailContainerBuild = mailContentBuildingService.buildMailNewSharingWithRecipient(mailContainer, anonymousShareEntry.getAnonymousUrl(), actor);
//		testMailGenerate(mailContainerBuild);
	
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Ignore
	@Test
	public void testBuildMailNewGuest() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
//		User actor = john;
//		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
//		mailContainer.setLanguage(Language.FRENCH);
//		List<Document> docs = new ArrayList<Document>();
//		docs.add(aDocument);
//		
//		String email = john.getMail();
//		User recipient = jane;
//		
//		// buildMailNewGuest
//		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailNewGuest(actor, mailContainer, john, recipient, "password");
//		testMailGenerate(mailContainerBuild);
//
//		// buildMailNewGuestWithRecipient
//		mailContainerBuild =  mailContentBuildingService.buildMailNewGuestWithRecipient(actor, mailContainer, john, recipient, "password");
//		testMailGenerate(mailContainerBuild);
//		
//		// buildMailNewGuestWithOneRecipient
//		List<MailContainerWithRecipient> mailContainerBuildList = mailContentBuildingService.buildMailNewGuestWithOneRecipient(actor, mailContainer, john, recipient, "password");
//		for (MailContainerWithRecipient mailContainerWithRecipient : mailContainerBuildList) {
//			Assert.assertTrue(mailContainerWithRecipient.getRecipient().equals(recipient.getMail()));
//			testMailGenerate(mailContainerWithRecipient);
//		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Ignore
	@Test
	public void testBuildMailResetPassword() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
//		User actor = john;
//		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
//		mailContainer.setLanguage(Language.FRENCH);
//		List<Document> docs = new ArrayList<Document>();
//		docs.add(aDocument);
//		
//		String email = john.getMail();
//		User recipient = jane;
//		
//		// buildMailResetPassword
//		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailResetPassword(actor, mailContainer, recipient, "password");
//		testMailGenerate(mailContainerBuild);
//
//		// buildMailResetPasswordWithRecipient
//		mailContainerBuild =  mailContentBuildingService.buildMailResetPasswordWithRecipient(actor, mailContainer, recipient, "password");
//		testMailGenerate(mailContainerBuild);
//		
//		// buildMailResetPasswordWithOneRecipient
//		List<MailContainerWithRecipient> mailContainerBuildList = mailContentBuildingService.buildMailResetPasswordWithOneRecipient(actor, mailContainer, recipient, "password");
//		for (MailContainerWithRecipient mailContainerWithRecipient : mailContainerBuildList) {
//			Assert.assertTrue(mailContainerWithRecipient.getRecipient().equals(recipient.getMail()));
//			testMailGenerate(mailContainerWithRecipient);
//		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Ignore
	@Test
	public void testBuildMailNewSharing() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
//		User actor = john;
//		MailContainer mailContainer = new MailContainer("subjet","contentTxt","contentHTML");
//		mailContainer.setLanguage(Language.FRENCH);
//		List<DocumentVo> docsVo = new ArrayList<DocumentVo>();
//		docsVo.add(new DocumentVo(aDocument.getUuid(), aDocument.getName(), "", aDocument.getCreationDate(), aDocument.getExpirationDate(), aDocument.getType(), john.getLogin(), false, false, aDocument.getSize()));
//		
//		
//		User recipient = jane;
//		
//		// buildMailNewSharing
//		MailContainer mailContainerBuild =  mailContentBuildingService.buildMailNewSharing(actor, mailContainer, john, jane, docsVo, "linShareUrl", "linShareUrlParam", "password", true, "jwsEncryptUrl");
//		testMailGenerate(mailContainerBuild);
//
//		// buildMailNewSharingWithRecipient
////		MailContainerWithRecipient mailContainerWithRecipientBuild =  mailContentBuildingService.buildMailNewSharingWithRecipient(actor, mailContainer, jane, docsVo, "linShareUrl", "linShareUrlParam", "password", true, "jwsEncryptUrl");
////		testMailGenerate(mailContainerWithRecipientBuild);
////		Assert.assertTrue(mailContainerWithRecipientBuild.getRecipient().equals(recipient.getMail()));

		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
