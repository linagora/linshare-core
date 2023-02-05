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
package org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested;

import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;

import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name = "NestedLanguageParameter",
		description = "A Language NestedParameter"
)
public class NestedLanguageParameterDto extends NestedParameterDto<Language> {

	protected List<Language> languages;

	public NestedLanguageParameterDto() {
		super();
	}

	public NestedLanguageParameterDto(Language value, Language parentValue, List<Language> languages) {
		super(value, parentValue);
		this.languages = languages;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("value", value)
				.add("parentValue", parentValue)
				.add("isOverriden", isOverriden())
				.add("languages", languages)
				.toString();
	}

}
