/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.service;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.utils.LinShareWiser;
import org.linagora.linshare.utils.TestMailResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", "classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-mongo.xml", "classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class MailContentBuildingServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(MailContentBuildingServiceImplTest.class);

	@Autowired
	private MailBuildingService mailBuildingService;

	@Autowired
	private DomainBusinessService domainBusinessService;

	@Autowired
	private NotifierService notifierService;

	/**
	 * If you want send generated emails to a webmail, enable this field and
	 * configure file : src/test/resources/linshare-test.properties with
	 * properties : - mail.smtp.host= - mail.smtp.port=
	 */
	private boolean sendMail = true;

	private String recipientForSendMail = "felton.gumper@int6.linshare.dev";

	@Test
	public void testBuildAllMails() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<TestMailResult> findErrors = Lists.newArrayList();
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		for (MailContentType type : MailContentType.values()) {
			logger.info("Building mail {} ", type);
			if (mailBuildingService.fakeBuildIsSupported(type)) {
				for (Language lang : Language.values()) {
					logger.info("Building mail {} with language {}", type, lang);
					List<ContextMetadata> contexts = mailBuildingService.getAvailableVariables(type);
					for (int flavor = 0; flavor < contexts.size(); flavor++) {
						MailContainerWithRecipient build = mailBuildingService.fakeBuild(type, cfg, lang, flavor, true);
						findErrors.addAll(testMailGenerate(type, build));
						String subject = type + " : CONTEXT=" + flavor + " : " + "LANG=" + lang + " : ";
						build.setSubject(subject + build.getSubject());
						sendMail(build);
					}
				}
			} else {
				logger.warn("Building mail {} was skipped. Not yet supported ?", type);
			}
		}
		logger.info("test testBuildAllMails is complete.");
		if (!findErrors.isEmpty()) {
			logger.error("Some errors were found :");
			for (TestMailResult result : findErrors) {
				logger.error(result.toString());
				if (logger.isTraceEnabled()) {
					logger.trace("StrPattern : {}", result.getStrPattern());
					logger.trace("Data : {}", result.getData());
				}
			}
		}
		Assertions.assertTrue(findErrors.isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildOneMail() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		MailContentType type = MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
		logger.info("Building mail {} ", type);
		List<TestMailResult> findErrors = Lists.newArrayList();
		List<ContextMetadata> contexts = mailBuildingService.getAvailableVariables(type);
		for (int flavor = 0; flavor < contexts.size(); flavor++) {
			MailContainerWithRecipient build = mailBuildingService.fakeBuild(type, cfg, Language.FRENCH, flavor, true);
			String subject = type + " : CONTEXT=" + flavor + " : " + "LANG=" + Language.FRENCH + " : ";
			build.setSubject(subject + build.getSubject());
			findErrors.addAll(testMailGenerate(type, build));
			sendMail(build);
		}
		if (!findErrors.isEmpty()) {
			for (TestMailResult result : findErrors) {
				logger.error(result.toString());
				logger.error(result.toString());
				if (logger.isTraceEnabled()) {
					logger.trace("StrPattern : {}", result.getStrPattern());
					logger.trace("Data : {}", result.getData());
				}
			}
		}
		Assertions.assertTrue(findErrors.isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUploadRequetUploadedEntryMail() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		MailContentType type = MailContentType.UPLOAD_REQUEST_UPLOADED_FILE;
		logger.info("Building mail {} ", type);
		List<TestMailResult> findErrors = Lists.newArrayList();
		List<ContextMetadata> contexts = mailBuildingService.getAvailableVariables(type);
		for (int flavor = 0; flavor < contexts.size(); flavor++) {
			MailContainerWithRecipient build = mailBuildingService.fakeBuild(type, cfg, Language.FRENCH, flavor, true);
			String subject = type + " : CONTEXT=" + flavor + " : " + "LANG=" + Language.FRENCH + " : ";
			build.setSubject(subject + build.getSubject());
			findErrors.addAll(testMailGenerate(type, build));
			sendMail(build);
		}
		if (!findErrors.isEmpty()) {
			for (TestMailResult result : findErrors) {
				logger.error(result.toString());
				logger.error(result.toString());
				if (logger.isTraceEnabled()) {
					logger.trace("StrPattern : {}", result.getStrPattern());
					logger.trace("Data : {}", result.getData());
				}
			}
		}
		Assertions.assertTrue(findErrors.isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private void sendMail(MailContainerWithRecipient mail) {
		if (sendMail) {
			mail.setRecipient(recipientForSendMail);
			mail.setFrom("linshare-noreply@linagora.com");
			notifierService.sendNotification(mail);
		}
	}

	public List<TestMailResult> testMailGenerate(MailContentType type, MailContainer mailContainer) {
		Assertions.assertNotNull(mailContainer);
		logger.debug("Subject: {}", mailContainer.getSubject());
		logger.debug("Content: {}", mailContainer.getContent());
		Assertions.assertTrue(!mailContainer.getSubject().isEmpty(), "We are expecting a subject template, not an empty string");
		Assertions.assertTrue(!mailContainer.getContent().isEmpty(), "We are expecting a body/content template, not an empty string");
		List<TestMailResult> findErrors = Lists.newArrayList();
		findErrors.addAll(LinShareWiser.testMailGenerate(type, mailContainer.getSubject()));
		findErrors.addAll(LinShareWiser.testMailGenerate(type, mailContainer.getContent()));
		return findErrors;
	}

}
