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

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContainerDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentDto;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;

public interface MailContentFacade {

	MailContentDto find(String uuid) throws BusinessException;

	MailContentDto create(MailContentDto dto) throws BusinessException;

	MailContentDto update(MailContentDto dto) throws BusinessException;

	MailContentDto delete(String uuid) throws BusinessException;

	Set<MailContentDto> findAll(String domainIdentifier, boolean only) throws BusinessException;

	MailContainerDto fakeBuild(String mailContentUuid, String language, String mailConfigUuid, Integer flavor);

	MailContainerDto fakeBuild(MailContentDto dto, String language, String mailConfigUuid, Integer flavor);

	Response fakeBuildHtml(String mailContentUuid, String language, String mailConfigUuid, boolean subject, Integer flavor);

	List<ContextMetadata> getAvailableVariables(String mailContentUuid);
}
