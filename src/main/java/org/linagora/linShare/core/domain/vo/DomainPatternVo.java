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
	private String getUserResult;
	
	public DomainPatternVo() {
	}

	public DomainPatternVo(DomainPattern domainPattern) {
		this.identifier = domainPattern.getIdentifier();
		this.patternDescription = domainPattern.getDescription();
		this.getUserCommand = domainPattern.getGetUserCommand();
		this.getAllDomainUsersCommand = domainPattern.getGetAllDomainUsersCommand();
		this.authCommand = domainPattern.getAuthCommand();
		this.searchUserCommand = domainPattern.getSearchUserCommand();
		this.getUserResult = domainPattern.getGetUserResult();
	}

	public DomainPatternVo(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String authCommand,
			String searchUserCommand, String getUserResult) {
		super();
		this.identifier = identifier;
		this.patternDescription = description;
		this.getUserCommand = getUserCommand;
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.getUserResult = getUserResult;
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
	public String getGetUserResult() {
		return getUserResult;
	}

	public void setGetUserResult(String getUserResult) {
		this.getUserResult = getUserResult;
	}
	
	@Override
	public String toString() {
		return identifier;
	}

}
