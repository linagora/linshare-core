package org.linagora.linshare.core.facade.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericTapestryFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(GenericTapestryFacade.class);


	protected final AccountService accountService;

	public GenericTapestryFacade(final AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	/*
	 * Helpers
	 */
	protected User getActor(UserVo userVo) throws BusinessException {
		Validate.notEmpty(userVo.getLsUuid(), "Missing actor uuid");
		User actor = (User) accountService.findByLsUuid(userVo.getLsUuid());
		if (actor == null) {
			logger.error("Can't find actor : " + userVo.getLsUuid());
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"You are not authorized to use this facade");
		}
		return actor;
	}
}
