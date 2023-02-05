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
package org.linagora.linshare.core.domain.entities.fields;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.data.domain.Sort.Direction;

public enum SortOrder {

	ASC, DESC;

	public static Order addOrder(SortOrder order, UserFields sortField) {
		return SortOrder.ASC.equals(order) ? Order.asc(sortField.toString()) : Order.desc(sortField.toString());
	}

	public static Order addAccountTypeSortOrder(SortOrder order) {
		return SortOrder.ASC.equals(order) ? Property.forName("class").asc() : Property.forName("class").desc();
	}
	
	public static Order addDomainSortOrder(SortOrder order) {
		return SortOrder.ASC.equals(order) ? Order.asc("d.label") : Order.desc("d.label");
	}

	public static Direction getSortDir(SortOrder dir) {
		return SortOrder.ASC.equals(dir) ? Direction.ASC : Direction.DESC;
	}
}
