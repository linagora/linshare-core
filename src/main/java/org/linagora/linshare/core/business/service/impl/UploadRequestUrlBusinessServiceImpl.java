/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
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
package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.UploadRequestUrlRepository;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.utils.HashUtils;

public class UploadRequestUrlBusinessServiceImpl implements
		UploadRequestUrlBusinessService {

	private final UploadRequestUrlRepository uploadRequestUrlRepository;

	private final PasswordService passwordService;

	private final ContactRepository contactRepository;

	private final String baseUrl;

	public UploadRequestUrlBusinessServiceImpl(
			UploadRequestUrlRepository uploadRequestUrlRepository,
			ContactRepository contactRepository ,
			PasswordService passwordService, String baseUrl) {
		super();
		this.uploadRequestUrlRepository = uploadRequestUrlRepository;
		this.passwordService = passwordService;
		this.contactRepository = contactRepository ;
		this.baseUrl = baseUrl;
	}

	@Override
	public UploadRequestUrl findByUuid(String uuid) {
		return uploadRequestUrlRepository.findByUuid(uuid);
	}

	@Override
	public UploadRequestUrl create(UploadRequest request, Boolean passwordProtected, Contact contact)
			throws BusinessException {
		Contact recipient = contactRepository.find(contact);
		if (recipient == null) {
			recipient = contactRepository.create(contact);
		}

		UploadRequestUrl url = new UploadRequestUrl(request, baseUrl, recipient);
		if (passwordProtected) {
			String password = passwordService.generatePassword();
			// We store it temporary in this object for mail notification.
			url.setTemporaryPlainTextPassword(password);
			url.setPassword(HashUtils.hashSha1withBase64(password.getBytes()));
		}
		request.getUploadRequestURLs().add(url);
		return uploadRequestUrlRepository.create(url);
	}

	@Override
	public UploadRequestUrl update(UploadRequestUrl url)
			throws BusinessException {
		return uploadRequestUrlRepository.update(url);
	}

	@Override
	public void delete(UploadRequestUrl uploadRequestUrl) throws BusinessException {
		UploadRequest uploadRequest = uploadRequestUrl.getUploadRequest();
		UploadRequestGroup uploadRequestGroup = uploadRequest.getUploadRequestGroup();
		List<UploadRequestUrl> uploadRequestURLs = uploadRequestUrlRepository.findByUploadRequest(uploadRequest);
		if (uploadRequestGroup.getRestricted()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_DELETE_RECIPIENT_FROM_RESTRICTED_REQUEST,
					"Cannot delete a recipient of an upload request in mode restricted");
		}
		if (uploadRequestURLs.size() < 2) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_DELETE_LAST_RECIPIENT,
					"Cannot delete the last recipient of an upload request in shared mode");
		}else if (!uploadRequestUrl.getUploadRequestEntries().isEmpty()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_URL_EXISTS,
					"Cannot delete upload request url with existed entries");
		}
		uploadRequestUrlRepository.delete(uploadRequestUrl);
	}
}
