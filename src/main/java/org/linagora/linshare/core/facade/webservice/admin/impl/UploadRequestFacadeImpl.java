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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadRequestFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestHistoryDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadRequestService;

import com.google.common.collect.Sets;

@Deprecated
public class UploadRequestFacadeImpl extends AdminGenericFacadeImpl implements UploadRequestFacade {

	private final UploadRequestService uploadRequestService;

	public UploadRequestFacadeImpl(AccountService accountService,
			final UploadRequestService uploadRequestService) {
		super(accountService);
		this.uploadRequestService = uploadRequestService;
	}

	@Deprecated
	@Override
	public Set<UploadRequestHistoryDto> findAllHistory(String uploadRequestUuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Set<UploadRequestHistoryDto> dtos = Sets.newHashSet();
		UploadRequest uploadRequest = uploadRequestService.find(authUser, null, uploadRequestUuid);
		Set<UploadRequestHistory> res = uploadRequest.getUploadRequestHistory();
		for (UploadRequestHistory u: res) {
			dtos.add(new UploadRequestHistoryDto(u));
		}
		return dtos;
	}

	@Deprecated
	@Override
	public Set<UploadRequestDto> findAll(List<UploadRequestStatus> status, Date afterDate, Date beforeDate) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Set<UploadRequestDto> dtos = Sets.newHashSet();
		Set<UploadRequest> res = uploadRequestService.findAll(authUser, status, afterDate, beforeDate);
		for (UploadRequest u: res) {
			dtos.add(new UploadRequestDto(u, false));
		}
		return dtos;
	}
}
