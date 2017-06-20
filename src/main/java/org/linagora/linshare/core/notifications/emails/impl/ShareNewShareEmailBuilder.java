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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareNewShareEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareNewShareEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_NEW_SHARE_FOR_RECIPIENT;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareNewShareEmailContext emailCtx = (ShareNewShareEmailContext) context;

		User shareOwner = emailCtx.getShareOwner();
		ShareContainer shareContainer = emailCtx.getShareContainer();
		String linshareURL = getLinShareUrl(shareOwner);

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("anonymous", emailCtx.isAnonymous());
		ctx.setVariable("customMessage", shareContainer.getMessage());
		ctx.setVariable("customSubject", shareContainer.getSubject());
		ctx.setVariable("expiryDate", shareContainer.getExpiryDate());
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareNote", shareContainer.getSharingNote());
		ctx.setVariable("shareOwner", emailCtx.getMailContactShareOwner());
		ctx.setVariable("shareRecipient", emailCtx.getMailContactShareRecipient());
		ctx.setVariable("linshareURL", linshareURL);

		List<Share> shares = Lists.newArrayList();
		if (emailCtx.isAnonymous()) {
			AnonymousUrl url = emailCtx.getAnonymousUrl();
			ctx.setVariable("anonymousURL", emailCtx.getAnonymousUrl().getFullUrl(linshareURL));
			ctx.setVariable("protected", url.getTemporaryPlainTextPassword() != null);
			ctx.setVariable("password", url.getTemporaryPlainTextPassword());
			for (AnonymousShareEntry s : url.getAnonymousShareEntries()) {
				shares.add(new Share(s));
			}
		} else {
			for (ShareEntry s : emailCtx.getShares()) {
				Share share = new Share(s);
				share.setHref(getRecipientShareLink(linshareURL, s.getUuid()));
				shares.add(share);
			}
		}
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());

		if (emailCtx.isAnonymous()) {
			ctx.setVariable("linshareURL", getLinShareAnonymousURL(shareOwner));
		} else {
			ctx.setVariable("linshareURL", getLinShareUrl(emailCtx.getShareRecipient()));
		}

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		// share with an internal
		res.add(getUserFakeContext(language));
		// share with an external
		res.add(getExternalFakeContext(language));
		// share with an external with password
		res.add(getExternalFakeContext2(language));
		// share without subject and custom message
		res.add(getUserFakeContextWithoutSubject(language));
		// share without subject and custom message (one doc only)
		res.add(getUserFakeContextWithoutSubject2(language));
		return res;
	}

	protected Context getExternalFakeContext(Language language) {
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", true);
		ctx.setVariable("anonymousURL", fakeLinshareURL + "/#external/test");
		ctx.setVariable("customMessage", "Some personal message");
		ctx.setVariable("customSubject", "Some personal subject");
		ctx.setVariable("expiryDate", getFakeExpirationDate());
		ctx.setVariable("protected", false);
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareNote", "a note for a share");
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("unknown@linshare.org"));
		List<Share> shares = Lists.newArrayList();
		shares.add(new Share("a-shared-file.txt", true));
		shares.add(new Share("second-shared-file.txt", false));
		shares.add(new Share("third-shared-file.txt", true));
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());
		return ctx;
	}

	protected Context getExternalFakeContext2(Language language) {
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", true);
		ctx.setVariable("anonymousURL", fakeLinshareURL + "/#external/test");
		ctx.setVariable("customMessage", "Some personal message");
		ctx.setVariable("customSubject", "Some personal subject");
		ctx.setVariable("expiryDate", getFakeExpirationDate());
		ctx.setVariable("protected", true);
		ctx.setVariable("password", "a generated password");
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareNote", "a note for a share");
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("unknown@linshare.org"));
		List<Share> shares = Lists.newArrayList();
		shares.add(new Share("a-shared-file.txt", true));
		shares.add(new Share("second-shared-file.txt", false));
		shares.add(new Share("third-shared-file.txt", true));
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());
		return ctx;
	}

	protected Context getUserFakeContext(Language language) {
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", false);
		ctx.setVariable("customMessage", "Some personal message");
		ctx.setVariable("customSubject", "Some personal subject");
		ctx.setVariable("expiryDate", getFakeExpirationDate());
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareNote", "a note for a share");
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		List<Share> shares = Lists.newArrayList();
		shares.add(getNewFakeShare("a-shared-file.txt", fakeLinshareURL));
		shares.add(getNewFakeShare("second-shared-file.txt", fakeLinshareURL));
		shares.add(getNewFakeShare("third-shared-file.txt", fakeLinshareURL));
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());
		return ctx;
	}

	protected Context getUserFakeContextWithoutSubject(Language language) {
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", false);
		ctx.setVariable("customMessage", null);
		ctx.setVariable("customSubject", null);
		ctx.setVariable("expiryDate", getFakeExpirationDate());
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareNote", "a note for a share");
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		List<Share> shares = Lists.newArrayList();
		shares.add(getNewFakeShare("a-shared-file.txt", fakeLinshareURL));
		shares.add(getNewFakeShare("second-shared-file.txt", fakeLinshareURL));
		shares.add(getNewFakeShare("third-shared-file.txt", fakeLinshareURL));
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());
		return ctx;
	}

	protected Context getUserFakeContextWithoutSubject2(Language language) {
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", false);
		ctx.setVariable("customMessage", null);
		ctx.setVariable("customSubject", null);
		ctx.setVariable("expiryDate", getFakeExpirationDate());
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareNote", "a note for a share");
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		List<Share> shares = Lists.newArrayList();
		shares.add(getNewFakeShare("a-shared-file.txt", fakeLinshareURL));
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());
		return ctx;
	}
}
