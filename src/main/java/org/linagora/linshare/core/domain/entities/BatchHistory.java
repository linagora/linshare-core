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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.BatchType;

public class BatchHistory {

	private long id;

	private String uuid;

	private Date executionDate;

	private Date activeDate;

	private String status;

	private BatchType batchType;

	private Long errors;

	private Long unhandledErrors;

	public BatchHistory() {
	}

	public BatchHistory(BatchType batchType) {
		this.batchType = batchType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public BatchType getBatchType() {
		return batchType;
	}

	public void setBatchType(BatchType batchType) {
		this.batchType = batchType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public Long getErrors() {
		return errors;
	}

	public void setErrors(Long errors) {
		this.errors = errors;
	}

	public Long getUnhandledErrors() {
		return unhandledErrors;
	}

	public void setUnhandledErrors(Long unhandledErrors) {
		this.unhandledErrors = unhandledErrors;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	@Override
	public String toString() {
		return "BatchHistory [id=" + id + ", uuid=" + uuid + ", executionDate=" + executionDate + ", activeDate="
				+ activeDate + ", status=" + status + ", batchType=" + batchType + ", errors=" + errors
				+ ", unhandledErrors=" + unhandledErrors + "]";
	}
}
