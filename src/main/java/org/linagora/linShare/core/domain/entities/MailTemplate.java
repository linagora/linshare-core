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
import org.linagora.linShare.core.domain.constants.MailTemplateEnum;

/**
 * Object that contains all informations needed to construct
 * the email template content.
 * 
 * @author sduprey
 *
 */
public class MailTemplate {
	private MailTemplateEnum mailTemplate;

	private String contentTXT;

	private String contentHTML;

	private Language language;
	
	public MailTemplate() {
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param mailTemplate
	 */
	public MailTemplate(MailTemplate mailTemplate) {
		this.contentHTML = mailTemplate.getContentHTML();
		this.contentTXT = mailTemplate.getContentTXT();
		this.language = mailTemplate.getLanguage();
		this.mailTemplate = mailTemplate.getMailTemplate();
	}

	public MailTemplateEnum getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(MailTemplateEnum mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	public String getContentTXT() {
		return contentTXT;
	}

	public void setContentTXT(String contentTXT) {
		this.contentTXT = contentTXT;
	}

	public String getContentHTML() {
		return contentHTML;
	}

	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

}
