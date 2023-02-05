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
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareWarnSenderAboutShareExpirationEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareWarnSenderAboutShareExpirationEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareWarnSenderAboutShareExpirationEmailContext emailCtx = (ShareWarnSenderAboutShareExpirationEmailContext) context;

		User shareOwner = (User) emailCtx.getShareEntry().getEntryOwner();
		User shareRecipient = emailCtx.getShareEntry().getRecipient();
		Share share = new Share(emailCtx.getShareEntry());
		String linshareURL = getLinShareUrl(shareOwner);

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("shareOwner", new MailContact(shareOwner));
		ctx.setVariable("shareRecipient", new MailContact(shareRecipient));
		ctx.setVariable("share", share);
		ctx.setVariable(linshareURL, linshareURL);
		ctx.setVariable("daysLeft", emailCtx.getDaysLeft());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		Share share = new Share("a-shared-file.txt", true);
		share.setHref(getRecipientShareLink(fakeLinshareURL, share.getUuid()));
		ctx.setVariable("share", share);
		ctx.setVariable("daysLeft", Integer.valueOf(7));
		res.add(ctx);
		return res;
	}

}
