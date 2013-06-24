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

package org.linagora.linshare.view.tapestry.pages.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserFacade;
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
    
    @Inject
    private AbstractDomainFacade domainFacade;
    
	@Inject
	private UserFacade userFacade;
    
	@Persist
	@Property
	private boolean inModify;	
	
	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.lists.Index index;
	
	@Validate("required")
	@Property
	private String visibilitySelection;
	
	@Property
	private String targetListMails;
	
	@Property
	private String newOwner;
	
	@Property
	private int autocompleteMin=3;
	
	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;
	
	@SetupRender
	public void init() {
			if(mailingList == null) {
				mailingList=new MailingListVo();
			}
	}
	
	public void onActivate(long persistenceId) throws BusinessException {
		if (persistenceId != 0) {
			inModify = true;
			mailingList = mailingListFacade.retrieveMailingList(persistenceId);
		} else {
			inModify = false;
			mailingList = null;
		}
		
		if(!mailingList.getMails().isEmpty()){
			for(MailingListContact current : mailingList.getMails()){
				MailingListContactVo currentVo = new MailingListContactVo(current);
				if(targetListMails == null){
					targetListMails = currentVo.getMail();
				} else {
				targetListMails = targetListMails+","+currentVo.getMail();
				}
			}
		}
    }
	
	/**
	 * AutoCompletion for mail field.
	 * @param value the value entered by the user
	 * @return list the list of string matched by value.
	 */
	public List<String> onProvideCompletionsFromTargetMails(String value){
		List<String> res = new ArrayList<String>();
		try {
			List<UserVo> founds = userFacade.searchUser(value, null, null, null, loginUser);
			if (founds != null && founds.size() > 0) {
				for (UserVo userVo : founds) {
					res.add(userVo.getMail());
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return res;
	}

	public List<String> onProvideCompletionsFromOwner(String input) {
		List<UserVo> searchResults;

		List<String> elements = new ArrayList<String>();
		try {
			searchResults = recipientFavouriteFacade.recipientsOrderedByWeightDesc(performSearch(input),loginUser);

			for (UserVo user : searchResults) {
	            String completeName = user.getFirstName().trim() + " " + user.getLastName().trim();
	            if (!elements.contains(completeName)) {
	                elements.add(completeName);
	            }
			}

			return elements;
		} catch (BusinessException e) {
			logger.error("Error while trying to autocomplete", e);
		}
		return elements;
	}
	
	/** Perform a user search using the user search pattern.
	 * @param input user search pattern.
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
	            userSet.addAll(userFacade.searchUser(input.trim(), null, null, null, loginUser));
	            userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, null, loginUser));
	            userSet.addAll(userFacade.searchUser(null, lastName_, firstName_, null, loginUser));
	        } else {
	        	userSet.addAll(userFacade.searchUser(null, null, null, null, loginUser));
	        }
		} catch (BusinessException e) {
			logger.error("Error while trying to autocomplete", e);
		}
		return new ArrayList<UserVo>(userSet);
	}
	
	
    public Object onActionFromCancel() {
        mailingList=null;
        inModify=false;
        return index;
     }

	public Object onSuccess() throws BusinessException{
		if(inModify == true){
			List<MailingListContact> mails = new ArrayList<MailingListContact>();
			if ((targetListMails != null) &&(targetListMails.length()>0)) {
				for(String current : Arrays.asList(targetListMails.split(","))) {
					MailingListContact contact = new MailingListContact(current);
					mails.add(contact);
				}
				mailingList.setMails(mails);
			}
			List<MailingListContactVo> add =new ArrayList<MailingListContactVo>();
			add = mailingListFacade.getListOfMailAdd(mailingList);
			
			List<MailingListContactVo> remove =new ArrayList<MailingListContactVo>();
			remove = mailingListFacade.getListOfMailRemove(mailingList);

			mailingList.getMails().clear();
			for(MailingListContactVo current : remove) {
				mailingListFacade.deleteMailingListContact(current.getPersistenceId());
			}
			
			mailingList.getMails().clear();
			for(MailingListContactVo contactVo : add) {
				mailingList.getMails().add(new MailingListContact(contactVo));
			}
			if(newOwner != null) {
				List<UserVo> list = new ArrayList<UserVo>();
				list = performSearch(newOwner);
				UserVo newUser = list.get(0);
				mailingList.setOwner(newUser);
				mailingListFacade.checkUniqueId(mailingList, newUser);
				
				   
				domain = domainFacade.retrieveDomain(newUser.getDomainIdentifier());
				mailingList.setDomain(domain);
				
			}
			else {
				mailingList.setOwner(loginUser);
				domain = domainFacade.retrieveDomain(loginUser.getDomainIdentifier());
				mailingList.setDomain(domain);
			}
			
			 mailingListFacade.updateMailingList(mailingList);
		}
		else {
			mailingList.setOwner(loginUser);
			domain = domainFacade.retrieveDomain(loginUser.getDomainIdentifier());
			mailingList.setDomain(domain);
			
			List<MailingListContact> mails = new ArrayList<MailingListContact>();
			if ((targetListMails != null) &&(targetListMails.length()>0)) {
				for(String current : Arrays.asList(targetListMails.split(","))) {
					MailingListContact contact = new MailingListContact(current);
					mails.add(contact);
				}
				mailingList.setMails(mails);
			}
			
			if(visibilitySelection.equals("Public")) {
				mailingList.setPublic(true);
			} else {
				mailingList.setPublic(false);
			}
			
			if(mailingListFacade.mailingListIdentifierUnicity(mailingList, loginUser)){
			mailingListFacade.checkUniqueId(mailingList,loginUser);
			mailingListFacade.createMailingList(mailingList);
			}
			else{
				mailingListFacade.checkUniqueId(mailingList,loginUser);
				mailingListFacade.createMailingList(mailingList);
			}
		}
		mailingList=null;
		return index;
	}
}
