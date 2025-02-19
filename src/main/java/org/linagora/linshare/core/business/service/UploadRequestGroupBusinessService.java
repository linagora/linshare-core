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

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BusinessException;

import javax.annotation.Nonnull;

public interface UploadRequestGroupBusinessService {

	List<UploadRequestGroup> findAll(Account owner, List<UploadRequestStatus> uploadRequestStatus);

	UploadRequestGroup findByUuid(String uuid);

	UploadRequestGroup create(UploadRequestGroup group)
			throws BusinessException;

	UploadRequestGroup update(UploadRequestGroup group)
			throws BusinessException;

	void delete(UploadRequestGroup group) throws BusinessException;

	UploadRequestGroup updateStatus(UploadRequestGroup uploadRequestGroup, UploadRequestStatus status);

	List<String> findOutdatedRequests();

	Integer countNbrUploadedFiles(UploadRequestGroup uploadRequestGroup);

	Long computeEntriesSize(UploadRequestGroup uploadRequestGroup);

	void transferUploadRequestGroupsFromGuestToInternal(@Nonnull final Account guest, @Nonnull final Account owner);
}
