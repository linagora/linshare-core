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

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface WorkGroupNodeAbstractService {

	WorkGroupNode find(Account actor, User owner, WorkGroup workGroup, String workGroupNodeUuid)
			throws BusinessException;

	String getNewName(Account actor, User owner, WorkGroup workGroup, WorkGroupNode nodeParent, String currentName);

	void checkUniqueName(WorkGroup workGroup, WorkGroupNode nodeParent, String name);

	boolean isUniqueName(WorkGroup workGroup, WorkGroupNode nodeParent, String name);

	String computeFileName(WorkGroupDocument document, WorkGroupDocumentRevision revision, boolean isDocument);

	void addMembersToLog(String workGroupUuid, AuditLogEntryUser log);
}
