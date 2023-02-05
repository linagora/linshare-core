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

import org.linagora.linshare.core.domain.constants.TargetKind;

import io.swagger.v3.oas.annotations.media.Schema;

public class CopyDto {

	@Schema(description = "The resource's uuid")
	protected String uuid;

	@Schema(description = "The source kind of where the document is copied from (SharedSpace, personalSpace ..)")
	protected TargetKind kind;

	@Schema(description = "contextUuid | if TargetKind = UPLOAD_REQUEST it's upload request uuid , if TargetKind = SHARED_SPACE it's shared space uuid ")
	protected String contextUuid;

	public CopyDto() {
		super();
	}

	public TargetKind getKind() {
		return kind;
	}

	public void setKind(TargetKind kind) {
		this.kind = kind;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContextUuid() {
		return contextUuid;
	}

	public void setContextUuid(String contextUuid) {
		this.contextUuid = contextUuid;
	}

}
