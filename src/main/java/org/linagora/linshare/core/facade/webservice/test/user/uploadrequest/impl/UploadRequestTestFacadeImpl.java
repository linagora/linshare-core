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
package org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.UploadRequestTestFacade;
import org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadRequestTestFacadeImpl extends GenericFacadeImpl implements UploadRequestTestFacade {


	private final UploadRequestGroupService uploadRequestGroupService;

	private final UploadRequestService uploadRequestService;

	public UploadRequestTestFacadeImpl(
			final AccountService accountService,
			final UploadRequestGroupService uploadRequestGroupService,
			UploadRequestService uploadRequestService) {
		super(accountService);
		this.uploadRequestGroupService = uploadRequestGroupService;
		this.uploadRequestService = uploadRequestService;
	}

	@Override
	public List<UploadRequestDto> findAllUploadRequestsURl(String actorUuid, String groupUuid) {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		Validate.notEmpty(groupUuid, "Upload request group Uuid must be set");
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.find(authUser, actor, groupUuid);
		List<UploadRequest> requests = uploadRequestService.findAll(authUser, actor, uploadRequestGroup, null);
		return ImmutableList.copyOf(Lists.transform(requests, UploadRequestDto.transform(false)));
	}
}
