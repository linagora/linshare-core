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

import java.io.Serializable;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailTemplateEnum;

/**
 * Object that contains all informations needed to construct
 * the email template content.
 * 
 * @author sduprey
 *
 */
public class MailTemplate implements Serializable {

	private static final long serialVersionUID = -6690960642670961316L;

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

	public MailTemplate(MailTemplateEnum mailTemplate, String contentHTML,
			String contentTXT, Language language) {
		super();
		this.mailTemplate = mailTemplate;
		this.contentTXT = contentTXT;
		this.contentHTML = contentHTML;
		this.language = language;
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
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MailTemplate other = (MailTemplate) obj;
        if (this.mailTemplate != other.mailTemplate) {
            return false;
        }
        if (this.language != other.language) {
            return false;
        }
        
        return true;
	}
	
	@Override
	public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.mailTemplate != null ? this.mailTemplate.hashCode() : 0);
        hash = 71 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
	}
	
	
}
