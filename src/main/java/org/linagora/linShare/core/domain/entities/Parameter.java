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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.linagora.linShare.core.domain.constants.TimeUnit;

public class Parameter implements Serializable {

	private static final long serialVersionUID = 6390630184268725483L;

	//we have only one line of parameter (many columns for each param)
	public static final long CONSTANT_KEY_ID = 1; 
	
	/** Surrogate key. */
    private long id;

	private Long fileSizeMax;
	private Long userAvailableSize;
	private Boolean activeMimeType;
	private Boolean activeSignature;
	private Boolean activeEncipherment;
	private Boolean activeDocTimeStamp;
	
    private Integer guestAccountExpiryTime;
    private TimeUnit guestAccountExpiryUnit;
    private String customLogoUrl;

    private List<ShareExpiryRule> shareExpiryRules;
    private Set<WelcomeText> welcomeTexts;
    
    
    private Boolean deleteDocWithShareExpiryTime;
    private TimeUnit defaultShareExpiryUnit;
    private Integer defaultShareExpiryTime;

    public Parameter() {
    	this.fileSizeMax = null;
		this.userAvailableSize = null;
		this.activeMimeType = false;
		this.activeSignature = false;
		this.activeEncipherment = false;
		this.activeDocTimeStamp=false;
        this.customLogoUrl = null;
		this.id=CONSTANT_KEY_ID;
        this.guestAccountExpiryTime = null;
        this.guestAccountExpiryUnit = TimeUnit.DAY;
        this.defaultShareExpiryTime = null;
        this.defaultShareExpiryUnit = TimeUnit.DAY;
        this.deleteDocWithShareExpiryTime = false;
    }

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getFileSizeMax() {
		return fileSizeMax;
	}

	public void setFileSizeMax(Long fileSizeMax) {
		this.fileSizeMax = fileSizeMax;
	}

	public Long getUserAvailableSize() {
		return userAvailableSize;
	}

	public void setUserAvailableSize(Long userAvailableSize) {
		this.userAvailableSize = userAvailableSize;
	}

	public Boolean getActiveMimeType() {
		return activeMimeType;
	}

	public Boolean getActiveSignature() {
		return activeSignature;
	}

	public void setActiveSignature(Boolean activeSignature) {
		this.activeSignature = activeSignature;
	}
	
	public void setActiveDocTimeStamp(Boolean activeDocTimeStamp) {
		this.activeDocTimeStamp = activeDocTimeStamp;
	}
	public Boolean getActiveDocTimeStamp() {
		return activeDocTimeStamp;
	}

	public void setActiveMimeType(Boolean activeMimeType) {
		this.activeMimeType = activeMimeType;
	}
    
	public Boolean getActiveEncipherment() {
		return activeEncipherment;
	}

	public void setActiveEncipherment(Boolean activeEncipherment) {
		this.activeEncipherment = activeEncipherment;
	}

	public Integer getGuestAccountExpiryTime() {
        return guestAccountExpiryTime;
    }

    public void setGuestAccountExpiryTime(Integer guestAccountExpiryTime) {
        this.guestAccountExpiryTime = guestAccountExpiryTime;
    }

    public TimeUnit getGuestAccountExpiryUnit() {
        return guestAccountExpiryUnit;
    }

    public void setGuestAccountExpiryUnit(TimeUnit guestAccountExpiryUnit) {
        this.guestAccountExpiryUnit = guestAccountExpiryUnit;
    }

    public List<ShareExpiryRule> getShareExpiryRules() {
        return shareExpiryRules;
    }

    public void addShareExpiryRules(ShareExpiryRule shareExpiryRule) {
        if (this.shareExpiryRules == null) {
            this.shareExpiryRules = new ArrayList<ShareExpiryRule>();
        }
        this.shareExpiryRules.add(shareExpiryRule);
        Collections.sort(this.shareExpiryRules);
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

    public String getCustomLogoUrl() {
        return customLogoUrl;
    }

    public void setCustomLogoUrl(String customLogoUrl) {
        this.customLogoUrl = customLogoUrl;
    }

    public TimeUnit getDefaultShareExpiryUnit() {
		return defaultShareExpiryUnit;
	}

	public Integer getDefaultShareExpiryTime() {
		return defaultShareExpiryTime;
	}

	public void setDefaultShareExpiryUnit(TimeUnit defaultShareExpiryUnit) {
		this.defaultShareExpiryUnit = defaultShareExpiryUnit;
	}

	public void setDefaultShareExpiryTime(Integer defaultShareExpiryTime) {
		this.defaultShareExpiryTime = defaultShareExpiryTime;
	}

	public Boolean getDeleteDocWithShareExpiryTime() {
		return deleteDocWithShareExpiryTime;
	}

	public void setDeleteDocWithShareExpiryTime(Boolean deleteDocWithShareExpiryTime) {
		this.deleteDocWithShareExpiryTime = deleteDocWithShareExpiryTime;
	}

	
	@Override
    public boolean equals(Object o1){
    	if(null==o1 || !(o1 instanceof Parameter)){
    		return false;
    	}else{
    		if(o1==this || ((Parameter)o1).id==this.id){
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
