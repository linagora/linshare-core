/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.ShareEntry;

public class ShareWarnSenderAboutShareExpirationEmailContext extends EmailContext {

	protected ShareEntry shareEntry;

	protected Integer daysLeft;

	public ShareWarnSenderAboutShareExpirationEmailContext(ShareEntry shareEntry, Integer daysLeft) {
		super(shareEntry.getEntryOwner().getDomain(), false);
		this.shareEntry = shareEntry;
		this.daysLeft = daysLeft;
		this.language = shareEntry.getEntryOwner().getExternalMailLocale();
	}

	public ShareEntry getShareEntry() {
		return shareEntry;
	}

	public void setShareEntry(ShareEntry shareEntry) {
		this.shareEntry = shareEntry;
	}

	public Integer getDaysLeft() {
		return daysLeft;
	}

	public void setDaysLeft(Integer daysLeft) {
		this.daysLeft = daysLeft;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD;
	}

	@Override
	public String getMailRcpt() {
		return shareEntry.getEntryOwner().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return shareEntry.getRecipient().getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareEntry, "Missing shareEntry");
		Validate.notNull(daysLeft, "Missing daysLeft");
	}

}
