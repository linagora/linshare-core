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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;

public class UploadRequestActivationEmailContext extends GenericUploadRequestEmailContext {

	private List<Contact> recipients;

	/**
	 * @param owner
	 * @param request
	 * @param requestUrl
	 * @param recipients
	 */
	public UploadRequestActivationEmailContext(User owner, UploadRequest request, UploadRequestUrl requestUrl,
			List<Contact> recipients) {
		super(owner.getDomain(), false, owner, requestUrl, request, false);
		this.recipients = recipients;
	}

	public List<Contact> getRecipients() {
		return recipients;
	}

	@Override
	public MailContentType getType() {
		if (warnOwner) {
			return MailContentType.UPLOAD_REQUEST_ACTIVATED_FOR_OWNER;
		}
		return MailContentType.UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT;
	}

	@Override
	public MailActivationType getActivation() {
		if (warnOwner) {
			return MailActivationType.UPLOAD_REQUEST_ACTIVATED_FOR_OWNER;
		}
		return MailActivationType.UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT;
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
			return null;
		}
		return owner.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(uploadRequest, "Missing upload uploadRequest");
		if (!warnOwner) {
			Validate.notNull(requestUrl, "Missing upload upload request url");
		}
		Validate.notNull(owner, "Missing upload request owner");
	}

}
