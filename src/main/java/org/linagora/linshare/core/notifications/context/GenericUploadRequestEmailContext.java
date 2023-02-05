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
