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
	private String authCommand;
	private String searchUserCommand;
	private String getUserResult;
	
	protected DomainPattern() {
		this.identifier = null;
	}
	
	

	public DomainPattern(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String authCommand,
			String searchUserCommand, String getUserResult) {
		this.identifier = identifier;
		this.description = description;
		this.getUserCommand = getUserCommand;
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.getUserResult = getUserResult;
	}



	public DomainPattern(DomainPatternVo domainPatternVo) {
		this.identifier = domainPatternVo.getIdentifier();
		this.description = domainPatternVo.getDescription();
		this.getUserCommand = domainPatternVo.getGetUserCommand();
		this.getAllDomainUsersCommand = domainPatternVo.getGetAllDomainUsersCommand();
		this.authCommand = domainPatternVo.getAuthCommand();
		this.searchUserCommand = domainPatternVo.getSearchUserCommand();
		this.getUserResult = domainPatternVo.getGetUserResult();
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

}
