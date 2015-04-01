/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateMailingList {

	private static Logger logger = LoggerFactory.getLogger(UpdateMailingList.class);

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@Inject
	private MailingListFacade mailingListFacade;

	@SessionState
	private UserVo userVo;

	private MailingListVo selected;

	@Inject
	private Messages messages;
	
	private String identifier;

	public void onActivate(String uuid) throws BusinessException {
		selected = mailingListFacade.findByUuid(userVo, uuid);
	}
	
	public Object onActivate() {
		if (selected == null)
			return Index.class;
		identifier = selected.getIdentifier();
		return null;
	}

	public Object onPassivate() {
		return selected.getUuid();
	}

	public void onValidateFromIdentifier(String value)
			throws ValidationException, BusinessException {
		if (!value.equals(identifier)) {
			if (!mailingListFacade.identifierIsAvailable(userVo, value)) {
				throw new ValidationException(String.format(
						messages.get("pages.lists.manageList.identifierExist"),
						mailingListFacade.findAvailableIdentifier(userVo, value)));
			}
		}
	}

	public Object onActionFromCancel() throws BusinessException {
		this.selected = null;
		return Index.class;
	}

	public Object onSuccess() throws BusinessException {
		mailingListFacade.updateList(userVo, selected);
		return Index.class;
	}

	public void setSelected(MailingListVo selected) {
		this.selected = selected;
	}

	public MailingListVo getSelected() {
		return this.selected;
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}
}
