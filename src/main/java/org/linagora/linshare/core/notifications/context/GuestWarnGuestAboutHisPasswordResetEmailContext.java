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
import org.linagora.linshare.core.domain.entities.Guest;

public class GuestWarnGuestAboutHisPasswordResetEmailContext extends EmailContext {

	protected Guest guest;

	public GuestWarnGuestAboutHisPasswordResetEmailContext (Guest guest) {
		super(guest.getDomain(), false);
		this.guest = guest;
		this.language = guest.getMailLocale();
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET;
	}

	@Override
	public String getMailRcpt() {
		return guest.getMail();
	}

	@Override
	public String getMailReplyTo() {
		return null;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(guest, "Missing guest");
	}

}
