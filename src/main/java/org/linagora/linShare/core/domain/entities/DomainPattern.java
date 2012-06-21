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
    private String ldapUid;
	
	protected DomainPattern() {
		this.identifier = null;
	}
	
	
	public DomainPattern(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String authCommand,
			String searchUserCommand, String mail, String firstName, String lastName, String ldapUid) {
		this.identifier = identifier;
		this.description = description;
		this.getUserCommand = getUserCommand;
		this.getAllDomainUsersCommand = getAllDomainUsersCommand;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.userMail = mail;
		this.userFirstName = firstName;
		this.userLastName = lastName;
		this.ldapUid = ldapUid;
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
		this.ldapUid = domainPatternVo.getLdapUid();
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

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstname(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getLdapUid() {
		return ldapUid;
	}

	public void setLdapUid(String ldapUid) {
		this.ldapUid = ldapUid;
	}

	@Override
	public String toString() {
		return "DomainPattern : " + identifier;
	}

}
