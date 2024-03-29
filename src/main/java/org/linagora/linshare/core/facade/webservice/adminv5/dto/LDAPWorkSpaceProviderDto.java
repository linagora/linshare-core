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

import org.linagora.linshare.core.domain.constants.WorkSpaceProviderType;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "LDAPWorkSpaceProvider", description = "A LDAP workSpace provider")
public class LDAPWorkSpaceProviderDto extends AbstractWorkSpaceProviderDto {

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

	@Schema(description = "WorkSpaceFilter lite dto used only as reference when creating providers", title = "WorkSpaceFilter (nested)")
	public class WorkSpaceFilterDto {

		@Schema(description = "WorkSpaceFilter's uuid", required = true)
		protected String uuid;

		@Schema(description = "WorkSpaceFilter's name", required = false)
		protected String name;

		public WorkSpaceFilterDto() {
			super();
		}

		protected WorkSpaceFilterDto(String uuid, String name) {
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
			return "WorkSpaceFilterDto [uuid=" + uuid + ", name=" + name + "]";
		}
	}

	@Schema(description = "It indicates the related ldap server to this workSpace provider.", required = true)
	protected LdapServerDto ldapServer;

	@Schema(description = "It indicates the related workSpace filter to this workSpace provider.", required = true)
	protected WorkSpaceFilterDto workSpaceFilter;

	@Schema(description = "BaseDn, starting point of the LDAP queries", required = true)
	protected String baseDn;

	@Schema(description = "If true it will search in other LDAP domains.", required = true)
	protected Boolean searchInOtherDomains;

	public LDAPWorkSpaceProviderDto() {
		super();
	}

	public LDAPWorkSpaceProviderDto(LdapWorkSpaceProvider workSpaceProvider) {
		super(workSpaceProvider);
		this.ldapServer = new LdapServerDto(workSpaceProvider.getLdapConnection().getUuid(),
				workSpaceProvider.getLdapConnection().getClass().getName());
		this.workSpaceFilter = new WorkSpaceFilterDto(workSpaceProvider.getWorkSpaceFilter().getUuid(),
				workSpaceProvider.getWorkSpaceFilter().getLabel());
		this.baseDn = workSpaceProvider.getBaseDn();
		this.searchInOtherDomains = workSpaceProvider.getSearchInOtherDomains();
	}

	@Schema(defaultValue = "LDAP_PROVIDER")
	@Override
	public WorkSpaceProviderType getType() {
		return WorkSpaceProviderType.LDAP_PROVIDER;
	}

	public LdapServerDto getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(LdapServerDto ldapServer) {
		this.ldapServer = ldapServer;
	}

	public WorkSpaceFilterDto getWorkSpaceFilter() {
		return workSpaceFilter;
	}

	public void setWorkSpaceFilter(WorkSpaceFilterDto workSpaceFilter) {
		this.workSpaceFilter = workSpaceFilter;
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
	public static Function<LdapWorkSpaceProvider, LDAPWorkSpaceProviderDto> toDto() {
		return new Function<LdapWorkSpaceProvider, LDAPWorkSpaceProviderDto>() {
			@Override
			public LDAPWorkSpaceProviderDto apply(LdapWorkSpaceProvider workSpaceProvider) {
				return new LDAPWorkSpaceProviderDto(workSpaceProvider);
			}
		};
	}

	@Override
	public String toString() {
		return "LDAPWorkSpaceProviderDto [uuid=" + uuid + ", domain=" + domain + ", type=" + type + "]";
	}
}
