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
package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class builds an email notification and sends the email.
 * 
 * 0.8.2 : now the sender is always linshare email address and
 * the email address of the human acting in LinShare is in the 
 * REPLY-TO address (avoid spam exclusion).
 */
public class MailNotifierServiceImpl implements NotifierService {

	/** Class logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MailNotifierServiceImpl.class);

	private static final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

	/** The smtpServer that will send the email. */
	private volatile String smtpServer;

	/** The smtp user. */
	private final String smtpUser;

	/** The smtp password. */
	private final String smtpPassword;

	/** The smtp port. */
	private volatile int smtpPort;

	/** Is the server needing authentification. */
	private final boolean needsAuth;

	/** Mail charset. */
	private final String charset;

	/** Is starttls enabled **/
	private final boolean startTlsEnable;

	/** Is SSL enabled **/
	private final boolean sslEnable;

	private final String sslProtocols;

	/**
	 * see http://java.sun.com/developer/EJTechTips/2004/tt0625.html for
	 * multipart/alternative
	 */
	public MailNotifierServiceImpl(@Nonnull String smtpServer,
			int smtpPort,
			@Nullable String smtpUser,
			@Nullable String smtpPassword,
			boolean needsAuth,
			@Nonnull String charset,
			boolean startTlsEnable,
			boolean sslEnable,
			@Nullable String sslProtocols) {
		this.smtpServer = smtpServer;
		this.smtpPort = smtpPort;
		this.smtpUser = smtpUser;
		this.smtpPassword = smtpPassword;
		this.needsAuth = needsAuth;
		this.charset = charset;
		this.startTlsEnable = startTlsEnable;
		this.sslEnable = sslEnable;
		this.sslProtocols = sslProtocols;
	}

	private static boolean isPureAscii(@Nullable final String v) {
		return v!=null && !v.isBlank() && asciiEncoder.canEncode(v);
	}

	@Override
	public void sendNotification(@Nonnull final String smtpSender, @Nullable final String replyTo,
			@Nonnull final String recipient, @Nonnull final String subject, @Nonnull final String htmlContent,
			@Nullable final String inReplyTo, @Nullable final String references,
			@Nullable final Map<String, DataSource> attachments) throws SendFailedException, TechnicalException {

		if (this.smtpServer.isBlank()) {
			LOGGER.warn("Mail notifications are disabled.");
			return;
		}
		final Session session = getMailSession();
		final MimeMessage message = this.buildMimeMessage(session, smtpSender, replyTo, recipient, subject, htmlContent,
				inReplyTo, references, attachments);
		this.sendMimeMessage(session, message);
	}

	private @Nonnull MimeMessage buildMimeMessage(@Nonnull final Session session,
			@Nonnull final String smtpSender,
			@Nullable final String replyTo,
			@Nonnull final String recipient,
			@Nonnull final String subject,
			@Nonnull final String htmlContent,
			@Nullable final String inReplyTo,
			@Nullable final String references,
			@Nullable final Map<String, DataSource> attachments) throws TechnicalException {
		try {
			// Define message
			final MimeMessage messageMim = new MimeMessage(session);

			messageMim.setFrom(new InternetAddress(smtpSender));
			messageMim.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
			if (replyTo != null) {
				final InternetAddress[] reply = new InternetAddress[] { new InternetAddress(replyTo) };
				messageMim.setReplyTo(reply);
			}
			// En-têtes
			setHeaders(messageMim, inReplyTo, references);
			messageMim.setContent(buildMultipartContent(htmlContent, attachments, this.charset));
			messageMim.setSubject(subject, charset);
			// RFC 822 "Date" header field
			// Indicates that the message is complete and ready for delivery
			messageMim.setSentDate(new GregorianCalendar().getTime());
			return messageMim;
		} catch (final AddressException e) {
			throw new TechnicalException(TechnicalErrorCode.MAIL_VALIDATION, "Invalid email address", e);
		} catch (final MessagingException e) {
			throw new TechnicalException(TechnicalErrorCode.MAIL_COMPOSITION, "Message composition failed", e);
		}
	}

	private static void setHeaders(@Nonnull final MimeMessage message, @Nullable final String inReplyTo, @Nullable final String references) throws MessagingException {

		if (isPureAscii(inReplyTo)) {
				message.setHeader("In-Reply-To", inReplyTo);
		}
		if (isPureAscii(references)) {
				message.setHeader("References", references);
		}

	}

	private static Multipart buildMultipartContent(@Nonnull final String htmlContent,
			@Nullable final Map<String, DataSource> attachments, @Nonnull final String charset)
			throws TechnicalException {
		try {
			// Create a "related" Multipart message
			// content type is multipart/alternative
			// it will contain two part BodyPart 1 and 2
			final Multipart mp = new MimeMultipart("alternative");

			// BodyPart 2
			// content type is multipart/related
			// A multipart/related is used to indicate that message parts should
			// not be considered individually but rather
			// as parts of an aggregate whole. The message consists of a root
			// part (by default, the first) which reference other parts inline,
			// which may in turn reference other parts.
			final Multipart html_mp = new MimeMultipart("related");

			// Include an HTML message with images.
			// BodyParts: the HTML file and an image

			// Get the HTML file
			final BodyPart rel_bph = new MimeBodyPart();
			rel_bph.setDataHandler(
					new DataHandler(new ByteArrayDataSource(htmlContent, "text/html; charset=" + charset)));
			html_mp.addBodyPart(rel_bph);

			// Create the second BodyPart of the multipart/alternative,
			// set its content to the html multipart, and add the
			// second bodypart to the main multipart.
			final BodyPart alt_bp2 = new MimeBodyPart();
			alt_bp2.setContent(html_mp);
			mp.addBodyPart(alt_bp2);

			if (attachments != null) {
				// <img src="cid:image.part.1@linshare.org" />
				final Set<String> keySet = attachments.keySet();
				for (final String identifier : keySet) {
					final DataSource dataSource = attachments.get(identifier);
					final MimeBodyPart rel_bpi = new MimeBodyPart();
					rel_bpi.setFileName(dataSource.getName());
					rel_bpi.setText(dataSource.getName());
					rel_bpi.setDataHandler(new DataHandler(dataSource));
					rel_bpi.setHeader("Content-ID", "<" + identifier + ">");
					rel_bpi.setDisposition("inline");
					html_mp.addBodyPart(rel_bpi);
				}
			}
			return mp;
		} catch (final IOException | MessagingException e) {
			throw new TechnicalException(TechnicalErrorCode.MAIL_COMPOSITION, "Failed to build multipart email content",
					e);
		}
	}

	private void sendMimeMessage(@Nonnull final Session session, final @Nonnull MimeMessage messageMim) throws SendFailedException, TechnicalException {
		try {
			messageMim.saveChanges();
			// Connect to smtp server, if needed
			if (this.needsAuth) {
				// Since we used html tags, the content must be marker as text/html
				final Transport tr = session.getTransport("smtp");
				try {
					tr.connect(this.smtpServer, this.smtpPort, this.smtpUser, this.smtpPassword);
					tr.sendMessage(messageMim, messageMim.getAllRecipients());
				} finally {
					tr.close();
				}
			} else {
				// Send message
				Transport.send(messageMim);
			}
		} catch (final SendFailedException e) {
			LOGGER.error("SMTP delivery failed to at least one recipient.", e);
			throw e;
		} catch (final MessagingException e) {
			throw new TechnicalException(TechnicalErrorCode.MAIL_DELIVERY, "SMTP transport error", e);
		}
	}

	/**
	 * Create some properties and get the default Session
	 */
	private @Nonnull Session getMailSession() {

		// Set the host smtp address
		final Properties props = new Properties();
		props.put("mail.smtp.host", smtpServer);
		// if ssl is enabled
		if (sslEnable) {
			props.put("mail.smtp.socketFactory.port", String.valueOf(smtpPort));
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		} else if (startTlsEnable) {
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.ssl.protocols", sslProtocols);
		}
		props.put("mail.smtp.port", String.valueOf(smtpPort));

		if (needsAuth) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}

		// create some properties and get the default Session
		final Session session = Session.getInstance(props, null);
		if (LOGGER.isDebugEnabled()) {
			session.setDebug(true);
		} else {
			session.setDebug(false);
		}

		return session;
	}

	@Override
	public @Nonnull List<String> sendNotification(@Nonnull final List<MailContainerWithRecipient> mailContainerWithRecipient) throws BusinessException {
		final List<String> failedRecipients = new ArrayList<>();
		if (!mailContainerWithRecipient.isEmpty()) {
			for (final MailContainerWithRecipient mailContainer : mailContainerWithRecipient) {
				if (mailContainer == null) {
					continue;
				}
				try {
					if (mailContainer.getRecipient() == null) {
						LOGGER.error("can not send mails, no recipient");
					} else {
						this.sendNotification(mailContainer.getFrom(), mailContainer.getReplyTo(), mailContainer.getRecipient(),
								mailContainer.getSubject(), mailContainer.getContent(),
								mailContainer.getInReplyTo(), mailContainer.getReferences(), mailContainer.getAttachments());
					}
				} catch (final SendFailedException e) {
					LOGGER.error("Failed to send email to {}: {}", mailContainer.getRecipient(), e.getMessage(), e);
					failedRecipients.add(mailContainer.getRecipient());
				}
			}
		}
		return failedRecipients;
	}

	@Override
	public @Nonnull String getHost() {
		return this.smtpServer;
	}

	@Override
	public void setHost(@Nonnull final String host) {
		LOGGER.info("Reconfiguring Smtp current server ...");
		this.smtpServer = host;
		LOGGER.info("Smtp current server reconfigured to " + this.smtpServer);
	}

	@Override
	public @Nonnull int getPort() {
		return this.smtpPort;
	}

	@Override
	public void setPort(final int port) {
		LOGGER.info("Reconfiguring Smtp current port from {} to {}", this.smtpPort, port);
		if (port <= 0) {
			throw new IllegalArgumentException("invalid port value : " + port);
		}
		LOGGER.info("Reconfiguring Smtp port from {} to {}", this.smtpPort, port);
		this.smtpPort = port;
		LOGGER.info("Smtp current port reconfigured to {} ", port);
	}

	public boolean isStartTlsEnable() {
		return this.startTlsEnable;
	}

	public boolean isSslEnable() {
		return this.sslEnable;
	}
}
