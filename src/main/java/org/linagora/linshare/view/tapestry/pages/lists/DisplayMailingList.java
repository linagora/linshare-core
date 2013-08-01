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
import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
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

	private static Logger logger = LoggerFactory.getLogger(DisplayMailingList.class);

	@Inject
	private MailingListFacade mailingListFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@Persist
	@Property
	private String recipientsSearch;

	@Inject
	private UserFacade userFacade;

	@SessionState(create = false)
	@Property
	private MailingListVo mailingList;

	@Persist
	private List<MailingListContactVo> lists;
	
	@Persist
	@Property
	private List<UserVo> contacts;

	@Property
	private UserVo contact;

	@Persist
	@Validate("required")
	@Property
	private String firstName;

	@Persist
	@Validate("required")
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

    @Inject
    private Messages messages;
    
	@Property
	private int autocompleteMin = 3;
	
	@Persist
	private boolean inModify;

	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.lists.Index index;

	@SetupRender
	public void init() throws BusinessException {
		if (!mailingList.getMails().isEmpty()) {
			contacts = new ArrayList<UserVo>();
			lists = mailingList.getMails();
			for(MailingListContactVo current :mailingList.getMails()){
				contacts.add(MailCompletionService.getUserFromDisplay(current.getDisplay()));
			}
		}
	}

	public void onActivate(long persistenceId) throws BusinessException {
		if (persistenceId != 0) {
			mailingList = mailingListFacade.retrieveMailingList(persistenceId);
		} else {
			mailingList = null;
		}
		inModify = false;
		displayGrid = false;
	}

	public List<String> onProvideCompletionsFromSearchUser(String input) throws BusinessException {
		return mailingListFacade.completionOnUsers(loginUser, input);
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
	
	public void onSuccessFromAddContactForm() throws BusinessException {
		String display = MailCompletionService.formatLabel(email, firstName,lastName, false);
		MailingListContactVo newContact = new MailingListContactVo(email,display);
		if(mailingList.getMails() == null){
			List<MailingListContactVo> current = new ArrayList<MailingListContactVo>();
			mailingList.setMails(current);
		}
		mailingList.addContact(newContact);
		mailingListFacade.updateMailingList(mailingList);
		mailingList = mailingListFacade.retrieveMailingList(mailingList.getPersistenceId());

		email = null;
		firstName = null;
		lastName = null;
	}

	public void onSuccessFromEditContactForm() throws BusinessException {
		if(inModify == true){
			String display = MailCompletionService.formatLabel(email, firstName,lastName, false);

			MailingListContactVo contact= mailingListFacade.retrieveMailingListContact(oldEmail,mailingList);
			contact.setDisplay(display);
			contact.setMail(email);
			
			mailingListFacade.updateMailingListContact(contact);
			mailingList = mailingListFacade.retrieveMailingList(mailingList.getPersistenceId());
			inModify = false;
			email = null;
			oldEmail = null;
			firstName = null;
			lastName = null;
		}
	}

	
	public void onSuccessFromSearchUserForm() throws BusinessException {
		results = mailingListFacade.searchAmongUsers(loginUser, recipientsSearch);
		displayGrid = true;
	}

	public boolean getIsInList() throws BusinessException {
			return mailingListFacade.checkUserIsContact(mailingList.getMails(), result.getMail());
	}

	public boolean getUserIsOwner(){
		return loginUser.equals(mailingList.getOwner());
	}
	
	
	public void onActionFromAddUser(String firstName, String lastName, String mail) throws BusinessException {
		List<UserVo> selectedUser = userFacade.searchUser(mail, firstName, lastName, loginUser);
		if(selectedUser!=null){
		String display = MailCompletionService.formatLabel(selectedUser.get(0).getMail(), selectedUser.get(0).getFirstName(),selectedUser.get(0).getLastName(), false);

		MailingListContactVo newContact = new MailingListContactVo(mail,display);
		mailingList.addContact(newContact);
		mailingListFacade.updateMailingList(mailingList);
		mailingList = mailingListFacade.retrieveMailingList(mailingList.getPersistenceId());
		}
	}

	public void onActionFromDeleteUser(String firstName, String lastName, String mail) throws BusinessException {
		List<UserVo> selectedUser = userFacade.searchUser(mail, firstName, lastName, loginUser);
		if(selectedUser!=null){
			
		for (MailingListContactVo current : mailingList.getMails()) {
			if (current.getMail().equals(selectedUser.get(0).getMail())) {
				this.contactToDelete = current.getPersistenceId();
			}
		}

		mailingListFacade.deleteMailingListContact(mailingList,this.contactToDelete);
		mailingList = mailingListFacade.retrieveMailingList(mailingList.getPersistenceId());
		}
	}

	public void onActionFromEditContact(String mail) {
		UserVo contactForEdit = null;
		for(MailingListContactVo current : lists){
			if(current.getMail().equals(mail)){
				contactForEdit=MailCompletionService.getUserFromDisplay(current.getDisplay());
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
		
		for(MailingListContactVo current : lists){
			if(current.getMail().equals(mail)){
				this.contactToDelete = current.getPersistenceId();
			}
		}
	}

	@OnEvent(value = "contactDeleteEvent")
	public void deleteList() throws BusinessException {
		logger.debug("todelete:" + this.contactToDelete);
		mailingListFacade.deleteMailingListContact(mailingList, contactToDelete);
		mailingList = mailingListFacade.retrieveMailingList(mailingList.getPersistenceId());
	}

	public boolean getIsEmpty() {
		return mailingList.getMails().isEmpty();
	}

	public boolean isInModify() {
		return inModify;
	}

}
