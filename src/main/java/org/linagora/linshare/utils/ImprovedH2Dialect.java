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
package org.linagora.linshare.utils;

import org.hibernate.dialect.H2Dialect;

public class ImprovedH2Dialect extends H2Dialect {

	@Override
	public String getDropSequenceString(String sequenceName) {
		// Adding the "if exists" clause to avoid warnings
		return "drop sequence if exists " + sequenceName;
	}

	@Override
	public boolean dropConstraints() {
		// We don't need to drop constraints before dropping tables, that just
		// leads to error
		// messages about missing tables when we don't have a schema in the
		// database
		return false;
	}

	@Override
	public boolean supportsIfExistsBeforeTableName() {
		return true;
	}

	@Override
	public boolean supportsIfExistsAfterTableName() {
		return false;
	}

	@Override
	public String getCascadeConstraintsString() {
		return " CASCADE ";
	}
	
	
}
