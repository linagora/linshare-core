/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.UploadRequestGroupResourceAccessControl;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UploadRequestGroupServiceImpl extends GenericServiceImpl<Account, UploadRequestGroup> implements UploadRequestGroupService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestGroupServiceImpl.class);

	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;
	private final UploadRequestGroupResourceAccessControl groupRac;
	private final LogEntryService logEntryService;

	public UploadRequestGroupServiceImpl(
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService,
			final UploadRequestGroupResourceAccessControl groupRac,
			final LogEntryService logEntryService
			) {
		super(groupRac);
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.groupRac = groupRac;
		this.logEntryService = logEntryService;
	}

	@Override
	public List<UploadRequestGroup> findAllGroupRequest(Account actor, Account owner, List<String> statusList)
			throws BusinessException {
		preChecks(actor, owner);
		groupRac.checkListPermission(actor, owner, UploadRequestGroup.class, BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN,
				null);
		List<UploadRequestStatus> uploadRequestStatus = Lists.newArrayList();
		if (statusList != null && !statusList.isEmpty()) {
			statusList.forEach(s -> uploadRequestStatus.add(UploadRequestStatus.fromString(s)));
		}
		return uploadRequestGroupBusinessService.findAll(owner, uploadRequestStatus);
	}

	@Override
	public UploadRequestGroup findRequestGroupByUuid(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		UploadRequestGroup req = uploadRequestGroupBusinessService.findByUuid(uuid);
		groupRac.checkReadPermission(actor,
				req.getUploadRequests().iterator().next().getUploadRequestGroup().getOwner(), UploadRequestGroup.class,
				BusinessErrorCode.UPLOAD_REQUEST_FORBIDDEN, req);
		return req;
	}
}
