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

import java.util.List;

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

public class ManageMailingList {

	@Inject
	private MailingListFacade mailingListFacade;

	@SessionState(create = false)
	@Property
	private MailingListVo mailingListVo;

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
	private int autocompleteMin = 3;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@Component
	private Form form;

	public void onActivate(String uuid) throws BusinessException {
		if (uuid != null) {
			mailingListVo = mailingListFacade.retrieveList(uuid);
			oldIdentifier = mailingListVo.getIdentifier();
			oldOwner = mailingListVo.getOwner();
		}
	}

	public List<String> onProvideCompletionsFromOwner(String input) throws BusinessException {
		return mailingListFacade.completionOnUsers(loginUser, input);
	}

	boolean onValidate(String newIdentifier) throws ValidationException,BusinessException {
		if (newIdentifier != null) {
			if (!mailingListVo.getOwner().equals(oldOwner)) {
				String copy = mailingListFacade.checkUniqueId(mailingListVo.getOwner(),newIdentifier);
				if (!copy.equals(newIdentifier)) {
					return false;
				}
			} else {
				if (!newIdentifier.equals(oldIdentifier)) {
					String copy = mailingListFacade.checkUniqueId(mailingListVo.getOwner(),newIdentifier);
					if (!copy.equals(newIdentifier)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public Object onActionFromCancel() {
		mailingListVo = null;
		oldIdentifier = null;
		oldOwner = null;
		return index;
	}

	public Object onSuccess() throws BusinessException, ValidationException {
		if (newOwner != null) {
			if (newOwner.substring(newOwner.length() - 1).equals(">")) {
				mailingListFacade.setNewOwner(mailingListVo, newOwner);
			} else {
				form.recordError(String.format(messages.get("pages.administration.lists.unavailableOwner"),newOwner));
				return null;
			}
		}

		if (onValidate(mailingListVo.getIdentifier())) {
			mailingListFacade.updateList(mailingListVo);
		} else {
			String copy = mailingListFacade.checkUniqueId(mailingListVo.getOwner(), mailingListVo.getIdentifier());
			form.recordError(String.format(messages.get("pages.administration.lists.changeOwner"),mailingListVo.getOwner().getFullName(), copy));
			mailingListVo.setOwner(oldOwner);
			return null;
		}
		mailingListVo = null;
		return index;
	}
}