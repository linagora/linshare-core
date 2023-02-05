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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "UpdateUsersEmailState")
@Schema(name = "UpdateUsersEmailState", description = "")
public class UpdateUsersEmailStateDto {

	@Schema(description = "Total")
	private long total;

	@Schema(description = "Updated")
	private long updated;

	@Schema(description = "NotUpdated")
	private long notUpdated;

	@Schema(description = "Skipped")
	private long skipped;

	public UpdateUsersEmailStateDto() {
		super();
	}

	public UpdateUsersEmailStateDto(long total, long updated, long notUpdated, long skipped) {
		super();
		this.total = total;
		this.updated = updated;
		this.notUpdated = notUpdated;
		this.skipped = skipped;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public long getNotUpdated() {
		return notUpdated;
	}

	public void setNotUpdated(long notUpdated) {
		this.notUpdated = notUpdated;
	}

	public long getSkipped() {
		return skipped;
	}

	public void setSkipped(long skipped) {
		this.skipped = skipped;
	}
}
