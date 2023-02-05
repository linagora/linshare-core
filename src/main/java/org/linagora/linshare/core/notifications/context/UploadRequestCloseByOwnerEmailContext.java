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
package org.linagora.linshare.core.notifications.context;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;

public class UploadRequestCloseByOwnerEmailContext extends GenericUploadRequestEmailContext{

	/**
	 * warn the recipient
	 * @param owner
	 * @param requestUrl
	 * @param uploadRequest
	 */
	public UploadRequestCloseByOwnerEmailContext(User owner, UploadRequestUrl requestUrl, UploadRequest uploadRequest) {
		super(owner.getDomain(), false, owner, requestUrl, uploadRequest, false);
	}

	@Override
	public MailContentType getType() {
		return MailContentType.UPLOAD_REQUEST_CLOSED_BY_OWNER;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.UPLOAD_REQUEST_CLOSED_BY_OWNER;
	}

	@Override
	public String getMailRcpt() {
		return requestUrl.getContact().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return owner.getMail();
	}

	@Override
	public void validateRequiredField() {
		super.validateRequiredField();
		Validate.notNull(requestUrl, "Missing upload request url");
	}
}
