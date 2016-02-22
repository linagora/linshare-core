/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import org.apache.commons.lang.Validate;
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
}
