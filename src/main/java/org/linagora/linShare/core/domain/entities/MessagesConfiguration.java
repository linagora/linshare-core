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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MessagesConfiguration implements Serializable {

	private static final long serialVersionUID = 6390630183338725483L;
	
	/** Surrogate key. */
    private long id;
    private Set<WelcomeText> welcomeTexts;
    private Set<MailTemplate> mailTemplates;
    private Set<MailSubject> mailSubjects;

    public MessagesConfiguration() {
    }

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    public Set<WelcomeText> getWelcomeTexts() {
        return welcomeTexts;
    }

    public void addWelcomeText(WelcomeText welcomeText) {
        if (this.welcomeTexts == null) {
            this.welcomeTexts = new HashSet<WelcomeText>();
        }
        welcomeTexts.add(welcomeText);
    }

    public void setWelcomeTexts(Set<WelcomeText> welcomeTexts) {
        this.welcomeTexts = welcomeTexts;
    }

	public void setMailTemplates(Set<MailTemplate> mailTemplates) {
		this.mailTemplates = mailTemplates;
	}

	public Set<MailTemplate> getMailTemplates() {
		return mailTemplates;
	}

    public void addMailTemplate(MailTemplate mailTemplate) {
        if (this.mailTemplates == null) {
            this.mailTemplates = new HashSet<MailTemplate>();
        }
        mailTemplates.add(mailTemplate);
    }

	public void setMailSubjects(Set<MailSubject> mailSubjects) {
		this.mailSubjects = mailSubjects;
	}

	public Set<MailSubject> getMailSubjects() {
		return mailSubjects;
	}

    public void addMailSubject(MailSubject mailSubject) {
        if (this.mailSubjects == null) {
            this.mailSubjects = new HashSet<MailSubject>();
        }
        mailSubjects.add(mailSubject);
    }
	
	@Override
    public boolean equals(Object o1){
    	if(null==o1 || !(o1 instanceof MessagesConfiguration)){
    		return false;
    	}else{
    		if(o1==this || ((MessagesConfiguration)o1).id==this.id){
    			return true;
    		}else{
    			return false;
    		}
    	}
    }
    
    @Override
    public int hashCode(){
    	return new Long(this.id).hashCode();
    }
}
