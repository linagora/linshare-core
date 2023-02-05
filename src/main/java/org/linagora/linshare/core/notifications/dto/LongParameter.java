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

public class LongParameter implements Parameter {

	protected Boolean modified;

	protected Long value;

	protected Long oldValue;

	public LongParameter(Long value, Long oldValue) {
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

	public LongParameter(Long value, boolean modified) {
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

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public Long getOldValue() {
		return oldValue;
	}

	public void setOldValue(Long oldValue) {
		this.oldValue = oldValue;
	}

	@Override
	public String toString() {
		return "LongParameter [modified=" + modified + ", value=" + value + ", oldValue=" + oldValue + "]";
	}
}
