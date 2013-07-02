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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayMailingList {

	private static Logger logger = LoggerFactory
			.getLogger(DisplayMailingList.class);

	@Inject
	private MailingListFacade mailingListFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@Property
	private String recipientsSearch;

	@Inject
	private UserFacade userFacade;

	@SessionState(create = false)
	@Property
	private MailingListVo mailingList;

	@Persist
	private List<MailingListContactVo> lists;

	private MailingListContactVo list;
	
	@Persist
	@Property
	private List<UserVo> contacts;

	@Property
	private UserVo contact;
	
	@Property
	private UserVo contactForEdit;

	@Persist
	@Property
	private String firstName;

	@Persist
	@Property
	private String lastName;

	@Persist
	@Validate("required")
	@Property
	private String email;
	
	@Persist
	private String oldEmail;

	@Persist
	@Property(write = false)
	private boolean displayGrid;
	
	@SessionState
	private UserVo loginUser;

	@Persist
	@Property
	private List<UserVo> results;

	@Property
	private UserVo result;
	
	@Property
	@Persist(value = "flash")
	private long contactToDelete;

	@Property
	private int autocompleteMin = 3;

	private boolean isEmpty;
	
	@Persist
	private boolean inModify;

	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.lists.Index index;

	@SetupRender
	public void init() throws BusinessException {
		isEmpty = mailingList.getMails().isEmpty();
		if (!isEmpty) {
			lists = new ArrayList<MailingListContactVo>();
			lists = mailingList.getMails();
			contacts = new ArrayList<UserVo>();
			for(MailingListContactVo current :lists){
				contacts.add(mailingListFacade.getUserFromDisplay(current.getDisplay()));
			}
		}
	}

	public void onActivate(long persistenceId) throws BusinessException {
		if (persistenceId != 0) {
			mailingList = mailingListFacade.retrieveMailingList(persistenceId);
		} else {
			mailingList = null;
		}
	
		email = null;
		firstName = null;
		lastName = null;
		inModify = false;
		displayGrid = false;
	}

	public List<String> onProvideCompletionsFromSearchUser(String input) {
		List<UserVo> searchResults = performSearch(input);
		List<UserVo> fromAuthorized = new ArrayList<UserVo>();

		for(UserVo current :searchResults){
			if(userFacade.findUserFromAuthorizedDomainOnly(loginUser.getDomainIdentifier(),current.getMail()) != null){
				fromAuthorized.add(current);
			}
		}
		
		List<String> elements = new ArrayList<String>();
		for (UserVo user : fromAuthorized) {
			logger.debug("current user: "+user.getFullName());
			String completeName = MailCompletionService.formatLabel(user);
			if (!elements.contains(completeName)) {
				logger.debug("add user: "+user.getFullName());
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
				userSet.addAll(userFacade.searchUser(input.trim(), null, null,
						loginUser));
			}
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_,
					loginUser));

			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,
					loginUser));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(
					input.trim(), loginUser));

			return recipientFavouriteFacade.recipientsOrderedByWeightDesc(
					new ArrayList<UserVo>(userSet), loginUser);
		} catch (BusinessException e) {
			logger.error("Error while searching user in QuickSharePopup", e);
		}
		return new ArrayList<UserVo>();
	}
	

	public Object onActionFromBack() {
		mailingList = null;
		lists = null;
		displayGrid = false;
		recipientsSearch = null;
		results = null;
		index.setFromCreate(false);
		email = null;
		firstName = null;
		lastName = null;
		inModify = false;
		oldEmail = null;
		return index;
	}

	public void onSelectedFromReset() throws BusinessException{
		inModify = false;
		email = null;
		firstName = null;
		oldEmail = null;
		lastName = null;
	}
	
	public Object onSuccessFromForm() throws BusinessException {
		String display = MailCompletionService.formatLabel(email, firstName,
				lastName, false);

		MailingListContactVo newContact = new MailingListContactVo(email,
				display);
		if(mailingList.getMails() == null){
			List<MailingListContactVo> current = new ArrayList<MailingListContactVo>();
			mailingList.setMails(current);
		}
		mailingList.addContact(newContact);
		mailingListFacade.updateMailingList(mailingList);
		mailingList = mailingListFacade.retrieveMailingList(mailingList
				.getPersistenceId());

		email = null;
		firstName = null;
		lastName = null;
		return null;
	}

	public Object onSuccessFromFormBis() throws BusinessException {
		if(inModify == true){
			String display = MailCompletionService.formatLabel(email, firstName,
					lastName, false);

			MailingListContactVo contact= mailingListFacade.retrieveMailingListContact(oldEmail);
			contact.setDisplay(display);
			contact.setMail(email);
			
			mailingListFacade.updateMailingListContact(contact);
			mailingList = mailingListFacade.retrieveMailingList(mailingList
					.getPersistenceId());
			inModify = false;
			email = null;
			oldEmail = null;
			firstName = null;
			lastName = null;
		}
		return null;
	}

	
	public Object onSuccessFromForms() throws BusinessException {
		results = null;
		if (recipientsSearch != null) {
			if(recipientsSearch.substring(recipientsSearch.length()-1).equals(">"))
			{
				int index1 = recipientsSearch.indexOf("<");
				int index2 = recipientsSearch.indexOf(">");
				
				recipientsSearch = recipientsSearch.substring(index1+1, index2);
				UserVo selectedUser = userFacade.findUserFromAuthorizedDomainOnly(
						loginUser.getDomainIdentifier(), recipientsSearch);
				results = new ArrayList<UserVo>();
				results.add(selectedUser);
			} else {
			List<UserVo>searchResults = new ArrayList<UserVo>();
			searchResults = performSearch(recipientsSearch);
			results = new ArrayList<UserVo>();

			for(UserVo current :searchResults){
				if(userFacade.findUserFromAuthorizedDomainOnly(loginUser.getDomainIdentifier(),current.getMail()) != null){
					results.add(current);
				}
			}
			}
		}
		displayGrid = true;
		return null;
	}

	public boolean getIsInList() throws BusinessException {
		String chain = "\"" + result.getLastName() + " "
				+ result.getFirstName() + "\" <" + result.getMail() + ">";

		boolean inList = false;
		if (!mailingList.getMails().isEmpty()) {
			List<MailingListContactVo> listing = new ArrayList<MailingListContactVo>();
			listing = mailingList.getMails();
			for (MailingListContactVo contact : listing) {
				if (contact.getDisplay().equals(chain)) {
					inList = true;
				}
			}
		}
		return inList;
	}

	public boolean getUserIsOwner(){
		return loginUser.equals(mailingList.getOwner());
	}
	
	
	public void onActionFromAddUser(String mail) throws BusinessException {

		UserVo selectedUser = userFacade.findUserFromAuthorizedDomainOnly(
				loginUser.getDomainIdentifier(), mail);
		if(selectedUser!=null){
		String display = MailCompletionService.formatLabel(
				selectedUser.getMail(), selectedUser.getFirstName(),
				selectedUser.getLastName(), false);

		MailingListContactVo newContact = new MailingListContactVo(mail,
				display);
		mailingList.addContact(newContact);
		mailingListFacade.updateMailingList(mailingList);
		mailingList = mailingListFacade.retrieveMailingList(mailingList
				.getPersistenceId());
		}
	}

	public void onActionFromDeleteUser(String mail) throws BusinessException {
		UserVo selectedUser = userFacade.findUserFromAuthorizedDomainOnly(
				loginUser.getDomainIdentifier(), mail);
		if(selectedUser!=null){
		String display = MailCompletionService.formatLabel(
				selectedUser.getMail(), selectedUser.getFirstName(),
				selectedUser.getLastName(), false);

		for (MailingListContactVo current : mailingList.getMails()) {
			if (current.getDisplay().equals(display)) {
				this.contactToDelete = current.getPersistenceId();
			}
		}

		mailingListFacade.deleteMailingListContact(mailingList,
				this.contactToDelete);
		mailingList = mailingListFacade.retrieveMailingList(mailingList
				.getPersistenceId());
		}
	}

	public void onActionFromEditContact(String mail) {
		for(MailingListContactVo current : this.lists){
			if(current.getMail().equals(mail)){
				contactForEdit=mailingListFacade.getUserFromDisplay(current.getDisplay());
			}
		}
		this.email = contactForEdit.getMail();
		this.firstName = contactForEdit.getFirstName();
		this.lastName = contactForEdit.getLastName();
		
		oldEmail = email;
		inModify = true;
		contactForEdit = null;
	}
	
	
	public void onActionFromDeleteContact(String mail) {
		for(MailingListContactVo current : this.lists){
			if(current.getMail().equals(mail)){
				this.contactToDelete = current.getPersistenceId();
			}
		}
	}

	@OnEvent(value = "contactDeleteEvent")
	public void deleteList() throws BusinessException {
		logger.debug("todelete:" + this.contactToDelete);
		mailingListFacade
				.deleteMailingListContact(mailingList, contactToDelete);
		mailingList = mailingListFacade.retrieveMailingList(mailingList
				.getPersistenceId());
	}

	public boolean getIsEmpty() {
		return isEmpty;
	}

	public List<MailingListContactVo> getLists() {
		return lists;
	}

	public void setLists(List<MailingListContactVo> lists) {
		this.lists = lists;
	}

	public MailingListContactVo getList() {
		return list;
	}

	public void setList(MailingListContactVo list) {
		this.list = list;
	}

	public boolean isInModify() {
		return inModify;
	}

	public void setInModify(boolean inModify) {
		this.inModify = inModify;
	}

}
