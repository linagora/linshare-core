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
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestUploadedFileEmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class UploadRequestUploadedFileEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_UPLOADED_FILE;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		UploadRequestUploadedFileEmailContext emailCtx = (UploadRequestUploadedFileEmailContext) context;
		User owner = emailCtx.getOwner();
		UploadRequestEntry entry = emailCtx.getEntry();
		UploadRequest request = emailCtx.getUploadRequest();
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		Context ctx = newTmlContext(emailCtx);
		String linshareURL = getLinShareUrl(owner);
		String href = getUploadRequestUploadedFileLink(linshareURL, request.getUploadRequestGroup().getUuid(),
				request.getUuid(), entry.getUuid());
		Document document = new Document(entry);
		document.setHref(href);
		ctx.setVariable("document", document);
		ctx.setVariable("requestUrl", href);

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language, true);
		Document document = getNewFakeUploadRequestUploadedFileLink("a-upload-request-file.txt", fakeLinshareURL);
		document.setSize(65985L);
		document.setCreationDate(new Date());
		ctx.setVariable("document", document);
		res.add(ctx);
		return res;
	}

}
