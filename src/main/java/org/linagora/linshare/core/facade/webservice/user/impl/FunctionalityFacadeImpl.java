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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.Lists;

public class FunctionalityFacadeImpl extends UserGenericFacadeImp implements
		FunctionalityFacade {

	private FunctionalityService functionalityService;

	public FunctionalityFacadeImpl(AccountService accountService,
			FunctionalityService functionalityService) {
		super(accountService);
		this.functionalityService = functionalityService;
	}

	@Override
	public FunctionalityDto find(String identifier, Version version) throws BusinessException {
		User authUser = checkAuthentication();
		Functionality functionality = functionalityService.find(authUser, identifier);
		FunctionalityDto dto = functionality.toUserDto(version);
		if (version.isLessThan(Version.V5)) {
			if(dto.getIdentifier().equals(FunctionalityNames.WORK_SPACE__CREATION_RIGHT.toString())) {
				dto.setIdentifier(FunctionalityNames.DRIVE__CREATION_RIGHT.toString());
			}
		}
		return dto;
	}

	@Override
	public List<FunctionalityDto> findAll(Version version) throws BusinessException {
		User authUser = checkAuthentication();
		List<FunctionalityDto> res = Lists.newArrayList();
		for (Functionality functionality : functionalityService.findAll(authUser)) {
			FunctionalityDto dto = functionality.toUserDto(version);
			if (version.isLessThan(Version.V5)) {
				if(dto.getIdentifier().equals(FunctionalityNames.WORK_SPACE__CREATION_RIGHT.toString())) {
					dto.setIdentifier(FunctionalityNames.DRIVE__CREATION_RIGHT.toString());
				}
			}
			res.add(dto);
		}
		return res;
	}

}
