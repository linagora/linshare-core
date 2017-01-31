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
