/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.linagora.linshare.core.domain.constants.WorkSpaceProviderType;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "LDAPDriveProvider", description = "A LDAP drive provider")
public class LDAPDriveProviderDto extends AbstractDriveProviderDto {

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

	@Schema(description = "DriveFilter lite dto used only as reference when creating providers", title = "DriveFilter (nested)")
	public class DriveFilterDto {

		@Schema(description = "DriveFilter's uuid", required = true)
		protected String uuid;

		@Schema(description = "DriveFilter's name", required = false)
		protected String name;

		public DriveFilterDto() {
			super();
		}

		protected DriveFilterDto(String uuid, String name) {
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
			return "DriveFilterDto [uuid=" + uuid + ", name=" + name + "]";
		}
	}

	@Schema(description = "It indicates the related ldap server to this drive provider.", required = true)
	protected LdapServerDto ldapServer;

	@Schema(description = "It indicates the related drive filter to this drive provider.", required = true)
	protected DriveFilterDto driveFilter;

	@Schema(description = "BaseDn, starting point of the LDAP queries", required = true)
	protected String baseDn;

	@Schema(description = "If true it will search in other LDAP domains.", required = true)
	protected Boolean searchInOtherDomains;

	public LDAPDriveProviderDto() {
		super();
	}

	public LDAPDriveProviderDto(LdapWorkSpaceProvider driveProvider) {
		super(driveProvider);
		this.ldapServer = new LdapServerDto(driveProvider.getLdapConnection().getUuid(),
				driveProvider.getLdapConnection().getClass().getName());
		this.driveFilter = new DriveFilterDto(driveProvider.getDriveFilter().getUuid(),
				driveProvider.getDriveFilter().getLabel());
		this.baseDn = driveProvider.getBaseDn();
		this.searchInOtherDomains = driveProvider.getSearchInOtherDomains();
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

	public DriveFilterDto getDriveFilter() {
		return driveFilter;
	}

	public void setDriveFilter(DriveFilterDto driveFilter) {
		this.driveFilter = driveFilter;
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
	public static Function<LdapWorkSpaceProvider, LDAPDriveProviderDto> toDto() {
		return new Function<LdapWorkSpaceProvider, LDAPDriveProviderDto>() {
			@Override
			public LDAPDriveProviderDto apply(LdapWorkSpaceProvider driveProvider) {
				return new LDAPDriveProviderDto(driveProvider);
			}
		};
	}

	@Override
	public String toString() {
		return "LDAPDriveProviderDto [uuid=" + uuid + ", domain=" + domain + ", type=" + type + "]";
	}
}
