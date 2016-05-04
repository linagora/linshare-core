/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.view.tapestry.pages.administration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import org.apache.tapestry5.util.EnumSelectModel;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.models.impl.SimpleSelectModel;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
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
	private Language currentExternalMailLocale;

	@Property
	private String cmisLocale;

	@Property
	private boolean isCmisActivated;

	@Property
	private String oldUserPassword;

	@Property
	private String newUserPassword;

	@Property
	private String confirmNewUserPassword;

	@Persist
	@Property
	private SimpleSelectModel<String> model;

	@Persist
	@Property
	private EnumSelectModel externalMailModel;

	@Inject
	private FunctionalityFacade functionalityFacade;

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

		isCmisActivated = functionalityFacade.isCmisSyncActivate(userVo.getDomainIdentifier());

		if (userVo.getLocale() != null) {
			currentLocale = userVo.getLocale().getTapestryLocale();
		}
		if (userVo.getExternalMailLocale() != null) {
			currentExternalMailLocale = userVo.getExternalMailLocale();
		}
		if (userVo.getCmisLocale() != null) {
			cmisLocale = userVo.getCmisLocale();
		}

		model = new SimpleSelectModel<String>(locales, messages, "pages.administration.userconfig.select");
		externalMailModel = new EnumSelectModel(Language.class, messages);
	}

	public boolean getDisplayChangePassword() {
		return userVo.isGuest() || userVo.isSuperAdmin() || userVo.hasDelegationRole() || userVo.hasUploadPropositionRole();
	}

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */

	void onSuccessFromConfigUserform() throws BusinessException {
		userFacade.updateUserLocale(userVo, currentLocale, currentExternalMailLocale, cmisLocale);
		userVo = userFacade.findUserByLsUuid(userVo, userVo.getLsUuid());
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
			logger.debug(e.toString());
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

	public String getVersion() {
		Properties prop = new Properties();
		try {
			if (this.getClass().getResourceAsStream("/version.properties") != null) {
				prop.load(this.getClass().getResourceAsStream(
						"/version.properties"));
			} else {
				logger.debug("Impossible to load version.properties, Is this a dev environnement?");
			}
		} catch (IOException e) {
			logger.debug("Impossible to load version.properties, Is this a dev environnement?");
			logger.debug(e.toString());
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
