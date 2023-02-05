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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.MimeType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "MimeType")
@Schema(name = "MimeType", description = "MimeType")
public class MimeTypeDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "mimeType")
	private String mimeType;

	@Schema(description = "Extensions")
	private String extensions;

	@Schema(description = "Enable")
	private boolean enable;

	@Schema(description = "Creation date")
	private Date creationDate;

	@Schema(description = "Modification date")
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
