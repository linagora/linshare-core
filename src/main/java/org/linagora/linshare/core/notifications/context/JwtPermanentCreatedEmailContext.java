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

import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.PermanentToken;

public class JwtPermanentCreatedEmailContext extends AbstractJwtLongTimeEmailContext {

	public JwtPermanentCreatedEmailContext(Account creator,
			Account actor,
			PermanentToken jwtPermanentToken) {
		super(creator, actor, jwtPermanentToken);
		this.language = actor.getMailLocale();
	}

	@Override
	public MailContentType getType() {
		return MailContentType.ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED;
	}

}
