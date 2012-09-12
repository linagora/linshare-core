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
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.Language;

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

	private StringBuffer bufferTXT = new StringBuffer();
	private StringBuffer bufferHTML = new StringBuffer();

	
	public MailContainerWithRecipient(MailContainerWithRecipient mailContainer) {
		super(mailContainer);
		this.recipient		=	mailContainer.getRecipient();
		this.replyTo 		=	mailContainer.getReplyTo();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param mailContainer
	 */
	public MailContainerWithRecipient(MailContainer mailContainer, String recipient) {
		super(mailContainer);
		this.recipient=recipient;
	}

	/**
	 * Create a mailContainer, used by the MailContainerBuilder
	 * tapestry service.
	 * 
	 * @param personalMessage personalMessage: not required
	 * @param language language of the email
	 */
	public MailContainerWithRecipient(String personalMessage, Language language) {
		super(personalMessage, language);
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

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}	

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	
	
	/**
	 * Stuffs 
	 */
	
	
	public void appendTemplate(MailTemplate template) {
		bufferTXT.append(template.getContentTXT() + "\n");
		bufferHTML.append(template.getContentHTML() + "<br/>");
	}
	

	@Override
	public String getContentTXT() {
		contentTXT = bufferTXT.toString();
		return super.getContentTXT();
	}

	@Override
	public String getContentHTML() {
		contentHTML= bufferHTML.toString();
		return super.getContentHTML();
	}
	
	public void setMailSubject(MailSubject mailSubject) {
		this.subject = mailSubject.getContent();
	}

	@Override
	public void setContentTXT(String contentTXT) {
		this.bufferTXT= new StringBuffer(contentTXT);
		super.setContentTXT(contentTXT);
	}

	@Override
	public void setContentHTML(String contentHTML) {
		this.bufferHTML = new StringBuffer(contentHTML);
		super.setContentHTML(contentHTML);
	}
	
}
