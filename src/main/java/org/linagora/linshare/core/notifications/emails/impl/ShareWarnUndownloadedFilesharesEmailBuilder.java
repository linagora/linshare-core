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
import java.util.Map;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareWarnUndownloadedFilesharesEmailContext;
import org.linagora.linshare.core.notifications.dto.SEGDocument;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.ShareGroup;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareWarnUndownloadedFilesharesEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareWarnUndownloadedFilesharesEmailContext emailCtx = (ShareWarnUndownloadedFilesharesEmailContext) context;

		ShareEntryGroup group = emailCtx.getShareEntryGroup();

		User shareOwner = (User) group.getOwner();
		String linshareURL = getLinShareUrl(shareOwner);

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("shareOwner", new MailContact(shareOwner));
		ctx.setVariable("shareGroup", new ShareGroup(group));

		ctx.setVariable("linshareURL", linshareURL);

		List<SEGDocument> documents = Lists.newArrayList();
		Map<DocumentEntry, List<Entry>> tmpDocuments = group.getTmpDocuments();
		for (Map.Entry<DocumentEntry, List<Entry>> tmpDocument : tmpDocuments.entrySet()) {
			DocumentEntry documentEntry = tmpDocument.getKey();
			SEGDocument d = new SEGDocument(documentEntry);
			d.setHref(getOwnerDocumentLink(linshareURL, d.getUuid()));
			d.setAllDownloaded(allSharesWereDownloaded(documentEntry, group));
			d.setOneDownloaded(oneShareWasDownloaded(documentEntry, group));
			List<Entry> shares = tmpDocument.getValue();
			for (Entry entry : shares) {
				d.addShare(entry);
			}
			documents.add(d);
		}
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);

		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareGroup",
				new ShareGroup("My test subject", new Date(), getFakeExpirationDate(), getFakeExpirationDate()));

		List<SEGDocument> documents = Lists.newArrayList();

		SEGDocument d = null;

		// first document
		d = new SEGDocument("a-shared-file.txt", 653347L);
		d.setHref(getOwnerDocumentLink(fakeLinshareURL, d.getUuid()));
		d.setAllDownloaded(true);
		d.setOneDownloaded(true);
		d.addShare(new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"), true);
		d.addShare(new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"), true);
		d.addShare(new MailContact("unknown@linshare.org"), true);
		documents.add(d);

		// second document
		d = new SEGDocument("a-shared-file2.txt", 6533L);
		d.setHref(getOwnerDocumentLink(fakeLinshareURL, d.getUuid()));
		d.setAllDownloaded(false);
		d.setOneDownloaded(false);
		d.addShare(new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"), false);
		d.addShare(new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"), false);
		d.addShare(new MailContact("unknown@linshare.org"), false);
		documents.add(d);

		// third document
		d = new SEGDocument("a-shared-file3.txt", 653347L);
		d.setHref(getOwnerDocumentLink(fakeLinshareURL, d.getUuid()));
		d.setAllDownloaded(false);
		d.setOneDownloaded(true);
		d.addShare(new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"), true);
		d.addShare(new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"), false);
		d.addShare(new MailContact("unknown@linshare.org"), false);
		documents.add(d);

		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		res.add(ctx);
		return res;
	}

	private boolean allSharesWereDownloaded(DocumentEntry documentEntry, ShareEntryGroup shareEntryGroup) {
		Map<DocumentEntry, Boolean> map = shareEntryGroup.getTmpAllSharesWereNotDownloaded();
		boolean allSharesWereDownloaded = false;
		if (map.get(documentEntry) == null) {
			allSharesWereDownloaded = true;
		}
		return allSharesWereDownloaded;
	}

	private Boolean oneShareWasDownloaded(DocumentEntry documentEntry, ShareEntryGroup shareEntryGroup) {
		Boolean wasDownloaded = false;
		Map<DocumentEntry, Boolean> map = shareEntryGroup.getTmpDocumentsWereDownloaded();
		if (map.size() > 0) {
			wasDownloaded = map.get(documentEntry);
			if (wasDownloaded == null) {
				wasDownloaded = false;
			}
		}
		return wasDownloaded;
	}
}
