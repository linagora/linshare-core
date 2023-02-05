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
package org.linagora.linshare.ldap;

import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.domain.constants.NodeType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LdapGroupObject {

	protected String name;

	protected String prefix;

	// dn
	protected String externalId;

	protected List<String> members;

	protected Map<NodeType, Role> roles;

	public LdapGroupObject() {
		super();
		this.members = Lists.newArrayList();
		this.roles = Maps.newHashMap();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Map<NodeType, Role> getRoles() {
		return roles;
	}

	public void setRoles(Map<NodeType, Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "LdapGroupObject [name=" + name + ", externalId=" + externalId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LdapGroupObject other = (LdapGroupObject) obj;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		return true;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

	/** Helpers **/

	// do not remove it ! it is called using reflection.
	public List<String> addMember(String member) {
		if (members == null) {
			members = Lists.newArrayList();
		}
		members.add(member);
		return members;
	}

	public String getNameWithPrefix() {
		return prefix + name;
	}

	public LdapGroupObject removePrefix() {
		if (prefix != null) {
			if (name != null) {
				String currPrefix = name.substring(0, prefix.length());
				if (prefix.equals(currPrefix)) {
					this.name = this.name.substring(prefix.length());
				}
			}
		}
		return this;
	}

	public Role getRole(NodeType nodeType) {
		return getRoles().get(nodeType);
	}

	public String getContributorsDn() {
		return "cn=contributors," + externalId;
	}

	public String getWritersDn() {
		return "cn=writers," + externalId;
	}

	public String getWorkSpaceWritersDn() {
		return "cn=workspace_writers," + externalId;
	}
}
