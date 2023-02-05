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
package org.linagora.linshare.core.facade.webservice.user;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.utils.Version;

import com.google.common.io.ByteSource;


public interface ShareFacade extends GenericFacade {

	public void sharedocument (String targetMail, String uuid, int securedShare) throws BusinessException;

	void multiplesharedocuments(String targetMail, List<String> uuid, int securedShare, String messageOpt, String inReplyToOpt, String referencesOpt) throws BusinessException;

	void multiplesharedocuments(List<String> mails, List<String> uuid, int securedShare, String messageOpt, String inReplyToOpt, String referencesOpt) throws BusinessException;

	void multiplesharedocuments(List<ShareDto> shares, boolean secured, String message) throws BusinessException;

	public List<ShareDto> getReceivedShares(Version version) throws BusinessException;

	public ShareDto getReceivedShare(Version version, String shareEntryUuid) throws BusinessException;

	public List<ShareDto> getShares(Version version) throws BusinessException;

	public ShareDto getShare(Version version, String shareUuid) throws BusinessException;

	public ByteSource getDocumentByteSource(String shareEntryUuid) throws BusinessException;

	public ByteSource getThumbnailByteSource(String shareEntryUuid, ThumbnailType kind) throws BusinessException;

	Set<ShareDto> create(ShareCreationDto createDto);

	ShareDto delete(String shareUuid, Boolean received) throws BusinessException;

	Set<AuditLogEntryUser> findAll(String actorUuid, String uuid, List<LogAction> actions,
			List<AuditLogEntryType> types, String beginDate, String endDate);
}
