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

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.SelectModelFactory;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.unbound.tapestry.tagselect.LabelAwareValueEncoder;

public class ManageMailingList {

	private static Logger logger = LoggerFactory.getLogger(Index.class);

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	private UserVo loginUser;

	@Persist
	@Property
	private MailingListVo mailingListVo;

	@Property
	private AbstractDomainVo domain;

	@Persist
	@Property
	private String oldIdentifier;

	@Persist
	@Property
	private UserVo oldOwner;

	@Property
	@Persist
	private UserVo newOwner;

	@Property
	private static final int autocompleteMin = 3;

	@Component
	private Form form;

	@Inject
	private Messages messages;

	@Inject
	private MailingListFacade mailingListFacade;

	@Inject
	private PersistentLocale persistentLocale;

	@Inject
	private AbstractDomainFacade domainFacade;

	@Inject
	private UserFacade userFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@Inject
	private SelectModelFactory selectModelFactory;

	public Object onActivate() {

		if (mailingListVo == null) {
			return Index.class;
		} else {
			oldIdentifier = mailingListVo.getIdentifier();
			newOwner = mailingListVo.getOwner();
			return null;
		}
	}

	public SelectModel onProvideCompletionsFromNewOwner(String input) throws BusinessException {
		return selectModelFactory.create(mailingListFacade.completionOnUsers(loginUser, input), "completeName");
	}

	public LabelAwareValueEncoder<UserVo> getEncoder() {
		return new LabelAwareValueEncoder<UserVo>() {
			@Override
			public String toClient(UserVo value) {
				return value.getDomainIdentifier() + "," + value.getMail();
			}

			@Override
			public UserVo toValue(String clientValue) {
				String[] spl = StringUtils.split(clientValue, ',');
				String d, m;

				if (spl.length == 2) {
					d = spl[0];
					m = spl[1];
				} else {
					return null;
				}
				try {
					return userFacade.findUser(d, m);
				} catch (BusinessException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public String getLabel(UserVo arg0) {
				return arg0.getCompleteName();
			}
		};
	}

	public Object onActionFromCancel() {
		mailingListVo = null;
		oldIdentifier = null;
		return Index.class;
	}

	public void onValidateFromForm() {
		if (newOwner == null) {
			form.recordError(String.format(messages.get("pages.lists.administration.newOwnerNotFound")));
			return;
		}
		if (!mailingListVo.getOwner().equals(newOwner) || 
			(mailingListVo.getOwner().equals(newOwner) && 
			!mailingListVo.getIdentifier().equals(oldIdentifier))) {
			
			if (!mailingListFacade.identifierIsAvailable(newOwner, mailingListVo.getIdentifier())) {
				String copy = mailingListFacade.findAvailableIdentifier(newOwner, mailingListVo.getIdentifier());
				form.recordError(String.format(messages.get("pages.administration.lists.changeOwner"),
						newOwner.getFullName(), copy));
			}
		}
		
	}

	public Object onSuccess() throws BusinessException, ValidationException {
		mailingListVo.setOwner(newOwner);
		mailingListFacade.updateList(loginUser, mailingListVo);
		mailingListVo = null;
		return Index.class;
	}

	public void setList(MailingListVo list) {
		this.mailingListVo = list;
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}
}