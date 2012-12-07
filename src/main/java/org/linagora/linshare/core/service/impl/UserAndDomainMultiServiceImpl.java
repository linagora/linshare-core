package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.UserAndDomainMultiService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAndDomainMultiServiceImpl implements UserAndDomainMultiService {

	private static final Logger logger = LoggerFactory.getLogger(UserAndDomainMultiServiceImpl.class);
	
	private final AbstractDomainService abstractDomainService;
	private final UserService userService;
	
	public UserAndDomainMultiServiceImpl(
			AbstractDomainService abstractDomainService, UserService userService) {
		super();
		this.abstractDomainService = abstractDomainService;
		this.userService = userService;
	}

	
	
	@Override
	public void deleteDomainAndUsers(User actor, String domainIdentifier) throws BusinessException {
		logger.debug("deleteDomainAndUsers: begin");
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		
		if(domain != null ){
			logger.debug("Delete all subdomains users");
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				userService.deleteAllUsersFromDomain(actor, subDomain.getIdentifier());
			}
			logger.debug("Delete domain users");
			userService.deleteAllUsersFromDomain(actor, domainIdentifier);
			
			if(logger.isDebugEnabled())
				logger.debug("Delete domain "+ domainIdentifier + " and its subdomains");
			abstractDomainService.deleteDomain(domainIdentifier);
		}else{
			logger.error("Domain not authorized for this user");
		}
		logger.debug("deleteDomainAndUsers: end");		
	}



	@Override
	public User findOrCreateUser(String mail, String domainId) throws BusinessException {
		return userService.findUserInDB(domainId,mail);
	}

}
