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
package org.linagora.linshare.webservice.admin;

import java.util.Set;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;

public interface ThreadRestService {

	public Set<WorkGroupDto> findAll(String pattern, String threadName, String memberName) throws BusinessException;

	public WorkGroupDto find(String uuid) throws BusinessException;

	public void head(String uuid) throws BusinessException;

	public Set<WorkGroupMemberDto> members(String uuid)
			throws BusinessException;

	public WorkGroupDto update(WorkGroupDto thread) throws BusinessException;

	public WorkGroupDto delete(WorkGroupDto thread) throws BusinessException;
}
