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

package org.linagora.linshare.view.tapestry.pages.lists;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayMailingList {

	private static final Logger logger = LoggerFactory
			.getLogger(DisplayMailingList.class);
	
	@Inject
	private MailingListFacade mailingListFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@Inject
	private UserAutoCompleteFacade userAutoCompleteFacade;

	@Persist
	@Property
	private String recipientsSearch;

	@Inject
	private UserFacade userFacade;

	@SessionState(create = false)
	@Property
	private MailingListVo mailingListVo;

	@Persist
	@Property
	private List<MailingListContactVo> contacts;

	@Property
	private MailingListContactVo contact;

	@Persist
	@Property
	private String firstName;

	@Persist
	@Property
	private String lastName;

	@Persist
	@Property
	private String mail;

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
	private String contactToDelete;

	@Property
	@Persist
	private String contactToUpdate;

	@Inject
	private Messages messages;

	@Persist
	@Property
	private boolean inModify;

	@Persist
	@Property
	private boolean fromReset;

	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.administration.Index indexAdmin;

	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.lists.Index index;
	
	private final Integer truncatedValue = 40;

	@SetupRender
	public void init() throws BusinessException {
		if (!mailingListVo.getContacts().isEmpty()) {
			contacts = mailingListVo.getContacts();
		}
	}

	public void onActivate(String uuid) throws BusinessException {
		if (uuid != null) {
			mailingListVo = mailingListFacade.findByUuid(loginUser, uuid);
		} else {
			mailingListVo = null;
		}
		displayGrid = false;
	}

	public Object onActionFromBack() {
		mailingListVo = null;
		displayGrid = false;
		recipientsSearch = null;
		results = null;
		index.setFromCreate(false);
		mail = null;
		firstName = null;
		lastName = null;
		inModify = false;
		contacts = null;

		if (loginUser.isSuperAdmin()) {
			return indexAdmin;
		} else {
			return index;
		}
	}

	public void onSelectedFromReset() throws BusinessException {
		mail = null;
		firstName = null;
		lastName = null;
		fromReset = true;
	}

	public void onSuccessFromContactForm() throws BusinessException {
		if (inModify == true) {
			if (!fromReset) {
				try {
					MailingListContactVo contact = mailingListFacade.searchContact(loginUser, contactToUpdate);
					contact.setFirstName(firstName);
					contact.setLastName(lastName);
					contact.setMail(mail);
					mailingListFacade.updateContact(loginUser, contact);	
				} catch (BusinessException e) {
					logger.error("cannot retrieve user" + e.getMessage());
					logger.debug(e.toString());
				}
			}
		} else {
			MailingListContactVo newContact = new MailingListContactVo(mail, firstName, lastName);
			mailingListFacade.addNewContactToList(loginUser, mailingListVo, newContact);
		}
		mailingListVo = mailingListFacade.findByUuid(loginUser, mailingListVo.getUuid());
		inModify = false;
		mail = null;
		firstName = null;
		lastName = null;
		fromReset = false;
	}

	public void onSuccessFromSearchUserForm() throws BusinessException {
		results = userAutoCompleteFacade.autoCompleteUser(loginUser, recipientsSearch);
		displayGrid = true;
	}

	public void onSuccessFromResetSearchUserForm() throws BusinessException {
		results = null;
		recipientsSearch = null;
		displayGrid = false;
	}

	public boolean getIsInList() throws BusinessException {
		return mailingListVo.isAlreadyAContact(result.getMail());
	}

	public boolean getUserIsOwner() {
		return loginUser.equals(mailingListVo.getOwner());
	}

	public void onActionFromAddUser(String lsUuid) throws BusinessException {
		for (UserVo user: results) {
			if (user.getLsUuid().equals(lsUuid)) {
				mailingListFacade.addUserToList(loginUser, mailingListVo, user.getDomainIdentifier(), user.getMail());
				mailingListVo = mailingListFacade.findByUuid(loginUser, mailingListVo.getUuid());
				return;
			}
		}
	}

	public void onActionFromDeleteUser(String mail) throws BusinessException {
		MailingListContactVo contactToRemove = mailingListFacade.findContactByMail(loginUser, mailingListVo.getUuid(), mail);
		mailingListFacade.deleteContact(loginUser, contactToRemove.getUuid());
		mailingListVo = mailingListFacade.findByUuid(loginUser, mailingListVo.getUuid());
	}

	public void onActionFromEditContact(String uuid) throws BusinessException {
		MailingListContactVo contact = mailingListFacade.searchContact(loginUser, uuid);
		this.contactToUpdate = uuid;
		this.mail = contact.getMail();
		this.firstName = contact.getFirstName();
		this.lastName = contact.getLastName();
		inModify = true;
	}

	public void onActionFromDeleteContact(String contactUuid) {
		contactToDelete = contactUuid;
	}

	@OnEvent(value = "contactDeleteEvent")
	public void deleteContactFromList() throws BusinessException {
		mailingListFacade.deleteContact(loginUser, contactToDelete);
		mailingListVo = mailingListFacade.findByUuid(loginUser, mailingListVo.getUuid());
	}

	public boolean getIsEmpty() {
		return mailingListVo.getContacts().isEmpty();
	}

	public boolean isInModify() {
		return inModify;
	}

	public String getTruncatedMailingListIdentifier() {
		String result = StringUtils.abbreviate(mailingListVo.getIdentifier(), truncatedValue);
		return result;
	}
}
