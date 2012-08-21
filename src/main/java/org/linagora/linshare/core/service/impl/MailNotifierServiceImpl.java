/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
 */
package org.linagora.linshare.core.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class builds an email notification and sends the email.
 * 
 * 0.8.2 : now the sender is always linshare email address and
 * the email address of the human acting in LinShare is in the 
 * REPLY-TO address (avoid spam exclusion).
 */
public class MailNotifierServiceImpl implements NotifierService {

	/** The smtpServer that will send the email. */
	private final String smtpServer;

	/** The smtp user. */
	private final String smtpUser;

	/** The sender identity. */
	private final String smtpSender;

	/** The smtp password. */
	private final String smtpPassword;

	/** The smtp port. */
	private final int smtpPort;

	/** Is the server needing authentification. */
	private final boolean needsAuth;

	/** Mail charset. */
	private final String charset;
	
	/** Display LinShare logo ? */
	private final boolean displayLogo;

	/** Class logger */
	private static final Logger logger = LoggerFactory.getLogger(MailNotifierServiceImpl.class);

	/**
	 * see http://java.sun.com/developer/EJTechTips/2004/tt0625.html for
	 * multipart/alternative
	 */
	public MailNotifierServiceImpl(String smtpServer, int smtpPort,
			String smtpSender, String smtpUser, String smtpPassword,
			boolean needsAuth, String charset, boolean displayLogo) {
		this.smtpServer = smtpServer;
		this.smtpPort = smtpPort;
		this.smtpSender = smtpSender;
		this.smtpUser = smtpUser;
		this.smtpPassword = smtpPassword;
		this.needsAuth = needsAuth;
		this.charset = charset;
		this.displayLogo = displayLogo;
	}

	/**
	 * Send notification to a recipient.
	 * 
	 * @param recipient
	 *            notification recipient.
	 * @param subject
	 *            subject.
	 * @param content
	 *            content.
	 */
	@Override
	public void sendNotification(String replyTo, String recipient, String subject, String htmlContent, String textContent) throws SendFailedException{

		// get the mail session
		Session session = getMailSession();

		// Define message
		MimeMessage messageMim = new MimeMessage(session);

		try {
			messageMim.setFrom(new InternetAddress(smtpSender));

			if (replyTo != null) {
				InternetAddress reply[] = new InternetAddress[1];
				reply[0] = new InternetAddress(replyTo);
				messageMim.setReplyTo(reply);
			}

			messageMim.addRecipient(javax.mail.Message.RecipientType.TO,
					new InternetAddress(recipient));

			messageMim.setSubject(subject, charset);

			// Create a "related" Multipart message
			// content type is multipart/alternative
			// it will contain two part BodyPart 1 and 2
			Multipart mp = new MimeMultipart("alternative");

			// BodyPart 1 is text file
			BodyPart alt_bp1 = new MimeBodyPart();
			alt_bp1.setDataHandler(new DataHandler(new ByteArrayDataSource(
					textContent, "text/plain")));
			mp.addBodyPart(alt_bp1);

			// BodyPart 2
			// content type is multipart/related
			// A multipart/related is used to indicate that message parts should
			// not be considered individually but rather
			// as parts of an aggregate whole. The message consists of a root
			// part (by default, the first) which reference other parts inline,
			// which may in turn reference other parts.
			Multipart html_mp = new MimeMultipart("related");

			// Include an HTML message with images.
			// BodyParts: the HTML file and an image

			// Get the HTML file
			BodyPart rel_bph = new MimeBodyPart();
			rel_bph.setDataHandler(new DataHandler(new ByteArrayDataSource(
					htmlContent, "text/html; charset=" + charset)));
			html_mp.addBodyPart(rel_bph);

			// inline image ?
			if (displayLogo) {
				String cid = "image.part.1@linshare.org";
				MimeBodyPart rel_bpi = new MimeBodyPart();
			
				// Initialize and add the image file to the html body part
				rel_bpi.setFileName("mail_logo.png");
				rel_bpi.setText("linshare");
				URL resource = getClass().getResource("/org/linagora/linshare/core/service/mail_logo.png");
				if(resource == null) {
					logger.error("Embedded logo was not found.");
					throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION, "Error sending notification : embedded logo was not found.");
				}
				rel_bpi.setDataHandler(new DataHandler(resource));
				rel_bpi.setHeader("Content-ID", "<" + cid + ">");
				rel_bpi.setDisposition("inline");
				html_mp.addBodyPart(rel_bpi);
			}

			// Create the second BodyPart of the multipart/alternative,
			// set its content to the html multipart, and add the
			// second bodypart to the main multipart.
			BodyPart alt_bp2 = new MimeBodyPart();
			alt_bp2.setContent(html_mp);
			mp.addBodyPart(alt_bp2);

			messageMim.setContent(mp);
			
			// RFC 822 "Date" header field
			// Indicates that the message is complete and ready for delivery
			messageMim.setSentDate(new GregorianCalendar().getTime());
			
			// Since we used html tags, the content must be marker as text/html
			// messageMim.setContent(content,"text/html; charset="+charset);

			Transport tr = session.getTransport("smtp");

			// Connect to smtp server, if needed
			if (needsAuth) {
				tr.connect(smtpServer, smtpPort, smtpUser, smtpPassword);
				messageMim.saveChanges();
				tr.sendMessage(messageMim, messageMim.getAllRecipients());
				tr.close();
			} else {
				// Send message
				Transport.send(messageMim);
			}
		} catch (SendFailedException e) {
			logger.error("Error sending notification on " + smtpServer + " port " + smtpPort +"to"+ recipient, e);
			throw e;
		} catch (MessagingException e) {
			logger.error("Error sending notification on " + smtpServer + " port " + smtpPort, e);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION, "Error sending notification", e);
		} catch (Exception e) {
			logger.error("Error sending notification on " + smtpServer + " port " + smtpPort, e);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION, "Error sending notification", e);
		}
	}

	/**
	 * Create some properties and get the default Session
	 */
	private Session getMailSession() {

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", smtpPort + "");

		if (needsAuth) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
		
		// create some properties and get the default Session
		Session session = Session.getInstance(props, null);
		if (logger.isDebugEnabled()) {
			session.setDebug(true);
		} else {
			session.setDebug(false);
		}

		return session;
	}

	/**
	 * Send notification giving a mailContainer object.
	 */
	@Override
	public void sendNotification(String replyTo, String recipient,
			MailContainer mailContainer) throws SendFailedException{
		sendNotification(replyTo, recipient, mailContainer.getSubject(),
				mailContainer.getContentHTML(), mailContainer.getContentTXT());

	}
	
	
	/**
	 * Send multiple notifications giving a mailContainerWithRecipient object.
	 */	
	@Override
	public void sendAllNotifications(List<MailContainerWithRecipient> mailContainerWithRecipient) throws BusinessException {
		
		List<String> unknownRecipients = new ArrayList<String>();	
		
		for (MailContainerWithRecipient mailContainer : mailContainerWithRecipient) {
			try {
				sendNotification(mailContainer.getReplyTo(), mailContainer.getRecipient(), mailContainer);
			} catch (SendFailedException e) {
				unknownRecipients.add(mailContainer.getRecipient());
			}
		}
		
		if(!unknownRecipients.isEmpty()){
			logger.error("Addresses unreachables : " + unknownRecipients.toString());
			throw new BusinessException(BusinessErrorCode.RELAY_HOST_NOT_ENABLE, "Address Unreachable", unknownRecipients);
		}
	}	

}
