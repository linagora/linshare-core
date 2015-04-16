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

package org.linagora.linshare.view.tapestry.components;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.constants.ThreadRoles;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class ListThreadMembers {

	private static final Logger logger = LoggerFactory.getLogger(ListThreadMembers.class);

	@SessionState
	@Property
	private UserVo userVo;

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private ThreadVo thread;
	
	@Component(parameters = { "style=bluelighting", "show=false", "width=650", "height=225" })
	private WindowWithEffects memberEditWindow;

	@InjectComponent
	private ConfirmPopup confirmPopup;

	@InjectComponent
	private Zone memberEditTemplateZone;

	@InjectComponent
	private Zone reloadZone;

	@Persist
	private List<ThreadMemberVo> members;

	@Property
	@Persist
	private boolean show;

	@Property
	private ThreadMemberVo member;

	@Persist
	private ThreadMemberVo toDelete;

	@Persist
	@Property
	private String selectedMemberId;

	@Persist
	@Property
	private String pattern;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private ThreadRoles threadRole;

	/*
	 * Injected beans
	 */

	@Inject
	private Messages messages;

	@Inject
	private ThreadEntryFacade threadEntryFacade;

	@Inject
	private UserFacade userFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;


	@SetupRender
	public void init() throws BusinessException {
		if (show) {
			updateMemberList();
		}
	}
	
	/*
	 * Events handling
	 */

	public void onSuccessFromMemberSearch() throws BusinessException {
		StringUtils.trim(pattern);
		updateMemberList();
		show = true;
	}
	
	public Zone onActionFromEditMember(String identifier) {
		logger.info("Trying to edit member with identifier : " + identifier);
		selectedMemberId = identifier;
		return memberEditTemplateZone;
	}

	public void onActionFromDeleteMember(String identifier) {
		toDelete = Iterables.find(members, ThreadMemberVo.equalTo(identifier));
		selectedMemberId = identifier;
	}

	public void onDeleteMember() {
		try {
			logger.info("Trying to delete " + toDelete.getLsUuid());
			threadEntryFacade.deleteMember(userVo, thread, toDelete);
			members = threadEntryFacade.getThreadMembers(userVo, thread);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
	}

	public Zone onValueChangedFromThreadRole() {
		return reloadZone;
	}
	
	/*
	 * Getters
	 */

	public List<ThreadMemberVo> getMembers() {
		return ImmutableList.copyOf(Iterables.filter(members,
				ThreadMemberVo.hasRole(threadRole)));
	}

	public boolean getIsDeletable() throws BusinessException {
		return threadEntryFacade.memberIsDeletable(userVo, thread);
	}

	public String getCountMembers() {
		try {
			return " (Total : "
					+ threadEntryFacade.countMembers(userVo, thread) + ")";
		} catch (BusinessException e) {
			logger.info("Couldn't get thread's members count.");
			return "";
		}
	}

	/*
	 * Helpers
	 */

	private void updateMemberList() throws BusinessException {
		members = StringUtils.isNotBlank(pattern) ?
				threadEntryFacade.searchAmongMembers(userVo, thread, pattern, "all") :
				threadEntryFacade.getThreadMembers(userVo, thread);
	}
}
