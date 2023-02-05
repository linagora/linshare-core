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

public enum OperationHistoryTypeEnum {
	CREATE(0), DELETE(1);

	private int type;

	private OperationHistoryTypeEnum(int type) {
		this.type = type;
	}

	public int toInt() {
		return type;
	}

	public static OperationHistoryTypeEnum fromInt(int value) {
		for (OperationHistoryTypeEnum operationType : values()) {
			if (operationType.type == value) {
				return operationType;
			}
		}
		throw new IllegalArgumentException(
				"Doesn't match any AccountPurgeStepEnum");
	}
}
