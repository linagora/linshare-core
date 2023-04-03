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
package org.linagora.linshare.core.facade.webservice.admin;

import java.util.Set;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;

public interface MimePolicyFacade extends AdminGenericFacade {

	MimePolicyDto create(MimePolicyDto dto) throws BusinessException;

	MimePolicyDto delete(String uuid) throws BusinessException;

	MimePolicyDto find(String uuid, boolean full) throws BusinessException;

	Set<MimePolicyDto> findAll(String domainId, boolean onlyCurrentDomain)
			throws BusinessException;

	MimePolicyDto update(MimePolicyDto dto) throws BusinessException;

	MimePolicyDto enableAllMimeTypes(String uuid) throws BusinessException;

	MimePolicyDto disableAllMimeTypes(String uuid) throws BusinessException;

    void assign(String domainUuid, String mimePolicyUuid);
}
