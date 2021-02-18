/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SafeDetailFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SafeDetailService;
import org.linagora.linshare.mongo.entities.SafeDetail;

import com.google.common.base.Strings;

public class SafeDetailFacadeImpl extends GenericFacadeImpl implements SafeDetailFacade {

	private final SafeDetailService safeDetailService;

	public SafeDetailFacadeImpl(AccountService accountService,
			final SafeDetailService safeDetailService) {
		super(accountService);
		this.safeDetailService = safeDetailService;
	}

	@Override
	public List<SafeDetail> findAll(String actorUuid) throws BusinessException {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return safeDetailService.findAll(authUser, actor);
	}

	@Override
	public SafeDetail delete(String actorUuid, String uuid, SafeDetail safeDetail) throws BusinessException {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			safeDetail = safeDetailService.find(authUser, actor, uuid);
		} else {
			Validate.notNull(safeDetail, "safeDetail must be set");
			Validate.notEmpty(safeDetail.getUuid(), "safeDetail uuid must be set");
			safeDetail = safeDetailService.find(authUser, actor, safeDetail.getUuid());
		}
		return safeDetailService.delete(authUser, actor, safeDetail);
	}

	@Override
	public SafeDetail find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required safeDetail uuid");
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return safeDetailService.find(authUser, actor, uuid);
	}

	@Override
	public SafeDetail create(String actorUuid, SafeDetail safeDetail) throws BusinessException {
		Validate.notEmpty(safeDetail.getContainerUuid(), "Missing required container uuid");
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return safeDetailService.create(authUser, actor, safeDetail);
	}
}
