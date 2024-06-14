/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.InternalRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InconsistentUserServiceImpl implements InconsistentUserService {

	final private static Logger logger = LoggerFactory
			.getLogger(InconsistentUserServiceImpl.class);

	private final InternalRepository internateRepository;

	private final AbstractDomainService abstractDomainService;

	private final  UserRepository<User> userRepository;

	public InconsistentUserServiceImpl(
			final InternalRepository internalRepository,
			final AbstractDomainService abstractDomainService,
			final UserRepository<User> userRepository) {
		super();
		this.internateRepository = internalRepository;
		this.abstractDomainService = abstractDomainService;
		this.userRepository=userRepository;
	}

	@Override
	public void updateDomain(User actor, String uuid, String domain) throws BusinessException {

		checkPermissions(actor);

		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.CANNOT_UPDATE_USER,
					"Only superadmins can update an inconsistent user.");
		}

		User u = userRepository.findByLsUuid(uuid);

		AbstractDomain d = abstractDomainService.retrieveDomain(domain);

		logger.info("Trying to set inconsistent user '" + u + "' domain to '" + d + "'.");
		if (u == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Attempt to update an user entity failed : User not found.");
		} else if (d == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Attempt to update an user entity failed : Domain does not exist.");
		}
		final User userInTargetDomain = userRepository.findByDomainAndMail(domain, u.getMail());

		if (userInTargetDomain != null) {
			throw new BusinessException(BusinessErrorCode.USER_ALREADY_EXISTS_IN_DOMAIN_TARGET,
					"Attempt to update an user entity failed : The user : " + u.getFullName()
							+ " is already in the Domain : " + domain);
		}
		u.setDomain(d);
		userRepository.update(u);
	}
	
	@Override
	public List<Internal> findAllInconsistent(User actor) throws BusinessException {
		checkPermissions(actor);
		return internateRepository.findAllInconsistent();
	}

	@Override
	public List<String> findAllUserUuids(Account actor) throws BusinessException {
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor has no rights to use this service.");
		}
		return internateRepository.findAllUsersUuid();
	}

	@Override
	public List<String> findAllIconsistentsUuid(Account actor) throws BusinessException {
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor has no rights to use this service.");
		}
		return internateRepository.findAllInconsistentsUuid();
	}

	private void checkPermissions(User actor) throws BusinessException {
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor must be either superadmin.");
		}
	}
}
