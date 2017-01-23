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

import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareNewShareEmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class ShareNewShareEmailBuilder extends EmailBuilder{

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_NEW_SHARE_FOR_RECIPIENT;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareNewShareEmailContext emailCtx = (ShareNewShareEmailContext)context;

		User shareOwner = emailCtx.getShareOwner();
		ShareContainer shareContainer = emailCtx.getShareContainer();
		String linshareURL = getLinShareUrl(shareOwner);

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();

//		StringBuffer names = new StringBuffer();
//		long shareSize = 0;
//		for (ShareEntry share : shares) {
//			if (recipient.getLsUuid().equals(share.getRecipient().getLsUuid())) {
//				shareSize += 1;
//				names.append("<li><a href='"
//						+ getReceivedSharedFileDownloadLink(recipient, share) + "'>"
//						+ share.getName() + "</a></li>");
//			}
//		}

		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("shareOwner", emailCtx.getMailContactShareOwner());
		ctx.setVariable("shareRecipient", emailCtx.getMailContactShareRecipient());
		ctx.setVariable("customSubject", shareContainer.getSubject());
		ctx.setVariable("customMessage", shareContainer.getMessage());
		ctx.setVariable("sharingNote", shareContainer.getSharingNote());
		ctx.setVariable("linshareURL", linshareURL);

		List<Share> shares = Lists.newArrayList();
		if (emailCtx.isAnonymous()) {
			// TODO
		} else {
			for (ShareEntry share : emailCtx.getShares()) {
				shares.add(new Share(share));
			}
		}
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());

		if (emailCtx.isAnonymous()) {
			ctx.setVariable("linshareURL", getLinShareAnonymousURL(shareOwner));
		} else {
			ctx.setVariable("linshareURL", getLinShareUrl(emailCtx.getShareRecipient()));
		}

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(),
				ctx, emailCtx);
		return buildMailContainer;
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = new Context(Language.toLocale(language));
		String linshareURL = "http://127.0.0.1/";
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("customSubject", "Some personal subject");
		ctx.setVariable("customMessage", "Some personal message");
		ctx.setVariable("sharingNote", "a sharing note");

		ctx.setVariable("document", new Document("a-shared-file.txt"));
		ctx.setVariable("share", new Share("a-shared-file.txt", true));

		ctx.setVariable("linshareURL", linshareURL);

		List<Share> shares = Lists.newArrayList();
		shares.add(getNewFakeShare("a-shared-file.txt", linshareURL));
		shares.add(getNewFakeShare("second-shared-file.txt", linshareURL));
		shares.add(getNewFakeShare("third-shared-file.txt", linshareURL));
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());

		res.add(ctx);
		return res;
	}
}
