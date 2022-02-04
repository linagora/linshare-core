/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
