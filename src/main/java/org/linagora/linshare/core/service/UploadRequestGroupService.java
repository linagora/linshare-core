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

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.utils.FileAndMetaData;

public interface UploadRequestGroupService {

	List<UploadRequestGroup> findAll(Account actor, Account owner, List<UploadRequestStatus> statusList)
			throws BusinessException;

	UploadRequestGroup find(Account actor, Account owner, String uuid);

	UploadRequestGroup create(Account actor, User owner, UploadRequest req, List<Contact> contacts,
			String subject, String body, Boolean collectiveMode) throws BusinessException;

	UploadRequestGroup update(User authUser, User actor, UploadRequestGroup uploadRequestGroup, Boolean force);

	List<String> findOutdatedRequestsGroup(SystemAccount account);

	UploadRequestGroup updateStatus(Account actor, Account owner, String requestGroupUuid, UploadRequestStatus stat, boolean copy);

	UploadRequestGroup addNewRecipients(User authUser, User actor, UploadRequestGroup uploadRequestGroup,
			List<ContactDto> recipientEmail);

	FileAndMetaData downloadEntries(Account authUser, Account actor, UploadRequestGroup uploadRequestGroup, String requestUuid);

	Integer countNbrUploadedFiles(UploadRequestGroup uploadRequestGroup);

	Long computeEntriesSize(UploadRequestGroup uploadRequestGroup);

	void transferUploadRequestGroupsFromGuestToInternal(final Account guest, final Account owner);
}
