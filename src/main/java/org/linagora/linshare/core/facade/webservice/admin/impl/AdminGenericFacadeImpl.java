/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AdminGenericFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;

public class AdminGenericFacadeImpl extends GenericFacadeImpl implements
		AdminGenericFacade {

	public AdminGenericFacadeImpl(final AccountService accountService) {
		super(accountService);
	}

	protected User checkAuthentication(Role role) throws BusinessException {
		User authUser = super.checkAuthentication();

		if (role != Role.SUPERADMIN && role != Role.ADMIN) {
			logger.error("Programmatic error: role must be set either to SUPERADMIN or ADMIN but is " + role.name());
			throw new IllegalArgumentException(
					"role must be either SUPERADMIN or ADMIN");
		}
		if (!(authUser.hasAdminRole() || authUser.hasSuperAdminRole())) {
			logger.error("Current authUser is trying to access to a forbbiden api : " + authUser.getAccountRepresentation());
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		if (role.equals(Role.SUPERADMIN)) {
			if (!(authUser.hasSuperAdminRole())) {
				logger.error("Current authUser is trying to access to a forbbiden api : " + authUser.getAccountRepresentation());
				throw new BusinessException(
						BusinessErrorCode.WEBSERVICE_FORBIDDEN,
						"You are not authorized to use this service");
			}
		}
		return authUser;
	}
}