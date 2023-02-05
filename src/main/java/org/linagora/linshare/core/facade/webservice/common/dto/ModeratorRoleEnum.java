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
package org.linagora.linshare.core.facade.webservice.common.dto;

import org.linagora.linshare.core.domain.constants.ModeratorRole;

public enum ModeratorRoleEnum {

	ADMIN, SIMPLE, ALL;

	public static ModeratorRoleEnum fromString(String s) {
		try {
			return ModeratorRoleEnum.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing moderator role.");
		}
	}

	public static ModeratorRole toModeratorRole(ModeratorRoleEnum dto) {
		try {
			return ModeratorRole.valueOf(dto.toString());
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing moderator role.");
		}
	}
}
