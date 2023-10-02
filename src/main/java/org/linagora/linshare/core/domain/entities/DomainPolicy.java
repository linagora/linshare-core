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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;

public class DomainPolicy {

	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	private String uuid;

	private String label;

	private DomainAccessPolicy domainAccessPolicy;

	private String description;

	private Date creationDate;

	private Date modificationDate;

	public DomainPolicy() {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.domainAccessPolicy = new DomainAccessPolicy();
	}

	public DomainPolicy(DomainPolicyDto dto) {
		super();
		this.uuid = dto.getIdentifier();
		this.label = dto.getLabel();
		this.description = dto.getDescription();
		this.domainAccessPolicy = new DomainAccessPolicy();
	}

	public DomainPolicy(String label) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.label = label;
		this.domainAccessPolicy = new DomainAccessPolicy();
	}

	public DomainPolicy(String label, DomainAccessPolicy policy) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.label = label;
		this.domainAccessPolicy = policy;
	}

	public DomainPolicy(String label, String description,
			DomainAccessPolicy domainAccessPolicy) {
		this.uuid = UUID.randomUUID().toString();
		this.label = label;
		this.description = description;
		this.domainAccessPolicy = domainAccessPolicy;
	}

	public DomainPolicy(String label, String description) {
		this.uuid = UUID.randomUUID().toString();
		this.label = label;
		this.description = description;
		this.domainAccessPolicy = new DomainAccessPolicy();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomainAccessPolicy getDomainAccessPolicy() {
		return domainAccessPolicy;
	}

	public void setDomainAccessPolicy(DomainAccessPolicy domainAccessPolicy) {
		this.domainAccessPolicy = domainAccessPolicy;
	}

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "DomainPolicy [uuid=" + uuid + ", label=" + label + "]";
	}

}
