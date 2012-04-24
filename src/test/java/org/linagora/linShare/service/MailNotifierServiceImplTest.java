package org.linagora.linShare.service;

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
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

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
    	String txtContent = "content";
    	
    	String fromUser = "foobar@foodomain.com";
    	String recipient = "johndoe@unknow.com";
    	
    	MailContainer mailContainer = new MailContainer(subject,txtContent,"");
    	
    	mailNotifierService.sendNotification(fromUser, recipient, mailContainer);
    	
    	
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
        }else{
        	Assert.fail();
        }
        
        mailNotifierService.sendNotification(fromUser, recipient, subject, "<span>htmlContent</span>", txtContent);
        
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
        }else{
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
    	String contentHTML = "contentHTML";
    	
    	String fromUser = "foobar@foodomain.com";
    	String recipient = "johndoe@unknow.com";
    	String recipient2 = "janesmith@unknow.com";
    	
    	MailContainer mailContainer = new MailContainer(subject,contentTxt,contentHTML);
    	
    	MailContainerWithRecipient mailContainerWithRecipient = new MailContainerWithRecipient(mailContainer, recipient);;
    	MailContainerWithRecipient mailContainerWithRecipient2 = new MailContainerWithRecipient(mailContainer, recipient2);;

    	List<MailContainerWithRecipient> mailContainerWithRecipientList = new ArrayList<MailContainerWithRecipient>();
    	
		mailContainerWithRecipientList.add(mailContainerWithRecipient);
		mailContainerWithRecipientList.add(mailContainerWithRecipient2);
		
		mailNotifierService.sendAllNotifications(fromUser,mailContainerWithRecipientList);
    	
    	
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
            
            wMsg = wiser.getMessages().get(1);
            msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", LINSHARE_MAIL, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient2,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", fromUser,
                    msg.getReplyTo()[0].toString());
            
        }else{
        	logger.error("No mail received");
        	Assert.fail();
        }
        
		mailNotifierService.sendAllNotifications(mailContainerWithRecipientList);
    	
    	
        if (wiser.getMessages().size() > 0) {
            WiserMessage wMsg = wiser.getMessages().get(2);
            MimeMessage msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", LINSHARE_MAIL, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", LINSHARE_MAIL,
                    msg.getReplyTo()[0].toString());
            
            wMsg = wiser.getMessages().get(3);
            msg = wMsg.getMimeMessage();

            assertNotNull("message was null", msg);
            assertEquals("'Subject' did not match", subject, msg.getSubject());
            assertEquals("'From' address did not match", LINSHARE_MAIL, msg.getFrom()[0].toString());
            assertEquals("'To' address did not match", recipient2,
                msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
            assertEquals("'ReplyTo' address did not match", LINSHARE_MAIL,
                    msg.getReplyTo()[0].toString());
            
        }else{
        	logger.error("No mail received");
        	Assert.fail();
        }
		logger.debug(LinShareTestConstants.END_TEST);

    }
    
}
