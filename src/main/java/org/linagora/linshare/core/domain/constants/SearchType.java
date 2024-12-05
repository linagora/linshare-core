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
package org.linagora.linshare.core.domain.constants;

public enum SearchType {

	SHARING, USERS, THREAD_MEMBERS, UPLOAD_REQUESTS, WORKGROUP_MEMBERS, WORKGROUP_AUTHORS, CONTACT_LIST;

	public static SearchType fromString(String value) {
		try {
			return SearchType.valueOf(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Doesn't match an existing SearchType");
		}
	}
}
