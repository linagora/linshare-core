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

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AsyncTask;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.exception.BusinessException;

public interface AsyncTaskService {

	AsyncTask find(Account actor, Account owner, String uuid, boolean retry);

	void delete(Account actor, Account owner, String uuid)
			throws BusinessException;

	AsyncTask create(Account actor, Account owner, AsyncTask task)
			throws BusinessException;

	AsyncTask processing(Account actor, Account owner, String asyncTaskUuid);

	AsyncTask success(Account actor, Account owner, String asyncTaskUuid,
			String resourceUuid);

	AsyncTask fail(Account actor, Account owner, String asyncTaskUuid,
			String errorMsg);

	AsyncTask fail(Account actor, Account owner, String asyncTaskUuid,
			Integer errorCode, String errorName, String errorMsg);

	List<AsyncTask> findAll(Account actor, Account owner, UpgradeTask upgradeTask);
}
