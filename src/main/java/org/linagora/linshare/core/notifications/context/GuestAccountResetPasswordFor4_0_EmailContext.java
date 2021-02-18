/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2020-2021 LINAGORA
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

import java.util.Date;

import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Guest;

/**
 * Custom Context only used for notify guests to reset passwords since LinShare
 * migration from 2.3 to 4.0 because of changing passwords encoding strategy
 *
 */
public class GuestAccountResetPasswordFor4_0_EmailContext extends GuestAccountResetPasswordEmailContext {

	private Date urlExpirationDate;

	public GuestAccountResetPasswordFor4_0_EmailContext(Guest guest, String resetPasswordTokenUuid,
			Date urlExpirationDate) {
		super(guest, resetPasswordTokenUuid);
		this.urlExpirationDate = urlExpirationDate;
	}

	public Date getUrlExpirationDate() {
		return urlExpirationDate;
	}

	public void setUrlExpirationDate(Date urlExpirationDate) {
		this.urlExpirationDate = urlExpirationDate;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.GUEST_ACCOUNT_RESET_PASSWORD_FOR_4_0;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.GUEST_ACCOUNT_RESET_PASSWORD_FOR_4_0;
	}

}
