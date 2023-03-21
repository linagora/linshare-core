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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;

import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MimePolicy")
@Schema(name = "MimePolicy", description = "MimePolicy")
public class MimePolicyDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Name")
	private String name;

	@Schema(description = "DomainId")
	private String domainId;

	@Schema(description = "DomainName")
	private String domainName;

	@Schema(description = "Creation date")
	private Date creationDate;

	@Schema(description = "Modification date")
	private Date modificationDate;

	@Schema(description = "Mime types")
	private Set<MimeTypeDto> mimeTypes;

	public MimePolicyDto(final MimePolicy m, boolean full) {
		this.uuid = m.getUuid();
		this.name = m.getName();
		this.creationDate = m.getCreationDate();
		this.modificationDate = m.getModificationDate();
		this.domainId = m.getDomain().getUuid();
		this.domainName = m.getDomain().getLabel();
		if (full) {
			mimeTypes = Sets.newHashSet();
			for (MimeType mimeType : m.getMimeTypes()) {
				mimeTypes.add(new MimeTypeDto(mimeType));
			}
		}
	}

	public MimePolicyDto(final MimePolicy m) {
		this(m, false);
	}

	public MimePolicyDto() {
		super();
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

	public Set<MimeTypeDto> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(Set<MimeTypeDto> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
