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
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountDto;
import org.linagora.linshare.utils.Version;

public interface TechnicalAccountFacade extends AdminGenericFacade {

	TechnicalAccountDto create(TechnicalAccountDto dto, Version version)
			throws BusinessException;

	TechnicalAccountDto find(String uuid) throws BusinessException;

	Set<TechnicalAccountDto> findAll() throws BusinessException;

	TechnicalAccountDto update(TechnicalAccountDto dto)
			throws BusinessException;

	TechnicalAccountDto delete(String uuid) throws BusinessException;

	TechnicalAccountDto delete(TechnicalAccountDto dto) throws BusinessException;

	void changePassword(String uuid, PasswordDto password)
			throws BusinessException;
}
