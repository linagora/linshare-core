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
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkSpaceWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class WorkSpaceWarnUpdatedMemberEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.WORK_SPACE_WARN_UPDATED_MEMBER;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		WorkSpaceWarnUpdatedMemberEmailContext emailCtx = (WorkSpaceWarnUpdatedMemberEmailContext) context;
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("nestedMembers", emailCtx.getNestedMembers());
		ctx.setVariable("nbrWorkgroupsUpdated", emailCtx.getNestedMembers().size());
		return buildMailContainer(emailCtx, ctx);
	}

	protected MailContainerWithRecipient buildMailContainer(WorkGroupWarnUpdatedMemberEmailContext emailCtx, Context ctx) {
		MailContact owner = null;
		if (emailCtx.getOwner() instanceof SystemAccount) {
			owner = new MailContact(emailCtx.getOwner().getMail());
		} else {
			owner = new MailContact((User) emailCtx.getOwner());
		}
		SharedSpaceMember workSpaceMember = emailCtx.getWorkGroupMember();
		User member = emailCtx.getUserMember();
		String linshareURL = getLinShareUrl(member);
		ctx.setVariable("owner", owner);
		ctx.setVariable("member", new MailContact(member));
		ctx.setVariable("owner", owner);
		ctx.setVariable("workSpaceMember", workSpaceMember);
		ctx.setVariable("workSpaceName", workSpaceMember.getNode().getName());
		ctx.setVariable("workSpaceLink", getWorkSpaceLink(linshareURL, workSpaceMember.getNode().getUuid()));
		ctx.setVariable(linshareURL, linshareURL);
		MailConfig cfg = member.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		SharedSpaceMemberDrive workSpaceMember = getNewFakeSharedSpaceMemberWorkSpace("work_space_name_1");
		List<SharedSpaceMember> nestedMembers = Lists.newArrayList();
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_1", workSpaceMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "ADMIN", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_2", workSpaceMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "CONTRIBUTOR", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_3", workSpaceMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "READER", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_4", workSpaceMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "READER", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		ctx.setVariable("nestedMembers", nestedMembers.subList(0, 3));
		ctx.setVariable("nbrWorkgroupsUpdated", nestedMembers.size());
		ctx.setVariable("member", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("owner", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("workSpaceMember", workSpaceMember);
		ctx.setVariable("workSpaceName", workSpaceMember.getNode().getName());
		ctx.setVariable("workSpaceLink", getWorkSpaceLink(fakeLinshareURL, "fake_uuid"));
		ctx.setVariable("linshareURL", fakeLinshareURL);
		res.add(ctx);
		return res;
	}

}
