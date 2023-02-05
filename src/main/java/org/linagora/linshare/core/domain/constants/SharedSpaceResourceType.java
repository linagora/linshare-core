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

public enum SharedSpaceResourceType {

	FOLDER,
	FILE,
	WORK_GROUP,
	WORK_SPACE,
	DRIVE, // COMPAT, issue #1269
	WORKGROUP,  // COMPAT, issue #1269
	MEMBER;

	public static SharedSpaceResourceType fromString(String s) {
		try {
			return SharedSpaceResourceType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing resource type");
		}
	}

	public static SharedSpaceResourceType fromNodeType(String nodeType) {
		return fromString(nodeType);
	}
}
