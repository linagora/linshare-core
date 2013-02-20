package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceUserFacade;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class WebServiceUserFacadeImpl implements WebServiceUserFacade {

	private final UserService userService;
	
	public WebServiceUserFacadeImpl(UserService userService) {
		super();
		this.userService = userService;
	}

	@Override
	public List<UserDto> getUsers() throws BusinessException {
		User actor = getAuthentication();
		List<UserDto> res = new ArrayList<UserDto>();
		// we return all users without any filters
		List<User> users = userService.searchUser(null, null, null, null, actor);
		for (User user : users) {
			res.add(new UserDto(user));
		}
		return res;
	}

	private User getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
	     String name =  (auth != null) ? auth.getName() : null; //get logged in username
	     if (name == null) {
	    	 return null;
	     }
	     User user = userService.findByLsUid(name);
	     return user;
	}
	
	@Override
	public User checkAuthentication() throws BusinessException {
		
		User actor = getAuthentication();
		
		if (actor== null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
		
		return actor;
	}
	
}
