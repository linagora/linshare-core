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

import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "LDAPGroupProvider", description = "A LDAP group provider")
public class LDAPGroupProviderDto extends AbstractGroupProviderDto {

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

	@Schema(description = "GroupFilter lite dto used only as reference when creating providers", title = "GroupFilter (nested)")
	public class GroupFilterDto {

		@Schema(description = "GroupFilter's uuid", required = true)
		protected String uuid;

		@Schema(description = "GroupFilter's name", required = false)
		protected String name;

		public GroupFilterDto() {
			super();
		}

		protected GroupFilterDto(String uuid, String name) {
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
			return "GroupFilterDto [uuid=" + uuid + ", name=" + name + "]";
		}
	}

	@Schema(description = "It indicates the related ldap server to this group provider.", required = true)
	protected LdapServerDto ldapServer;

	@Schema(description = "It indicates the related group filter to this group provider.", required = true)
	protected GroupFilterDto groupFilter;

	@Schema(description = "BaseDn, starting point of the LDAP queries", required = true)
	protected String baseDn;

	@Schema(description = "If true it will search in other LDAP domains.", required = true)
	protected Boolean searchInOtherDomains;

	public LDAPGroupProviderDto() {
		super();
	}

	public LDAPGroupProviderDto(LdapGroupProvider groupProvider) {
		super(groupProvider);
		this.ldapServer = new LdapServerDto(groupProvider.getLdapConnection().getUuid(),
				groupProvider.getLdapConnection().getClass().getName());
		this.groupFilter = new GroupFilterDto(groupProvider.getGroupPattern().getUuid(),
				groupProvider.getGroupPattern().getLabel());
		this.baseDn = groupProvider.getBaseDn();
		this.searchInOtherDomains = groupProvider.getSearchInOtherDomains();
	}

	@Schema(defaultValue = "LDAP_PROVIDER")
	@Override
	public GroupProviderType getType() {
		return GroupProviderType.LDAP_PROVIDER;
	}

	public LdapServerDto getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(LdapServerDto ldapServer) {
		this.ldapServer = ldapServer;
	}

	public GroupFilterDto getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(GroupFilterDto groupFilter) {
		this.groupFilter = groupFilter;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public Boolean getSearchInOtherDomains() {
		return searchInOtherDomains;
	}

	public void setSearchInOtherDomains(Boolean searchInOtherDomains) {
		this.searchInOtherDomains = searchInOtherDomains;
	}

	/*
	 * Transformers
	 */
	public static Function<LdapGroupProvider, LDAPGroupProviderDto> toDto() {
		return new Function<LdapGroupProvider, LDAPGroupProviderDto>() {
			@Override
			public LDAPGroupProviderDto apply(LdapGroupProvider groupProvider) {
				return new LDAPGroupProviderDto(groupProvider);
			}
		};
	}

	@Override
	public String toString() {
		return "LDAPGroupProviderDto [uuid=" + uuid + ", domain=" + domain + ", type=" + type + "]";
	}
}
