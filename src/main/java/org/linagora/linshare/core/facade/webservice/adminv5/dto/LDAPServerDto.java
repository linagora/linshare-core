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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.domain.entities.LdapConnection;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@JsonDeserialize(builder = LDAPServerDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LdapServer")
@Schema(name = "LdapServer", description = "A LDAP server connection")
public class LDAPServerDto extends AbstractServerDto {

	public static LDAPServerDto from(LdapConnection ldapConnection) {
		return builder()
			.bindDn(ldapConnection.getSecurityPrincipal())
			.bindPassword(ldapConnection.getSecurityCredentials())
			.uuid(ldapConnection.getUuid())
			.name(ldapConnection.getLabel())
			.url(ldapConnection.getProviderUrl())
			.serverType(ldapConnection.getType())
			.creationDate(ldapConnection.getCreationDate())
			.modificationDate(ldapConnection.getModificationDate())
			.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(LDAPServerDto ldapServerDto) {
		return (Builder) new Builder()
			.bindDn(ldapServerDto.getBindDn())
			.bindPassword(ldapServerDto.getBindPassword())
			.uuid(ldapServerDto.getUuid())
			.name(ldapServerDto.getName())
			.url(ldapServerDto.getUrl())
			.serverType(ldapServerDto.getServerType())
			.creationDate(ldapServerDto.getCreationDate())
			.modificationDate(ldapServerDto.getModificationDate());
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends AbstractServerDtoBuilder<LDAPServerDto> {
		private String bindDn;
		private String bindPassword;

		public Builder bindDn(String bindDn) {
			this.bindDn = bindDn;
			return this;
		}

		public Builder bindPassword(String bindPassword) {
			this.bindPassword = bindPassword;
			return this;
		}

		@Override
		public LDAPServerDto build() {
			validation();
			return new LDAPServerDto(bindDn, bindPassword, uuid, name, description, url, serverType, creationDate, modificationDate);
		}
	}

	@Schema(description = "Ldap server's bindDn", required = false)
	private final String bindDn;

	@Schema(description = "Ldap server's password", required = false)
	private final String bindPassword;

	private LDAPServerDto(String bindDn, String bindPassword, String uuid, String name, String description, String url, ServerType serverType, Date creationDate, Date modificationDate) {
		super(uuid, name, description, url, serverType, creationDate, modificationDate);
		this.bindDn = bindDn;
		this.bindPassword = bindPassword;
	}

	public String getBindDn() {
		return bindDn;
	}

	public String getBindPassword() {
		return bindPassword;
	}

	public LdapConnection toLdapServerObject() {
		LdapConnection connection = new LdapConnection();
		connection.setUuid(getUuid());
		connection.setLabel(getName());
		connection.setProviderUrl(getUrl());
		connection.setSecurityPrincipal(getBindDn());
		connection.setSecurityCredentials(getBindPassword());
		connection.setCreationDate(getCreationDate());
		connection.setModificationDate(getModificationDate());
		return connection;
	}
}
