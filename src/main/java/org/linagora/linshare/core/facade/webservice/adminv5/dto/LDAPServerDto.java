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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.LdapConnection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LdapServer")
@Schema(name = "LdapServer", description = "A LDAP server connection")
public class LDAPServerDto extends AbstractServerDto {

	@Schema(description = "Ldap server's bindDn", required = false)
	private String bindDn;

	@Schema(description = "Ldap server's password", required = false)
	private String bindPassword;

	protected LDAPServerDto() {
		super();
	}

	public LDAPServerDto(LdapConnection ldapConnection) {
		this.uuid = ldapConnection.getUuid();
		this.name = ldapConnection.getLabel();
		this.url = ldapConnection.getProviderUrl();
		this.bindDn = ldapConnection.getSecurityPrincipal();
		this.bindPassword = ldapConnection.getSecurityCredentials();
		this.serverType = ldapConnection.getServerType();
		this.creationDate = ldapConnection.getCreationDate();
		this.modificationDate = ldapConnection.getModificationDate();
	}

	public LdapConnection toLdapServerObject() {
		LdapConnection connection = new LdapConnection();
		connection.setUuid(getUuid());
		connection.setLabel(getName());
		connection.setProviderUrl(getUrl());
		connection.setSecurityPrincipal(getBindDn());
		connection.setSecurityCredentials(getBindPassword());
		connection.setServerType(getServerType());
		connection.setCreationDate(getCreationDate());
		connection.setModificationDate(getModificationDate());
		return connection;
	}

	public String getBindDn() {
		return bindDn;
	}

	public void setBindDn(String bindDn) {
		this.bindDn = bindDn;
	}

	public String getBindPassword() {
		return bindPassword;
	}

	public void setBindPassword(String bindPassword) {
		this.bindPassword = bindPassword;
	}

	/*
	 * Transformers
	 */
	public static Function<LdapConnection, LDAPServerDto> toDto() {
		return new Function<LdapConnection, LDAPServerDto>() {
			@Override
			public LDAPServerDto apply(LdapConnection arg0) {
				return new LDAPServerDto(arg0);
			}
		};
	}
}
