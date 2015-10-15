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

package org.linagora.linshare.core.facade.webservice.uploadrequest.impl;

import java.io.File;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MimePolicyService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;

public class UploadRequestUrlFacadeImpl implements UploadRequestUrlFacade {

	private final UploadRequestService uploadRequestService;

	private final UploadRequestUrlService uploadRequestUrlService;

	private final MimePolicyService mimePolicyService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public UploadRequestUrlFacadeImpl(
			final UploadRequestService uploadRequestService,
			final UploadRequestUrlService uploadRequestUrlService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			final MimePolicyService mimePolicyService) {
		this.uploadRequestService = uploadRequestService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.mimePolicyService = mimePolicyService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	public UploadRequestDto find(String uploadRequestUrlUuid, String password) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uploadRequestUrlUuid, password);
		UploadRequestDto dto = transform(requestUrl);
		return dto;
	}

	@Override
	public UploadRequestDto close(String uuid, String password)
			throws BusinessException {
		Validate.notEmpty(uuid);
		UploadRequestUrl url = uploadRequestUrlService.find(uuid, password);
		uploadRequestService.closeRequestByRecipient(url);
		return transform(uploadRequestUrlService.find(uuid, password));
	}

	@Override
	public void addUploadRequestEntry(String uploadRequestUrlUuid,
			String password, File file, String fileName) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		Validate.notNull(file);
		Validate.notEmpty(fileName);
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrlUuid, file, fileName, password);
	}

	@Override
	public void deleteUploadRequestEntry(String uploadRequestUrlUuid,
			String password, EntryDto entry) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		Validate.notNull(entry);
		Validate.notEmpty(entry.getUuid());
		uploadRequestUrlService.deleteUploadRequestEntry(uploadRequestUrlUuid, password, entry.getUuid());
	}

	@Override
	public void deleteUploadRequestEntry(String uploadRequestUrlUuid,
			String password, String entryUuid) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		Validate.notEmpty(entryUuid);
		uploadRequestUrlService.deleteUploadRequestEntry(uploadRequestUrlUuid, password, entryUuid);
	}

	/*
	 * Helpers
	 */

	private UploadRequestDto transform(UploadRequestUrl requestUrl) throws BusinessException {
		if (requestUrl==null) {
			return null;
		}
		UploadRequestDto dto = new UploadRequestDto(requestUrl);
		Account owner = requestUrl.getUploadRequest().getOwner();
		Functionality functionality = functionalityReadOnlyService.getMimeTypeFunctionality(owner.getDomain());
		if (functionality.getActivationPolicy().getStatus()) {
			Set<MimeType> mimeTypes = mimePolicyService.findAllMyMimeTypes(owner);
			for (MimeType mimeType : mimeTypes) {
				String extension = mimeType.getExtensions();
				if (mimeType.getEnable()) {
					if (!extension.isEmpty()) {
						dto.addExtensions(extension.substring(1));
					}
					if (extension.equals(".jpg")) {
						dto.addExtensions("jpeg");
					}
				}
			}
		}
		return dto;
	}
}
