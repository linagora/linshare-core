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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.util.LocaleUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linShare.core.Facade.AbstractDomainFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linShare.view.tapestry.models.impl.SimpleSelectModel;
import org.linagora.linShare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linShare.view.tapestry.objects.MessageSeverity;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;


/**
 * User config page
 * @author ncharles
 *
 */
public class UserConfig {
	@Inject 
	private Logger logger;

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;
	
 	/* ***********************************************************
	 *                         Parameters
	 ************************************************************ */

	
	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	
	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;
	
	@Inject
	private SymbolSource symbolSource;
	
	@Inject
	private Messages messages;
	
	@Inject
	private UserFacade userFacade;
	
    @Inject
    private AbstractDomainFacade domainFacade;
	
	@Inject
	private PersistentLocale persistentLocale;
	
	
    @InjectComponent
    private Form changePassword;
    
	
	/* ***********************************************************
	 *                Properties & injected symbol, ASO, etc
	 ************************************************************ */
	
	@SessionState
	@Property
	private UserVo userVo;
	
	@Property
	private List<String> locales;
	
	@Property
	private String currentLocale;
	
	@Property
	private String oldUserPassword;
	
	@Property
	private String newUserPassword;
	
	@Property
	private String confirmNewUserPassword;
	
	@SuppressWarnings("unused")
	@Persist
	@Property
	private SimpleSelectModel<String> model;
	 
	/* ***********************************************************
	 *                       Phase processing
	 ************************************************************ */
	 
	@SetupRender
	public void initLanguage() throws BusinessException{
		if(null==locales || locales.size()==0){
			if(null!=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES)){
				String stringLocales=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES);
				String[]listLocales=stringLocales.split(",");
				
				locales=this.getSupportedLocales(listLocales);		
			}
		}
		
		if (userVo.getLocale() !=null) {
			currentLocale = userVo.getLocale();
			
		}
		
		model = new SimpleSelectModel<String>(locales, messages, "pages.administration.userconfig.select");

	}
	
	public boolean getDisplayChangePassword() {
		return userVo.isGuest() || userVo.isSuperAdmin();
	}
	
	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */
	
	void onSuccessFromConfigUserform() throws BusinessException {
		userFacade.updateUserLocale(userVo,currentLocale);
		userVo = userFacade.findUserInDb(userVo.getMail(), userVo.getDomainIdentifier());
		persistentLocale.set(LocaleUtils.toLocale(currentLocale));
	}
	
    
    public boolean onValidateFormFromChangePassword() {
    	if (changePassword.getHasErrors()) {
    		return false;
    	}
    	
    	if (!newUserPassword.equals(confirmNewUserPassword)) {
    		changePassword.recordError(messages.get("pages.administration.userconfig.error.password"));
    		return false;
    	}
    	
    	try {
			userFacade.changePassword(userVo, oldUserPassword, newUserPassword);
		} catch (BusinessException e) {
			changePassword.recordError(messages.get("pages.administration.userconfig.error.wrong.password"));
    		return false;
		}
		businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.PASSWORD_CHANGE_SUCCESS, MessageSeverity.INFO));
        return true;
    }
    
    
    @CleanupRender
    public void cleanupRender(){
    	changePassword.clearErrors();
    }
	
	
	/* ***********************************************************
	 *                          Helpers
	 ************************************************************ */
	
	private List<String> getSupportedLocales(String[]locales){
		ArrayList<String> newLocales=new ArrayList<String>();
		for(String currentLocale:locales){
			newLocales.add(currentLocale);
		}

		return newLocales;
	}
	/* ***********************************************************
	 *                      Getters & Setters
	 ************************************************************ */ 


    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
}
