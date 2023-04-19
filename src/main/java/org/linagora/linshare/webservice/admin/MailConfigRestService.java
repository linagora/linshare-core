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

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailAttachmentDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailConfigDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailFooterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;

public interface MailConfigRestService {

	Set<MailConfigDto> findAll(String domainId, boolean onlyCurrentDomain)
			throws BusinessException;

    MailConfigDto find(String uuid) throws BusinessException;

	void head(String uuid) throws BusinessException;

	MailConfigDto create(MailConfigDto dto) throws BusinessException;

	MailConfigDto update(MailConfigDto dto) throws BusinessException;

	MailConfigDto delete(MailConfigDto dto) throws BusinessException;

	Set<MailFooterDto> findAllFooters(String mailConfigUuid)throws BusinessException;

	Set<MailContentDto> findAllContents(String mailConfigUuid, String mailContentType) throws BusinessException;

	List<MailAttachmentDto> findAllMailAttachments(String uuid) throws BusinessException;

	Set<DomainDto> findAllAssociatedDomains(String mailConfigUuid) throws BusinessException;

}
