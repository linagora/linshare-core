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

	/**
	 * Create a mailContainer, used by the MailContainerBuilder
	 * tapestry service.
	 * 
	 * @param personalMessage personalMessage: not required
	 * @param language language of the email
	 */
	public MailContainer(String personalMessage, Language language) {
		super();
		this.personalMessage = personalMessage;
		this.language = language;
	}
	
	
	
	
	
	public MailContainer(String locale) {
		super();
		this.personalMessage = "";
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
        if (locale.getLanguage() == "nl_NL" || locale.getLanguage() == "nl") {
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
	
}
