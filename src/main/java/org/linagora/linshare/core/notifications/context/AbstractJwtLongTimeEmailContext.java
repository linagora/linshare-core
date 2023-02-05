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
import org.linagora.linshare.mongo.entities.PermanentToken;

public abstract class AbstractJwtLongTimeEmailContext extends EmailContext{

	protected PermanentToken jwtLongTime;

	protected Account recipient;

	protected Account owner;

	public AbstractJwtLongTimeEmailContext(Account creator,
			Account actor,
			PermanentToken jwtLongTime) {
		super(actor.getDomain(), true);
		this.jwtLongTime = jwtLongTime;
		this.language = actor.getMailLocale();
		this.recipient = actor;
		this.owner = creator;
	}

	@Override
	public abstract MailContentType getType();

	@Override
	public abstract MailActivationType getActivation();

	@Override
	public String getMailRcpt() {
		return recipient.getMail();
	}

	@Override
	public String getMailReplyTo() {
		return recipient.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(jwtLongTime, "Missing permanent token");
	}

	public PermanentToken getJwtLongTime() {
		return jwtLongTime;
	}

	public Account getRecipient() {
		return recipient;
	}

	public Account getOwner() {
		return owner;
	}

}
