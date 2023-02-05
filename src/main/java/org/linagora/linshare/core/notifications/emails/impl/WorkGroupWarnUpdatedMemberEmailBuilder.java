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
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class WorkGroupWarnUpdatedMemberEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.WORKGROUP_WARN_UPDATED_MEMBER;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		WorkGroupWarnUpdatedMemberEmailContext emailCtx = (WorkGroupWarnUpdatedMemberEmailContext) context;
		Context ctx = new Context(emailCtx.getLocale());
		return buildMailContainer(emailCtx, ctx);
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		SharedSpaceMember workGroupMember = getNewFakeSharedSpaceMember("work_group_name-1");
		ctx.setVariable("member", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("owner", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("threadMember", workGroupMember);
		ctx.setVariable("workGroupName", workGroupMember.getNode().getName());
		ctx.setVariable("workGroupLink", getWorkGroupLink(fakeLinshareURL, "fake_uuid"));
		res.add(ctx);
		return res;
	}

	protected MailContainerWithRecipient buildMailContainer(WorkGroupWarnUpdatedMemberEmailContext emailCtx, Context ctx) {
		MailContact owner = null;
		if (emailCtx.getOwner() instanceof SystemAccount) {
			owner = new MailContact(emailCtx.getOwner().getMail());
		} else {
			owner = new MailContact((User) emailCtx.getOwner());
		}
		SharedSpaceMember workGroupMember = emailCtx.getWorkGroupMember();
		User member = emailCtx.getUserMember();
		String linshareURL = getLinShareUrl(member);
		ctx.setVariable("owner", owner);
		ctx.setVariable("member", new MailContact(member));
		ctx.setVariable("owner", owner);
		ctx.setVariable("threadMember", workGroupMember);
		ctx.setVariable("workGroupName", workGroupMember.getNode().getName());
		ctx.setVariable("workGroupLink", getWorkGroupLink(linshareURL, workGroupMember.getNode().getUuid()));
		ctx.setVariable(linshareURL, linshareURL);
		MailConfig cfg = member.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}
}
