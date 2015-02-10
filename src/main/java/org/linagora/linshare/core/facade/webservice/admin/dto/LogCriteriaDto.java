package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.List;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class LogCriteriaDto extends org.linagora.linshare.core.facade.webservice.common.dto.LogCriteriaDto{

	@ApiModelProperty(value = "List of actor's mail")
	private List<String> actorMails; // The selected user

	@ApiModelProperty(value = "First name of the actor")
	private String actorFirstName;

	@ApiModelProperty(value = "Last name of the actor")
	private String actorLastName;

	@ApiModelProperty(value = "Domain of the actor")
	private String actorDomain;

	public LogCriteriaDto(List<String> actorMails, String actorFirstName,
			String actorLastName, String actorDomain) {
		this.actorMails = actorMails;
		this.actorFirstName = actorFirstName;
		this.actorLastName = actorLastName;
		this.actorDomain = actorDomain;
	}

	public List<String> getActorMails() {
		return actorMails;
	}

	public void setActorMails(List<String> mails) {
		this.actorMails = mails;
	}

	public String getActorFirstName() {
		return actorFirstName;
	}

	public void setActorFirstName(String firstname) {
		this.actorFirstName = firstname;
	}

	public String getActorLastName() {
		return actorLastName;
	}

	public void setActorLastName(String lastname) {
		this.actorLastName = lastname;
	}

	public String getActorDomain() {
		return actorDomain;
	}

	public void setActorDomain(String actorDomain) {
		this.actorDomain = actorDomain;
	}
}
