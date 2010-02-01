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
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.domain.entities.WelcomeText;

public class ParameterVo implements Serializable {

	private static final long serialVersionUID = -3129935137911233245L;
	
	private final Long fileSizeMax;
	private final Long userAvailableSize;
	private final Boolean activeMimeType;
	private final Boolean activeSignature;
	private final Boolean activeEncipherment;
    private final Integer guestAccountExpiryTime;
    private final TimeUnit guestAccountExpiryUnit;
    private final String customLogoUrl;

    private final Boolean deleteDocWithShareExpiryTime;
    private final TimeUnit defaultShareExpiryUnit;
    private final Integer defaultShareExpiryTime;
    
    private List<ShareExpiryRule> shareExpiryRules;

    private Set<WelcomeText> welcomeTexts;

	public ParameterVo() {
		this(null, null,false, null,null,null, TimeUnit.DAY, null, null, null, null, null, null);
	}

	public ParameterVo(Long fileSizeMax, Long userAvailableSize, Boolean activeMimeType, Boolean activeSignature,
		Boolean activeEncipherment,Integer guestAccountExpiryTime, TimeUnit guestAccountExpiryUnit, String customLogoUrl, 
		TimeUnit defaultShareExpiryUnit, Integer defaultShareExpiryTime,
        List<ShareExpiryRule> shareExpiryRules, Boolean deleteDocWithShareExpiryTime, Set<WelcomeText> welcomeTexts) {
		this.fileSizeMax = fileSizeMax;
		this.userAvailableSize = userAvailableSize;
		this.activeMimeType = activeMimeType;
		this.activeSignature = activeSignature;
		this.activeEncipherment = activeEncipherment;
        this.guestAccountExpiryTime = guestAccountExpiryTime;
        this.guestAccountExpiryUnit = guestAccountExpiryUnit;
        this.customLogoUrl = customLogoUrl;
        this.shareExpiryRules = shareExpiryRules;
        this.welcomeTexts = welcomeTexts;
        this.defaultShareExpiryUnit = defaultShareExpiryUnit;
        this.defaultShareExpiryTime = defaultShareExpiryTime;
        this.deleteDocWithShareExpiryTime = deleteDocWithShareExpiryTime;
        
	}

    public ParameterVo(Parameter parameter) {
		this.fileSizeMax = parameter.getFileSizeMax();
		this.userAvailableSize = parameter.getUserAvailableSize();
		this.activeMimeType = parameter.getActiveMimeType();
		this.activeSignature = parameter.getActiveSignature();
		this.activeEncipherment = parameter.getActiveEncipherment();
        this.guestAccountExpiryTime = parameter.getGuestAccountExpiryTime();
        this.guestAccountExpiryUnit = parameter.getGuestAccountExpiryUnit();
        this.customLogoUrl = parameter.getCustomLogoUrl();
        this.deleteDocWithShareExpiryTime = parameter.getDeleteDocWithShareExpiryTime();

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

	public List<ShareExpiryRule> getShareExpiryRules() {
        return shareExpiryRules;
    }

    public Set<WelcomeText> getWelcomeTexts() {
        return welcomeTexts;
    }

    public Boolean getDeleteDocWithShareExpiryTime() {
		return deleteDocWithShareExpiryTime;
	}

	public Parameter getParameter() {
        Parameter parameter = new Parameter();
        parameter.setFileSizeMax(fileSizeMax);
        parameter.setUserAvailableSize(userAvailableSize);
        parameter.setActiveMimeType(activeMimeType);
        parameter.setActiveSignature(activeSignature);
        parameter.setActiveEncipherment(activeEncipherment);
        parameter.setGuestAccountExpiryTime(guestAccountExpiryTime);
        parameter.setGuestAccountExpiryUnit(guestAccountExpiryUnit);
        parameter.setCustomLogoUrl(customLogoUrl);
        parameter.setDeleteDocWithShareExpiryTime(deleteDocWithShareExpiryTime);
        parameter.setDefaultShareExpiryTime(defaultShareExpiryTime);
        parameter.setDefaultShareExpiryUnit(defaultShareExpiryUnit);
        
        for (ShareExpiryRule shareExpiryRule : shareExpiryRules) {
            parameter.addShareExpiryRules(shareExpiryRule);
        }

        if (welcomeTexts!=null) {
        	for (WelcomeText welcomeText : welcomeTexts) {
        		parameter.addWelcomeText(welcomeText);
        	}
        }

        return parameter;
    }
}
