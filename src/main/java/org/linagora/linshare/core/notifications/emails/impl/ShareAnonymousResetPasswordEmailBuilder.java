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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareAnonymousResetPasswordEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareAnonymousResetPasswordEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_ANONYMOUS_RESET_PASSWORD;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareAnonymousResetPasswordEmailContext emailCtx = (ShareAnonymousResetPasswordEmailContext) context;

		User shareOwner = emailCtx.getShareOwner();
		ShareContainer shareContainer = emailCtx.getShareContainer();

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Language externalMailLocale = emailCtx.getShareContainer().getExternalMailLocale().orElse(Language.ENGLISH);
		Context ctx = new Context(Language.toLocale(externalMailLocale));
		ctx.setVariable("expiryDate", shareContainer.getExpiryDate());
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareOwner", emailCtx.getMailContactShareOwner());
		ctx.setVariable("shareRecipient", emailCtx.getMailContactShareRecipient());

		List<Share> shares = Lists.newArrayList();
		AnonymousUrl url = emailCtx.getAnonymousUrl();
		String linshareURL = getLinShareUrlForExternals(shareOwner);
		ctx.setVariable("anonymousURL", emailCtx.getAnonymousUrl().getFullUrl(linshareURL));
		ctx.setVariable("password", url.getTemporaryPlainTextPassword());
		for (AnonymousShareEntry s : url.getAnonymousShareEntries()) {
			shares.add(new Share(s));
			ctx.setVariable("filesSharesLink", getGlobalSharesLink(shares, linshareURL));
			ctx.setVariable("shares", shares);
			ctx.setVariable("sharesCount", shares.size());
		}
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	protected String getGlobalSharesLink(List<Share> shares, String linshareURL) {
		String link = shares.get(0).getHref();
		for (int i = 1; i < shares.size(); i++) {
			link = addNewFilesParam(link, shares.get(i).getUuid());
		}
		return link;
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", true);
		ctx.setVariable("anonymousURL", "#/external/");
		ctx.setVariable("shareRecipient", new MailContact("anonymous@mail.com"));
		ctx.setVariable("shareOwner", new MailContact("peterwilson@linshare.org", "peter", "wilson"));
		List<Share> shares = Lists.newArrayList();
		shares.add(new Share("a-shared-file.txt", true));
		shares.add(new Share("second-shared-file.txt", true));
		shares.add(new Share("third-shared-file.txt", true));
		ctx.setVariable("shares", shares);
		ctx.setVariable("filesSharesLink", getGlobalSharesLink(shares, fakeLinshareURL));
		ctx.setVariable("password", "xxxxx");
		res.add(ctx);
		return res;
	}

}
