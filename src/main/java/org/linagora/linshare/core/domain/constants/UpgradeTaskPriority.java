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

public enum UpgradeTaskPriority {

	// Upgrade task need to be run and complete in order to restore the service.
	MANDATORY,
	// Upgrade task need to be run but it is not required to restore the service.
	// Upgrade task processing can be postpone.
	REQUIRED,
	OPTIONAL;

	public static UpgradeTaskPriority fromString(String s) {
		try {
			return UpgradeTaskPriority.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.DATABASE_INCOHERENCE, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
