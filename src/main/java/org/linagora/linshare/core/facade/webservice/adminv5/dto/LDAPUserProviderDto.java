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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "LDAPUserProvider", description = "A LDAP user provider")
public class LDAPUserProviderDto extends AbstractUserProviderDto {

	@Schema(description = "LdapServer lite dto used only as reference when creating providers", title = "LdapServer (nested)")
	public class LdapServerDto {

		@Schema(description = "LdapServer's uuid", required = true)
		protected String uuid;

		@Schema(description = "LdapServer's name", required = false)
		protected String name;

		public LdapServerDto() {
			super();
		}

		protected LdapServerDto(String uuid, String name) {
			super();
			this.uuid = uuid;
			this.name = name;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "LdapServer [uuid=" + uuid + ", name=" + name + "]";
		}
	}

	@Schema(description = "UserFilter lite dto used only as reference when creating providers", title = "UserFilter (nested)")
	public class UserFilterDto {

		@Schema(description = "UserFilter's uuid", required = true)
		protected String uuid;

		@Schema(description = "UserFilter's name", required = false)
		protected String name;

		public UserFilterDto() {
			super();
		}

		protected UserFilterDto(String uuid, String name) {
			super();
			this.uuid = uuid;
			this.name = name;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "UserFilterDto [uuid=" + uuid + ", name=" + name + "]";
		}
	}

	@Schema(required = true)
	private LdapServerDto ldapServer;

	@Schema(required = true)
	private UserFilterDto userFilter;

	@Schema(description = "BaseDn, starting point of the LDAP queries", required = true)
	private String baseDn;

	protected LDAPUserProviderDto() {
		super();
	}

	public LDAPUserProviderDto(AbstractDomain domain, LdapUserProvider up) {
		super(up);
		this.ldapServer = new LdapServerDto(
				up.getLdapConnection().getUuid(),
				up.getLdapConnection().getLabel());
		this.userFilter = new UserFilterDto(
				up.getPattern().getUuid(),
				up.getPattern().getLabel());
		this.baseDn = up.getBaseDn();
		this.type = UserProviderType.LDAP_PROVIDER;
	}


	public LdapServerDto getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(LdapServerDto ldapServer) {
		this.ldapServer = ldapServer;
	}

	public UserFilterDto getUserFilter() {
		return userFilter;
	}

	public void setUserFilter(UserFilterDto userFilter) {
		this.userFilter = userFilter;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	@Schema(defaultValue = "LDAP_PROVIDER")
	@Override
	public UserProviderType getType() {
		return UserProviderType.LDAP_PROVIDER;
	}

	@Override
	public String toString() {
		return "LDAPUserProvider [ldapServer=" + ldapServer + ", userFilter=" + userFilter + ", baseDn="
				+ baseDn + ", uuid=" + uuid + "]";
	}

}
