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
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Moderator;

public class GuestModeratorCreationEmailContext extends EmailContext {

	protected Moderator moderator;

	protected Account actor;

	public GuestModeratorCreationEmailContext(Account actor, Moderator moderator) {
		super(moderator.getAccount().getDomain(), false);
		this.actor = actor;
		this.moderator = moderator;
		this.language = moderator.getAccount().getMailLocale();
	}

	
	public Moderator getModerator() {
		return moderator;
	}

	public void setModerator(Moderator moderator) {
		this.moderator = moderator;
	}


	public Account getActor() {
		return actor;
	}

	public void setActor(Account actor) {
		this.actor = actor;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.GUEST_MODERATOR_CREATION;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.GUEST_MODERATOR_CREATION;
	}

	@Override
	public String getMailRcpt() {
		return moderator.getAccount().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return actor.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(actor, "Missing actor");
		Validate.notNull(moderator.getAccount(), "Missing account");
		Validate.notNull(moderator.getGuest(), "Missing guest");
	}

}
