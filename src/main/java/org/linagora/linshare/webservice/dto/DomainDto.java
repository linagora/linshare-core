/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.webservice.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.linagora.linshare.core.domain.entities.AbstractDomain;

@JsonIgnoreProperties({"route"})
@XmlRootElement(name = "Domain")
public class DomainDto {

	private String identifier;
	private String label;
	private String description = "";
	private String type;
	private String userRole;
	private String locale;
	private DomainPolicyDto policy;
	private List<LDAPUserProviderDto> providers = new ArrayList<LDAPUserProviderDto>();
	private List<DomainDto> children = new ArrayList<DomainDto>();
	private String parent = "";

	public DomainDto(final AbstractDomain domain) {
		this.identifier = domain.getIdentifier();
		this.label = domain.getLabel();
		this.description = domain.getDescription();
		this.locale = domain.getDefaultLocale();
		this.type = domain.getDomainType().toString();
		this.userRole = domain.getDefaultRole().toString();
		this.policy = new DomainPolicyDto(domain.getPolicy());
		if (domain.getUserProvider() != null) {
			this.providers
					.add(new LDAPUserProviderDto(domain.getUserProvider()));
		}
		for (AbstractDomain child : domain.getSubdomain()) {
			DomainDto childDto = new DomainDto(child);
			this.children.add(childDto);
			childDto.parent = this.identifier;
		}
	}

	public DomainDto() {
		super();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public DomainPolicyDto getPolicy() {
		return policy;
	}

	public void setPolicy(DomainPolicyDto policy) {
		this.policy = policy;
	}

	public List<LDAPUserProviderDto> getProviders() {
		return providers;
	}

	public void setProviders(List<LDAPUserProviderDto> providers) {
		this.providers = providers;
	}

	public List<DomainDto> getChildren() {
		return children;
	}

	public void setChildren(List<DomainDto> children) {
		this.children = children;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
}
