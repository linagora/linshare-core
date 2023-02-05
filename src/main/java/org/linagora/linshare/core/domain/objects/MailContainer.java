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
package org.linagora.linshare.core.domain.objects;

import java.util.Locale;

import org.linagora.linshare.core.domain.constants.Language;

/**
 * Object that contains the informations used to build the email content.
 * Information hiding principle: for example, the view does not have to know
 * that the services have to build two types of email contents (txt and html).
 * 
 * @author sduprey
 * 
 */
public class MailContainer {
	protected String subject;
	protected String content;
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
		this.content = mailContainer.getContent();
		this.personalMessage = mailContainer.getPersonalMessage();
		this.language = mailContainer.getLanguage();
	}

	public MailContainer(Language language) {
		super();
		this.personalMessage = "";
		this.language = language;
		this.subject = null;
		this.content = null;
	}

	public MailContainer(String locale) {
		super();
		this.personalMessage = "";
		this.language = Language.fromLocale(new Locale(locale));
		this.subject = null;
		this.content = null;
	}

	public MailContainer(Language locale, String content, String subject) {
		super();
		this.content = content;
		this.language = locale;
		this.subject = subject;
	}

	public MailContainer(String locale, String message) {
		super();
		this.personalMessage = message;
		this.language = Language.fromLocale(new Locale(locale));
		this.subject = null;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
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

	/**
	 * Helpers
	 */
	public Locale getLocale() {
		return Language.toLocale(language);
	}

}
