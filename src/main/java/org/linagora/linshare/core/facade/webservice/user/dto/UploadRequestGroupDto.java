package org.linagora.linshare.core.facade.webservice.user.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "UploadRequestGroup")
public class UploadRequestGroupDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Subject")
	private String subject;

	@ApiModelProperty(value = "Body")
	private String body;

	@ApiModelProperty(value = "creationDate")
	private Date creationDate;

	@ApiModelProperty(value = "modificationDate")
	private Date modifiationDate;

	@ApiModelProperty(value = "UploadRequest")
	private List<UploadRequestDto> uploadRequestDtos = Lists.newArrayList();

	public UploadRequestGroupDto() {
	}

	public UploadRequestGroupDto(UploadRequestGroup group) {
		this.uuid = group.getUuid();
		this.subject = group.getSubject();
		this.body = group.getBody();
		this.creationDate = group.getCreationDate();
		this.modifiationDate = group.getModificationDate();
		this.uploadRequestDtos = ImmutableList.copyOf(Lists.transform(Lists.newArrayList(group.getUploadRequests()), UploadRequestDto.toDto(false)));
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModifiationDate() {
		return modifiationDate;
	}

	public void setModifiationDate(Date modifiationDate) {
		this.modifiationDate = modifiationDate;
	}

	public List<UploadRequestDto> getUploadrequestDtos() {
		return uploadRequestDtos;
	}

	public void setUploadrequestDtos(List<UploadRequestDto> uploadrequestDtos) {
		this.uploadRequestDtos = uploadrequestDtos;
	}

	/*
	 * Transformers
	 */
	public static Function<UploadRequestGroup, UploadRequestGroupDto> toDto() {
		return new Function<UploadRequestGroup, UploadRequestGroupDto>() {
			@Override
			public UploadRequestGroupDto apply(UploadRequestGroup arg0) {
				return new UploadRequestGroupDto(arg0);
			}
		};
	}
}
