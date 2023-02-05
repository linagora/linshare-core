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
package org.linagora.linshare.core.notifications.emails.impl;

import java.util.Formatter;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.GuestAccountResetPasswordEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class GuestAccountResetPasswordEmailBuilder extends EmailBuilder {

	protected String urlTemplateForGuestReset;

	public GuestAccountResetPasswordEmailBuilder(String urlTemplateForGuestReset) {
		super();
		this.urlTemplateForGuestReset = urlTemplateForGuestReset;
	}

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.GUEST_ACCOUNT_RESET_PASSWORD_LINK;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		GuestAccountResetPasswordEmailContext emailCtx = (GuestAccountResetPasswordEmailContext) context;
		Guest guest = emailCtx.getGuest();
		String linshareURL = getLinShareUrl(guest);
		MailConfig cfg = guest.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("customMessage", null);
		ctx.setVariable("guest", new MailContact(guest));
		ctx.setVariable("guestExpirationDate", guest.getExpirationDate());
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("resetLink", getResetLink(linshareURL, emailCtx.getResetPasswordTokenUuid()));
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		ctx.setVariable("customMessage", null);
		ctx.setVariable("guest", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("guestExpirationDate", getFakeExpirationDate());
		ctx.setVariable("resetLink", getResetLink(fakeLinshareURL, "cb1443d0-a34f-4d0b-92e4-c19d4eeb7fae"));
		res.add(ctx);
		return res;
	}

	protected String getResetLink(String linshareURL, String token) {
		StringBuilder sb = new StringBuilder();
		sb.append(linshareURL);
		Formatter formatter = new Formatter(sb);
		formatter.format(urlTemplateForGuestReset, token);
		formatter.close();
		return sb.toString();
	}
}
