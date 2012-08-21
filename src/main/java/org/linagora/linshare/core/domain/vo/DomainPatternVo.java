package org.linagora.linshare.core.domain.vo;

import java.util.Map;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;

public class DomainPatternVo {

	private String identifier;
	private String patternDescription;
	private String authCommand;
	private String searchUserCommand;
	private String userMail;
	private String userFirstName;
	private String userLastName;
    private String ldapUid;
    private String autoCompleteCommand;

    @NonVisual
    private boolean system;

	public DomainPatternVo() {
	}

	public DomainPatternVo(DomainPattern domainPattern) {
		this.identifier = domainPattern.getIdentifier();
		this.patternDescription = domainPattern.getDescription();
		this.authCommand = domainPattern.getAuthCommand();
		this.searchUserCommand = domainPattern.getSearchUserCommand();
        this.autoCompleteCommand = domainPattern.getAutoCompleteCommand();
        this.system = domainPattern.getSystem();
        
        Map<String, LdapAttribute> attributes = domainPattern.getAttributes();
        this.userMail = attributes.get(DomainPattern.USER_MAIL).getAttribute();
        this.userFirstName = attributes.get(DomainPattern.USER_FIRST_NAME).getAttribute();
        this.userLastName = attributes.get(DomainPattern.USER_LAST_NAME).getAttribute();
        this.ldapUid = attributes.get(DomainPattern.USER_UID).getAttribute();
	}

    /*
     * Legacy constructor for compatibility with Linshare 0.11
     */
	public DomainPatternVo(String identifier, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String authCommand,
			String searchUserCommand, String mail, String firstName, String lastName, String ldapUid) {
		super();
		this.identifier = identifier;
		this.patternDescription = description;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.userMail = mail;
		this.userFirstName = firstName;
		this.userLastName = lastName;
        this.ldapUid = ldapUid;
	}

	public DomainPatternVo(String identifier, String description, String getUserCommand,
            String getAllDomainUsersCommand, String authCommand, String searchUserCommand, String mail,
            String firstName, String lastName, String ldapUid, String autoCompleteCommand, boolean system) {
		super();
		this.identifier = identifier;
		this.patternDescription = description;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.userMail = mail;
		this.userFirstName = firstName;
		this.userLastName = lastName;
        this.ldapUid = ldapUid;
        this.autoCompleteCommand = autoCompleteCommand;
        this.system = system;
	}


	public void setIdentifier(String identifier) {
		if(identifier != null)
			this.identifier = identifier.trim();
        else
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
		if(patternDescription != null)
			this.patternDescription = patternDescription.trim();
		else
			this.patternDescription = description;
	}

	@Validate("required")
	public String getAuthCommand() {
		return authCommand;
	}

	public void setAuthCommand(String authCommand) {
		if(authCommand != null)
			this.authCommand = authCommand.trim();
		else
			this.authCommand = authCommand;
	}

	@Validate("required")
	public String getSearchUserCommand() {
		return searchUserCommand;
	}

	public void setSearchUserCommand(String searchUserCommand) {
		if(searchUserCommand != null)
			this.searchUserCommand = searchUserCommand.trim();
		else
			this.searchUserCommand = searchUserCommand;
	}

	@Validate("required")
	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		if(userMail != null)
			this.userMail = userMail.trim();
		else
			this.userMail = userMail;
	}

	@Validate("required")
	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		if(userFirstName != null)
			this.userFirstName = userFirstName.trim();
		else
			this.userFirstName = userFirstName;
	}

	@Validate("required")
	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		if(userLastName != null)
			this.userLastName = userLastName.trim();
		else
			this.userLastName = userLastName;
	}

	@Validate("required")
	public String getLdapUid() {
		return ldapUid;
	}

	public void setAutoCompleteCommand(String autoCompleteCommand) {
		if(autoCompleteCommand != null)
			this.autoCompleteCommand = autoCompleteCommand.trim();
		else
			this.autoCompleteCommand = autoCompleteCommand;
	}


	@Validate("required")
	public String getAutoCompleteCommand() {
		return autoCompleteCommand;
	}


	public boolean getSystem() {
		return system;
	}


	public void setSystem(boolean system) {
        this.system = system;
	}


	@Override
	public String toString() {
		return identifier;
	}

}
