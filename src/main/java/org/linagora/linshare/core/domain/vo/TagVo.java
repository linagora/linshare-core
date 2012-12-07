/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
 */
package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.EntryTagAssociation;

public class TagVo {
	
	protected String name;
	
	protected String tagEnumValue;
	
	protected Boolean isTagEnum;

	public TagVo(String name) {
		super();
		this.name = name;
		this.isTagEnum = false;
	}
	
	public TagVo(String name, String tagEnumValue) {
		super();
		this.name = name;
		this.tagEnumValue = tagEnumValue;
		this.isTagEnum = true;
	}
	
	public TagVo(EntryTagAssociation entryTag) {
		this.name = entryTag.getTag().getName();
		if (entryTag.getTagEnumValue() != null) {
			this.tagEnumValue = entryTag.getTagEnumValue().getValue();
			this.isTagEnum = true;
		}
		else
			this.isTagEnum = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return isTagEnum ? tagEnumValue : name;
	}
	
	public String getFullName() {
		return name + (isTagEnum ? ":" + tagEnumValue : "");
	}

	public String getTagEnumValue() {
		return tagEnumValue;
	}

	public void setTagEnumValue(String tagEnumValue) {
		this.tagEnumValue = tagEnumValue;
	}
}
