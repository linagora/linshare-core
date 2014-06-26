package org.linagora.linshare.webservice.uploadrequest.dto;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.User;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class ContactDto {

	@ApiModelProperty(value = "FirstName")
	private String firstName;

	@ApiModelProperty(value = "LastName")
	private String lastName;

	@ApiModelProperty(value = "Mail")
	private String mail;

	public ContactDto(String firstName, String lastName, String mail) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
	}

	public ContactDto(Account account) {
		super();
		User u = (User)account;
		this.firstName = u.getFirstName();
		this.lastName = u.getLastName();
		this.mail = u.getMail();
	}

	public ContactDto(Contact contact) {
		super();
		// TODO support first and last name in contact ?
		this.firstName = null;
		this.lastName = null;
		this.mail = contact.getMail();
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder("Contact : ");
		if (!StringUtils.isBlank(firstName)
				&& !StringUtils.isBlank(this.lastName)) {
			res.append(this.firstName);
			res.append(" ");
			res.append(this.lastName);
			res.append(" : ");
		}
		res.append(this.mail);
		return res.toString();
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

}
