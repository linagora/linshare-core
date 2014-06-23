package org.linagora.linshare.webservice.dto;

import org.linagora.linshare.core.domain.entities.TechnicalAccount;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class TechnicalAccountDto extends AccountDto {

	@ApiModelProperty(value = "Name")
	private String name;

	@ApiModelProperty(value = "Mail")
	private String mail;
	
	@ApiModelProperty(value = "TechnicalAccountPermissionUuid")
	private String technicalAccountPermissionUuid;

	public TechnicalAccountDto(TechnicalAccount account) {
		super(account, false);
		this.name = account.getLastName();
		this.mail = account.getMail();
		this.technicalAccountPermissionUuid = account.getPermission().getUuid();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getTechnicalAccountPermissionUuid() {
		return technicalAccountPermissionUuid;
	}

	public void setTechnicalAccountPermissionUuid(
			String technicalAccountPermissionUuid) {
		this.technicalAccountPermissionUuid = technicalAccountPermissionUuid;
	}
}
