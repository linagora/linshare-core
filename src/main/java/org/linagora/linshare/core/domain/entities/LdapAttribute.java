/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
		setSync(new Boolean(value));
	}

	public void setSync(Boolean value) {
		this.sync = value;
	}

	public Boolean getSync() {
		return sync;
	}

	public void setSystem(boolean value) {
		setSystem(new Boolean(value));
	}

	public void setSystem(Boolean value) {
		this.system = value;
	}

	public Boolean getSystem() {
		return system;
	}

	public void setEnable(boolean value) {
		setEnable(new Boolean(value));
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
}
