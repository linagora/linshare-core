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
package org.linagora.linshare.core.notifications.emails;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.NewSharingEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class NewSharingEmailBuilder extends EmailBuilder{

	public NewSharingEmailBuilder(TemplateEngine templateEngine, boolean insertLicenceTerm,
			MailActivationBusinessService mailActivationBusinessService,
			FunctionalityReadOnlyService functionalityReadOnlyService) {
		super(templateEngine, insertLicenceTerm, mailActivationBusinessService, functionalityReadOnlyService);
	}

	@Override
	public MailContainerWithRecipient build(EmailContext context) throws BusinessException {
		NewSharingEmailContext emailCtx = (NewSharingEmailContext)context;

		User recipient = emailCtx.getRecipient();
		User sender = emailCtx.getSender();
		MailContainer input = emailCtx.getContainer();
		Set<ShareEntry> shareEntries = emailCtx.getShares();

		if (isDisable(recipient, emailCtx.getActivation())) {
			return null;
		}

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());

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

		Context ctx = new Context(input.getLocale());
		ctx.setVariable("sender", new MailContact(sender));
		ctx.setVariable("recipient", new MailContact(recipient));
		ctx.setVariable("customSubject", input.getSubject());

		List<Share> shares = Lists.newArrayList();
		for (ShareEntry share : shareEntries) {
			shares.add(new Share(share));
		}
		ctx.setVariable("shares", shares);
		ctx.setVariable("sharesCount", shares.size());

		ctx.setVariable("recipientServerUrl", getLinShareUrlForAUserRecipient(recipient));
		// TODO getReceivedSharedFileDownloadLink

		container.setSubject(input.getSubject());
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, container,
				input.getPersonalMessage(), context.getType(), ctx);
		return buildMailContainer;
	}

	@Override
	public MailContainerWithRecipient fakeBuild(MailConfig cfg, Language language) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}
