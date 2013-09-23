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

import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.constants.MailingListVisibility;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserFacade;

public class ManageMailingList {

	@Inject
	private MailingListFacade mailingListFacade;

	@SessionState(create = false)
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

	@Persist
	@Property
	private String oldIdentifier;

	@InjectPage
	private org.linagora.linshare.view.tapestry.pages.lists.Index index;

	@Validate("required")
	@Property
	private MailingListVisibility visibility;

	@Inject
	private Messages messages;

	@Inject
	private PersistentLocale persistentLocale;

	@Property
	private String newOwner;

	@Property
	private int autocompleteMin = 3;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@SetupRender
	public void init() {
		if (mailingList == null) {
			mailingList = new MailingListVo();
			mailingList.setIdentifier("");
			mailingList.setDescription("");
		}
	}

	public void onActivate(String uuid) throws BusinessException {
		if (uuid != null) {
			inModify = true;
			mailingList = mailingListFacade.searchList(uuid);
			oldIdentifier = mailingList.getIdentifier();

		} else {
			inModify = false;
			mailingList = null;
		}
	}

	public Object onActionFromCancel() {
		mailingList = null;
		inModify = false;
		oldIdentifier = null;
		index.setFromCreate(false);
		return index;
	}

	public String getCurrentVisibility() {
		if (mailingList.isPublic()) {
			return String.format((messages.get("pages.lists.visibility.public")));
		} else {
			return String.format((messages.get("pages.lists.visibility.private")));
		}
	}

	void onValidateFromIdentifier(String value) throws ValidationException, BusinessException {
		if (value != null) {
			if (!value.substring(0, 1).matches("[A-Za-z]+")) {
				throw new ValidationException(String.format(messages.get("pages.lists.manageList.letter")));
			}
		}
		if (!value.equals(oldIdentifier)) {
			if (!mailingListFacade.identifierIsAvailable(loginUser, value)) {
				String copy = mailingListFacade.findAvailableIdentifier(loginUser, value);
				throw new ValidationException(String.format(messages.get("pages.lists.manageList.identifierExist"),
						copy));
			}
		}
	}

	public Object onSuccess() throws BusinessException {
		if (visibility.toString().equals(MailingListVisibility.Public.toString())) {
			mailingList.setPublic(true);
		} else {
			mailingList.setPublic(false);
		}
		mailingList.setOwner(loginUser);
		domain = domainFacade.retrieveDomain(loginUser.getDomainIdentifier());
		mailingList.setDomainId(domain.getIdentifier());

		if (inModify == true) {
			mailingListFacade.updateList(loginUser, mailingList);
			index.setFromCreate(false);
		} else {
			mailingListFacade.createList(loginUser, mailingList);
			index.setFromCreate(true);
		}
		inModify = false;
		mailingList = null;
		return index;
	}
}
