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

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UploadRequestEntryVo;
import org.linagora.linshare.core.domain.vo.UploadRequestVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.UploadRequestFacade;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Lists;

public class UploadRequestFacadeImpl implements UploadRequestFacade {

	private final UserService userService;
	private final UploadRequestService uploadRequestService;
	private final DocumentEntryService documentEntryService;

	public UploadRequestFacadeImpl(final UserService userService,
			final UploadRequestService uploadRequestService,
			final DocumentEntryService documentEntryService) {
		this.userService = userService;
		this.uploadRequestService = uploadRequestService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public List<UploadRequestVo> findAll(UserVo actorVo)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		List<UploadRequestVo> ret = Lists.newArrayList();

		for (UploadRequest req : uploadRequestService.findAllRequest(actor)) {
			ret.add(new UploadRequestVo(req));
		}
		return ret;
	}

	@Override
	public UploadRequestVo findRequestByUuid(UserVo actorVo, String uuid)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		// TODO
		return new UploadRequestVo(uploadRequestService.findRequestByUuid(
				actor, uuid));
	}

	@Override
	public UploadRequestVo createRequest(UserVo actorVo, UploadRequestVo req)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadRequestGroup grp = new UploadRequestGroup(req);
		UploadRequest e = req.toEntity();

		// TODO functionalityFacade
		grp = uploadRequestService.createRequestGroup(actor, grp);

		e.setNotificationDate(e.getExpiryDate()); // FIXME functionalityFacade
		e.setUploadRequestGroup(grp);
		grp.getUploadRequests().add(e);
		return new UploadRequestVo(uploadRequestService.createRequest(actor, e));
	}

	@Override
	public UploadRequestVo updateRequest(UserVo actorVo, UploadRequestVo req)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadRequest e = uploadRequestService.findRequestByUuid(actor,
				req.getUuid());

		e.setMaxFileCount(req.getMaxFileCount());
		e.setMaxFileSize(req.getMaxFileSize());
		e.setMaxDepositSize(req.getMaxDepositSize());
		e.setActivationDate(req.getActivationDate());
		e.setExpiryDate(req.getExpiryDate());
		e.setLocale(req.getLocale().getTapestryLocale());
		return new UploadRequestVo(uploadRequestService.updateRequest(actor, e));
	}

	@Override
	public void deleteRequest(UserVo actorVo, UploadRequestVo req)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		// TODO
		uploadRequestService.deleteRequest(actor, req.toEntity());
	}

	@Override
	public UploadRequestVo closeRequest(UserVo actorVo, UploadRequestVo req)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadRequest e = uploadRequestService.findRequestByUuid(actor,
				req.getUuid());

		e.updateStatus(UploadRequestStatus.STATUS_CLOSED);
		return new UploadRequestVo(uploadRequestService.updateRequest(actor, e));
	}

	@Override
	public UploadRequestVo archiveRequest(UserVo actorVo, UploadRequestVo req)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());

		UploadRequest e = uploadRequestService.findRequestByUuid(actor,
				req.getUuid());
		e.updateStatus(UploadRequestStatus.STATUS_ARCHIVED);
		return new UploadRequestVo(uploadRequestService.updateRequest(actor, e));
	}

	@Override
	public List<UploadRequestEntryVo> findAllEntries(UserVo actorVo,
			UploadRequestVo req) throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		UploadRequest e = uploadRequestService.findRequestByUuid(actor,
				req.getUuid());
		List<UploadRequestEntryVo> ret = Lists.newArrayList();

		for (UploadRequestEntry ent : e.getUploadRequestEntries()) {
			ret.add(new UploadRequestEntryVo(ent));
		}
		return ret;
	}

	@Override
	public InputStream getFileStream(UserVo actorVo, UploadRequestEntryVo entry)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());

		return documentEntryService.getDocumentStream(actor, entry
				.getDocument().getIdentifier());
	}
}
