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
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.SEGDocument;
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
		final List<MailContact> recipientsWithVisibility = emailCtx.createRecipientDataAgainstContactListViewStatus();
		List<SEGDocument> documents = Lists.newArrayList();
		Map<DocumentEntry, List<Entry>> tmpDocuments = group.getTmpDocuments();
		ctx.setVariable("recipients", recipientsWithVisibility);
		ctx.setVariable("recipientsCount", recipientsWithVisibility.size());
		for (Map.Entry<DocumentEntry, List<Entry>> tmpDocument : tmpDocuments.entrySet()) {
			DocumentEntry documentEntry = tmpDocument.getKey();
			SEGDocument d = new SEGDocument(documentEntry);
			d.setHref(getOwnerDocumentLink(linshareURL, d.getUuid()));
			d.setAllDownloaded(allSharesWereDownloaded(documentEntry, group));
			d.setOneDownloaded(oneShareWasDownloaded(documentEntry, group));
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

		// Documents
		List<SEGDocument> documents = Lists.newArrayList();
		SEGDocument d = new SEGDocument("config.inc.php", 653347L);
		d.setHref(getOwnerDocumentLink(fakeLinshareURL, d.getUuid()));
		d.setAllDownloaded(false);
		d.setOneDownloaded(false);
		documents.add(d);

		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		List<MailContact> recipients = Lists.newArrayList();

		MailContact listContact = new MailContact();
		listContact.setContactListName("invisible_contact_list");
		recipients.add(listContact);

		recipients.add(new MailContact("abbey.curry@linshare.org", "Abbey", "CURRY"));
		recipients.add(new MailContact("grant.big@linshare.org", "Grant", "BIG"));

		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());

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
