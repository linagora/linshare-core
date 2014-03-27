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
package org.linagora.linshare.view.tapestry.pages.administration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.DisplayableAccountOccupationEntryVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;




public class AccountOccupation {
	@Inject 
	private Logger logger;

	@SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;
	
    @SessionState
    private UserVo userLoggedIn;

	
	/* ***********************************************************
	 *                Injected services 
	 ************************************************************ */  

	@Inject
	private Messages messages;

	@Inject
	private DocumentFacade documentFacade;
	
	
	@Inject
	private AbstractDomainFacade domainFacade;
	
	
	@Inject
	private UserFacade userFacade;
	
	@SuppressWarnings("unused")
	@InjectComponent
	private TextArea actorMails;
	
	@SuppressWarnings("unused")
	@Component
	private Form formReport;
	
	/* ***********************************************************
	 *                         Properties
	 ************************************************************ */    

	@Property
	private String actorListMails;
	
	/**
	 * the list of traces matching the request
	 */
	@Persist @Property
	private List<DisplayableAccountOccupationEntryVo> accountOccupationEntries;
	
	@Property
	private DisplayableAccountOccupationEntryVo accountOccupationEntry;
	
	
	@Persist @Property(write=false) @SuppressWarnings("unused") //used in tml
	private boolean displayGrid;
	
	private boolean reset;
	
	@Property
	@Persist
	private AccountOccupationCriteriaBean criteria;
	
	
	@Persist
	@Property
	private List<String> domains;
	
	@Property
	@Persist
	private boolean superadmin;
	
	
	@Property
	private int autocompleteMin;
	
	@Inject
	private FunctionalityFacade functionalityFacade;
	
	
	/* ***********************************************************
	 *                       Phase processing
	 ************************************************************ */
	
	@SetupRender
	public void init() throws BusinessException {
		autocompleteMin = functionalityFacade.completionThreshold(userLoggedIn.getDomainIdentifier());
	}
	
	
	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */	
	
	public void onActivate() throws BusinessException {
		if(null == criteria) {
			criteria = new AccountOccupationCriteriaBean();
		} else {
			if ((criteria.getActorMails()!=null) && (criteria.getActorMails().size()>0)) {
				actorListMails = "";
				for (String mail : criteria.getActorMails()) {
					actorListMails += mail + ",";
				}	
			}
		}
		
		domains = domainFacade.getAllDomainIdentifiers(userLoggedIn);
		superadmin = userLoggedIn.isSuperAdmin();
		
	}
	
	public Object onSuccessFromFormReport() throws BusinessException  {
		
		if (reset){
			criteria = new AccountOccupationCriteriaBean();
			accountOccupationEntries = null;
			return null;
		}
		
		if ((actorListMails != null) &&(actorListMails.length()>0)) {
			criteria.setActorMails(Arrays.asList(actorListMails.split(",")));
		}

		accountOccupationEntries = documentFacade.getAccountOccupationStat(criteria);
		Collections.sort(accountOccupationEntries);
		
		displayGrid = true;
		
		return null;
	}
	
	Object onActionFromReset() throws BusinessException { 
		reset = true; 
		return onSuccessFromFormReport();
	}
	
	
	/**
	 * AutoCompletion for mails field.
	 * @param value the value entered by the user
	 * @return list the list of string matched by value.
	 */
	public List<String> onProvideCompletionsFromActorMails(String value){
		return userFacade.findMails(value);
	}
	
	public String getFormattedUserAvailableQuota() {
		return FileUtils.getFriendlySize(accountOccupationEntry.getUserAvailableQuota(), messages);
	}
	
	public String getFormattedUserUsedQuota() {
		return FileUtils.getFriendlySize(accountOccupationEntry.getUserUsedQuota(), messages);
	}
	
	public String getFormattedUserTotalQuota() {
		return FileUtils.getFriendlySize(accountOccupationEntry.getUserTotalQuota(), messages);
	}

    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
}
