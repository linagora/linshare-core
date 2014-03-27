/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.linagora.linshare.core.domain.vo.DomainPatternVo;
import org.linagora.linshare.webservice.dto.DomainPatternDto;

public class DomainPattern {
    /**
     * Database persistence identifier
     */
    private long persistenceId;

    private final String identifier;
    private String description;
    private String authCommand;
    private String searchUserCommand;
    private String autoCompleteCommandOnAllAttributes;
    private String autoCompleteCommandOnFirstAndLastName;
    private Boolean system;
    private Integer searchPageSize;
    private Integer searchSizeLimit;
    private Integer completionPageSize;
    private Integer completionSizeLimit;

    private Map<String, LdapAttribute> attributes;
    
    public static final String USER_MAIL = "user_mail";
    public static final String USER_FIRST_NAME = "user_firstname";
    public static final String USER_LAST_NAME = "user_lastname";
    public static final String USER_UID = "user_uid";
    
    public static final Map<String , String> USER_METHOD_MAPPING = new HashMap<String , String>();
    
    static {
    	USER_METHOD_MAPPING.put(DomainPattern.USER_LAST_NAME, "setLastName");
    	USER_METHOD_MAPPING.put(DomainPattern.USER_FIRST_NAME, "setFirstName");
    	USER_METHOD_MAPPING.put(DomainPattern.USER_MAIL, "setMail");
    	USER_METHOD_MAPPING.put(DomainPattern.USER_UID, "setLdapUid");
    };
    
    protected DomainPattern() {
        this.identifier = null;
    }

    /**
     * For tests only.
     * @param identifier
     * @param description
     * @param getUserCommand
     * @param getAllDomainUsersCommand
     * @param authCommand
     * @param searchUserCommand
     * @param attributes
     */
    public DomainPattern(String identifier, String description,
            String getUserCommand, String getAllDomainUsersCommand,
            String authCommand,
            String searchUserCommand, Map<String, LdapAttribute> attributes) {
	this.identifier = identifier;
	this.description = description;
	this.authCommand = authCommand;
	this.searchUserCommand = searchUserCommand;
	this.attributes = attributes;
	this.autoCompleteCommandOnAllAttributes = "";
	this.autoCompleteCommandOnFirstAndLastName = "";
	this.searchPageSize = 0;
	this.searchSizeLimit = 0;
	this.completionPageSize = 0;
	this.completionSizeLimit = 0;
    }

    public DomainPattern(DomainPatternVo domainPatternVo) {
        this.identifier = domainPatternVo.getIdentifier();
        this.description = domainPatternVo.getPatternDescription();
        this.authCommand = domainPatternVo.getAuthCommand();
        this.searchUserCommand = domainPatternVo.getSearchUserCommand();
        this.system = domainPatternVo.getSystem();
        
        this.autoCompleteCommandOnAllAttributes = domainPatternVo.getAutoCompleteCommandOnAllAttributes();
        this.autoCompleteCommandOnFirstAndLastName = domainPatternVo.getAutoCompleteCommandOnFirstAndLastName();
    	this.searchPageSize = domainPatternVo.getSearchPageSize();
		this.searchSizeLimit = domainPatternVo.getSearchSizeLimit();
		this.completionPageSize = domainPatternVo.getCompletionPageSize();
		this.completionSizeLimit = domainPatternVo.getCompletionSizeLimit();
		
        
        this.attributes = new HashMap<String, LdapAttribute>();
        this.attributes.put(USER_MAIL, 			new LdapAttribute(USER_MAIL,		domainPatternVo.getUserMail(), true));
        this.attributes.put(USER_FIRST_NAME,	new LdapAttribute(USER_FIRST_NAME,	domainPatternVo.getUserFirstName(), true));
        this.attributes.put(USER_LAST_NAME,		new LdapAttribute(USER_LAST_NAME,	domainPatternVo.getUserLastName(), true));
        this.attributes.put(USER_UID,			new LdapAttribute(USER_UID,			domainPatternVo.getLdapUid(), false));
    }

    public DomainPattern(DomainPatternDto domainPatternDto) {
        this.identifier = domainPatternDto.getIdentifier();
        this.description = domainPatternDto.getDescription();
        this.authCommand = domainPatternDto.getAuthCommand();
        this.searchUserCommand = domainPatternDto.getSearchUserCommand();
        this.system = false;
        
        this.autoCompleteCommandOnAllAttributes = domainPatternDto.getAutoCompleteCommandOnAllAttributes();
        this.autoCompleteCommandOnFirstAndLastName = domainPatternDto.getAutoCompleteCommandOnFirstAndLastName();
        this.searchPageSize = domainPatternDto.getSearchPageSize();
		this.searchSizeLimit = domainPatternDto.getSearchSizeLimit();
		this.completionPageSize = domainPatternDto.getCompletionPageSize();
		this.completionSizeLimit = domainPatternDto.getCompletionSizeLimit();
		
        this.attributes = new HashMap<String, LdapAttribute>();
        this.attributes.put(USER_MAIL, 			new LdapAttribute(USER_MAIL,		domainPatternDto.getUserMail(), true));
        this.attributes.put(USER_FIRST_NAME,	new LdapAttribute(USER_FIRST_NAME,	domainPatternDto.getUserFirstName(), true));
        this.attributes.put(USER_LAST_NAME,		new LdapAttribute(USER_LAST_NAME,	domainPatternDto.getUserLastName(), true));
        this.attributes.put(USER_UID,			new LdapAttribute(USER_UID,			domainPatternDto.getLdapUid(), false));
    }

    /**
     * For tests only.
     * @param identifier
     * @param description
     * @param authCommand
     * @param searchUserCommand
     * @param searchPageSize
     * @param searchSizeLimit
     * @param attributes
     * @param autoCompleteCommandOnAllAttributes
     * @param autoCompleteCommandOnFirstAndLastName
     * @param completionPageSize
     * @param completionSizeLimit
     * @param system
     */
	public DomainPattern(String identifier, String description, String authCommand,
            String searchUserCommand, Integer searchPageSize, Integer searchSizeLimit, Map<String, LdapAttribute> attributes, String autoCompleteCommandOnAllAttributes, String autoCompleteCommandOnFirstAndLastName,
            Integer completionPageSize, Integer completionSizeLimit, boolean system) {
		super();
		this.identifier = identifier;
		this.description = description;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
        this.attributes = attributes;
        this.autoCompleteCommandOnAllAttributes = autoCompleteCommandOnAllAttributes;
        this.autoCompleteCommandOnFirstAndLastName = autoCompleteCommandOnFirstAndLastName;
        this.system = system;
        this.searchSizeLimit = searchSizeLimit;
        this.searchPageSize = searchPageSize;
        this.completionSizeLimit = completionSizeLimit;
        this.completionPageSize = completionPageSize;
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
	
	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}
	
	public String getAttribute(String field) {
		return attributes.get(field).getAttribute().trim().toLowerCase();
	}

	public Integer getCompletionPageSize() {
		return completionPageSize;
	}

	public void setCompletionPageSize(Integer completionPageSize) {
		this.completionPageSize = completionPageSize;
	}

	public Integer getSearchPageSize() {
		return searchPageSize;
	}

	public void setSearchPageSize(Integer searchPageSize) {
		this.searchPageSize = searchPageSize;
	}

	public Integer getCompletionSizeLimit() {
		return completionSizeLimit;
	}

	public void setCompletionSizeLimit(Integer completionSizeLimit) {
		this.completionSizeLimit = completionSizeLimit;
	}

	public Integer getSearchSizeLimit() {
		return searchSizeLimit;
	}

	public void setSearchSizeLimit(Integer searchSizeLimit) {
		this.searchSizeLimit = searchSizeLimit;
	}

	public String getAutoCompleteCommandOnAllAttributes() {
		return autoCompleteCommandOnAllAttributes;
	}

	public void setAutoCompleteCommandOnAllAttributes(String autoCompleteCommandOnAllAttributes) {
		this.autoCompleteCommandOnAllAttributes = autoCompleteCommandOnAllAttributes;
	}

	public String getAutoCompleteCommandOnFirstAndLastName() {
		return autoCompleteCommandOnFirstAndLastName;
	}

	public void setAutoCompleteCommandOnFirstAndLastName(String autoCompleteCommandOnFirstAndLastName) {
		this.autoCompleteCommandOnFirstAndLastName = autoCompleteCommandOnFirstAndLastName;
	}
	
}
