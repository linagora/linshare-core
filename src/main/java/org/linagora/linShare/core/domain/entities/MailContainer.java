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
package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.Language;

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
	private String subject;
	private String contentTXT;
	private String contentHTML;
	private String personalMessage;
	private String urlInternal;
	private String urlBase;
	private Language language;

	/**
	 * Builder provided for testing purpose.
	 * 
	 * @param subject
	 * @param contentTxt
	 * @param contentHTML
	 */
	public MailContainer(String subject, String contentTxt, String contentHTML) {
		super();
		this.subject = subject;
		this.contentTXT = contentTxt;
		this.contentHTML = contentHTML;
	}

	/**
	 * Create a mailContainer, used by the MailContainerBuilder
	 * tapestry service.
	 * 
	 * @param contentTXT template holding mail content in text format
	 * @param contentHTML template holding mail content in html format
	 * @param personalMessage personalMessage: not required
	 * @param language language of the email
	 * @param urlBase the linshare application url 
	 * @param urlInternal the internal user connection url
	 */
	public MailContainer(String contentTXT, String contentHTML,
			String personalMessage, Language language,
			String urlBase, String urlInternal) {
		super();
		this.contentTXT = contentTXT;
		this.contentHTML = contentHTML;
		this.personalMessage = personalMessage;
		this.language = language;
		this.urlBase = urlBase;
		this.urlInternal = urlInternal;
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

	public void setUrlInternal(String urlInternal) {
		this.urlInternal = urlInternal;
	}

	public String getUrlInternal() {
		return urlInternal;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public String getUrlBase() {
		return urlBase;
	}
}
