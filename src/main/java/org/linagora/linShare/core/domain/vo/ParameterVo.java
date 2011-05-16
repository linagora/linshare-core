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
package org.linagora.linShare.core.domain.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.domain.constants.TimeUnit;
import org.linagora.linShare.core.domain.entities.MailSubject;
import org.linagora.linShare.core.domain.entities.MailTemplate;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.domain.entities.WelcomeText;

public class ParameterVo implements Serializable {

	private static final long serialVersionUID = -3129935137911233245L;
	
	private final String identifier;
	
	private final Long fileSizeMax;
	private final Long userAvailableSize;
	private final Long globalQuota;
	private final Long usedQuota;
	private final Boolean globalQuotaActive;
	private final Boolean activeMimeType;
	private final Boolean activeSignature;
	private final Boolean activeEncipherment;
	private final Boolean activeDocTimeStamp;
	
    private final Integer guestAccountExpiryTime;
    private final TimeUnit guestAccountExpiryUnit;
    private final String customLogoUrl;

    private final Boolean deleteDocWithShareExpiryTime;
    private final TimeUnit defaultShareExpiryUnit;
    private final Integer defaultShareExpiryTime;
    private final TimeUnit defaultFileExpiryUnit;
    private final Integer defaultFileExpiryTime;
    
    private List<ShareExpiryRule> shareExpiryRules;

    private Set<WelcomeText> welcomeTexts;
    private Set<MailTemplate> mailTemplates;
    private Set<MailSubject> mailSubjects;

	public ParameterVo() {
		this(null,null, null, null, null, false, false, null,null,null,null, TimeUnit.DAY, null, null, null, null, null, null, null, null, null, null);
	}

	public ParameterVo(String identifier, Long fileSizeMax, Long userAvailableSize, 
		Long globalQuota, Long usedQuota, Boolean globalQuotaActive,
		Boolean activeMimeType, Boolean activeSignature,
		Boolean activeEncipherment,Boolean activeDocTimeStamp,Integer guestAccountExpiryTime, 
		TimeUnit guestAccountExpiryUnit, String customLogoUrl, 
		TimeUnit defaultShareExpiryUnit, Integer defaultShareExpiryTime,
		TimeUnit defaultFileExpiryUnit, Integer defaultFileExpiryTime,
        List<ShareExpiryRule> shareExpiryRules, Boolean deleteDocWithShareExpiryTime, 
        Set<WelcomeText> welcomeTexts, Set<MailTemplate> mailTemplates, Set<MailSubject> mailSubjects) {
		this.identifier = identifier;
		this.fileSizeMax = fileSizeMax;
		this.userAvailableSize = userAvailableSize;
		this.globalQuota = globalQuota;
		this.usedQuota = usedQuota;
		this.globalQuotaActive = globalQuotaActive;
		this.activeMimeType = activeMimeType;
		this.activeSignature = activeSignature;
		this.activeEncipherment = activeEncipherment;
		this.activeDocTimeStamp=activeDocTimeStamp;
        this.guestAccountExpiryTime = guestAccountExpiryTime;
        this.guestAccountExpiryUnit = guestAccountExpiryUnit;
        this.customLogoUrl = customLogoUrl;
        this.shareExpiryRules = shareExpiryRules;
        this.welcomeTexts = welcomeTexts;
        this.defaultShareExpiryUnit = defaultShareExpiryUnit;
        this.defaultShareExpiryTime = defaultShareExpiryTime;
        this.defaultFileExpiryUnit = defaultFileExpiryUnit;
        this.defaultFileExpiryTime = defaultFileExpiryTime;
        this.deleteDocWithShareExpiryTime = deleteDocWithShareExpiryTime;
        this.mailTemplates = mailTemplates;
        this.mailSubjects = mailSubjects;
	}

    public ParameterVo(Parameter parameter) {
    	this.identifier = parameter.getIdentifier();
		this.fileSizeMax = parameter.getFileSizeMax();
		this.userAvailableSize = parameter.getUserAvailableSize();
		this.globalQuota = parameter.getGlobalQuota();
		this.usedQuota = parameter.getUsedQuota();
		this.globalQuotaActive = parameter.getGlobalQuotaActive();
		this.activeMimeType = parameter.getActiveMimeType();
		this.activeSignature = parameter.getActiveSignature();
		this.activeEncipherment = parameter.getActiveEncipherment();
		this.activeDocTimeStamp=parameter.getActiveDocTimeStamp();
        this.guestAccountExpiryTime = parameter.getGuestAccountExpiryTime();
        this.guestAccountExpiryUnit = parameter.getGuestAccountExpiryUnit();
        this.customLogoUrl = parameter.getCustomLogoUrl();
        this.deleteDocWithShareExpiryTime = parameter.getDeleteDocWithShareExpiryTime();
        this.defaultFileExpiryUnit = parameter.getDefaultFileExpiryUnit();
        this.defaultFileExpiryTime = parameter.getDefaultFileExpiryTime();

        this.shareExpiryRules = new ArrayList<ShareExpiryRule>();
        for (ShareExpiryRule shareExpiryRule : parameter.getShareExpiryRules()) {
            shareExpiryRules.add(shareExpiryRule);
        }
        
        this.welcomeTexts = new HashSet<WelcomeText>();
        if (parameter.getWelcomeTexts()!=null) {
        	for (WelcomeText welcomeText : parameter.getWelcomeTexts()) {
        		welcomeTexts.add(welcomeText);
        	}
        }
        
        this.mailTemplates = new HashSet<MailTemplate>();
        if (parameter.getMailTemplates()!=null) {
        	for (MailTemplate mailTemplate : parameter.getMailTemplates()) {
        		mailTemplates.add(mailTemplate);
        	}
        }
        
        this.mailSubjects = new HashSet<MailSubject>();
        if (parameter.getMailSubjects()!=null) {
        	for (MailSubject mailSubject : parameter.getMailSubjects()) {
        		mailSubjects.add(mailSubject);
        	}
        }
        
        this.defaultShareExpiryUnit = parameter.getDefaultShareExpiryUnit();
        this.defaultShareExpiryTime = parameter.getDefaultShareExpiryTime();
    }

	public Long getFileSizeMax() {
		return fileSizeMax;
	}

	public Long getUserAvailableSize() {
		return userAvailableSize;
	}

	public Boolean getActiveMimeType() {
		return activeMimeType;
	}
	
	public Boolean getActiveSignature() {
		return activeSignature;
	}

	public Boolean getActiveEncipherment() {
		return activeEncipherment;
	}
	public Boolean getActiveDocTimeStamp() {
		return activeDocTimeStamp;
	}

	public Integer getGuestAccountExpiryTime() {
        return guestAccountExpiryTime;
    }

    public TimeUnit getGuestAccountExpiryUnit() {
        return guestAccountExpiryUnit;
    }

    public String getCustomLogoUrl() {
        return customLogoUrl;
    }

    public TimeUnit getDefaultShareExpiryUnit() {
		return defaultShareExpiryUnit;
	}

	public Integer getDefaultShareExpiryTime() {
		return defaultShareExpiryTime;
	}
	
	public Integer getDefaultFileExpiryTime() {
		return defaultFileExpiryTime;
	}
	
	public TimeUnit getDefaultFileExpiryUnit() {
		return defaultFileExpiryUnit;
	}

	public List<ShareExpiryRule> getShareExpiryRules() {
        return shareExpiryRules;
    }

    public Set<WelcomeText> getWelcomeTexts() {
        return welcomeTexts;
    }

    public Boolean getDeleteDocWithShareExpiryTime() {
		return deleteDocWithShareExpiryTime;
	}
    
    public Long getGlobalQuota() {
		return globalQuota;
	}
    
    public Long getUsedQuota() {
		return usedQuota;
	}
    
    public Boolean getGlobalQuotaActive() {
		return globalQuotaActive;
	}
    

	public Parameter getParameter() {
        Parameter parameter = new Parameter();
        parameter.setIdentifier(identifier);
        parameter.setFileSizeMax(fileSizeMax);
        parameter.setUserAvailableSize(userAvailableSize);
        parameter.setGlobalQuota(globalQuota);
        parameter.setUsedQuota(usedQuota);
        parameter.setGlobalQuotaActive(globalQuotaActive);
        parameter.setActiveMimeType(activeMimeType);
        parameter.setActiveSignature(activeSignature);
        parameter.setActiveEncipherment(activeEncipherment);
        parameter.setActiveDocTimeStamp(activeDocTimeStamp);
        parameter.setGuestAccountExpiryTime(guestAccountExpiryTime);
        parameter.setGuestAccountExpiryUnit(guestAccountExpiryUnit);
        parameter.setCustomLogoUrl(customLogoUrl);
        parameter.setDeleteDocWithShareExpiryTime(deleteDocWithShareExpiryTime);
        parameter.setDefaultShareExpiryTime(defaultShareExpiryTime);
        parameter.setDefaultShareExpiryUnit(defaultShareExpiryUnit);
        parameter.setDefaultFileExpiryTime(defaultFileExpiryTime);
        parameter.setDefaultFileExpiryUnit(defaultFileExpiryUnit);
        
        for (ShareExpiryRule shareExpiryRule : shareExpiryRules) {
            parameter.addShareExpiryRules(shareExpiryRule);
        }

        if (welcomeTexts!=null) {
        	for (WelcomeText welcomeText : welcomeTexts) {
        		parameter.addWelcomeText(welcomeText);
        	}
        }

        if (mailTemplates!=null) {
        	for (MailTemplate mailTemplate : mailTemplates) {
        		parameter.addMailTemplate(mailTemplate);
        	}
        }

        if (mailSubjects!=null) {
        	for (MailSubject mailSubject : mailSubjects) {
        		parameter.addMailSubject(mailSubject);
        	}
        }

        return parameter;
    }

	public void setMailTemplates(Set<MailTemplate> mailTemplates) {
		this.mailTemplates = mailTemplates;
	}

	public Set<MailTemplate> getMailTemplates() {
		return mailTemplates;
	}

	public void setMailSubjects(Set<MailSubject> mailSubjects) {
		this.mailSubjects = mailSubjects;
	}

	public Set<MailSubject> getMailSubjects() {
		return mailSubjects;
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
