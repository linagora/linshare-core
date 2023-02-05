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
package org.linagora.linshare.core.facade.webservice.adminv5;

import java.util.Optional;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.fields.DomainField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface DomainFacade {

	Set<DomainDto> findAll(boolean tree);

	PageContainer<DomainDto> findAll(
			Optional<String> domainType,
			Optional<String> name, Optional<String> description,
			Optional<String> parentUuid,
			SortOrder sortOrder, DomainField sortField,
			Integer pageNumber, Integer pageSize);

	DomainDto find(String domain, boolean tree, boolean detail);

	DomainDto create(DomainDto dto, boolean dedicatedDomainPolicy, String addItToDomainPolicy);

	DomainDto update(String uuid, DomainDto dto);

	DomainDto delete(String uuid, DomainDto dto);
}
