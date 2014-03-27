/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.domain.entities;

import java.util.Locale;

import org.linagora.linshare.core.domain.constants.Language;

/**
 * Object that contains the informations used to build the email
 * content. Information hiding principle: for example, the view does 
 * not have to know that the services have to build two types of email 
 * contents (txt and html).
 * 
 * @author sduprey
 *
 */
public class MailContainer {
	protected String subject;
	protected String contentTXT;
	protected String contentHTML;
	protected String personalMessage;
	protected Language language;

	// Additional fields for Thunderbird plugin.
	protected String inReplyTo = "";
	protected String references = "";
	
	
	/**
	 * Copy constructor
	 * 
	 * @param mailContainer
	 */
	public MailContainer(MailContainer mailContainer) {
		this.subject = mailContainer.getSubject();
		this.contentTXT = mailContainer.getContentTXT();
		this.contentHTML = mailContainer.getContentHTML();
		this.personalMessage = mailContainer.getPersonalMessage();
		this.language = mailContainer.getLanguage();
	}


	
	
	
	
	public MailContainer(Language language) {
		super();
		this.personalMessage = "";
		this.language = language;
		this.subject = null;
		this.contentTXT = null;
		this.contentHTML = null;
	}
	
	
	
	public MailContainer(String locale) {
		super();
		this.personalMessage = "";
		this.language = getLanguageFromLocale(new Locale(locale));
		this.subject = null;
		this.contentTXT = null;
		this.contentHTML = null;
	}

	public MailContainer(String locale,String message, String subject) {
		super();
		this.personalMessage = message;
		this.language = getLanguageFromLocale(new Locale(locale));
		this.subject = subject;
		this.contentTXT = null;
		this.contentHTML = null;
	}
	
	public MailContainer(String locale,String message) {
		super();
		this.personalMessage = message;
		this.language = getLanguageFromLocale(new Locale(locale));
		this.subject = null;
		this.contentTXT = null;
		this.contentHTML = null;
	}
	
	
	
	
	private Language getLanguageFromLocale(Locale locale) {
        if (Locale.FRENCH.equals(locale)) {
        	return Language.FRENCH;
        }
        /* java.util.Locale doesn't support dutch */
        if (locale.getLanguage().equals("nl_NL") || locale.getLanguage().equals("nl")) {
        	return Language.DUTCH;
        }
        return Language.DEFAULT;
    }
	
	
	

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContentTXT(String contentTXT) {
		this.contentTXT = contentTXT;
	}

	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}

	public String getSubject() {
		return subject;
	}

	public String getContentTXT() {
		return contentTXT;
	}

	public String getContentHTML() {
		return contentHTML;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Language getLanguage() {
		return language;
	}

	public String getPersonalMessage() {
		return personalMessage;
	}
	
	public void setPersonalMessage(String personalMessage) {
		this.personalMessage = personalMessage;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}
	
}
