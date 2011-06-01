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
package org.linagora.linShare.view.tapestry.pages.administration.domains;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.constants.TimeUnit;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.domain.vo.AllowedMimeTypeVO;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.pages.administration.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IncludeJavaScriptLibrary(value = {"../../../components/UserSearchResults.js"})
public class MultipleDomainUpdate {

    private static final int FACTORMULTI = 1024;
    
	private static Logger logger = LoggerFactory.getLogger(Index.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
	private Messages messages;
    @Inject
    private DomainFacade domainFacade;
    @Inject
    private ParameterFacade parameterFacade;
    @Inject
    private UserFacade userFacade;
    
    @SessionState
    private UserVo loginUser;
    
	// The form that holds the admin params
	@InjectComponent
	private Form administrationForm;
    
    

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @Property
    private Long fileSizeMax; //ko
    @Property
    private Long userAvailableSize; //ko
    @Property
    private Long globalQuota;
    @Property
    private Boolean activeMimeType;
    
    @Property
    private Boolean closedDomain;
    @Property
    private Boolean restrictedDomain;
    @Property
    private Boolean domainWithGuests;
    @Property
    private Boolean guestsCanCreateOther;
    
    
    @Property
    private Boolean activeSignature;
    @Property
    private Boolean activeEncipherment;
    @Property
    private Boolean activeDocTimeStamp;
    @Property
    private Boolean activeGlobalQuota;
    @Property
    private Boolean deleteTempAdmin;
    @SuppressWarnings("unused")
	@Property
    private List<AllowedMimeTypeVO> supportedMimeType;
    @Property
    private Integer guestAccountExpiryTime;
    @Property
    private TimeUnit guestAccountExpiryUnit;
    @SuppressWarnings("unused")
	@Property
    private ShareExpiryRule shareExpiryRule;
    @Persist
    @Property
    private List<ShareExpiryRule> shareExpiryRules;
    
    @Property
    @Persist
    private Boolean needDeleteTempAdmin;
    
    @Property
    private TimeUnit defaultShareExpiryUnit;
    @Property
    private Integer defaultShareExpiryTime;
    
    @Property
    private TimeUnit defaultFileExpiryUnit;
    @Property
    private Integer defaultFileExpiryTime;
    
    @Property
    private Boolean deleteDocWithShareExpiryTime;
    
    @Property
    private String customLogoUrl;

	@Inject @Symbol("linshare.secured-storage.disallow")
	@Property
	private boolean securedStorageDisallowed;

	@SuppressWarnings("unused")
	@Inject @Symbol("linshare.secured-storage.disallowed.job.file-cleaner.activate")
	@Property
	private boolean fileCleanerActivated;
	
	@Persist
	@Property
	private List<DomainVo> domains;
	
	@Property
	@Persist
	private boolean superadmin;
	
	
	@Property
	private boolean guestAccountExpiryTimeChecked;
	@Property
	private boolean userAvailableSizeChecked;
	@Property
	private boolean defaultFileExpiryChecked;
	@Property
	private boolean activeGlobalQuotaChecked;
	@Property
	private boolean globalQuotaChecked;
	@Property
	private boolean fileSizeMaxChecked;
	@Property
	private boolean activeMimeTypeChecked;
	@Property
	private boolean activeSignatureChecked;
	@Property
	private boolean activeEnciphermentChecked;
	@Property
	private boolean activeDocTimeStampChecked;
	@Property
	private boolean closedDomainChecked;
	@Property
	private boolean restrictedDomainChecked;
	@Property
	private boolean domainWithGuestsChecked;
	@Property
	private boolean guestsCanCreateOtherChecked;
	@Property
	private boolean defaultShareExpiryTimeChecked;
	@Property
	private boolean deleteDocWithShareExpiryTimeChecked;
	@Property
	private boolean customLogoUrlChecked;

    @SuppressWarnings("unused")
	@Property
    private Boolean valueCheck;

	@Property
	private DomainVo domain;
    
	@Persist
	@Property
	private List<DomainVo> selectedDomains;

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() throws BusinessException {
    	
    	ParameterVo p = new ParameterVo();
    	superadmin = loginUser.isSuperAdmin();
    	
    	if (superadmin) {
    		domains = domainFacade.findAllDomains();
    	}

        fileSizeMax = p.getFileSizeMax();
        userAvailableSize = p.getUserAvailableSize();
        globalQuota = p.getGlobalQuota();
        activeMimeType = p.getActiveMimeType();
        activeSignature = p.getActiveSignature();
        activeDocTimeStamp = p.getActiveDocTimeStamp();
        activeGlobalQuota = p.getGlobalQuotaActive();
        
        activeEncipherment = false;
        guestAccountExpiryTime = p.getGuestAccountExpiryTime();
        guestAccountExpiryUnit = p.getGuestAccountExpiryUnit();
        defaultShareExpiryUnit = p.getDefaultShareExpiryUnit();
        defaultShareExpiryTime = p.getDefaultShareExpiryTime();
        deleteDocWithShareExpiryTime = p.getDeleteDocWithShareExpiryTime();
        
        closedDomain = p.getClosedDomain();
        restrictedDomain = p.getRestrictedDomain();
        domainWithGuests = p.getDomainWithGuests();
        guestsCanCreateOther = p.getGuestCanCreateOther();
        
        defaultFileExpiryUnit = p.getDefaultFileExpiryUnit();
        defaultFileExpiryTime = p.getDefaultFileExpiryTime();
        
        customLogoUrl = p.getCustomLogoUrl();
        
        shareExpiryRules = p.getShareExpiryRules();
        if (shareExpiryRules == null) {
            shareExpiryRules = new ArrayList<ShareExpiryRule>();
        }

        if (fileSizeMax != null) {
            fileSizeMax = fileSizeMax / FACTORMULTI;
        }
        if (userAvailableSize != null) {
            userAvailableSize = userAvailableSize / FACTORMULTI;
        }
        if (globalQuota != null) {
        	globalQuota = globalQuota / FACTORMULTI;
        }
        
        //check temp admin account (created by import.sql)
        if(needDeleteTempAdmin==null){
        	if(loginUser!=null && loginUser.getMail().equals(UserFacade.ADMIN_TEMP_MAIL)) {
        		needDeleteTempAdmin =false;
        	} else {
        		UserVo uservo = userFacade.searchTempAdminUser();
            	if(uservo!=null) needDeleteTempAdmin = true;
        		else needDeleteTempAdmin=false;
        	}
        }
        
        guestAccountExpiryTimeChecked = false;
        userAvailableSizeChecked = false;
        defaultFileExpiryChecked = false;
        activeGlobalQuotaChecked = false;
        globalQuotaChecked = false;
        fileSizeMaxChecked = false;
        activeMimeTypeChecked = false;
        activeSignatureChecked = false;
        activeEnciphermentChecked = false;
        activeDocTimeStampChecked = false;
        closedDomainChecked = false;
        restrictedDomainChecked = false;
        domainWithGuestsChecked = false;
        guestsCanCreateOtherChecked = false;
        defaultShareExpiryTimeChecked = false;
        deleteDocWithShareExpiryTimeChecked = false;
        customLogoUrlChecked = false;
        

        selectedDomains = new ArrayList<DomainVo>();
        
    }

    public ValueEncoder<ShareExpiryRule> getShareExpiryRuleEncoder() {
    	return new ValueEncoder<ShareExpiryRule>() {
			public String toClient(ShareExpiryRule value) {
                return ""+shareExpiryRules.indexOf(value);
			}

			public ShareExpiryRule toValue(String clientValue) {
				int key = Integer.parseInt(clientValue);
                if (shareExpiryRules.size() > key) {
                    return shareExpiryRules.get(key);
                } else {
                    return null;
                }
			}
    	};
    }

    public Object onAddRow() {
        ShareExpiryRule expiryRule = new ShareExpiryRule();
        shareExpiryRules.add(expiryRule);
        return expiryRule;
    }

    public void onRemoveRow(ShareExpiryRule expiryRule) {
        shareExpiryRules.remove(expiryRule);
    }

    
    
    public void onValidateFormFromAdministrationForm() {
        boolean activeEnciph = securedStorageDisallowed ? false : activeEncipherment;
    	
    	//just validate JCE
    	if(activeEnciph==true||activeSignature==true){
    		if(!parameterFacade.checkPlatformEncryptSupportedAlgo()){
    			administrationForm.recordError(messages.get("pages.administration.index.jce.error"));
    		}
    	}
    }
    
    
    
    public Object onSuccessFromAdministrationForm() throws BusinessException {
        
        for (DomainVo selected : selectedDomains) {
			System.out.println(selected.getIdentifier());
		}

        if (fileSizeMax != null) {
            fileSizeMax = fileSizeMax * FACTORMULTI;
        }
        if (userAvailableSize != null) {
            userAvailableSize = userAvailableSize * FACTORMULTI;
        }
        if (globalQuota != null) {
        	globalQuota = globalQuota * FACTORMULTI;
        }
        
        boolean activeEnciph = securedStorageDisallowed ? false : activeEncipherment;
        
        for (DomainVo selected : selectedDomains) {
			DomainVo domainToUpdate = domainFacade.retrieveDomain(selected.getIdentifier());
			ParameterVo p = domainToUpdate.getParameterVo();

	        ParameterVo params = new ParameterVo(p.getIdentifier(), 
	        		fileSizeMaxChecked ? fileSizeMax : p.getFileSizeMax(), 
	        		userAvailableSizeChecked ? userAvailableSize : p.getUserAvailableSize(), 
	        		globalQuotaChecked ? globalQuota : p.getGlobalQuota(), 
	        		p.getUsedQuota(),
	        		activeGlobalQuotaChecked ? activeGlobalQuota : p.getGlobalQuotaActive(),
	        		activeMimeTypeChecked ? activeMimeType : p.getActiveMimeType(), 
	        		activeSignatureChecked ? activeSignature : p.getActiveSignature(),
	        		activeEnciphermentChecked ? activeEnciph : p.getActiveEncipherment(),
	        		activeDocTimeStampChecked ? activeDocTimeStamp : p.getActiveDocTimeStamp(),
	        		guestAccountExpiryTimeChecked ? guestAccountExpiryTime : p.getGuestAccountExpiryTime(),
	        		guestAccountExpiryTimeChecked ? guestAccountExpiryUnit : p.getGuestAccountExpiryUnit(), 
	        		customLogoUrlChecked ? customLogoUrl : p.getCustomLogoUrl(),
	        		defaultShareExpiryTimeChecked ? defaultShareExpiryUnit : p.getDefaultShareExpiryUnit(),  
	        		defaultShareExpiryTimeChecked ? defaultShareExpiryTime : p.getDefaultShareExpiryTime(), 
	        		defaultFileExpiryChecked ? defaultFileExpiryUnit : p.getDefaultFileExpiryUnit(), 
	        		defaultFileExpiryChecked ? defaultFileExpiryTime : p.getDefaultFileExpiryTime(), 
	        		p.getShareExpiryRules(), 
	        		deleteDocWithShareExpiryTimeChecked ? deleteDocWithShareExpiryTime : p.getDeleteDocWithShareExpiryTime(),
	        		p.getWelcomeTexts(), 
	        		p.getMailTemplates(), 
	        		p.getMailSubjects(),
	        		closedDomainChecked ? closedDomain : p.getClosedDomain(), 
	        		restrictedDomainChecked ? restrictedDomain : p.getRestrictedDomain(), 
	        		domainWithGuestsChecked ? domainWithGuests : p.getDomainWithGuests(), 
	        		guestsCanCreateOtherChecked ? guestsCanCreateOther : p.getGuestCanCreateOther());
	        params = parameterFacade.saveOrUpdate(params);
		}
        
        shareSessionObjects.addMessage(messages.get("pages.administration.domains.multipleDomainUpdate.success"));
        
        return org.linagora.linShare.view.tapestry.pages.administration.domains.Index.class;
        
    }

    public boolean isSelected() {
        return false;
    }

    public void setSelected(boolean selected) {
    	if (selected && !selectedDomains.contains(domain)) {
    		selectedDomains.add(domain);
    	}
    	if (!selected && selectedDomains.contains(domain)) {
    		selectedDomains.remove(domain);
    	}
    }

    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }

}
