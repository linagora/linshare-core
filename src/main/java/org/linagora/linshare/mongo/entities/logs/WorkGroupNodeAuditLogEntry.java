/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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