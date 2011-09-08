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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.DisplayableAccountOccupationEntryVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;




public class AccountOccupation {
	@Inject 
	private Logger logger;

	@SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

	
	/* ***********************************************************
	 *                Injected services 
	 ************************************************************ */  

	@Inject
	private Messages messages;

	@Inject
	private DocumentFacade documentFacade;
	
	
	@Inject
	private DomainFacade domainFacade;
	
	
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
	
	
	/* ***********************************************************
	 *                       Phase processing
	 ************************************************************ */
	
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
		
		domains = domainFacade.getAllDomainIdentifiers();
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
