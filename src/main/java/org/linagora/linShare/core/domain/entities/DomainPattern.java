package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.vo.DomainPatternVo;

public class DomainPattern {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

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
	
	protected DomainPattern() {
		this.identifier = null;
	}
	
	

	public DomainPattern(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String isAdminCommand, String authCommand,
			String searchUserCommand, String getUserResult,
			String getAllDomainUsersResult, String isAdminResult,
			String authResult, String searchUserResult) {
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



	public DomainPattern(DomainPatternVo domainPatternVo) {
		this.identifier = domainPatternVo.getIdentifier();
		this.description = domainPatternVo.getDescription();
		this.getUserCommand = domainPatternVo.getGetUserCommand();
		this.getAllDomainUsersCommand = domainPatternVo.getGetAllDomainUsersCommand();
		this.isAdminCommand = domainPatternVo.getIsAdminCommand();
		this.authCommand = domainPatternVo.getAuthCommand();
		this.searchUserCommand = domainPatternVo.getSearchUserCommand();
		this.getUserResult = domainPatternVo.getGetUserResult();
		this.getAllDomainUsersResult = domainPatternVo.getGetAllDomainUsersResult();
		this.isAdminResult = domainPatternVo.getIsAdminResult();
		this.authResult = domainPatternVo.getAuthResult();
		this.searchUserResult = domainPatternVo.getSearchUserResult();
	}
	
	public long getPersistenceId() {
		return persistenceId;
	}
	
	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getIdentifier() {
		return identifier;
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
