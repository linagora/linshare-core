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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;

@XmlRootElement(name = "ListStringUserPreference")
public class ListStringUserPreference extends UserPreference {

	protected List<String> values;

	public ListStringUserPreference() {
		super();
	}

	public ListStringUserPreference(String key, String... value) {
		super(key);
		this.values = Lists.newArrayList();
		for (String string : value) {
			this.values.add(string);
		}
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public void validate() {
		super.validate();
		Validate.notEmpty(values, "Missing user preference value");
	}

}
