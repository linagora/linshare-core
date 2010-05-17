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
import org.linagora.linShare.core.domain.constants.MailSubjectEnum;

/**
 * Object that contains all informations needed to construct
 * the email subject.
 * 
 * @author sduprey
 *
 */
public class MailSubject {
	
	private MailSubjectEnum mailSubject;

	private String content;

	private Language language;
	
	public MailSubject() {
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param object
	 */
	public MailSubject(MailSubject object) {
		super();
		this.setContent(object.getContent());
		this.setLanguage(object.getLanguage());
		this.setMailSubject(object.getMailSubject());
	}

	public MailSubjectEnum getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(MailSubjectEnum mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
        final MailSubject other = (MailSubject) obj;
        if (this.mailSubject != other.mailSubject) {
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
        hash = 71 * hash + (this.mailSubject != null ? this.mailSubject.hashCode() : 0);
        hash = 71 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
	}
	

}
