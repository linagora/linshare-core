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
import java.util.Map;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkSpaceWarnDeletedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WorkSpaceWarnDeletedMemberEmailBuilder extends WorkGroupWarnDeletedMemberEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.WORK_SPACE_WARN_DELETED_MEMBER;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		WorkSpaceWarnDeletedMemberEmailContext emailCtx = (WorkSpaceWarnDeletedMemberEmailContext) context;
		return buildMailContainer(emailCtx);
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		SharedSpaceMemberDrive workSpaceMember = getNewFakeSharedSpaceMemberWorkSpace("work_space_name_1");
		Map<String, Object> variables = Maps.newHashMap();
		variables.put("member", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		variables.put("owner", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		variables.put("threadMember", workSpaceMember);
		variables.put("workGroupName", workSpaceMember.getNode().getName());
		variables.put("workGroupLink", getWorkGroupLink(fakeLinshareURL, "fake_uuid"));
		variables.put("linshareURL", fakeLinshareURL);
		ctx.setVariables(variables);
		res.add(ctx);
		return res;
	}

}
