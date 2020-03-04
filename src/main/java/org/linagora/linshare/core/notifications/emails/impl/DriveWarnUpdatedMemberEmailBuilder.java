/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.notifications.emails.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.DriveWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class DriveWarnUpdatedMemberEmailBuilder extends WorkGroupWarnUpdatedMemberEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.DRIVE_WARN_UPDATED_MEMBER;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		DriveWarnUpdatedMemberEmailContext emailCtx = (DriveWarnUpdatedMemberEmailContext) context;
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("nestedMembers", emailCtx.getNestedMembers());
		ctx.setVariable("nbrWorkgroupsUpdated", emailCtx.getNestedMembers().size());
		return buildMailContainer(emailCtx, ctx);
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		SharedSpaceMemberDrive driveMember = getNewFakeSharedSpaceMemberDrive("drive_name_1");
		List<SharedSpaceMember> nestedMembers = Lists.newArrayList();
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_1", driveMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "ADMIN", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_2", driveMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "CONTRIBUTOR", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_3", driveMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "READER", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		nestedMembers.add(new SharedSpaceMember(
				new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_4", driveMember.getNode().getUuid(),
						NodeType.WORK_GROUP, new Date(), new Date()),
				new LightSharedSpaceRole(UUID.randomUUID().toString(), "READER", NodeType.WORK_GROUP), new SharedSpaceAccount(
						UUID.randomUUID().toString(), "Peter Wilson", "Peter", "Wilson", "peter.wilson@linshare.org")));
		ctx.setVariable("nestedMembers", nestedMembers.subList(0, 3));
		ctx.setVariable("nbrWorkgroupsUpdated", nestedMembers.size());
		ctx.setVariable("member", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("owner", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("threadMember", driveMember);
		ctx.setVariable("workGroupName", driveMember.getNode().getName());
		ctx.setVariable("workGroupLink", getWorkGroupLink(fakeLinshareURL, "fake_uuid"));
		ctx.setVariable("linshareURL", fakeLinshareURL);
		res.add(ctx);
		return res;
	}

}