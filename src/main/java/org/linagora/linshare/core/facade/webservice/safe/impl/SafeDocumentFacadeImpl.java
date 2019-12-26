/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.safe.impl;

import java.io.File;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.safe.SafeDocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SafeDetailService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class SafeDocumentFacadeImpl extends GenericFacadeImpl
		implements SafeDocumentFacade {

	private final SafeDetailService safeDetailService;

	private final ThreadService threadService;

	private final WorkGroupNodeService workGroupNodeService;

	public SafeDocumentFacadeImpl(SafeDetailService safeDetailService,
			ThreadService threadService,
			WorkGroupNodeService workGroupNodeService,
			final AccountService accountService) {
		super(accountService);
		this.threadService = threadService;
		this.workGroupNodeService = workGroupNodeService;
		this.safeDetailService = safeDetailService;
	}

	@Override
	public SafeDetail findSafeDetail(String actorUuid, String safeUuid) throws BusinessException {
		Validate.notEmpty(safeUuid);
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SafeDetail safeDetail = safeDetailService.find(authUser, actor, safeUuid);
		Validate.notNull(safeDetail);
		return safeDetail;
	}

	@Override
	public User findUser(String safeUuid) throws BusinessException {
		Validate.notEmpty(safeUuid);
		User authUser = checkAuthentication();
		Validate.notNull(authUser);
		return authUser;
	}

	@Override
	public WorkGroupEntryDto create(String actorUuid, String workgroupUuid,
			File file, String fileName, Boolean strict) {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(workgroupUuid, "Missing required thread uuid");
		Validate.notNull(file, "Missing required file");
		Validate.notNull(fileName, "Missing required fileName");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, workgroupUuid);
		WorkGroupNode node = workGroupNodeService.create(authUser, actor, workGroup, file, fileName, null, strict);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		// why ?
//		dto.setWorkGroup(new WorkGroupLightDto(thread));
		return dto;
	}
}
