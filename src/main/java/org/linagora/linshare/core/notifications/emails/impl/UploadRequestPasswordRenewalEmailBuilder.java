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
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class UploadRequestPasswordRenewalEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_PASSWORD_RENEWAL;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		// TODO UploadRequests : new email
		return null;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		res.add(getForRecipient(language));
		return res;
	}

	private Context getForRecipient(Language language) {
		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("password", "a new generated password");
		return ctx;
	}

}
