package org.linagora.linShare.core.domain.vo;

import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linShare.core.domain.entities.DomainPattern;

public class DomainPatternVo {

	private String identifier;
	private String patternDescription;
	private String getUserCommand;
	private String getAllDomainUsersCommand;
	private String authCommand;
	private String searchUserCommand;
	private String userMail;
	private String userFirstName;
	private String userLastName;
	
	public DomainPatternVo() {
	}

	public DomainPatternVo(DomainPattern domainPattern) {
		this.identifier = domainPattern.getIdentifier();
		this.patternDescription = domainPattern.getDescription();
		this.getUserCommand = domainPattern.getGetUserCommand();
		this.getAllDomainUsersCommand = domainPattern.getGetAllDomainUsersCommand();
		this.authCommand = domainPattern.getAuthCommand();
		this.searchUserCommand = domainPattern.getSearchUserCommand();
		this.userMail = domainPattern.getUserMail();
		this.userFirstName = domainPattern.getUserFirstName();
		this.userLastName = domainPattern.getUserLastName();
	}

	public DomainPatternVo(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String authCommand,
			String searchUserCommand, String mail, String firstName, String lastName) {
		super();
		this.identifier = identifier;
		this.patternDescription = description;
		this.getUserCommand = getUserCommand;
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.userMail = mail;
		this.userFirstName = firstName;
		this.userLastName = lastName;
	}

	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Validate("required")
	public String getIdentifier() {
		return identifier;
	}

	public String getPatternDescription() {
		return patternDescription;
	}

	public void setPatternDescription(String description) {
		this.patternDescription = description;
	}

	@Validate("required")
	public String getGetUserCommand() {
		return getUserCommand;
	}

	public void setGetUserCommand(String getUserCommand) {
		this.getUserCommand = getUserCommand;
	}

	@Validate("required")
	public String getGetAllDomainUsersCommand() {
		return getAllDomainUsersCommand;
	}

	public void setGetAllDomainUsersCommand(String getAllDomainUsersCommand) {
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
	}

	@Validate("required")
	public String getAuthCommand() {
		return authCommand;
	}

	public void setAuthCommand(String authCommand) {
		this.authCommand = authCommand;
	}

	@Validate("required")
	public String getSearchUserCommand() {
		return searchUserCommand;
	}

	public void setSearchUserCommand(String searchUserCommand) {
		this.searchUserCommand = searchUserCommand;
	}

	@Validate("required")
	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	@Validate("required")
	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	@Validate("required")
	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	@Override
	public String toString() {
		return identifier;
	}

}
