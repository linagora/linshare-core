/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

package org.linagora.linshare.view.tapestry.pages.administration.lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageMailingList {

	private static Logger logger = LoggerFactory.getLogger(ManageMailingList.class);
	
    @Inject
    private MailingListFacade mailingListFacade;
	
	@SessionState(create=false)
    @Property
    private MailingListVo mailingList;
    
    @SessionState
    private UserVo loginUser;
	
    @Property
    private AbstractDomainVo domain;
    
	@Persist
	@Property
	private String oldIdentifier;
    
	@Persist
	@Property
	private UserVo oldOwner;
	
    @Inject
    private Messages messages;
    
	@Inject
	private PersistentLocale persistentLocale;
	
    @Inject
    private AbstractDomainFacade domainFacade;
    
	@Inject
	private UserFacade userFacade;
    
	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.administration.lists.Index index;
	
	@Property
	private String newOwner;
	
	@Property
	private int autocompleteMin=3;
	
	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;
	
    @Component
    private Form form;
	
	
	public void onActivate(long persistenceId) throws BusinessException {
		if (persistenceId != 0) {
			mailingList = mailingListFacade.retrieveMailingList(persistenceId);
			oldIdentifier = mailingList.getIdentifier();
			oldOwner = mailingList.getOwner();
		} 
    }

	public List<String> onProvideCompletionsFromOwner(String input) {
		List<UserVo> searchResults = performSearch(input);
		List<String> elements = new ArrayList<String>();
		
		for (UserVo user : searchResults) {
			String completeName = MailCompletionService.formatLabel(user);
			if (!elements.contains(completeName)) {
				elements.add(completeName);
			}
		}

		return elements;
	}

	/**
	 * Perform a user search using the user search pattern.
	 * 
	 * @param input
	 *            user search pattern.
	 * @return list of users.
	 */
	private List<UserVo> performSearch(String input) {

		Set<UserVo> userSet = new HashSet<UserVo>();

		String firstName_ = null;
		String lastName_ = null;

		if (input != null && input.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

		try {
			if (input != null) {
				userSet.addAll(userFacade.searchUser(input.trim(), null, null,loginUser));
			}
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_,loginUser));
			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,loginUser));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input.trim(), loginUser));
			return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet), loginUser);
		} catch (BusinessException e) {
			logger.error("Error while searching user in QuickSharePopup", e);
		}
		return new ArrayList<UserVo>();
	}
	
   boolean onValidate(String value) throws ValidationException, BusinessException {
    	if (value != null) {
            if(!mailingList.getOwner().equals(oldOwner)){
                String copy = mailingListFacade.checkUniqueId(value,mailingList.getOwner());
                if (!copy.equals(value)) {
                	return false;
                }
            } else {
                if(!value.equals(oldIdentifier)){
                	String copy = mailingListFacade.checkUniqueId(value,mailingList.getOwner());
                	if (!copy.equals(value)) {
                		return false;
                	}
                }
            }
        }
    	return true;
    }
   
    public Object onActionFromCancel() {
        mailingList=null;
		oldIdentifier = null;
		oldOwner = null;
        return index;
     }

	public Object onSuccess() throws BusinessException, ValidationException{
		if(newOwner!=null){
			if (newOwner.substring(newOwner.length()-1).equals(">")) {
				int index1 = newOwner.indexOf("<");
				int index2 = newOwner.indexOf(">");
				newOwner = newOwner.substring(index1+1, index2);
				
				UserVo selectedUser = userFacade.findUserFromAuthorizedDomainOnly(
						loginUser.getDomainIdentifier(), newOwner);

				
				mailingList.setOwner(selectedUser);
				domain = domainFacade.retrieveDomain(selectedUser
						.getDomainIdentifier());
				mailingList.setDomain(domain);
			} else {
				mailingList.setOwner(loginUser);
				domain = domainFacade.retrieveDomain(loginUser
					.getDomainIdentifier());
				mailingList.setDomain(domain);
			}
		}
			if(onValidate(mailingList.getIdentifier())){
			 mailingListFacade.updateMailingList(mailingList);
			} else{ 
            	String copy = mailingListFacade.checkUniqueId(mailingList.getIdentifier(),mailingList.getOwner());
					form.recordError(String.format(messages.get("pages.administration.changeOwner"), mailingList.getOwner().getFullName(),copy));

				mailingList.setOwner(oldOwner);
				return null;
			}
				mailingList=null;
				return index;
		}
}