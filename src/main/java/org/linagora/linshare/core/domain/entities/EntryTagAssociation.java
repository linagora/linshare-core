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
package org.linagora.linshare.core.domain.entities;

public class EntryTagAssociation {
	
	private Long id;
	
	private Entry entry;
	
	private Tag tag;
	
	private TagEnumValue tagEnumValue;
	
	public EntryTagAssociation() {
	}
	
	public EntryTagAssociation(Entry entry, Tag tag) {
		this.tag = tag;
		this.entry=entry;
	}
	
	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setEntry(Entry value) {
		this.entry = value;
	}
	
	public Entry getEntry() {
		return entry;
	}
	
	public void setTag(Tag value) {
		this.tag = value;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public void setTagEnumValue(TagEnumValue value) {
		this.tagEnumValue = value;
	}
	
	public TagEnumValue getTagEnumValue() {
		return tagEnumValue;
	}
}
