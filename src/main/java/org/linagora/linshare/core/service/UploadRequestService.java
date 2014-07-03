/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestService {

	List<UploadRequest> findAllRequest(User actor);

	UploadRequest findRequestByUuid(User actor, String uuid) throws BusinessException;

	UploadRequest createRequest(User actor, UploadRequest req)
			throws BusinessException;

	UploadRequest updateRequest(User actor, UploadRequest req)
			throws BusinessException;

	void deleteRequest(User actor, UploadRequest req) throws BusinessException;

	UploadRequestGroup findRequestGroupByUuid(User actor, String uuid);

	UploadRequestGroup createRequestGroup(User actor, UploadRequestGroup group)
			throws BusinessException;

	UploadRequestGroup updateRequestGroup(User actor, UploadRequestGroup group)
			throws BusinessException;

	void deleteRequestGroup(User actor, UploadRequestGroup group)
			throws BusinessException;

	UploadRequestHistory findRequestHistoryByUuid(User actor, String uuid);

	UploadRequestHistory createRequestHistory(User actor,
			UploadRequestHistory history) throws BusinessException;

	UploadRequestHistory updateRequestHistory(User actor,
			UploadRequestHistory history) throws BusinessException;

	void deleteRequestHistory(User actor, UploadRequestHistory history)
			throws BusinessException;

	UploadRequestTemplate findRequestTemplateByUuid(User actor, String uuid);

	UploadRequestTemplate createRequestTemplate(User actor,
			UploadRequestTemplate template) throws BusinessException;

	UploadRequestTemplate updateRequestTemplate(User actor,
			UploadRequestTemplate template) throws BusinessException;

	void deleteRequestTemplate(User actor, UploadRequestTemplate template)
			throws BusinessException;

	UploadRequestUrl findRequestUrlByUuid(User actor, String uuid);

	UploadRequestUrl createRequestUrl(User actor, UploadRequestUrl url)
			throws BusinessException;

	UploadRequestUrl updateRequestUrl(User actor, UploadRequestUrl url)
			throws BusinessException;

	void deleteRequestUrl(User actor, UploadRequestUrl url)
			throws BusinessException;

	UploadRequestEntry findRequestEntryByUuid(Account actor, String uuid);

	UploadRequestEntry createRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException;

	UploadRequestEntry updateRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException;

	void deleteRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException;

	/*
	 * Business methods
	 */

	UploadRequest setStatusToClosed(Account actor, UploadRequest req) throws BusinessException;
}
