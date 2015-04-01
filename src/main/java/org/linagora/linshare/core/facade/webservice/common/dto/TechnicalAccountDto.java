/*
 * LinShbjectare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.List;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class TechnicalAccountDto extends AccountDto {

	@ApiModelProperty(value = "Password, only set at creation")
	private String password = "";

	@ApiModelProperty(value = "Enable")
	private boolean enable;

	@ApiModelProperty(value = "Name")
	private String name;

	@ApiModelProperty(value = "Mail")
	private String mail;

	@ApiModelProperty(value = "Permissions")
	private List<String> permissions = Lists.newArrayList();

	@ApiModelProperty(value = "Role")
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
