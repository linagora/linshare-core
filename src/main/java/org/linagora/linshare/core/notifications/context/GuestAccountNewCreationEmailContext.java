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
import org.linagora.linshare.core.domain.entities.User;

public class GuestAccountNewCreationEmailContext extends EmailContext {

	protected User creator;

	protected Guest guest;

	protected String resetPasswordTokenUuid;

	public GuestAccountNewCreationEmailContext(User creator, Guest guest, String resetPasswordTokenUuid) {
		super(guest.getDomain(), false);
		this.creator = creator;
		this.guest = guest;
		this.resetPasswordTokenUuid= resetPasswordTokenUuid;
		this.language = guest.getMailLocale();
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	public String getResetPasswordTokenUuid() {
		return resetPasswordTokenUuid;
	}

	public void setResetPasswordTokenUuid(String resetPasswordTokenUuid) {
		this.resetPasswordTokenUuid = resetPasswordTokenUuid;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.GUEST_ACCOUNT_NEW_CREATION;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.GUEST_ACCOUNT_NEW_CREATION;
	}

	@Override
	public String getMailRcpt() {
		return guest.getMail();
	}

	@Override
	public String getMailReplyTo() {
		return creator.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(creator, "Missing creator");
		Validate.notNull(guest, "Missing guest");
		Validate.notNull(resetPasswordTokenUuid, "Missing resetPasswordTokenUuid");
	}

}
