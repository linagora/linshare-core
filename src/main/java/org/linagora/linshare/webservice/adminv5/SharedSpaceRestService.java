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
package org.linagora.linshare.webservice.adminv5;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

public interface SharedSpaceRestService {

	SharedSpaceNode find(String uuid) throws BusinessException;

	SharedSpaceNode delete(SharedSpaceNode node, String uuid) throws BusinessException;

	SharedSpaceNode update(SharedSpaceNode node, String uuid) throws BusinessException;

	Response members(String uuid, String accountUuid, Set<String> roles, String email, String firstName, String lastName, String pattern, String type, String sortOrder, String sortField, Integer pageNumber, Integer pageSize) throws BusinessException;

	Response findAll(String accountUuid, List<String> domainUuids, String sortOrder, String sortField, Set<String> nodeTypes,
			Set<String> roles, String name, Integer greaterThanOrEqualTo, Integer lessThanOrEqualTo, Integer pageNumber, Integer pageSize);

	SharedSpaceMember findMember(String memberUuid) throws BusinessException;

	SharedSpaceMember addMember(SharedSpaceMember member) throws BusinessException;

	SharedSpaceMember deleteMember(SharedSpaceMember member, String memberUuid) throws BusinessException;

	SharedSpaceMember updateMember(SharedSpaceMember member, String memberUuid, boolean force, Boolean propagate) throws BusinessException;

	SharedSpaceNode update(PatchDto patchNode, String uuid) throws BusinessException;
}
