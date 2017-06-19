/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-test.xml"
		})
public class MailNotifierServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(MailNotifierServiceImplTest.class);

	@Autowired
	private NotifierService mailNotifierService;

    private Wiser wiser;
    
	private static String LINSHARE_MAIL = "linShare@yourdomain.com"; 

	public MailNotifierServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}
    
    @Before
    public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		logger.debug(LinShareTestConstants.END_SETUP);
    }

    @After
    public void tearDown() {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
        wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
    }

    @Test
    public void testSendNotification() throws MessagingException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

    	String subject = "subject";
    	Language locale = Language.ENGLISH;
    	String fromUser = "foobar@foodomain.com";
    	String fromDomain = LINSHARE_MAIL;
    	String recipient = "johndoe@unknow.com";

		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(locale);
		mailContainer.setSubject(subject);
		mailContainer.setContent("");
		mailContainer.setFrom(fromDomain);
		mailContainer.setReplyTo(fromUser);
		mailContainer.setRecipient(recipient);

		mailNotifierService.sendNotification(mailContainer);

        if (wiser.getMessages().size() > 0) {
            WiserMessage wMsg = wiser.getMessages().get(0);
            MimeMessage msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", LINSHARE_MAIL, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", fromUser,
                    msg.getReplyTo()[0].toString());
        }else {
        	Assert.fail();
        }

        mailNotifierService.sendNotification(fromDomain, fromUser, recipient, subject, "<span>htmlContent</span>", null,null, null);
        
        if (wiser.getMessages().size() > 0) {
            WiserMessage wMsg = wiser.getMessages().get(1);
            MimeMessage msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", LINSHARE_MAIL, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", fromUser,
                    msg.getReplyTo()[0].toString());
        }else {
        	logger.error("No mail received");
        	Assert.fail();
        }
		logger.debug(LinShareTestConstants.END_TEST);
    }
    
    @Test
    public void testSendAllNotifications() throws MessagingException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

    	String subject = "subject";
    	String contentTxt = "content";
    	Language locale = Language.ENGLISH;
    	String fromUser = "foobar@foodomain.com";
    	String fromDomain = LINSHARE_MAIL;
    	String recipient = "johndoe@unknow.com";
    	String recipient2 = "janesmith@unknow.com";
 
    	MailContainer mailContainer = new MailContainer(locale,contentTxt,subject);
 
    	MailContainerWithRecipient mailContainerWithRecipient = new MailContainerWithRecipient(mailContainer, recipient, LINSHARE_MAIL, fromDomain);
    	MailContainerWithRecipient mailContainerWithRecipient2 = new MailContainerWithRecipient(mailContainer, recipient2, LINSHARE_MAIL, fromDomain);

    	List<MailContainerWithRecipient> mailContainerWithRecipientList = new ArrayList<MailContainerWithRecipient>();

		mailContainerWithRecipientList.add(mailContainerWithRecipient);
		mailContainerWithRecipientList.add(mailContainerWithRecipient2);

		mailNotifierService.sendNotification(mailContainerWithRecipientList);

        if (wiser.getMessages().size() > 0) {
            WiserMessage wMsg = wiser.getMessages().get(0);
            MimeMessage msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", fromDomain, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", LINSHARE_MAIL,
                    msg.getReplyTo()[0].toString());
            
            wMsg = wiser.getMessages().get(1);
            msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", LINSHARE_MAIL, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient2,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", LINSHARE_MAIL,
                    msg.getReplyTo()[0].toString());
            
        }else {
        	logger.error("No mail received");
        	Assert.fail();
        }
        
        mailContainerWithRecipientList.get(0).setReplyTo(fromUser);
        mailContainerWithRecipientList.get(1).setReplyTo(fromUser);
		mailNotifierService.sendNotification(mailContainerWithRecipientList);

        if (wiser.getMessages().size() > 0) {
            WiserMessage wMsg = wiser.getMessages().get(2);
            MimeMessage msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", fromDomain, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", fromUser,
                    msg.getReplyTo()[0].toString());
            
            wMsg = wiser.getMessages().get(3);
            msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", fromDomain, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient2,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", fromUser,
                    msg.getReplyTo()[0].toString());
        }else {
        	logger.error("No mail received");
        	Assert.fail();
        }
		logger.debug(LinShareTestConstants.END_TEST);
    }
}
