/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.notifications.emails.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestCloseByOwnerEmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class UploadRequestCloseByOwnerEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_CLOSED_BY_OWNER;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		UploadRequestCloseByOwnerEmailContext emailCtx = (UploadRequestCloseByOwnerEmailContext) context;
		User owner = emailCtx.getOwner();
		UploadRequest request = emailCtx.getUploadRequest();
		UploadRequestGroup uploadRequestGroup = request.getUploadRequestGroup();

		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		List<MailContact> recipients = getRecipients(uploadRequestGroup);

		Context ctx = newTmlContext(emailCtx);
		ctx.setVariable("body", uploadRequestGroup.getBody());
		ctx.setVariable("isCollective", request.getUploadRequestGroup().isCollective());
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		res.add(getFakeSingleForRecipient(language));
		res.add(getFakeGroupedForRecipient(language));
		return res;
	}

	private Context getFakeGroupedForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		recipients.add(new MailContact("unknown2@linshare.org"));

		List<Document> documents = Lists.newArrayList();
		Document document = new Document("a-upload-request-file.txt");
		document.setSize(65985L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#recipientlink");
		documents.add(document);
		document = new Document("a-second-upload-request-file.txt");
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#recipientlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isCollective", true);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("totalSize", Long.valueOf(5682642));

		return ctx;
	}

	private Context getFakeSingleForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));

		List<Document> documents = Lists.newArrayList();
		Document document = new Document("a-upload-request-file.txt");
		document.setSize(65985L);
		document.setCreationDate(new Date());
		documents.add(document);
		document.setHref(fakeLinshareURL + "/#recipientlink");
		document = new Document("a-second-upload-request-file.txt");
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#recipientlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isCollective", false);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("totalSize", Long.valueOf(5682642));

		return ctx;
	}

}
