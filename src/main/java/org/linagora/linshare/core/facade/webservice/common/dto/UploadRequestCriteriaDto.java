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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "UploadRequestHistoryCriteria")
@Schema(name = "UploadRequestHistoryCriteria", description = "Criteria of an upload request history")
public class UploadRequestCriteriaDto {
	@Schema(description = "Status")
	private List<UploadRequestStatus> status = new ArrayList<UploadRequestStatus>();

	@Schema(description = "Min date limit")
	private Date afterDate;

	@Schema(description = "Max date limit")
	private Date beforeDate;

	public UploadRequestCriteriaDto() {
	}

	public List<UploadRequestStatus> getStatus() {
		return status;
	}

	public void setStatus(List<UploadRequestStatus> status) {
		this.status = status;
	}

	public Date getAfterDate() {
		return afterDate;
	}

	public void setAfterDate(Date afterDate) {
		this.afterDate = afterDate;
	}

	public Date getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(Date beforeDate) {
		this.beforeDate = beforeDate;
	}
}
