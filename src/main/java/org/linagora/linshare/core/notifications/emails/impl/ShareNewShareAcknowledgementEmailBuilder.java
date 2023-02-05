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
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareNewShareAcknowledgementEmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareNewShareAcknowledgementEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareNewShareAcknowledgementEmailContext emailCtx = (ShareNewShareAcknowledgementEmailContext) context;

		User shareOwner = emailCtx.getShareOwner();
		Set<Entry> shareEntries = emailCtx.getShares();
		ShareContainer shareContainer = emailCtx.getShareContainer();
		String linshareURL = getLinShareUrl(shareOwner);

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();

		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("shareOwner", new MailContact(shareOwner));
		ctx.setVariable("expirationDate", shareContainer.getExpiryDate());
		// TODO to be improve : sharingDate should be store inside the container
		// or retrieve from SEG.
		ctx.setVariable("creationDate", shareEntries.iterator().next().getCreationDate().getTime());

		List<Document> documents = transform(shareContainer.getDocuments(), true, getLinShareUrl(shareOwner));
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());

		List<MailContact> recipients = shareContainer.getMailContactRecipients();
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());

		ctx.setVariable("linshareURL", linshareURL);

		ctx.setVariable("customSubject", shareContainer.getSubject());
		ctx.setVariable("customMessage", shareContainer.getMessage());
		ctx.setVariable("sharingNote", shareContainer.getSharingNote());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);

		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("expirationDate", getFakeExpirationDate());
		ctx.setVariable("creationDate", new Date());

		List<Document> documents = Lists.newArrayList();
		documents.add(getNewFakeDocument("a-shared-file.txt", fakeLinshareURL));
		documents.add(getNewFakeDocument("second-shared-file.txt", fakeLinshareURL));
		documents.add(getNewFakeDocument("third-shared-file.txt", fakeLinshareURL));
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());

		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		recipients.add(new MailContact("unknown@linshare.org"));
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());

		ctx.setVariable("customSubject", "Some personal subject");
		ctx.setVariable("customMessage", "Some personal message");
		ctx.setVariable("sharingNote", "a sharing note");

		res.add(ctx);
		return res;
	}
}
