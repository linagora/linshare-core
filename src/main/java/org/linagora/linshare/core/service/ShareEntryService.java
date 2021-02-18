/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

import com.google.common.io.ByteSource;

public interface ShareEntryService {

	ShareEntry find(Account actor, Account owner, String uuid)  throws BusinessException ;

	/**
	 * Find underlying document uuid in order to create copy.
	 * @param actor
	 * @param owner
	 * @param uuid share uuid
	 * @return ShareEntry
	 * @throws BusinessException
	 */
	ShareEntry findForDownloadOrCopyRight(Account actor, Account owner, String uuid)  throws BusinessException;

	ShareEntry markAsCopied(Account actor, Account owner, String uuid, CopyMto copiedTo)  throws BusinessException;

	ShareEntry delete(Account actor, Account owner, String uuid, LogActionCause cause) throws BusinessException;

	ShareEntry update(Account actor, Account owner, ShareEntry shareEntry) throws BusinessException;

	ByteSource getThumbnailByteSource(Account actor, Account owner, String shareEntryUuid, ThumbnailType kind) throws BusinessException;

	ByteSource getByteSource(Account actor, Account owner, String shareEntryUuid) throws BusinessException;

	List<ShareEntry> findAllMyRecievedShareEntries(Account actor, Account owner);

	Set<ShareEntry> create(Account actor, User owner, ShareContainer shareContainer, ShareEntryGroup shareEntryGroup);

	List<String> findAllExpiredEntries(Account actor, Account owner);

}
