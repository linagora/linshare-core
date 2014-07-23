package org.linagora.linshare.webservice.delegation.dto;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.webservice.dto.GenericUserDto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class ShareCreationDto {

	@ApiModelProperty(value = "Recipients")
	protected List<GenericUserDto> recipients;

	@ApiModelProperty(value = "Document uuids")
	protected List<String> documents;

	@ApiModelProperty(value = "Description")
	protected String description;

	@ApiModelProperty(value = "Secured")
	protected Boolean secured;

	@ApiModelProperty(value = "ExpirationDate")
	protected Date expirationDate;

	@ApiModelProperty(value = "Subject")
	protected String subject;
	
	@ApiModelProperty(value = "Message")
	protected String message;

}
