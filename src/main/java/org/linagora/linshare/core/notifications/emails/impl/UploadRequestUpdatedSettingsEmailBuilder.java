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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestUpdateSettingsEmailContext;
import org.linagora.linshare.core.notifications.dto.BooleanParameter;
import org.linagora.linshare.core.notifications.dto.DateParameter;
import org.linagora.linshare.core.notifications.dto.IntegerParameter;
import org.linagora.linshare.core.notifications.dto.LongParameter;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.StringParameter;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class UploadRequestUpdatedSettingsEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_UPDATED_SETTINGS;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		UploadRequestUpdateSettingsEmailContext emailCtx = (UploadRequestUpdateSettingsEmailContext) context;
		User owner = emailCtx.getOwner();
		UploadRequest request = emailCtx.getUploadRequest();
		UploadRequest oldRequest = emailCtx.getOldRequest();

		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();

		List<MailContact> recipients = getRecipients(request);
		Context ctx = newTmlContext(emailCtx);
		ctx.setVariable("body", request.getUploadRequestGroup().getBody());
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());

		ctx.setVariable("totalMaxDepotSize", new LongParameter(request.getMaxDepositSize(), oldRequest.getMaxDepositSize()));
		ctx.setVariable("maxFileNum", new IntegerParameter(request.getMaxFileCount(), oldRequest.getMaxFileCount()));
		ctx.setVariable("maxFileSize", new LongParameter(request.getMaxFileSize(), oldRequest.getMaxFileSize()));

		ctx.setVariable("subject", new StringParameter(request.getUploadRequestGroup().getSubject(), oldRequest.getUploadRequestGroup().getSubject()));
		ctx.setVariable("message", new StringParameter(request.getUploadRequestGroup().getBody(), oldRequest.getUploadRequestGroup().getBody()));

		ctx.setVariable("activationDate",
				new DateParameter(request.getActivationDate(), oldRequest.getActivationDate()));
		ctx.setVariable("expiryDate", new DateParameter(request.getExpiryDate(), oldRequest.getExpiryDate()));

		ctx.setVariable("expiryDate", new DateParameter(request.getExpiryDate(), oldRequest.getExpiryDate()));

		ctx.setVariable("closureRight", new BooleanParameter(request.isCanClose(), oldRequest.isCanClose()));
		ctx.setVariable("deletionRight", new BooleanParameter(request.isCanDelete(), oldRequest.isCanDelete()));
		ctx.setVariable("local", new StringParameter(request.getLocale().toString(), oldRequest.getLocale().toString()));

		ctx.setVariable("enableNotification", new BooleanParameter(request.getEnableNotification(), oldRequest.getEnableNotification()));

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
//		res.add(getFakeIndividualForRecipient(language));
		res.add(getFakeCollectiveForRecipient(language));
		return res;
	}

	private Context getFakeCollectiveForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		recipients.add(new MailContact("unknown2@linshare.org"));

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("recipients", recipients);

		ctx.setVariable("recipientsCount", recipients.size());

		ctx.setVariable("subject", new StringParameter("a subject", false));
		ctx.setVariable("message", new StringParameter("a message", false));

		ctx.setVariable("totalMaxDepotSize", new LongParameter(8L, 30L));
		ctx.setVariable("maxFileNum", new IntegerParameter(50, 48));
		ctx.setVariable("maxFileSize", new LongParameter(70L, 69L));

		ctx.setVariable("expiryDate", new DateParameter(new Date(), getFakeExpirationDate()));
		ctx.setVariable("activationDate", new DateParameter(new Date(), false));

		ctx.setVariable("deletionRight", new BooleanParameter(true, false));
		ctx.setVariable("closureRight", new BooleanParameter(true, false));
		ctx.setVariable("local", new StringParameter("fr", "en"));

		ctx.setVariable("enableNotification", new BooleanParameter(true, false));
		return ctx;
	}

	private Context getFakeIndividualForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("protected", false);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());

		ctx.setVariable("totalMaxDepotSize", new IntegerParameter(8, null));
		ctx.setVariable("maxFileNum", new LongParameter(null, 60L));
		ctx.setVariable("maxFileSize", new LongParameter(70L, null));

		ctx.setVariable("subject", new StringParameter("new subject ", "old subject"));
		ctx.setVariable("message", new StringParameter("not changed message", false));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		// old: now => new: now +1 month
		ctx.setVariable("expiryDate", new DateParameter(calendar.getTime(), new Date()));
		ctx.setVariable("activationDate", new DateParameter(new Date(), null));
		// old: Forbidden => new: Allowed
		ctx.setVariable("deletionRight", new BooleanParameter(Boolean.valueOf(true), Boolean.valueOf(false)));
		ctx.setVariable("closureRight", new BooleanParameter(Boolean.valueOf(true), Boolean.valueOf(false)));
		ctx.setVariable("local", new StringParameter("fr", "en"));

		ctx.setVariable("enableNotification", new BooleanParameter(Boolean.valueOf(true), Boolean.valueOf(false)));

		return ctx;
	}

}
