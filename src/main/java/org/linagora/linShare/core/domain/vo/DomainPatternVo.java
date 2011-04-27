package org.linagora.linShare.core.domain.vo;

import org.linagora.linShare.core.domain.entities.DomainPattern;

public class DomainPatternVo {

	private final String identifier;
	private String description;
	private String getUserCommand;
	private String getAllDomainUsersCommand;
	private String isAdminCommand;
	private String authCommand;
	private String searchUserCommand;
	private String getUserResult;
	private String getAllDomainUsersResult;
	private String isAdminResult;
	private String authResult;
	private String searchUserResult;

	public DomainPatternVo(DomainPattern domainPattern) {
		this.identifier = domainPattern.getIdentifier();
		this.description = domainPattern.getDescription();
		this.getUserCommand = domainPattern.getGetUserCommand();
		this.getAllDomainUsersCommand = domainPattern.getGetAllDomainUsersCommand();
		this.isAdminCommand = domainPattern.getIsAdminCommand();
		this.authCommand = domainPattern.getAuthCommand();
		this.searchUserCommand = domainPattern.getSearchUserCommand();
		this.getUserResult = domainPattern.getGetUserResult();
		this.getAllDomainUsersResult = domainPattern.getGetAllDomainUsersResult();
		this.isAdminResult = domainPattern.getIsAdminResult();
		this.authResult = domainPattern.getAuthResult();
		this.searchUserResult = domainPattern.getSearchUserResult();
	}

	public DomainPatternVo(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String isAdminCommand, String authCommand,
			String searchUserCommand, String getUserResult,
			String getAllDomainUsersResult, String isAdminResult,
			String authResult, String searchUserResult) {
		super();
		this.identifier = identifier;
		this.description = description;
		this.getUserCommand = getUserCommand;
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
		this.isAdminCommand = isAdminCommand;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.getUserResult = getUserResult;
		this.getAllDomainUsersResult = getAllDomainUsersResult;
		this.isAdminResult = isAdminResult;
		this.authResult = authResult;
		this.searchUserResult = searchUserResult;
	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGetUserCommand() {
		return getUserCommand;
	}

	public void setGetUserCommand(String getUserCommand) {
		this.getUserCommand = getUserCommand;
	}

	public String getGetAllDomainUsersCommand() {
		return getAllDomainUsersCommand;
	}

	public void setGetAllDomainUsersCommand(String getAllDomainUsersCommand) {
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
	}

	public String getIsAdminCommand() {
		return isAdminCommand;
	}

	public void setIsAdminCommand(String isAdminCommand) {
		this.isAdminCommand = isAdminCommand;
	}

	public String getAuthCommand() {
		return authCommand;
	}

	public void setAuthCommand(String authCommand) {
		this.authCommand = authCommand;
	}

	public String getSearchUserCommand() {
		return searchUserCommand;
	}

	public void setSearchUserCommand(String searchUserCommand) {
		this.searchUserCommand = searchUserCommand;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getGetUserResult() {
		return getUserResult;
	}

	public void setGetUserResult(String getUserResult) {
		this.getUserResult = getUserResult;
	}

	public String getGetAllDomainUsersResult() {
		return getAllDomainUsersResult;
	}

	public void setGetAllDomainUsersResult(String getAllDomainUsersResult) {
		this.getAllDomainUsersResult = getAllDomainUsersResult;
	}

	public String getIsAdminResult() {
		return isAdminResult;
	}

	public void setIsAdminResult(String isAdminResult) {
		this.isAdminResult = isAdminResult;
	}

	public String getAuthResult() {
		return authResult;
	}

	public void setAuthResult(String authResult) {
		this.authResult = authResult;
	}

	public String getSearchUserResult() {
		return searchUserResult;
	}

	public void setSearchUserResult(String searchUserResult) {
		this.searchUserResult = searchUserResult;
	}
	
	

}
