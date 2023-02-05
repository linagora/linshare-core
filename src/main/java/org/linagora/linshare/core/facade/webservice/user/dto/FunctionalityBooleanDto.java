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
package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FunctionalityBoolean")
public class FunctionalityBooleanDto extends FunctionalityDto {

	protected Boolean value;

	public FunctionalityBooleanDto() {
		super();
	}

	public FunctionalityBooleanDto(String identifier, boolean enable,
			boolean canOverride, Boolean value) {
		super(identifier, enable, canOverride);
		this.value = value;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
}
