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

import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.WarnModeratorAboutGuestExpirationEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class WarnModeratorAboutGuestExpirationEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.GUEST_WARN_MODERATOR_ABOUT_GUEST_EXPIRATION;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		WarnModeratorAboutGuestExpirationEmailContext emailCtx = (WarnModeratorAboutGuestExpirationEmailContext) context;
		Guest guest = emailCtx.getGuest();
		AbstractDomain fromDomain = emailCtx.getFromDomain();
		String linshareURL = getLinShareUrl(fromDomain);
		MailConfig cfg = fromDomain.getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		// FIXME template owner variable should have been renamed into moderator.
		ctx.setVariable("owner", new MailContact(emailCtx.getModerator()));
		ctx.setVariable("guest", new MailContact(guest));
		ctx.setVariable("guestCreationDate", guest.getCreationDate());
		ctx.setVariable("guestExpirationDate", guest.getExpirationDate());
		ctx.setVariable("daysLeft", emailCtx.getDaysLeft());
		ctx.setVariable("linshareURL", linshareURL);
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		// FIXME template owner variable should have been renamed into moderator.
		ctx.setVariable("owner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("guest", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("guestCreationDate", getFakeCreationDate());
		ctx.setVariable("guestExpirationDate", getFakeExpirationDate());
		ctx.setVariable("daysLeft", Integer.valueOf(7));
		res.add(ctx);
		return res;
	}

}
