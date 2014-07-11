package org.linagora.linshare.webservice.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "UploadRequestHistoryCriteria")
@ApiModel(value = "UploadRequestHistoryCriteria", description = "Criteria of an upload request history")
public class UploadRequestCriteriaDto {
	@ApiModelProperty(value = "Status")
	private List<UploadRequestStatus> status = new ArrayList<UploadRequestStatus>();

	@ApiModelProperty(value = "Min date limit")
	private Date afterDate;

	@ApiModelProperty(value = "Max date limit")
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
