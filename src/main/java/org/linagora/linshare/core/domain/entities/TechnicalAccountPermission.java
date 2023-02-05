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
import java.util.Set;

import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountPermissionDto;

import com.google.common.collect.Sets;

public class TechnicalAccountPermission {

	private long id;

	private String uuid;

	private Date creationDate;

	private Date modificationDate;

	private Set<AccountPermission> accountPermissions = Sets.newHashSet();

	private Set<AbstractDomain> domains = Sets.newHashSet();

	public TechnicalAccountPermission() {
		super();
	}

	public TechnicalAccountPermission(TechnicalAccountPermissionDto dto) {
		super();
		this.uuid = dto.getUuid();
		Set<String> permissions = dto.getPermissions();

		for (String perm : permissions) {
			accountPermissions.add(new AccountPermission(perm));
		}

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public java.util.Set<AbstractDomain> getDomains() {
		return domains;
	}

	public Set<AbstractDomain> addDomain(AbstractDomain domain) {
		domains.add(domain);
		return domains;
	}

	public void setDomains(Set<AbstractDomain> domains) {
		this.domains = domains;
	}

	public Set<AccountPermission> getAccountPermissions() {
		return accountPermissions;
	}

	public void setAccountPermissions(Set<AccountPermission> accountPermissions) {
		this.accountPermissions = accountPermissions;
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

	/*
	 * Helpers
	 */

	public void addPermission(AccountPermission permission) {
		this.accountPermissions.add(permission);
	}
	public void addPermission(String permission) {
		this.accountPermissions.add(new AccountPermission(permission));
	}

	@Override
	public String toString() {
		return "TechnicalAccountPermission [id=" + id + ", uuid=" + uuid + "]";
	}
}
