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

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.WelcomeMessageAssignDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.WelcomeMessageDto;

public interface WelcomeMessageFacade {

	List<WelcomeMessageDto> findAll(String domainUuid) throws BusinessException;

	WelcomeMessageDto find(String domainUuid, String welcomeMessageUuid) throws BusinessException;

	WelcomeMessageDto create(String domainUuid, WelcomeMessageDto welcomeMessageDto) throws BusinessException;

	WelcomeMessageDto update(String domainUuid, String welcomeMessageUuid, WelcomeMessageDto welcomeMessageDto) throws BusinessException;

	WelcomeMessageDto delete(String domainUuid, String welcomeMessageUuid, WelcomeMessageDto welcomeMessageDto) throws BusinessException;

	WelcomeMessageDto assign(String domainUuid, String welcomeMessageUuid, WelcomeMessageAssignDto assignDto) throws BusinessException;

	List<DomainDto> associatedDomains(String welcomeMessageUuid) throws BusinessException;
}
