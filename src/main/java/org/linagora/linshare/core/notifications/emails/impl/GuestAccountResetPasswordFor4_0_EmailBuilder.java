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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.GuestAccountResetPasswordFor4_0_EmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

/**
 * Builder to create a mail for notify guests with passwords encoded in old
 * strategy since LS migration from 2.3 to 4.0
 *
 */
public class GuestAccountResetPasswordFor4_0_EmailBuilder extends GuestAccountResetPasswordEmailBuilder {
	
	private Date urlExpirationDate;
	
	public GuestAccountResetPasswordFor4_0_EmailBuilder(String urlTemplateForGuestReset) {
		super(urlTemplateForGuestReset);
	}

	public Date getUrlExpirationDate() {
		return urlExpirationDate;
	}

	public void setUrlExpirationDate(Date urlExpirationDate) {
		this.urlExpirationDate = urlExpirationDate;
	}

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.GUEST_ACCOUNT_RESET_PASSWORD_FOR_4_0;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		GuestAccountResetPasswordFor4_0_EmailContext emailCtx = (GuestAccountResetPasswordFor4_0_EmailContext) context;
		Guest guest = emailCtx.getGuest();
		Date urlExpirationDate = emailCtx.getUrlExpirationDate();
		String linshareURL = getLinShareUrl(guest);
		MailConfig cfg = guest.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("guest", new MailContact(guest));
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("urlExpirationDate", urlExpirationDate);
		ctx.setVariable("resetLink", getResetLink(linshareURL, emailCtx.getResetPasswordTokenUuid()));
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, 7);
		ctx.setVariable("urlExpirationDate", c.getTime());
		ctx.setVariable("guest", new MailContact("guest@linshare.org", "Guest", "Guest"));
		ctx.setVariable("resetLink", getResetLink(fakeLinshareURL, "cb1443d0-a34f-4d0b-92e4-c19d4eeb7fae"));
		res.add(ctx);
		return res;
	}

}
