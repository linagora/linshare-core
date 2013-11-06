package org.linagora.linshare.view.tapestry.components;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ListUserThread {

	private static final Logger logger = LoggerFactory
			.getLogger(ListUserThread.class);

	@SessionState
	@Property
	private UserVo userVo;

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private ThreadVo thread;

	@Persist
	@Property
	private List<UserVo> users;

	@Property
	private UserVo user;

	@Persist
	@Property
	private boolean show;

	@Persist
	@Property
	private String pattern;

	@Inject
	private UserFacade userFacade;

	@Inject
	private ThreadEntryFacade threadEntryFacade;


	public void onActivate() {
		if (!show) {
			users = Lists.newArrayList();
		}
	}

	@SetupRender
	public void init() throws BusinessException {
		if (show) {
			updateUserList();
		}
	}
	
	public void onSuccessFromUserSearch() throws BusinessException {
		StringUtils.trim(pattern);
		updateUserList();
		show = true;
	}

	public void onActionFromAdd(String uuid) throws BusinessException {
		for (UserVo u : users) {
			if (u.getLsUuid().equals(uuid))
				threadEntryFacade.addMember(userVo, thread,
						u.getDomainIdentifier(), u.getMail());
			return;
		}
	}

	public void updateUserList() throws BusinessException {
		users = ImmutableList.copyOf(Iterables.filter(
				threadEntryFacade.searchAmongUsers(userVo, pattern),
				isNotMember()));
	}
	
	private Predicate<UserVo> isNotMember() {
		return new Predicate<UserVo>() {
			@Override
			public boolean apply(UserVo input) {
				try {
					return !threadEntryFacade.userIsMember(input, thread);
				} catch (BusinessException e) {
					logger.error(e.getMessage());
					return true;
				}
			}
		};
	}
}
