/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

    @NonVisual
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
