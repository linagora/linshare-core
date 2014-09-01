/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

package org.linagora.linshare.core.facade.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UploadPropositionVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.UploadPropositionFacade;
import org.linagora.linshare.core.service.UploadPropositionService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UploadPropositionFacadeImpl implements UploadPropositionFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionFacadeImpl.class);

	private final UserService userService;
	private final UploadPropositionService uploadPropositionService;

	public UploadPropositionFacadeImpl(UserService userService,
			UploadPropositionService uploadPropositionService) {
		super();
		this.userService = userService;
		this.uploadPropositionService = uploadPropositionService;
	}

	@Override
	public List<UploadPropositionVo> findAllVisibles(UserVo actorVo)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		List<UploadProposition> res = uploadPropositionService.findAll(actor);
		List<UploadPropositionVo> ret = Lists.newArrayList();
		for (UploadProposition e : res) {
			if (e.isPending()) {
				ret.add(new UploadPropositionVo(e));
			}
		}
		return ret;
	}

	@Override
	public void accept(UserVo actorVo, UploadPropositionVo prop)
			throws BusinessException {
		logger.debug("actorVo: " + actorVo, ", prop: " + prop.getUuid());
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadProposition e = uploadPropositionService.find(actor,
				prop.getUuid());

		uploadPropositionService.accept(actor, e);
	}

	@Override
	public void reject(UserVo actorVo, UploadPropositionVo prop)
			throws BusinessException {
		logger.debug("actorVo: " + actorVo, ", prop: " + prop.getUuid());
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadProposition e = uploadPropositionService.find(actor,
				prop.getUuid());

		uploadPropositionService.reject(actor, e);
	}
}
