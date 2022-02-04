/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
