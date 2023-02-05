/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;

public abstract class LdapPattern {

	public static final String DN = "dn_attr";

	protected long id;

	protected String label;

	protected String uuid;

	protected String description;

	protected boolean system;

	protected Date creationDate;

	protected Date modificationDate;

	protected Map<String, LdapAttribute> attributes;

	public static final String USER_MAIL = "user_mail";
	public static final String USER_FIRST_NAME = "user_firstname";
	public static final String USER_LAST_NAME = "user_lastname";
	public static final String USER_UID = "user_uid";

	protected LdapPattern() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	@Override
	public String toString() {
		return "LdapPattern [label=" + label + ", uuid=" + uuid + "]";
	}

	public Map<String, String> getMethodsMapping() {
		Map<String, String> methodsMapping = Maps.newHashMap(); 
		methodsMapping.put(USER_LAST_NAME, "setLastName");
		methodsMapping.put(USER_FIRST_NAME, "setFirstName");
		methodsMapping.put(USER_MAIL, "setMail");
		methodsMapping.put(USER_UID, "setLdapUid");
		return methodsMapping;
	}
}
