/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.notifications.emails.impl;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.NewGuestEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

public class NewGuestEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.NEW_GUEST;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		NewGuestEmailContext emailCtx = (NewGuestEmailContext) context;
		User sender = emailCtx.getSender();
		User recipient = emailCtx.getRecipient();

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(recipient.getExternalMailLocale());

		Context ctx = new Context(container.getLocale());
		ctx.setVariable("sender", new MailContact(sender));
		ctx.setVariable("recipient", new MailContact(recipient));

		// LinShare URL for the email recipient.
		ctx.setVariable("linshareURL", getLinShareUrlForAUserRecipient(recipient));
		ctx.setVariable("prefixURL", "#/external/reset/");
		ctx.setVariable("passwordTokenUuid", emailCtx.getResetPasswordTokenUuid());
		// TODO FIXME
		// linShareUrl + "#/external/reset/" +
		// emailCtx.getResetPasswordTokenUuid());

		container.setRecipient(recipient);
		container.setReplyTo(sender);
		container.setFrom(getFromMailAddress(recipient));

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, container, getSupportedType(),
				ctx);
		return buildMailContainer;
	}

	@Override
	public Context getContextForFakeBuild(Language language) {
		Context ctx = new Context(Language.toLocale(language));
		ctx.setVariable("sender", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("recipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));

		// LinShare URL for the email recipient.
		ctx.setVariable("linshareURL", "http://127.0.0.1/");
		ctx.setVariable("prefixURL", "#/external/reset/");
		ctx.setVariable("passwordTokenUuid", "cb1443d0-a34f-4d0b-92e4-c19d4eeb7fae");
		return ctx;
	}

}
