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

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum LdapBatchMetaDataType {

	DELETED_GROUPS,
	UPDATED_GROUPS,
	CREATED_GROUPS,
	DELETED_MEMBERS,
	UPDATED_MEMBERS,
	CREATED_MEMBERS,

	DELETED_WORKSPACES,
	UPDATED_WORKSPACES,
	CREATED_WORKSPACES,
	DELETED_WORKSPACES_MEMBERS,
	UPDATED_WORKSPACES_MEMBERS,
	CREATED_WORKSPACES_MEMBERS;


	public static LdapBatchMetaDataType fromString(String s) {
		try {
			return LdapBatchMetaDataType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.DATABASE_INCOHERENCE,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
