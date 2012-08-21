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


public class Tag {

	public Tag() {
	}
	
	private Long id;
	
	private Account owner;
	
	private String name;
	
	private Boolean system = false;
	
	private Boolean visible = true;
	
	public Tag(Account owner, String name) {
		this.owner = owner;
		this.name = name;
		this.visible = true;
		this.system = false;
	}
	
	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSystem(Boolean value) {
		this.system = value;
	}
	
	public Boolean getSystem() {
		return system;
	}
	
	public void setVisible(boolean value) {
		setVisible(new Boolean(value));
	}
	
	public void setVisible(Boolean value) {
		this.visible = value;
	}
	
	public Boolean getVisible() {
		return visible;
	}
	
	public void setOwner(Account owner) {
		this.owner = owner;
	}
	
	public Account getOwner() {
		return owner;
	}
}
