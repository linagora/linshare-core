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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
public class TechnicalAccountDto extends AccountDto {

	@Schema(description = "Password, only set at creation")
	private String password = "";

	@Schema(description = "Enable")
	private boolean enable;

	@Schema(description = "Name")
	private String name;

	@Schema(description = "Mail")
	private String mail;

	@Schema(description = "Permissions")
	private List<String> permissions = Lists.newArrayList();

	@Schema(description = "Role")
	private Role role;

	public TechnicalAccountDto() {
		super();
	}

	public TechnicalAccountDto(TechnicalAccount account) {
		super(account, false);
		this.name = account.getLastName();
		this.mail = account.getMail();
		this.role = account.getRole();
		this.enable = account.isEnable();
		this.creationDate = account.getCreationDate();
		this.modificationDate = account.getModificationDate();
		this.locked = account.isLocked();
		TechnicalAccountPermission permission = account.getPermission();
		if (permission != null) {
			for (AccountPermission p : permission.getAccountPermissions()) {
				permissions.add(p.getPermission().name());
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Helper
	 */
	public static TechnicalAccount toObject(TechnicalAccountDto dto) {
		TechnicalAccount object = new TechnicalAccount();
		object.setLastName(dto.getName());
		object.setLsUuid(dto.getUuid());
		object.setMail(dto.getMail());
		object.setEnable(dto.isEnable());
		List<String> permissions = dto.getPermissions();
		if (permissions != null) {
			object.setPermission(new TechnicalAccountPermission());
			for (String perm : permissions) {
				object.getPermission().addPermission(perm);
			}
		}
		return object;
	}
}
