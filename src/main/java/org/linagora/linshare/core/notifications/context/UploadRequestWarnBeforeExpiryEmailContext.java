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

import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;

public class UploadRequestWarnBeforeExpiryEmailContext extends GenericUploadRequestEmailContext {

	public UploadRequestWarnBeforeExpiryEmailContext(User owner, UploadRequest uploadRequest,
			UploadRequestUrl requestUrl, boolean warnOwner) {
		super(owner.getDomain(), false, owner, requestUrl, uploadRequest, warnOwner);
	}

	@Override
	public MailContentType getType() {
		return MailContentType.UPLOAD_REQUEST_WARN_BEFORE_EXPIRY;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.UPLOAD_REQUEST_WARN_BEFORE_EXPIRY;
	}

	@Override
	public String getMailRcpt() {
		if (warnOwner) {
			return owner.getMail();
		}
		return requestUrl.getContact().getMail();
	}

	@Override
	public String getMailReplyTo() {
		if (warnOwner) {
			if (requestUrl != null) {
				return requestUrl.getContact().getMail();
			}
		}
		return owner.getMail();
	}

}
