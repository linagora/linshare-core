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

public class ContactLdapPattern extends LdapPattern {

	private String autoCompleteCommandOnAllAttributes;

	private String autoCompleteCommandOnFirstAndLastName;

	private Integer pageSize;

	private Integer sizeLimit;

	public String getAutoCompleteCommandOnAllAttributes() {
		return autoCompleteCommandOnAllAttributes;
	}

	public void setAutoCompleteCommandOnAllAttributes(
			String autoCompleteCommandOnAllAttributes) {
		this.autoCompleteCommandOnAllAttributes = autoCompleteCommandOnAllAttributes;
	}

	public String getAutoCompleteCommandOnFirstAndLastName() {
		return autoCompleteCommandOnFirstAndLastName;
	}

	public void setAutoCompleteCommandOnFirstAndLastName(
			String autoCompleteCommandOnFirstAndLastName) {
		this.autoCompleteCommandOnFirstAndLastName = autoCompleteCommandOnFirstAndLastName;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getSizeLimit() {
		return sizeLimit;
	}

	public void setSizeLimit(Integer sizeLimit) {
		this.sizeLimit = sizeLimit;
	}
}
