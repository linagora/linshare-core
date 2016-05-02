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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.InternalRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InconsistentUserServiceImpl implements InconsistentUserService {

	final private static Logger logger = LoggerFactory
			.getLogger(InconsistentUserServiceImpl.class);

	private final InternalRepository internateRepository;

	private final AbstractDomainService abstractDomainService;

	public InconsistentUserServiceImpl(
			final InternalRepository internalRepository,
			final AbstractDomainService abstractDomainService) {
		super();
		this.internateRepository = internalRepository;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public void updateDomain(User actor, String uuid, String domain)
			throws BusinessException {
		checkPermissions(actor);
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.CANNOT_UPDATE_USER,
					"Only superadmins can update an inconsistent user.");
		}

		Internal u = internateRepository.findByLsUuid(uuid);
		AbstractDomain d = abstractDomainService.retrieveDomain(domain);

		logger.info("Trying to set inconsistent user '" + u + "' domain to '"
				+ d + "'.");
		if (u == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Attempt to update an user entity failed : User not found.");
		} else if (d == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Attempt to update an user entity failed : Domain does not exist.");
		}
		Internal userInTargetDomain = internateRepository.findByMailAndDomain(domain, u.getMail());

		if (userInTargetDomain != null) {
			throw new BusinessException(
					BusinessErrorCode.USER_ALREADY_EXISTS_IN_DOMAIN_TARGET,
					"Attempt to update an user entity failed : The user : "
							+ u.getFullName() + " is already in the Domain : "
							+ domain);
		}
		u.setDomain(d);
		internateRepository.update(u);
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
