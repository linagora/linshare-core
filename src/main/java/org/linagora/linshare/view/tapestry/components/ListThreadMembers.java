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
import org.linagora.linshare.view.tapestry.enums.UserTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ListThreadMembers {

	private static final Logger logger = LoggerFactory.getLogger(ListThreadMembers.class);

	@SessionState
	@Property
	private UserVo userVo;

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private ThreadVo thread;
	
	@Component(parameters = { "style=bluelighting", "show=false", "width=520", "height=180" })
	private WindowWithEffects memberEditWindow;

	@Component(parameters = { "style=bluelighting", "show=false", "width=520", "height=180" })
	private WindowWithEffects threadEditWindow;

	@InjectComponent
	private ConfirmPopup confirmPopup;

	@InjectComponent
	private Zone memberEditTemplateZone;

	@InjectComponent
	private Zone threadEditTemplateZone;

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

	public Zone onActionFromEditThread() {
		logger.info("Trying to edit current thread ");
		return threadEditTemplateZone;
	}

	public void onActionFromDeleteMember(String identifier) {
		logger.info("Trying to delete a member.");

		for (ThreadMemberVo m : members) {
			logger.debug(m.getLsUuid() + " compared to parameter " + identifier);
			if (m.getLsUuid().equals(identifier)) {
				selectedMemberId = identifier;
				toDelete = m;
				logger.info("Trying to delete " + toDelete.getLsUuid());
			}
		}
	}

	public void onDeleteMember() {
		try {
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
	
	/*
	 * Helpers
	 */

	private void updateMemberList() throws BusinessException {
		members = StringUtils.isNotBlank(pattern) ?
				threadEntryFacade.searchAmongMembers(userVo, thread, pattern, "all") :
				threadEntryFacade.getThreadMembers(userVo, thread);
	}
}
