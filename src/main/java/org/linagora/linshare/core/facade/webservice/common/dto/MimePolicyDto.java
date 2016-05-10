/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "MimePolicy")
@ApiModel(value = "MimePolicy", description = "MimePolicy")
public class MimePolicyDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Name")
	private String name;

	@ApiModelProperty(value = "DomainId")
	private String domainId;

	@ApiModelProperty(value = "Creation date")
	private Date creationDate;

	@ApiModelProperty(value = "Modification date")
	private Date modificationDate;

	@ApiModelProperty(value = "Mime types")
	private Set<MimeTypeDto> mimeTypes;

	public MimePolicyDto(final MimePolicy m, boolean full) {
		this.uuid = m.getUuid();
		this.name = m.getName();
		this.creationDate = m.getCreationDate();
		this.modificationDate = m.getModificationDate();
		this.domainId = m.getDomain().getUuid();
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
}
