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
package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;

public interface AnonymousUrlBusinessService {

	AnonymousUrl find(String uuid);

	AnonymousUrl create(Boolean passwordProtected, Contact contact)
			throws BusinessException;

	void update(AnonymousUrl anonymousUrl) throws BusinessException;

	boolean isValidPassword(AnonymousUrl anonymousUrl, String password);

	boolean isExpired(AnonymousUrl anonymousUrl);

	SystemAccount getAnonymousURLAccount();

	List<String> findAllExpiredEntries();

	void delete(AnonymousUrl anonymousUrl);

}
