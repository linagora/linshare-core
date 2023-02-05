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

import java.io.File;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDocumentBusinessService;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public interface DocumentEntryRevisionBusinessService extends AbstractDocumentBusinessService {

	WorkGroupDocumentRevision createWorkGroupDocumentRevision(Account actor, WorkGroup workGroup, File myFile,
			Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType,
			WorkGroupNode nodeParent) throws BusinessException;

	WorkGroupNode findMostRecent(WorkGroup workGroup, String parentUuid);

}
