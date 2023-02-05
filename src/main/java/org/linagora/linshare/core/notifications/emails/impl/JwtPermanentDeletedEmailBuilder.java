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
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.JwtPermanentDeletedEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class JwtPermanentDeletedEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_DELETED;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		JwtPermanentDeletedEmailContext emailCtx = (JwtPermanentDeletedEmailContext) context;
		User owner = (User) emailCtx.getOwner();
		User recipient = (User) emailCtx.getRecipient();
		PermanentToken token = emailCtx.getJwtLongTime();
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("label", token.getLabel());
		ctx.setVariable("description", token.getDescription());
		ctx.setVariable("creationDate", token.getCreationDate());
		ctx.setVariable("owner", owner);
		ctx.setVariable("recipient", recipient);
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		PermanentToken token = new PermanentToken();
		token.setLabel("Token label");
		token.setDescription("Token description");
		token.setCreationDate(new Date());
		ctx.setVariable("label", token.getLabel());
		ctx.setVariable("description", token.getDescription());
		ctx.setVariable("creationDate", new Date());
		ctx.setVariable("owner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("recipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		res.add(ctx);
		return res;
	}

}
