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
package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.UploadRequestUrlRepository;


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
			url.setPassword(passwordService.encode((password)));
		}
		request.getUploadRequestURLs().add(url);
		return uploadRequestUrlRepository.create(url);
	}

	@Override
	public UploadRequestUrl update(UploadRequestUrl url)
			throws BusinessException {
		return uploadRequestUrlRepository.update(url);
	}
}
