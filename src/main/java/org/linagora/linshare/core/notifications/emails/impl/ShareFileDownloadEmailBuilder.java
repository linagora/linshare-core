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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareFileDownloadEmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareFileDownloadEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_FILE_DOWNLOAD;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(final EmailContext context) throws BusinessException {
		final ShareFileDownloadEmailContext emailCtx = (ShareFileDownloadEmailContext) context;

		final User shareOwner = (User) emailCtx.getEntry().getEntryOwner();
		final MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		final boolean isAnonymous = emailCtx.getAnonymous();
		final String linshareURL = getLinShareUrl(shareOwner);
		final Share downloadedShare = emailCtx.getShare();
		final Document document = emailCtx.getDocument();
		final Date shareDate = emailCtx.getEntry().getCreationDate().getTime();
		final Calendar expirationDate = emailCtx.getEntry().getExpirationDate();
		final Date expiryDate = expirationDate != null ? expirationDate.getTime() : null;
		document.setHref(getOwnerDocumentLink(linshareURL, document.getUuid()));

		final Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("actionDate", emailCtx.getActionDate());
		ctx.setVariable("anonymous", isAnonymous);
		ctx.setVariable("document", document);
		ctx.setVariable("expiryDate", expiryDate);
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("share", downloadedShare);
		ctx.setVariable("shareDate", shareDate);
		ctx.setVariable("shareOwner", new MailContact(shareOwner));

		if (isAnonymous) {
			ctx.setVariable("shareRecipient", emailCtx.getRecipient());

			final AnonymousShareEntry shareEntry = emailCtx.getAnonymousShareEntry();
			final Set<AnonymousShareEntry> anonymousShareEntries = shareEntry.getAnonymousUrl().getAnonymousShareEntries();
			final List<Share> shares = new ArrayList<>(anonymousShareEntries.size());
			for (final AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
				final Share share = new Share(anonymousShareEntry);
				if (share.equals(downloadedShare)) {
					share.setDownloading(true);
				}
				shares.add(share);
			}
			ctx.setVariable("shares", shares);
			ctx.setVariable("sharesCount", shares.size());
		} else {
			ctx.setVariable("shareRecipient", emailCtx.createRecipientDataAgainstContactListViewStatus());
			// TODO add share present in SEG related to the current share recipient
			// ShareEntry shareEntry = emailCtx.getShareEntry();
			// ShareEntryGroup shareEntryGroup = shareEntry.getShareEntryGroup();
			// Set<ShareEntry> shareEntries = shareEntryGroup.getShareEntries();
			ctx.setVariable("shares", List.of());
			ctx.setVariable("sharesCount", 0);
		}

		return buildMailContainerThymeleaf(cfg, getSupportedType(), ctx, emailCtx);
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		// share with an internal
		res.add(getUserFakeContext(language));
		// share with an external
		res.add(getExternalFakeContext(language));
		return res;
	}

	@Override
	protected Share getNewFakeShare(String name, String linshareURL) {
		Share share = new Share(name);
		if (linshareURL != null) {
			share.setHref(getOwnerDocumentLink(linshareURL, share.getUuid()));
		}
		return share;
	}

	protected Context getInitialFakeContext(Language language) {
		Date fakeExpirationDate = getFakeExpirationDate();
		Context ctx = newFakeContext(language);
		// first share
		Share first = getNewFakeShare("first-shared-file.txt", fakeLinshareURL);
		first.setDownloading(true);
		first.setDownloaded(true);
		first.setExpirationDate(fakeExpirationDate);
		// second share
		Share second = getNewFakeShare("second-shared-file.txt", fakeLinshareURL);
		second.setDownloaded(false);
		// third share
		Share third = getNewFakeShare("third-shared-file.txt", fakeLinshareURL);
		third.setDownloaded(true);
		// shares list
		List<Share> shares = Lists.newArrayList();
		shares.add(first);
		shares.add(second);
		shares.add(third);
		ctx.setVariable("actionDate", new Date());
		ctx.setVariable("anonymous", false);
		ctx.setVariable("expiryDate", fakeExpirationDate);
		ctx.setVariable("share", first);
		ctx.setVariable("shareDate", new Date());
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());
		return ctx;
	}

	protected Context getUserFakeContext(Language language) {
		Context ctx = getInitialFakeContext(language);
		ctx.setVariable("anonymous", true);
		ctx.setVariable("shareRecipient", new MailContact("unknown@linshare.org"));
		return ctx;
	}

	protected Context getExternalFakeContext(Language language) {
		Context ctx = getInitialFakeContext(language);
		ctx.setVariable("anonymous", false);
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		return ctx;
	}

}
