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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.entities.MimeType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "MimeType")
@ApiModel(value = "MimeType", description = "MimeType")
public class MimeTypeDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "mimeType")
	private String mimeType;

	@ApiModelProperty(value = "Extensions")
	private String extensions;

	@ApiModelProperty(value = "Enable")
	private boolean enable;

	@ApiModelProperty(value = "Creation date")
	private Date creationDate;

	@ApiModelProperty(value = "Modification date")
	private Date modificationDate;

	public MimeTypeDto(final MimeType m) {
		this.uuid = m.getUuid();
		this.mimeType = m.getMimeType();
		this.extensions = m.getExtensions();
		this.enable = m.getEnable();
		this.creationDate = m.getCreationDate();
		this.modificationDate = m.getModificationDate();
	}

	public MimeTypeDto(final MimeType m, boolean light) {
		this.mimeType = m.getMimeType();
		this.extensions = m.getExtensions();
		this.enable = m.getEnable();
		if (!light) {
			this.uuid = m.getUuid();
			this.creationDate = m.getCreationDate();
			this.modificationDate = m.getModificationDate();
		}
	}

	public MimeTypeDto() {
		super();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
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

	public static Function<MimeType, MimeTypeDto> toDto() {
		return new Function<MimeType, MimeTypeDto>() {
			@Override
			public MimeTypeDto apply(MimeType arg0) {
				return new MimeTypeDto(arg0, true);
			}
		};
	}

	public static Predicate<MimeTypeDto> isMimeTypeDisabled(final boolean isDisabled) {
		return new Predicate<MimeTypeDto>() {
			@Override
			public boolean apply(MimeTypeDto arg0) {
				if (isDisabled) {
					return !arg0.isEnable();
				} else {
					return arg0.isEnable();
				}
			}
		};
	}
}
