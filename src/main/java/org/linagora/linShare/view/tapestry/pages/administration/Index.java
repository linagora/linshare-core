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
package org.linagora.linShare.view.tapestry.pages.administration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.constants.TimeUnit;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.domain.vo.AllowedMimeTypeVO;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Index {

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
    private Boolean activeMimeType;
    
    
    @Property
    private Boolean activeSignature;
    @Property
    private Boolean activeEncipherment;
    @Property
    private Boolean activeDocTimeStamp;
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

	@Inject @Symbol("linshare.secured-storage.disallow")
	@Property
	private boolean securedStorageDisallowed;

	@SuppressWarnings("unused")
	@Inject @Symbol("linshare.secured-storage.disallowed.job.file-cleaner.activate")
	@Property
	private boolean fileCleanerActivated;
    

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() throws BusinessException {
        ParameterVo p = parameterFacade.loadConfig();

        fileSizeMax = p.getFileSizeMax();
        userAvailableSize = p.getUserAvailableSize();
        activeMimeType = p.getActiveMimeType();
        activeSignature = p.getActiveSignature();
        activeDocTimeStamp = p.getActiveDocTimeStamp();
        
        activeEncipherment = securedStorageDisallowed ? false : p.getActiveEncipherment();
        guestAccountExpiryTime = p.getGuestAccountExpiryTime();
        guestAccountExpiryUnit = p.getGuestAccountExpiryUnit();
        defaultShareExpiryUnit = p.getDefaultShareExpiryUnit();
        defaultShareExpiryTime = p.getDefaultShareExpiryTime();
        deleteDocWithShareExpiryTime = p.getDeleteDocWithShareExpiryTime();
        
        defaultFileExpiryUnit = p.getDefaultFileExpiryUnit();
        defaultFileExpiryTime = p.getDefaultFileExpiryTime();
        
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
    
    
    
    public void onSuccessFromAdministrationForm() throws BusinessException {

        if (fileSizeMax != null) {
            fileSizeMax = fileSizeMax * FACTORMULTI;
        }
        if (userAvailableSize != null) {
            userAvailableSize = userAvailableSize * FACTORMULTI;
        }
        boolean activeEnciph = securedStorageDisallowed ? false : activeEncipherment;

        ParameterVo p = parameterFacade.loadConfig();
        ParameterVo params = new ParameterVo(fileSizeMax, userAvailableSize, activeMimeType, activeSignature,activeEnciph,activeDocTimeStamp,guestAccountExpiryTime,
            guestAccountExpiryUnit, p.getCustomLogoUrl(),defaultShareExpiryUnit,  defaultShareExpiryTime, defaultFileExpiryUnit, defaultFileExpiryTime, shareExpiryRules, deleteDocWithShareExpiryTime,p.getWelcomeTexts(), p.getMailTemplates(), p.getMailSubjects());
        parameterFacade.createConfig(params);

        
        //delete admin temp account
        if(needDeleteTempAdmin && deleteTempAdmin){
        	userFacade.deleteTempAdminUser();
        	needDeleteTempAdmin = false;
        }
        
    }
    
    public String getVersion() {
    	Properties prop = new Properties();
    	try {
    		if (this.getClass().getResourceAsStream("/version.properties") != null) {
    			prop.load(this.getClass().getResourceAsStream("/version.properties"));
    		} else {
    			logger.debug("Impossible to load version.properties, Is this a dev environnement?");
    		}
		} catch (IOException e) {
			 logger.debug("Impossible to load version.properties, Is this a dev environnement?");
		}
		if (prop.getProperty("Implementation-Version") != null) {
			return prop.getProperty("Implementation-Version");	
		} else {
			return "trunk";
		}
    }

    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
    
}
