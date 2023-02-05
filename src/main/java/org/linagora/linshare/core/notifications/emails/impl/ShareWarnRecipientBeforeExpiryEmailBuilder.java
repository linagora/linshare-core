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
import org.linagora.linshare.core.notifications.context.ShareWarnRecipientBeforeExpiryEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareWarnRecipientBeforeExpiryEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_WARN_RECIPIENT_BEFORE_EXPIRY;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareWarnRecipientBeforeExpiryEmailContext emailCtx = (ShareWarnRecipientBeforeExpiryEmailContext) context;

		User shareOwner = emailCtx.getShareOwner();

		String linshareURL;
		Share share = null;
		boolean anonymous = emailCtx.isAnonymous();
		if (anonymous) {
			share = new Share(emailCtx.getAnonymousShareEntry());
			linshareURL = getLinShareAnonymousURL(shareOwner);
		} else {
			linshareURL = getLinShareUrl(emailCtx.getShareRecipient());
			share = new Share(emailCtx.getShareEntry());
			share.setHref(getRecipientShareLink(linshareURL, share.getUuid()));
		}

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Language externalMailLocale = emailCtx.getShareEntry().getShareEntryGroup().getExternalMailLocale();
		Context ctx = new Context(Language.toLocale(externalMailLocale));
		ctx.setVariable("anonymous", anonymous);
		ctx.setVariable("daysLeft", emailCtx.getDay());
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("share", share);
		ctx.setVariable("shareOwner", new MailContact(shareOwner));
		ctx.setVariable("shareRecipient", emailCtx.getMailContactRecipient());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Share share = new Share("a-shared-file.txt", true);
		share.setHref(getRecipientShareLink(fakeLinshareURL, share.getUuid()));
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", false);
		ctx.setVariable("daysLeft", Integer.valueOf(8));
		ctx.setVariable("share", share);
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		res.add(ctx);
		Context ctx2 = newFakeContext(language);
		ctx2.setVariable("anonymous", true);
		ctx2.setVariable("daysLeft", Integer.valueOf(8));
		ctx2.setVariable("share", share);
		ctx2.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx2.setVariable("shareRecipient", new MailContact("unkown@linshare.org"));
		res.add(ctx2);
		return res;
	}
}
