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
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;

public class ShareWarnUndownloadedFilesharesEmailContext extends EmailContext {

	protected ShareEntryGroup shareEntryGroup;

	public ShareWarnUndownloadedFilesharesEmailContext(ShareEntryGroup shareEntryGroup) {
		super(shareEntryGroup.getOwner().getDomain(), false);
		this.shareEntryGroup = shareEntryGroup;
		this.language = shareEntryGroup.getOwner().getMailLocale();
	}

	public ShareEntryGroup getShareEntryGroup() {
		return shareEntryGroup;
	}

	public void setShareEntryGroup(ShareEntryGroup shareEntryGroup) {
		this.shareEntryGroup = shareEntryGroup;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
	}

	@Override
	public String getMailRcpt() {
		return shareEntryGroup.getOwner().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return null;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareEntryGroup, "Missing shareEntry");
	}

}
