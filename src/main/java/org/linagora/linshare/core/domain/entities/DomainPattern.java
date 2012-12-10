package org.linagora.linshare.core.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.linagora.linshare.core.domain.vo.DomainPatternVo;

public class DomainPattern {
    /**
     * Database persistence identifier
     */
    private long persistenceId;

    private final String identifier;
    private String description;
    private String authCommand;
    private String searchUserCommand;
    private String autoCompleteCommand;
    private Boolean system;

    private Map<String, LdapAttribute> attributes;
    
    public static final String USER_MAIL = "user_mail";
    public static final String USER_FIRST_NAME = "user_firstname";
    public static final String USER_LAST_NAME = "user_lastname";
    public static final String USER_UID = "user_uid";

    protected DomainPattern() {
        this.identifier = null;
    }

    public DomainPattern(String identifier, String description,
            String getUserCommand, String getAllDomainUsersCommand,
            String authCommand,
            String searchUserCommand, Map<String, LdapAttribute> attributes) {
        this.identifier = identifier;
        this.description = description;
        this.authCommand = authCommand;
        this.searchUserCommand = searchUserCommand;
        this.attributes = attributes;
        this.autoCompleteCommand = "";
    }

    public DomainPattern(DomainPatternVo domainPatternVo) {
        this.identifier = domainPatternVo.getIdentifier();
        this.description = domainPatternVo.getPatternDescription();
        this.authCommand = domainPatternVo.getAuthCommand();
        this.searchUserCommand = domainPatternVo.getSearchUserCommand();
        this.autoCompleteCommand = domainPatternVo.getAutoCompleteCommand();
        this.system = domainPatternVo.getSystem();
        
        this.attributes = new HashMap<String, LdapAttribute>();
        this.attributes.put(USER_MAIL, 			new LdapAttribute(USER_MAIL,		domainPatternVo.getUserMail()));
        this.attributes.put(USER_FIRST_NAME,	new LdapAttribute(USER_FIRST_NAME,	domainPatternVo.getUserFirstName()));
        this.attributes.put(USER_LAST_NAME,		new LdapAttribute(USER_LAST_NAME,	domainPatternVo.getUserLastName()));
        this.attributes.put(USER_UID,			new LdapAttribute(USER_UID,			domainPatternVo.getLdapUid()));
    }


	public DomainPattern(String identifier, String description, String getUserCommand,
            String getAllDomainUsersCommand, String authCommand, String searchUserCommand, Map<String, LdapAttribute> attributes,
            String autoCompleteCommand, boolean system) {
		super();
		this.identifier = identifier;
		this.description = description;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
        this.attributes = attributes;
        this.autoCompleteCommand = autoCompleteCommand;
        this.system = system;
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

	@Override
	public String toString() {
		return "DomainPattern : " + identifier;
	}

	public Map<String, LdapAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, LdapAttribute> attributes) {
		this.attributes = attributes;
	}
	
	public String getAutoCompleteCommand() {
		return this.autoCompleteCommand;
	}

	public void setAutoCompleteCommand(String autoCompleteCommand) {
		this.autoCompleteCommand = autoCompleteCommand;
	}

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}
	
	public String getAttribute(String field) {
		return attributes.get(field).getAttribute();
	}

}