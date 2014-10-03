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
    
    public MessagesConfiguration(MessagesConfiguration m) {
    	
		for (WelcomeText welcomeText: m.getWelcomeTexts()) {
			addWelcomeText(welcomeText);
    	}
    			
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
    	if (null == o1 || !(o1 instanceof MessagesConfiguration)) {
    		return false;
    	} else {
    		return (o1 == this || ((MessagesConfiguration)o1).id == this.id);
    	}
    }
    
    @Override
    public int hashCode(){
    	return new Long(this.id).hashCode();
    }
}
