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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;

import com.google.common.collect.Sets;

public class TechnicalAccountPermissionDto {

	private String uuid;

	private Date creationDate;

	private Date modificationDate;

	private Set<String> permissions = Sets.newHashSet();

	private Set<String> domains = Sets.newHashSet();

	public TechnicalAccountPermissionDto() {
		super();
	}

	public TechnicalAccountPermissionDto(TechnicalAccountPermission tap) {
		super();
		this.uuid = tap.getUuid();
		this.creationDate = tap.getCreationDate();
		this.modificationDate = tap.getModificationDate();
		for (AccountPermission accountPermission : tap.getAccountPermissions()) {
			permissions.add(accountPermission.getPermission().name());
		}
		for (AbstractDomain domain : tap.getDomains()) {
			domains.add(domain.getUuid());
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Set<String> getDomains() {
		return domains;
	}

	public void setDomains(Set<String> domains) {
		this.domains = domains;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
}
