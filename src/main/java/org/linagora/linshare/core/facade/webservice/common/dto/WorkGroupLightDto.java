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

import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

import io.swagger.v3.oas.annotations.media.Schema;

public class WorkGroupLightDto {

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "Name")
	protected String name;

	public WorkGroupLightDto() {
	}

	public WorkGroupLightDto(String uuid, String name) {
		super();
		this.uuid = uuid;
		this.name = name;
	}

	public WorkGroupLightDto(WorkGroup thread) {
		super();
		this.uuid = thread.getLsUuid();
		this.name = thread.getName();
		this.creationDate = thread.getCreationDate();
	}

	public WorkGroupLightDto(SharedSpaceNodeNested node) {
		super();
		this.uuid = node.getUuid();
		this.name = node.getName();
		this.creationDate = node.getCreationDate();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
