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
import org.linagora.linshare.core.domain.entities.ShareEntry;

public class ShareFileShareDeletedEmailContext extends EmailContext {

	protected ShareEntry shareEntry;

	public ShareFileShareDeletedEmailContext(ShareEntry shareEntry) {
		super(shareEntry.getEntryOwner().getDomain(), false);
		this.shareEntry = shareEntry;
		this.language = shareEntry.getRecipient().getMailLocale();
	}

	public ShareEntry getShareEntry() {
		return shareEntry;
	}

	public void setShareEntry(ShareEntry shareEntry) {
		this.shareEntry = shareEntry;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_FILE_SHARE_DELETED;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_FILE_SHARE_DELETED;
	}

	@Override
	public String getMailRcpt() {
		return shareEntry.getRecipient().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return shareEntry.getEntryOwner().getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareEntry, "Missing shareEntry");
	}

}
