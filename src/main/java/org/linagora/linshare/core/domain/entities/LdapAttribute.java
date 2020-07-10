/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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


public class LdapAttribute {

	private Long id;

	private String field;

	private String attribute;

	private Boolean sync;

	private Boolean system;

	private Boolean enable;

	private Boolean completion;

	public LdapAttribute() {
	}

	public LdapAttribute(String field, String attribute, Boolean sync,
			Boolean system, Boolean enable, Boolean completion) {
		super();
		this.field = field;
		this.attribute = attribute;
		this.sync = sync;
		this.system = system;
		this.enable = enable;
		this.completion = completion;
	}

	public LdapAttribute(String field, String attribute, boolean completion) {
		super();
		this.field = field;
		this.attribute = attribute;
		this.sync = false;
		this.system = true;
		this.enable = true;
		this.completion = completion;
	}

	public LdapAttribute(String field, String attribute) {
		super();
		this.field = field;
		this.attribute = attribute;
		this.sync = false;
		this.system = true;
		this.enable = true;
		this.completion = false;
	}

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return id;
	}

	public void setField(String value) {
		this.field = value;
	}

	public String getField() {
		return field;
	}

	public void setAttribute(String value) {
		this.attribute = value;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setSync(boolean value) {
		setSync(Boolean.valueOf(value));
	}

	public void setSync(Boolean value) {
		this.sync = value;
	}

	public Boolean getSync() {
		return sync;
	}

	public void setSystem(boolean value) {
		setSystem(Boolean.valueOf(value));
	}

	public void setSystem(Boolean value) {
		this.system = value;
	}

	public Boolean getSystem() {
		return system;
	}

	public void setEnable(boolean value) {
		setEnable(Boolean.valueOf(value));
	}

	public void setEnable(Boolean value) {
		this.enable = value;
	}

	public Boolean getEnable() {
		return enable;
	}

	public Boolean getCompletion() {
		return completion;
	}

	public void setCompletion(Boolean completion) {
		this.completion = completion;
	}

	@Override
	public String toString() {
		return "LdapAttribute [field=" + field + ", attribute=" + attribute + ", sync=" + sync + ", system=" + system
				+ ", enable=" + enable + ", completion=" + completion + "]";
	}
}
