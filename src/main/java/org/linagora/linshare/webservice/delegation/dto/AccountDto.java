package org.linagora.linshare.webservice.delegation.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.User;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Account")
@ApiModel(value = "Account", description = "")
public class AccountDto {

	@ApiModelProperty(value = "Uuid")
	protected String uuid;

	@ApiModelProperty(value = "Mail")
	private String mail;

	@ApiModelProperty(value = "Name")
	private String name;

	@ApiModelProperty(value = "Domain")
	protected String domain;

	@ApiModelProperty(value = "Locale")
	protected String locale;

	public AccountDto() {
		super();
	}

	public AccountDto(String uuid, String mail, String name, String domain,
			String locale) {
		super();
		this.uuid = uuid;
		this.mail = mail;
		this.name = name;
		this.domain = domain;
		this.locale = locale;
	}

	public AccountDto(User u) {
		super();
		this.uuid = u.getLsUuid();
		this.mail = u.getMail();
		this.name = u.getLastName();
		this.domain = u.getDomainId();
		this.locale = u.getLocale();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

}
