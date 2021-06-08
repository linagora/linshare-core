/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.DriveDeletedWarnEmailContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class DriveDeletedtWarnEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.DRIVE_WARN_DELETED_DRIVE;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		DriveDeletedWarnEmailContext emailCtx = (DriveDeletedWarnEmailContext) context;
		Context ctx = new Context(emailCtx.getLocale());
		MailContact owner = null;
		if (emailCtx.getActor() instanceof SystemAccount) {
			owner = new MailContact(emailCtx.getActor().getMail());
		} else {
			owner = new MailContact((User) emailCtx.getActor());
		}
		User member = emailCtx.getUserMember();
		String linshareURL = getLinShareUrl(member);
		ctx.setVariable("driveName", emailCtx.getSharedSpaceMember().getNode().getName());
		ctx.setVariable("owner", emailCtx.getActor());
		ctx.setVariable("member", owner);
		ctx.setVariable("nestedNodes", emailCtx.getNestedNodes());
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
		SharedSpaceMemberDrive driveMember = getNewFakeSharedSpaceMemberDrive("Drive_test");
		List<SharedSpaceNodeNested> nestedNodes = Lists.newArrayList();
		nestedNodes.add(new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_1",
				driveMember.getNode().getUuid(), NodeType.WORK_GROUP, new Date(), new Date()));
		nestedNodes.add(new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_2",
				driveMember.getNode().getUuid(), NodeType.WORK_GROUP, new Date(), new Date()));
		nestedNodes.add(new SharedSpaceNodeNested(UUID.randomUUID().toString(), "workgroup_3",
				driveMember.getNode().getUuid(), NodeType.WORK_GROUP, new Date(), new Date()));
		ctx.setVariable("member", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("owner", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		ctx.setVariable("driveName", driveMember.getNode().getName());
		ctx.setVariable("nestedNodes", nestedNodes);
		res.add(ctx);
		return res;
	}
}
