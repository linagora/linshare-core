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
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkSpaceDeletedWarnEmailContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class WorkSpaceDeletedtWarnEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.WORK_SPACE_WARN_DELETED;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		WorkSpaceDeletedWarnEmailContext emailCtx = (WorkSpaceDeletedWarnEmailContext) context;
		Context ctx = new Context(emailCtx.getLocale());
		MailContact actor = new MailContact(emailCtx.getActor());
		AbstractDomain abstractDomain = emailCtx.getFromDomain();
		String linshareURL = getLinShareUrl(abstractDomain);
		ctx.setVariable("workSpaceName", emailCtx.getSharedSpaceMember().getNode().getName());
		ctx.setVariable("actor", actor);
		ctx.setVariable("member", emailCtx.getSharedSpaceMember());
		ctx.setVariable("nestedNodes", emailCtx.getNestedNodes());
		ctx.setVariable(linshareURL, linshareURL);
		MailConfig cfg = abstractDomain.getCurrentMailConfiguration();
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		SharedSpaceMemberDrive workSpaceMember = getNewFakeSharedSpaceMemberWorkSpace("WorkSpace_test");
		List<SharedSpaceNodeNested> nestedNodes = Lists.newArrayList();
		nestedNodes.add(new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_1",
				workSpaceMember.getNode().getUuid(), NodeType.WORK_GROUP, new Date(), new Date()));
		nestedNodes.add(new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_2",
				workSpaceMember.getNode().getUuid(), NodeType.WORK_GROUP, new Date(), new Date()));
		nestedNodes.add(new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_3",
				workSpaceMember.getNode().getUuid(), NodeType.WORK_GROUP, new Date(), new Date()));
		ctx.setVariable("member", workSpaceMember);
		ctx.setVariable("actor", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("workSpaceName", workSpaceMember.getNode().getName());
		ctx.setVariable("nestedNodes", nestedNodes);
		res.add(ctx);
		return res;
	}
}
