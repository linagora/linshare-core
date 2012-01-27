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
	private String userMail;
	private String userFirstName;
	private String userLastName;
	
	protected DomainPattern() {
		this.identifier = null;
	}
	
	public DomainPattern(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String authCommand,
			String searchUserCommand, String mail, String firstName, String lastName) {
		this.identifier = identifier;
		this.description = description;
		this.getUserCommand = getUserCommand;
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.userMail = mail;
		this.userFirstName = firstName;
		this.userLastName = lastName;
	}



	public DomainPattern(DomainPatternVo domainPatternVo) {
		this.identifier = domainPatternVo.getIdentifier();
		this.description = domainPatternVo.getPatternDescription();
		this.getUserCommand = domainPatternVo.getGetUserCommand();
		this.getAllDomainUsersCommand = domainPatternVo.getGetAllDomainUsersCommand();
		this.authCommand = domainPatternVo.getAuthCommand();
		this.searchUserCommand = domainPatternVo.getSearchUserCommand();
		this.userMail = domainPatternVo.getUserMail();
		this.userFirstName = domainPatternVo.getUserFirstName();
		this.userLastName = domainPatternVo.getUserLastName();
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
		if(description != null)
		this.description = description.trim();
	}

	public String getGetUserCommand() {
		return getUserCommand;
	}

	public void setGetUserCommand(String getUserCommand) {
		if(getUserCommand != null)
		this.getUserCommand = getUserCommand.trim();
	}

	public String getGetAllDomainUsersCommand() {
		return getAllDomainUsersCommand;
	}

	public void setGetAllDomainUsersCommand(String getAllDomainUsersCommand) {
		if(getAllDomainUsersCommand != null)
		this.getAllDomainUsersCommand = getAllDomainUsersCommand.trim();
	}

	public String getAuthCommand() {
		return authCommand;
	}

	public void setAuthCommand(String authCommand) {
		if(authCommand != null)
		this.authCommand = authCommand.trim();
	}

	public String getSearchUserCommand() {
		return searchUserCommand;
	}

	public void setSearchUserCommand(String searchUserCommand) {
		if(authCommand != null)
		this.searchUserCommand = searchUserCommand.trim();
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		if(userMail != null)
		this.userMail = userMail.trim();
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstname(String userFirstName) {
		if(userFirstName != null)
		this.userFirstName = userFirstName.trim();
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		if(userLastName != null)
		this.userLastName = userLastName.trim();
	}

	@Override
	public String toString() {
		return "DomainPattern : " + identifier;
	}

}
