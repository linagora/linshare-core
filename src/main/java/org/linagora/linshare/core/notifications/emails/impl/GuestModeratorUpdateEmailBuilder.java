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
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.GuestModeratorUpdateEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.StringParameter;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class GuestModeratorUpdateEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.GUEST_MODERATOR_UPDATE;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		GuestModeratorUpdateEmailContext emailCtx = (GuestModeratorUpdateEmailContext) context;
		Account actor = emailCtx.getActor();
		Account account = emailCtx.getModerator().getAccount();
		Guest guest = emailCtx.getModerator().getGuest();
		ModeratorRole role = emailCtx.getModerator().getRole();
		ModeratorRole oldRole = emailCtx.getOldRole();
		String linshareURL = getLinShareUrl(account);
		MailConfig cfg = account.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("actor", new MailContact(actor));
		ctx.setVariable("account", new MailContact(account));
		ctx.setVariable("guest", new MailContact(guest));
		ctx.setVariable("role", new StringParameter(role.toString(), oldRole.toString()));
		ctx.setVariable("guestLink", getGuestLink(linshareURL, guest.getLsUuid()));
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	protected String getGuestLink(String linshareURL, String guestUuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(linshareURL);
		Formatter formatter = new Formatter(sb);
		formatter.format(urlTemplateForGuests, guestUuid);
		formatter.close();
		return sb.toString();
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		ctx.setVariable("actor", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("account", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("guest", new MailContact("guest1@linshare.org", "Guest first name", "Guest last name"));
		ctx.setVariable("role", new StringParameter(ModeratorRole.SIMPLE.toString(), ModeratorRole.ADMIN.toString()));
		ctx.setVariable("guestLink", getGuestLink(fakeLinshareURL, "cb1443d0-a34f-4d0b-92e4-c19d4eeb7fae"));
		res.add(ctx);
		return res;
	}

}
