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
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareFileDownloadEmailContext emailCtx = (ShareFileDownloadEmailContext) context;

		User shareOwner = (User) emailCtx.getEntry().getEntryOwner();
		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Boolean isAnonymous = emailCtx.getAnonymous();
		String linshareURL = getLinShareUrl(shareOwner);
		Share downloadedShare = emailCtx.getShare();
		Document document = emailCtx.getDocument();
		Date shareDate = emailCtx.getEntry().getCreationDate().getTime();
		Date expiryDate = emailCtx.getEntry().getExpirationDate().getTime();
		document.setHref(getOwnerDocumentLink(linshareURL, document.getUuid()));

		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("actionDate", emailCtx.getActionDate());
		ctx.setVariable("anonymous", isAnonymous);
		ctx.setVariable("document", document);
		ctx.setVariable("expiryDate", expiryDate);
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("share", downloadedShare);
		ctx.setVariable("shareDate", shareDate);
		ctx.setVariable("shareOwner", new MailContact(shareOwner));
		ctx.setVariable("shareRecipient", emailCtx.getRecipient());

		List<Share> shares = Lists.newArrayList();
		if (isAnonymous) {
			AnonymousShareEntry shareEntry = emailCtx.getAnonymousShareEntry();
			Set<AnonymousShareEntry> anonymousShareEntries = shareEntry.getAnonymousUrl().getAnonymousShareEntries();
			for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
				Share share = new Share(anonymousShareEntry);
				if (share.equals(downloadedShare)) {
					share.setDownloading(true);
				}
				shares.add(share);
			}
		} else {
			// TODO add share present in SEG related to the current share
			// recipient
			// ShareEntry shareEntry = emailCtx.getShareEntry();
			// ShareEntryGroup shareEntryGroup =
			// shareEntry.getShareEntryGroup();
			// Set<ShareEntry> shareEntries = shareEntryGroup.getShareEntries();
		}
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());
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
