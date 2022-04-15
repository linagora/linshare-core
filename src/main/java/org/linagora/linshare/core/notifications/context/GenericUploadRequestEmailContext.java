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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;

public abstract class GenericUploadRequestEmailContext extends EmailContext {

	protected final User owner;

	protected final UploadRequestUrl requestUrl;

	protected final UploadRequest uploadRequest;

	protected final boolean warnOwner;

	public GenericUploadRequestEmailContext(AbstractDomain domain, boolean needToRetrieveGuestDomain, User owner,
			UploadRequestUrl requestUrl, UploadRequest uploadRequest, boolean warnOwner) {
		super(domain, needToRetrieveGuestDomain);
		this.owner = owner;
		this.requestUrl = requestUrl;
		this.warnOwner = warnOwner;
		this.uploadRequest = uploadRequest;
		if (warnOwner) {
			this.language = uploadRequest.getUploadRequestGroup().getOwner().getMailLocale();
		} else {
			this.language = uploadRequest.getLocale();
		}
	}

	public User getOwner() {
		return owner;
	}

	public boolean isWarnOwner() {
		return warnOwner;
	}

	public UploadRequestUrl getRequestUrl() {
		return requestUrl;
	}

	public UploadRequest getUploadRequest() {
		return uploadRequest;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(uploadRequest, "Missing upload request");
		Validate.notNull(owner, "Missing upload request owner");
	}
}
