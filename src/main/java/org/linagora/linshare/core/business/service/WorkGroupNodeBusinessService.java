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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.fields.DocumentKind;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceNodeField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.utils.DocumentCount;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface WorkGroupNodeBusinessService {

	List<DocumentCount> findTotalOccurenceOfMimeTypeByDomain(List<String> workgroupsByDomains, Date bDate, Date eDate);

	Long computeNodeSize(WorkGroup workGroup, String pattern, WorkGroupNodeType type);

	Long computeNodeCount(WorkGroup workGroup, String pattern, WorkGroupNode node);

	Map<String, WorkGroupNode> findAllSubNodes(WorkGroup workGroup, String pattern);

	FileAndMetaData downloadFolder(Account actor, User owner, WorkGroup workGroup, WorkGroupNode rootNode,
			Map<String, WorkGroupNode> map, List<WorkGroupNode> documentNodes, WorkGroupNodeAuditLogEntry log);

	List<WorkGroupNode> findAllSubDocuments(WorkGroup workGroup, String pattern);

	FileAndMetaData downloadArchiveRevision(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode rootNode,
			List<WorkGroupNode> documentNodes, WorkGroupNodeAuditLogEntry log);

	PageContainer<WorkGroupNode> findAll(WorkGroup workGroup, String parentUuid, String pattern,
			boolean caseSensitive, PageContainer<WorkGroupNode> pageContainer, Date creationDateAfter,
			Date creationDateBefore, Date modificationDateAfter, Date modificationDateBefore, List<WorkGroupNodeType> types,
			List<String> lastAuthors, Long minSize, Long maxSize, SortOrder sortOrder, SharedSpaceNodeField sortField,
			List<DocumentKind> documentKinds);

	void updateRelatedWorkGroupNodeResources(WorkGroupNode workGroupNode, Date dateNow);

	Long computeAllWorkgroupNodesSize(String workGroupUuid);

}
