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
package org.linagora.linshare.mongo.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "VersioningParameters")
public class VersioningParameters {

	protected Boolean enable;

	public VersioningParameters() {
		super();
	}

	public VersioningParameters(Boolean enable) {
		super();
		this.enable = enable;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof VersioningParameters)) {
			 return false;
		}
		VersioningParameters parameter = (VersioningParameters) object;
		if (this.enable != parameter.enable) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((enable == null) ? 0 : enable.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "VersioningParameters [enable=" + enable + "]";
	}

}
