package org.linagora.linshare.webservice.dto;

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
			domains.add(domain.getIdentifier());
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
