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
package org.linagora.linshare.core.domain.objects;

import java.util.Map;

import javax.activation.DataSource;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.User;

import com.google.common.collect.Maps;

/**
 * Object that contains the informations used to build the email
 * content and recipient address mail. Information hiding principle: for example, the view does
 * not have to know that the services have to build two types of email
 * contents (txt and html).
 *
 * @author ctjhoa
 *
 */
public class MailContainerWithRecipient extends MailContainer {

	private String recipient;
	private String replyTo;
	private String from;

	protected Map<String, DataSource> attachments = Maps.newHashMap();

	public MailContainerWithRecipient(MailContainerWithRecipient mailContainer) {
		super(mailContainer);
		this.recipient		=	mailContainer.getRecipient();
		this.replyTo 		=	mailContainer.getReplyTo();
		this.from			=	mailContainer.getFrom();
	}

	/**
	 * Copy constructor
	 * 
	 * @param mailContainer
	 */
	public MailContainerWithRecipient(MailContainer mailContainer, String recipient, String replyTo, String from) {
		super(mailContainer);
		this.recipient		=	recipient;
		this.replyTo 		=	replyTo;
		this.from			=	from;
	}

	public MailContainerWithRecipient(String locale) {
		super(locale);
	}

	public MailContainerWithRecipient(Language language) {
		super(language);
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(Contact c) {
		this.recipient = c.getMail();
	}

	public void setRecipient(User u) {
		this.recipient = u.getMail();
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}	

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(User u) {
		this.replyTo = u.getMail();
	}

	public void setReplyTo(Contact c) {
		this.replyTo = c.getMail();
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Map<String, DataSource> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, DataSource> attachments) {
		this.attachments = attachments;
	}

	public void addAttachment(String identifier, DataSource attachment) {
		this.attachments.put(identifier, attachment);
	}

}
