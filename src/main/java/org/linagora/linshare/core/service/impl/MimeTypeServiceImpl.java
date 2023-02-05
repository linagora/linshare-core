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
package org.linagora.linshare.core.service.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MimePolicyBusinessService;
import org.linagora.linshare.core.business.service.MimeTypeBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MimeTypeServiceImpl implements MimeTypeService {

	private static final Logger logger = LoggerFactory.getLogger(MimeTypeServiceImpl.class);

	final private MimePolicyBusinessService mimePolicyBusinessService;

	final private MimeTypeBusinessService mimeTypeBusinessService;

	final private DomainPermissionBusinessService domainPermissionService;

	public MimeTypeServiceImpl(
			MimeTypeBusinessService mimeTypeBusinessService,
			MimePolicyBusinessService mimePolicyBusinessService,
			DomainPermissionBusinessService domainPermissionService
			) {
		this.mimeTypeBusinessService = mimeTypeBusinessService;
		this.mimePolicyBusinessService = mimePolicyBusinessService;
		this.domainPermissionService = domainPermissionService;
	}

	@Override
	public MimeType find(Account actor, String uuid) throws BusinessException {
		Validate.notNull(actor);
		Validate.notEmpty(uuid);

		checkAdminFor(actor, uuid);
		return mimeTypeBusinessService.find(uuid);
	}

	@Override
	public MimeType update(Account actor, MimeType mimeTypeDto) throws BusinessException {
		Validate.notNull(actor);
		Validate.notNull(mimeTypeDto);
		Validate.notEmpty(mimeTypeDto.getUuid());

		checkAdminFor(actor, mimeTypeDto.getUuid());
		return mimeTypeBusinessService.update(mimeTypeDto);
	}

	@Override
	public void checkFileMimeType(Account actor, String fileName, String mimeType) throws BusinessException {
		Validate.notNull(actor);
		Validate.notEmpty(fileName);
		Validate.notEmpty(mimeType);

		String[] extras = { fileName };
		MimePolicy mimePolicy = actor.getDomain().getMimePolicy();
		mimePolicyBusinessService.load(mimePolicy);
		MimeType entity = mimeTypeBusinessService.findByMimeType(mimePolicy,
				mimeType);

		logger.debug("2)check the mimetype:" + mimeType);
		if (entity != null) {
			if (!entity.getEnable()) {
				logger.debug("mimetype not allowed: " + mimeType);
				throw new BusinessException(
						BusinessErrorCode.FILE_MIME_NOT_ALLOWED,
						"This kind of file is not allowed: " + mimeType, extras);
			}
		} else {
			String msg = "Mimetype is empty for this file" + mimeType;
			logger.error(msg);
			throw new BusinessException(
					BusinessErrorCode.FILE_MIME_NOT_ALLOWED, msg, extras);
		}
	}

	/*
	 * Check if the current actor is admin of the domain associated to
	 * the MimePolicy.
	 */
	private void checkAdminFor(Account actor, String uuid)
			throws BusinessException {
		MimeType mimeType = mimeTypeBusinessService.find(uuid);

		if (!domainPermissionService.isAdminforThisDomain(actor, mimeType
				.getMimePolicy().getDomain())) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to update this MimeType.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
	}

	@Override
	public void delete(Account actor, MimeType mimeType) throws BusinessException {
		Validate.notNull(actor);
		Validate.notNull(mimeType);
		Validate.notEmpty(mimeType.getUuid());
		checkAdminFor(actor, mimeType.getUuid());
		mimeTypeBusinessService.delete(mimeType);
	}
}
