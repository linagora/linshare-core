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
