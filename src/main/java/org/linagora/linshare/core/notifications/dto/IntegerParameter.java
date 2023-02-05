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
package org.linagora.linshare.core.notifications.dto;

public class IntegerParameter implements Parameter {

	protected Boolean modified;

	protected Integer value;

	protected Integer oldValue;

	public IntegerParameter(Integer value, Integer oldValue) {
		super();
		this.value = value;
		this.oldValue = oldValue;
		if (value == null && oldValue == null) {
			this.modified = false;
		} else if (value == null && oldValue != null || (value != null && oldValue == null)) {
			this.modified = true;
		} else {
			this.modified = !(value.equals(oldValue));
		}
	}

	public IntegerParameter(Integer value, boolean modified) {
		super();
		this.value = value;
		this.oldValue = null;
		this.modified = modified;
	}

	public Boolean getModified() {
		return modified;
	}

	public void setModified(Boolean modified) {
		this.modified = modified;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getOldValue() {
		return oldValue;
	}

	public void setOldValue(Integer oldValue) {
		this.oldValue = oldValue;
	}

	@Override
	public String toString() {
		return "Parameter [modified=" + modified + ", value=" + value + ", oldValue=" + oldValue + "]";
	}

}
