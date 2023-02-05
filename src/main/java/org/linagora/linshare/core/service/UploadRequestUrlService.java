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
package org.linagora.linshare.core.service;

import java.io.File;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.objects.UploadRequestContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.ChangeUploadRequestUrlPassword;

public interface UploadRequestUrlService {

	UploadRequestUrl find(String uuid, String password) throws BusinessException;

	UploadRequestContainer create(UploadRequest request, Contact contact, UploadRequestContainer container)
			throws BusinessException;

	UploadRequestEntry createUploadRequestEntry(String uploadRequestUrlUuid, File file, String fileName,
			String password) throws BusinessException;

	UploadRequestEntry deleteUploadRequestEntry(String uploadRequestUrlUuid, String password, String entryUuid)
			throws BusinessException;

	SystemAccount getUploadRequestSystemAccount();

	void changePassword(Account authUser, Account actor, String requestUrlUuid,
			ChangeUploadRequestUrlPassword reset);
}
