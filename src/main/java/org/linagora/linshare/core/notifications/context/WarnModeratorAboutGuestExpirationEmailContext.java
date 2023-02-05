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
import org.linagora.linshare.core.domain.entities.Moderator;

public class WarnModeratorAboutGuestExpirationEmailContext extends EmailContext {

	protected Guest guest;

	protected Moderator moderator;

	protected int daysLeft;

	public WarnModeratorAboutGuestExpirationEmailContext(Moderator moderator, Guest guest, int daysLeft) {
		super(moderator.getAccount().getDomain(), false);
		this.guest = guest;
		this.daysLeft = daysLeft;
		this.language = moderator.getAccount().getMailLocale();
		this.moderator = moderator;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	public int getDaysLeft() {
		return daysLeft;
	}

	public void setDaysLeft(int daysLeft) {
		this.daysLeft = daysLeft;
	}

	public Moderator getModerator() {
		return moderator;
	}

	public void setModerator(Moderator moderator) {
		this.moderator = moderator;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.GUEST_WARN_MODERATOR_ABOUT_GUEST_EXPIRATION;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.GUEST_WARN_MODERATOR_ABOUT_GUEST_EXPIRATION;
	}

	@Override
	public String getMailRcpt() {
		return moderator.getAccount().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return guest.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(guest, "Missing guest");
		Validate.notNull(daysLeft, "Missing daysLeft");
	}

}
