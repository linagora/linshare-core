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
package org.linagora.linshare.core.batches.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;
import org.linagora.linshare.core.batches.UploadRequestBatch;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.service.MailBuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UploadRequestBatchImpl implements UploadRequestBatch {

	private static final Logger logger = LoggerFactory.getLogger(UploadRequestBatchImpl.class);

	private final UploadRequestRepository uploadRequestRepository;
	private final MailBuildingService mailBuildingService;

	public UploadRequestBatchImpl(UploadRequestRepository uploadRequestRepository, MailBuildingService mailBuildingService) {
		this.uploadRequestRepository = uploadRequestRepository;
		this.mailBuildingService = mailBuildingService;
	}

	@Override
	public void updateStatus() {
		logger.info("Update upload request status");
		for (UploadRequest r : uploadRequestRepository.findByStatus(Lists.newArrayList(UploadRequestStatus.STATUS_CREATED))) {
			if (r.getActivationDate().before(new Date())) {
				try {
					r.updateStatus(UploadRequestStatus.STATUS_ENABLED);
					uploadRequestRepository.update(r);
					for (UploadRequestUrl u: r.getUploadRequestURLs()) {
						mailBuildingService.buildActivateUploadRequest((User) r.getOwner(), u);
					}
				} catch (BusinessException e) {
					logger.error("Fail to update upload request status of the request : " + r.getUuid());
				}
			}
		}
		for (UploadRequest r : uploadRequestRepository.findByStatus(Lists.newArrayList(UploadRequestStatus.STATUS_ENABLED))) {
			if (DateUtils.isSameDay(r.getExpiryDate(), r.getNotificationDate())) {
				try {
					for (UploadRequestUrl u: r.getUploadRequestURLs()) {
						mailBuildingService.buildUploadRequestBeforeExpiryWarnRecipient((User) r.getOwner(), u);
					}
					mailBuildingService.buildUploadRequestBeforeExpiryWarnOwner((User) r.getOwner(), r);
				} catch (BusinessException e) {
					logger.error("Fail to update upload request status of the request : " + r.getUuid());
				}
			}
			if (r.getExpiryDate().before(new Date())) {
				try {
					r.updateStatus(UploadRequestStatus.STATUS_CLOSED);
					uploadRequestRepository.update(r);
					for (UploadRequestUrl u: r.getUploadRequestURLs()) {
						mailBuildingService.buildUploadRequestExpiryWarnRecipient((User) r.getOwner(), u);
					}
					mailBuildingService.buildUploadRequestBeforeExpiryWarnOwner((User) r.getOwner(), r);
				} catch (BusinessException e) {
					logger.error("Fail to update upload request status of the request : " + r.getUuid());
				}
			}
		}
	}
}
