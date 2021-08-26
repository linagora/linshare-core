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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LDAPUserProvider")
@Schema(name = "LDAPUserProvider", description = "A LDAP user provider")
public class LDAPUserProviderDto extends AbstractUserProviderDto {

	@Schema(description = "LdapServer lite dto used only as reference when creating providers", title = "LdapServer (nested)")
	public class LdapServerDto {

		@Schema(description = "LdapServer's uuid", required = true)
		protected String uuid;

		@Schema(description = "LdapServer's name", required = false)
		protected String name;

		protected LdapServerDto() {
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

		protected UserFilterDto() {
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
	@XmlElement(name = "UserFilter")
	private UserFilterDto userFilter;

	@Schema(description = "BaseDn, starting point of the LDAP queries", required = true)
	private String baseDn;

	protected LDAPUserProviderDto() {
		super();
	}

	public LDAPUserProviderDto(AbstractDomain domain, LdapUserProvider up) {
		super();
		this.uuid = up.getUuid();
		this.creationDate = up.getCreationDate();
		this.modificationDate = up.getModificationDate();
		this.ldapServer = new LdapServerDto(
				up.getLdapConnection().getUuid(),
				up.getLdapConnection().getLabel());
		this.userFilter = new UserFilterDto(
				up.getPattern().getUuid(),
				up.getPattern().getLabel());
		this.baseDn = up.getBaseDn();
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
