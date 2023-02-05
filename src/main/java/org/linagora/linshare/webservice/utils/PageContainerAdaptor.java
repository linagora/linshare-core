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
package org.linagora.linshare.webservice.utils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PageContainerAdaptor <T, U> {

	public PageContainer<U> convert(PageContainer<T> pc, Function<T, U> convert) {
		ImmutableList<U> list = ImmutableList.copyOf(Lists.transform(pc.getPageResponse().getContent(), convert));
		PageContainer<U> container = new PageContainer<U>();
		container.setPageSize(pc.getPageSize());
		container.setPageNumber(pc.getPageNumber());
		container.setPageResponse(
				new PageResponse<>(pc.getPageResponse().getTotalElements(), pc.getPageResponse().getTotalPages(), list,
						pc.getPageResponse().isFirst(), pc.getPageResponse().isLast()));
		return container;
	}
}
