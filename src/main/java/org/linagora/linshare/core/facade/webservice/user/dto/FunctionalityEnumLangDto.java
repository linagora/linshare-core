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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;

@XmlRootElement(name = "FunctionalityEnumLangDto")
public class FunctionalityEnumLangDto extends FunctionalityDto {

	protected Language value;

	protected List<Language> units = new ArrayList<Language>();

	public FunctionalityEnumLangDto() {
		super();
	}

	public Language getValue() {
		return value;
	}

	public void setValue(Language value) {
		this.value = value;
	}

	public List<Language> getUnits() {
		return units;
	}

	public void setUnits(List<Language> units) {
		this.units = units;
	}

}
