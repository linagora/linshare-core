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

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestWarnBeforeExpiryEmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class UploadRequestWarnBeforeExpiryEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_WARN_BEFORE_EXPIRY;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		UploadRequestWarnBeforeExpiryEmailContext emailCtx = (UploadRequestWarnBeforeExpiryEmailContext) context;

		User owner = emailCtx.getOwner();
		boolean warnOwner = emailCtx.isWarnOwner();
		UploadRequestUrl requestUrl = emailCtx.getRequestUrl();
		UploadRequest request = emailCtx.getUploadRequest();

		Date d1 = request.getNotificationDate();
		Date d2 = request.getExpiryDate();
		int days = (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));

		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();

		List<MailContact> recipients = getRecipients(request);
		List<Document> documents = getUploadRequestDocuments(warnOwner, request, requestUrl);

		Context ctx = newTmlContext(emailCtx);
		ctx.setVariable("body", request.getUploadRequestGroup().getBody());
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isCollective", request.getUploadRequestGroup().isCollective());
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("remainingDays", days);
		ctx.setVariable("subject", request.getUploadRequestGroup().getSubject());
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		res.add(getFakeOwnerIndividual(language));
		res.add(getFakeRecipientIndividual(language));
		res.add(getFakeOwnerCollective(language));
		res.add(getFakeRecipientCollective(language));
		return res;
	}

	private Context getFakeOwnerIndividual(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		
		List<Document> documents = Lists.newArrayList();
		Document document = getNewFakeDocument("a-upload-request-file.txt", fakeLinshareURL);
		document.setSize(65985L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#ownerlink");
		documents.add(document);
		document = getNewFakeDocument("my-upload-request-file.txt", fakeLinshareURL);
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#ownerlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, true, false);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isCollective", false);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("remainingDays", 8);

		return ctx;
	}

	private Context getFakeOwnerCollective(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		recipients.add(new MailContact("unknown2@linshare.org"));

		List<Document> documents = Lists.newArrayList();
		Document document = getNewFakeDocument("a-upload-request-file.txt", fakeLinshareURL);
		document.setSize(65985L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#ownerlink");
		documents.add(document);
		document = getNewFakeDocument("my-upload-request-file.txt", fakeLinshareURL);
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#ownerlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, true, false);
		ctx.setVariable("remainingDays", 8);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isCollective", true);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("remainingDays", 8);

		return ctx;
	}

	private Context getFakeRecipientIndividual(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));

		List<Document> documents = Lists.newArrayList();
		Document document = getNewFakeDocument("a-upload-request-file.txt", fakeLinshareURL);
		document.setSize(65985L);
		document.setCreationDate(new Date());
		document.setMine(false);
		documents.add(document);
		document = getNewFakeDocument("my-upload-request-file.txt", fakeLinshareURL);
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#ownerlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, false);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isCollective", false);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("remainingDays", 8);

		return ctx;
	}

	private Context getFakeRecipientCollective(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		recipients.add(new MailContact("unknown2@linshare.org"));

		List<Document> documents = Lists.newArrayList();
		Document document = getNewFakeDocument("a-upload-request-file.txt", fakeLinshareURL);
		document.setSize(65985L);
		document.setCreationDate(new Date());
		document.setMine(false);
		documents.add(document);
		document = getNewFakeDocument("my-upload-request-file.txt", fakeLinshareURL);
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, false);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isCollective", true);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("remainingDays", 8);

		return ctx;
	}
}
