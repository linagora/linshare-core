/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
