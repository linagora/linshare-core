package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.AutoCompleteFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserService;

public class AutoCompleteFacadeImpl extends UserGenericFacadeImp implements AutoCompleteFacade {

	final private static int AUTO_COMPLETE_LIMIT = 20;

	private final UserService userService;

	public AutoCompleteFacadeImpl(final AccountService accountService, final UserService userService) {
		super(accountService);
		this.userService = userService;
	}

	@Override
	public Set<UserDto> findUser(String pattern) throws BusinessException {
		User actor = checkAuthentication();
		List<User> users = userService.autoCompleteUser(actor, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getUserDtoList(users);
	}

	@Override
	public Set<String> getMail(String pattern) throws BusinessException {
		User actor = checkAuthentication();
		Validate.notEmpty(pattern, "pattern must be set.");
		List<User> users = userService.autoCompleteUser(actor, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getMailList(users, AUTO_COMPLETE_LIMIT);
	}

	private Set<UserDto> getUserDtoList(List<User> users) {
		HashSet<UserDto> hashSet = new HashSet<UserDto>();
		int range = (users.size() < AUTO_COMPLETE_LIMIT ? users.size() : AUTO_COMPLETE_LIMIT);
		for (User user : users.subList(0, range)) {
			hashSet.add(UserDto.getSimple(user));
		}
		return hashSet;
	}

	private Set<String> getMailList(List<User> users, int limit) {
		Set<String> res = new HashSet<String>();
		for (User user : users) {
			res.add(user.getMail());
		}
		return res;
	}
}
