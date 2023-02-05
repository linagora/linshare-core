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

import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestCreatedEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class UploadRequestCreatedEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_CREATED;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		UploadRequestCreatedEmailContext emailCtx = (UploadRequestCreatedEmailContext) context;
		User owner = emailCtx.getOwner();
		UploadRequest request = emailCtx.getUploadRequest();

		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		List<MailContact> recipients = getRecipients(emailCtx.getRecipients());

		Context ctx = newTmlContext(emailCtx);
		ctx.setVariable("body", request.getUploadRequestGroup().getBody());
		ctx.setVariable("isCollective", request.getUploadRequestGroup().isCollective());
		if (request.getUploadRequestGroup().isCollective()) {
			ctx.setVariable("recipients", recipients);
			ctx.setVariable("recipientsCount", recipients.size());
		}
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		res.add(getFakeIndividualForRecipient(language));
		res.add(getFakeCollectiveForRecipient(language));
		return res;
	}

	private Context getFakeCollectiveForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		recipients.add(new MailContact("unknown2@linshare.org"));

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("isCollective", true);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("totalMaxDepotSize", Long.valueOf(45687656));
		ctx.setVariable("recipientsCount", recipients.size());

		return ctx;
	}

	private Context getFakeIndividualForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("isCollective", false);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("totalMaxDepotSize", null);
		ctx.setVariable("recipientsCount", recipients.size());

		return ctx;
	}

}
