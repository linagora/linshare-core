/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "Domain")
@Schema(name = "DomainV5", description = "A LinShare's domain")
public class DomainDto {

	@Schema(description = "Unique identifier of the resource")
	private String uuid;

	@Schema(description = "A common name, used to easily identify the resource.")
	private String name;

	@Schema(description = "An optional description of the domain. (only revealed if query param detail is true)")
	private String description;

	@Schema(description = "The kind of this domain. There is only four kinds: ROOT, TOP, SUB and GUEST domains")
	private DomainType type;

	@Schema(description = "creation date of this resource")
	private Date creationDate;

	@Schema(description = "modification date of this resource")
	private Date modificationDate;

	@Schema(description = "Every new users created in this domain will use this default as email language preference. "
			+ "(only revealed if query param detail is true)")
	private Language defaultEmailLanguage;

	@Schema(description = "Role used by default when creating an user. (only revealed if query param detail is true)")
	private Role defaultUserRole;

	@Schema(description = "Children domains. (only revealed if query param tree is true)")
	private List<DomainDto> children;

	@Schema(description = "Parent domain. (only revealed if query param tree is true)")
	private DomainDto parent;

	private DomainDto() {
		super();
	}

	public static DomainDto getUltraLight(final AbstractDomain domain) {
		DomainDto dto = new DomainDto();
		dto.setUuid(domain.getUuid());
		dto.setName(domain.getLabel());
		return dto;
	}

	public static DomainDto getLight(final AbstractDomain domain) {
		DomainDto dto = new DomainDto();
		dto.setUuid(domain.getUuid());
		dto.setName(domain.getLabel());
		dto.setType(domain.getDomainType());
		dto.setCreationDate(domain.getCreationDate());
		dto.setModificationDate(domain.getModificationDate());
		return dto;
	}

	public static DomainDto getTree(final AbstractDomain domain) {
		DomainDto dto = new DomainDto();
		dto.setUuid(domain.getUuid());
		dto.setName(domain.getLabel());
		dto.setType(domain.getDomainType());
		return dto;
	}

	public static DomainDto getTreeUp(final AbstractDomain domain) {
		DomainDto dto = new DomainDto();
		dto.setUuid(domain.getUuid());
		dto.setName(domain.getLabel());
		dto.setType(domain.getDomainType());
		if (domain.getParentDomain() != null) {
			dto.setParent(getTreeUp(domain.getParentDomain()));
		}
		return dto;
	}

	public static DomainDto getFull(final AbstractDomain domain) {
		DomainDto dto = new DomainDto();
		dto.setUuid(domain.getUuid());
		dto.setName(domain.getLabel());
		dto.setDescription(domain.getDescription());
		dto.setType(domain.getDomainType());
		dto.setCreationDate(domain.getCreationDate());
		dto.setModificationDate(domain.getModificationDate());
		dto.setDefaultEmailLanguage(domain.getExternalMailLocale());
		dto.setDefaultUserRole(domain.getDefaultRole());
		return dto;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Language getDefaultEmailLanguage() {
		return defaultEmailLanguage;
	}

	public void setDefaultEmailLanguage(Language defaultEmailLanguage) {
		this.defaultEmailLanguage = defaultEmailLanguage;
	}

	public Role getDefaultUserRole() {
		return defaultUserRole;
	}

	public void setDefaultUserRole(Role defaultUserRole) {
		this.defaultUserRole = defaultUserRole;
	}

	public List<DomainDto> getChildren() {
		return children;
	}

	public void setChildren(List<DomainDto> children) {
		this.children = children;
	}

	public DomainDto getParent() {
		return parent;
	}

	public void setParent(DomainDto parent) {
		this.parent = parent;
	}

	public DomainType getType() {
		return type;
	}

	public void setType(DomainType type) {
		this.type = type;
	}

	public void addChild(DomainDto child) {
		if (this.children == null) {
			this.children = Lists.newArrayList();
		}
		this.children.add(child);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainDto other = (DomainDto) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
