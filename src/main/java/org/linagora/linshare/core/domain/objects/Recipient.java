package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.webservice.dto.UserDto;

public class Recipient {

	protected String uuid;

	protected String mail;

	protected String firstName;

	protected String lastName;

	protected AbstractDomain domain;

	protected String domainIdentifier;

	protected String locale;

	public Recipient(String mail) {
		super();
		this.mail = mail;
	}

	public Recipient(String uuid, String mail, String firstName,
			String lastName, AbstractDomain domain, String locale) {
		super();
		this.uuid = uuid;
		this.mail = mail;
		this.firstName = firstName;
		this.lastName = lastName;
		this.domain = domain;
		this.locale = locale;
	}

	public Recipient(User user) {
		super();
		this.uuid = user.getLsUuid();
		this.mail = user.getMail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.domain = user.getDomain();
		this.locale = user.getExternalMailLocale();
	}

	public Recipient(UserVo userVo) {
		super();
		this.uuid = userVo.getLsUuid();
		this.mail = userVo.getMail();
		this.firstName = userVo.getFirstName();
		this.lastName = userVo.getLastName();
		this.domainIdentifier = userVo.getDomainIdentifier();
	}

	public Recipient(UserDto userDto) {
		super();
		this.uuid = userDto.getUuid();
		this.mail = userDto.getMail();
		this.firstName = userDto.getFirstName();
		this.lastName = userDto.getLastName();
		this.domainIdentifier = userDto.getDomain();
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}


	@Override
	public String toString() {
		return "Recipient [uuid=" + uuid + ", mail=" + mail
				+ ", domainIdentifier=" + domainIdentifier + "]";
	}

}
