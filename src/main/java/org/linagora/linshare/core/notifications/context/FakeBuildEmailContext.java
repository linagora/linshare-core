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

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;

public class FakeBuildEmailContext extends EmailContext {

	public FakeBuildEmailContext(Language language) {
		super(null, false);
		this.language = language;
	}

	@Override
	public MailContentType getType() {
		return null;
	}

	@Override
	public MailActivationType getActivation() {
		return null;
	}

	@Override
	public String getMailRcpt() {
		return null;
	}

	@Override
	public String getMailReplyTo() {
		return null;
	}

	@Override
	public void validateRequiredField() {
	}

}
