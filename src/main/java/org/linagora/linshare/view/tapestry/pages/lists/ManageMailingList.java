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


import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.constants.VisibilityType;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
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

	private static Logger logger = LoggerFactory
			.getLogger(ManageMailingList.class);

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
	private VisibilityType visibility;

	@Inject
	private PersistentLocale persistentLocale;

	@Property
	private String newOwner;

	@Property
	private int autocompleteMin = 3;

	@Property
	private String currentVisibility;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;
	
	@SetupRender
	public void init() {
		if (mailingList == null) {
			mailingList = new MailingListVo();
			mailingList.setIdentifier("");
			mailingList.setListDescription("");
		}
	}

	public void onActivate(long persistenceId) throws BusinessException {
		if (persistenceId != 0) {
			inModify = true;
			mailingList = mailingListFacade.retrieveMailingList(persistenceId);
			if (mailingList.isPublic() == true) {
				if (persistentLocale.get().toString().equals("fr")) {
					currentVisibility = "Publique";
				} else {
					currentVisibility = "Public";
				}
			} else {
				if (persistentLocale.get().toString().equals("fr")) {
					currentVisibility = "Privée";
				} else {
					currentVisibility = "Private";
				}
			}
			
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


    void onValidateFromIdentifier(String value) throws ValidationException, BusinessException {
        if (value != null) {
            if (!value.substring(0,1).matches("[A-Za-z]+")) {
            
                throw new ValidationException("Identifier must started with a letter");
            }
            if(!value.equals(oldIdentifier)){
            	String copy = mailingListFacade.checkUniqueId(value, loginUser);
            	if (!copy.equals(value)) {
            		throw new ValidationException("Identifier already exist, choose an available (ex: "+copy+")");
            	}
            }
        }

    }
	
	public Object onSuccess() throws BusinessException {
		if (inModify == true) {
			if (visibility.toString().equals("Public")) {
				mailingList.setPublic(true);
			} else {
				mailingList.setPublic(false);
			}

				mailingList.setOwner(loginUser);
				domain = domainFacade.retrieveDomain(loginUser
						.getDomainIdentifier());
				mailingList.setDomain(domain);
				
			mailingListFacade.updateMailingList(mailingList);
			index.setFromCreate(false);
			
		} else {
			mailingList.setOwner(loginUser);
			domain = domainFacade.retrieveDomain(loginUser
					.getDomainIdentifier());
			mailingList.setDomain(domain);

			if (visibility.toString().equals("Public")) {
				mailingList.setPublic(true);
			} else {
				mailingList.setPublic(false);
			}
			List<MailingListContactVo> current = new ArrayList<MailingListContactVo>();
			mailingList.setMails(current);
			

				mailingListFacade.createMailingList(mailingList);
				index.setFromCreate(true);
		}
		inModify = false;
		mailingList = null;
		return index;
	}
}
