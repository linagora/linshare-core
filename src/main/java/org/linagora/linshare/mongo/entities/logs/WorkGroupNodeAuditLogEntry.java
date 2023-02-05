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
package org.linagora.linshare.mongo.entities.logs;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupLightDto;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.light.AuditDownloadLightEntity;
import org.linagora.linshare.mongo.entities.light.AuditLightEntity;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;


@XmlRootElement
public class WorkGroupNodeAuditLogEntry extends AuditLogEntryUser {

	protected WorkGroupLightDto workGroup;

	protected WorkGroupNode resource;

	// Content of folder at download time
	protected List<AuditDownloadLightEntity> auditDownloadLightEntities;

	// Light content of related resources with Name and Uuid
	protected List<AuditLightEntity> auditLightEntities;

	protected WorkGroupNode resourceUpdated;

	@JsonInclude(Include.NON_NULL)
	protected CopyMto copiedTo;

	@JsonInclude(Include.NON_NULL)
	protected CopyMto copiedFrom;

	public WorkGroupNodeAuditLogEntry() {
		super();
	}

	public WorkGroupNodeAuditLogEntry(Account authUser, Account actor, LogAction action, AuditLogEntryType type,
			WorkGroupNode node, WorkGroup workGroup) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, node.getUuid());
		this.addRelatedResources(workGroup.getLsUuid());
		this.resource = buildCopy(node);
		// used only to get the name of the workgroup
		this.workGroup = new WorkGroupLightDto(workGroup);
		// Related accounts or related domains are provided by an external method named businessService.addMembersToRelatedAccountsAndRelatedDomains()
		// Unfortunately it should been done by the constructor, but it was not done like that :'(
	}

	private WorkGroupNode buildCopy(WorkGroupNode node) {
		WorkGroupNode copy = null;
		try {
			copy = (WorkGroupNode) node.clone();
			copy.setLastAuthor(null);
		} catch (CloneNotSupportedException e) {
			// Should never happen
			e.printStackTrace();
			copy = node;
		}
		return copy;
	}

	public WorkGroupNode getResource() {
		return resource;
	}

	public void setResource(WorkGroupNode resource) {
		this.resource = resource;
	}

	public WorkGroupNode getResourceUpdated() {
		return resourceUpdated;
	}

	public void initResourceUpdated(WorkGroupNode resourceUpdated) {
		this.resourceUpdated = buildCopy(resourceUpdated);
	}

	public void setResourceUpdated(WorkGroupNode resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}

	public WorkGroupLightDto getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroupLightDto workGroup) {
		this.workGroup = workGroup;
	}

	public CopyMto getCopiedTo() {
		return copiedTo;
	}

	public void setCopiedTo(CopyMto copiedTo) {
		this.copiedTo = copiedTo;
	}

	public CopyMto getCopiedFrom() {
		return copiedFrom;
	}

	public void setCopiedFrom(CopyMto copiedFrom) {
		this.copiedFrom = copiedFrom;
	}

	public List<AuditDownloadLightEntity> getAuditDownloadLightEntities() {
		return auditDownloadLightEntities;
	}

	public List<AuditDownloadLightEntity> addAuditDownloadLightEntity(AuditDownloadLightEntity lightEntity) {
		if (this.auditDownloadLightEntities == null) {
			this.auditDownloadLightEntities = Lists.newArrayList();
		}
		auditDownloadLightEntities.add(lightEntity);
		return auditDownloadLightEntities;
	}

	public List<AuditDownloadLightEntity> addAuditDownloadLightEntities(List<AuditDownloadLightEntity> lightEntities) {
		if (this.auditDownloadLightEntities == null) {
			this.auditDownloadLightEntities = Lists.newArrayList();
		}
		auditDownloadLightEntities.addAll(lightEntities);
		return auditDownloadLightEntities;
	}

	public List<AuditLightEntity> getAuditLightEntities() {
		return auditLightEntities;
	}

	public List<AuditLightEntity> addAuditLightEntity(AuditLightEntity lightEntity) {
		if (this.auditLightEntities == null) {
			this.auditLightEntities = Lists.newArrayList();
		}
		auditLightEntities.add(lightEntity);
		return auditLightEntities;
	}

	public List<AuditLightEntity> addAuditLightEntities(List<AuditLightEntity> lightEntities) {
		if (this.auditLightEntities == null) {
			this.auditLightEntities = Lists.newArrayList();
		}
		auditLightEntities.addAll(lightEntities);
		return auditLightEntities;
	}

}