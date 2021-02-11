/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

package org.linagora.linshare.core.domain.constants;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

import com.google.common.collect.Lists;

public enum UploadRequestStatus {
	DELETED,
	PURGED(DELETED),
	ARCHIVED(DELETED, PURGED),
	CLOSED(ARCHIVED, PURGED),
	ENABLED(CLOSED),
	CANCELED,
	CREATED(CANCELED, ENABLED);

	private final UploadRequestStatus[] next;

	private UploadRequestStatus(UploadRequestStatus... next) {
		this.next = next;
	}

	public UploadRequestStatus transition(final UploadRequestStatus status) throws BusinessException {
		if (!Arrays.asList(next).contains(status)) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_STATUS_BAD_TRANSITON,
					"Cannot transition from " + name() + " to " + status.name() + '.');
		}
		return status;
	}

	public static List<UploadRequestStatus> listAllowedStatusToUpdate(final UploadRequestStatus status) {
		List<UploadRequestStatus> list = Lists.newArrayList();
		if (CLOSED.equals(status)) {
			list.add(ENABLED);
		}
		if (ARCHIVED.equals(status)) {
			list.add(CLOSED);
		}
		if (DELETED.equals(status)) {
			list.add(ARCHIVED);
		}
		if (PURGED.equals(status)) {
			list.add(DELETED);
		}
		return list;
	}

	public static UploadRequestStatus fromString(String s) {
		try {
			return UploadRequestStatus.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(
					TechnicalErrorCode.NO_SUCH_UPLOAD_REQUEST_STATUS,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
