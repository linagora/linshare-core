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
		if (CANCELED.equals(status)) {
			list.add(CREATED);
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
