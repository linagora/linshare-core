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

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;

import com.google.common.io.ByteSource;


public interface AnonymousUrlService {

	public boolean isProtectedByPassword(Account actor, AnonymousUrl anonymousUrl) throws BusinessException;

	public ByteSource downloadDocument(Account actor, Account owner, String anonymousUrlUuid, String anonymousShareEntryUuid, String password) throws BusinessException;

	List<String> findAllExpiredEntries(Account actor, Account owner);

	AnonymousUrl find(Account actor, Account owner, String uuid, String password);

	AnonymousUrl find(Account actor, Account owner, String uuid);

	AnonymousUrl delete(Account actor, Account owner, String uuid);

	InputStream retrieveArchiveZipStream(Account actor, Account owner, String anonymousUrlUuid, String password) throws BusinessException ;

	ByteSource downloadThumbnail(Account actor, Account owner, String anonymousUrlUuid, String anonymousShareEntryUuid, String password, ThumbnailType kind) throws BusinessException;

	SystemAccount getAnonymousURLAccount();

	AnonymousShareEntry getAnonymousShareEntry(Account actor, Account owner, String anonymousUrlUuid, String anonymousShareEntryUuid, String password);
}